/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.editor.control.updatesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.editor.control.appdata.UpdatePlatformInfo;
import es.eucm.ead.editor.control.appdata.UpdateInfo;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder;
import es.eucm.ead.engine.I18N;
import es.eucm.network.requests.Request;
import es.eucm.network.requests.ResourceCallback;

/**
 * This system deals with auto-updates of the application. It is managed by the
 * {@link es.eucm.ead.editor.control.Controller}. The whole update process
 * starts by invoking {@link #startUpdateProcess()}. This method checks if
 * update is enabled (the system can be disabled through a user preference), and
 * if so, triggers an update process that is composed of 4 phases:<br/>
 * 
 * <ol>
 * <li><b> {@link #downloadUpdateInfo()}</b>. Tries to retrieve the update.json
 * file with info about the latest release remotely. This actually generates a
 * network request, encapsulated as a
 * {@link es.eucm.ead.editor.control.background.BackgroundTask}. The update
 * process stops until response to the request is obtained from
 * {@link es.eucm.network.requests.RequestHelper}.</li>
 * <li><b>
 * {@link #checkUpdateNeeded(es.eucm.ead.editor.control.appdata.UpdateInfo)}
 * </b>If (1) succeeds, it invokes
 * {@link #checkUpdateNeeded(es.eucm.ead.editor.control.appdata.UpdateInfo)}
 * with the {@link es.eucm.ead.editor.control.appdata.UpdateInfo} object
 * retrieved by the {@link es.eucm.network.requests.RequestHelper}. This method
 * compares the remote app version read with the one stored in this application.
 * If local version < remote version, the process continues</li>
 * <li><b>{@link #askUserConfirmation(String)}</b>If update is needed, asks the
 * user for a confirmation through a dialog. The process stops until the user
 * confirms or denies the update. If the user confirms the update, this method
 * invokes the last phase:</li>
 * <li><b>{@link #update(String)}. </b>The update system requests the
 * {@link es.eucm.ead.editor.control.Controller} to open a browser with the page
 * for downloading the new application bundle, which was specified in the
 * {@link es.eucm.ead.editor.control.appdata.UpdateInfo} object.</li>
 * </ol>
 * 
 * Created by Javier Torrente on 17/03/14.
 */
public class UpdateSystem {

	private static final String LOG_TAG = "UpdateSystem";

	/**
	 * Needed to: - Show confirmation dialog - Get access to the request helper
	 * - Get access to preferences - Submit new BackgroundTasks - Get access to
	 * I18N - Parse json (through applicationAssets)
	 */
	private Controller controller;

	/**
	 * To compare with the remote UpdateInfo object
	 */
	private ReleaseInfo releaseInfo;

	/**
	 * Additional param that can be passed upon construction to skip the user
	 * confirmation step. Needed to facilitate testing, only.
	 */
	private boolean skipUserConfirmation;

	/**
	 * This field is false whether the system is waiting for any operation to
	 * complete, true otherwise.
	 * 
	 * Needed for testing, basically
	 */
	private boolean done;

	/**
	 * Returns the current state of the UpdateSystem. It only distinguishes two
	 * states: - Started but incomplete (any of the operations scheduled is
	 * pending) - Completed: either because the whole update process completed
	 * successfully, or because any of the intermediate steps returned without
	 * achieving a successful state (for example, because the user denied the
	 * update, or because the remote update.json file could not be fetched).
	 * 
	 * Convenient for testing.
	 */
	public synchronized boolean isDone() {
		return done;
	}

	private synchronized void setDone() {
		done = true;
	}

	// Constructors

	/**
	 * Basic constructor.
	 * 
	 * @param releaseInfo
	 *            The release info object is read from appdata/release.json and
	 *            contains the url to check the latest version available, plus
	 *            other stuff required like the platform version.
	 * @param controller
	 *            Needed for accessing quite a lot of stuff (see comment above).
	 */
	public UpdateSystem(ReleaseInfo releaseInfo, Controller controller) {
		this(releaseInfo, controller, false);
	}

	/**
	 * Additional constructor, mainly for testing. Accepts an additional
	 * argument to skip user confirmation.
	 */
	public UpdateSystem(ReleaseInfo releaseInfo, Controller controller,
			boolean skipUserConfirmation) {
		this.controller = controller;
		this.releaseInfo = releaseInfo;
		this.skipUserConfirmation = skipUserConfirmation;
		done = false;
	}

	/**
	 * Starts the 4-step update process. The entity responsible for creating the
	 * UpdateSystem should just invoke this method to trigger the whole process
	 * that is self-controlled by UpdateSystem.
	 */
	public void startUpdateProcess() {
		// Check if user deactivated update feature
		if (isUpdateActivated()) {
			downloadUpdateInfo();
		} else {
			setDone();
		}
	}

	/**
	 * Phase 1: Downloads the update.json file from the url specified in
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo#setUpdateURL(String)}
	 * . Since this operation is asynchronous, it uses a
	 * {@link es.eucm.ead.editor.control.background.BackgroundTask} (see
	 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem.DownloadUpdateInfoTask}
	 * ).
	 */
	private void downloadUpdateInfo() {
		controller.getBackgroundExecutor().submit(new DownloadUpdateInfoTask(),
				new BackgroundExecutor.BackgroundTaskListener() {
					@Override
					public void completionPercentage(float percentage) {
						Gdx.app.debug(LOG_TAG,
								"Downloading update.json. Progress:"
										+ percentage);
					}

					@Override
					public void done(BackgroundExecutor backgroundExecutor,
							Object result) {
						Gdx.app.debug(LOG_TAG,
								"Downloading update.json. Complete!" + result);
					}

					@Override
					public void error(Throwable e) {
						Gdx.app.error(LOG_TAG,
								"Downloading update.json. Error occurred", e);
					}
				});

	}

	private class DownloadUpdateInfoTask extends BackgroundTask {

		@Override
		public Object call() throws Exception {

			// Try to download update.json. If updateURL is not present, disable
			// the update system
			if (releaseInfo.getUpdateURL() != null) {
				Request request = new Request();
				request.setUri(releaseInfo.getUpdateURL());
				request.setMethod("get");
				Gdx.app.debug(LOG_TAG,
						"Trying to retrieve update.json from url:"
								+ releaseInfo.getUpdateURL());
				controller.getRequestHelper().get(request,
						releaseInfo.getUpdateURL(),
						new ResourceCallback<String>() {
							@Override
							public void error(Throwable e) {
								Gdx.app.debug(
										LOG_TAG,
										"Error fetching update.json. UpdateSystem will be disabled",
										e);
								setDone();
							}

							@Override
							public void success(String data) {
								Gdx.app.debug(LOG_TAG,
										"Update.json fetched and read: " + data);
								try {
									UpdateInfo updateInfo = controller
											.getApplicationAssets().fromJson(
													UpdateInfo.class, data);
									if (updateInfo != null) {
										checkUpdateNeeded(updateInfo);
									}
								} catch (SerializationException e) {
									Gdx.app.error(
											LOG_TAG,
											"An error occurred while reading update.json from "
													+ releaseInfo
															.getUpdateURL()
													+ ". The update system will be disabled.",
											e);
									setDone();
								}

							}
						}, String.class, false);
			} else {
				Gdx.app.debug(LOG_TAG,
						"The update.json url is null. The update system will be disabled.");
				setDone();
			}

			return null;
		}

	}

	//

	/**
	 * Phase 2: Once update.json has been retrieved, check if localVersion <
	 * remoteVersion. If that's the case, moves on to phase 3 by invoking
	 * {@link #askUserConfirmation(String)}.
	 * 
	 * @param updateInfo
	 *            The updateInfo read from the previous phase (
	 *            {@link #downloadUpdateInfo()}.
	 */
	private void checkUpdateNeeded(UpdateInfo updateInfo) {
		boolean updateNeeded = false;
		String installerURL = null;
		if (updateInfo.getVersion() != null) {
			if (compareAppVersions(releaseInfo.getAppVersion(),
					updateInfo.getVersion()) < 0) {
				Gdx.app.debug(LOG_TAG,
						"This application is outdated. Checking if an url for the update is available.");
				// Iterate through platforms
				for (UpdatePlatformInfo platform : updateInfo.getPlatforms()) {
					if (platform.getOs() != null
							&& platform.getOs().toString()
									.equals(releaseInfo.getOs().toString())) {
						installerURL = platform.getUrl();
						updateNeeded = true;
						break;
					}
				}
			} else {
				Gdx.app.debug(
						LOG_TAG,
						"This application is up to date. No update is needed. The update system will be disabled.");
			}
		} else {
			Gdx.app.debug(
					LOG_TAG,
					"The update.json has a null version number. The update system will be disabled.");
		}

		if (updateNeeded) {
			askUserConfirmation(installerURL);
		} else {
			setDone();
		}
	}

	/**
	 * Phase 3: If update is required, ask for user confirmation. If the user
	 * confirms the operation, or if {@link #skipUserConfirmation} is set to
	 * true when the UpdateSystem is built, the system goes onto the last phase
	 * ({@link #update(String)})
	 */
	private void askUserConfirmation(final String installerURL) {
		if (skipUserConfirmation) {
			update(installerURL);
			return;
		}

		I18N i18N = controller.getApplicationAssets().getI18N();
		Gdx.app.debug(LOG_TAG, "Asking the user to confirm download from:"
				+ installerURL);
		controller
				.getViews()
				.showDialog(
						ConfirmationDialogBuilder.class.getCanonicalName(),
						i18N.m("update.title"),
						i18N.m("update.message", i18N.m("general.ok")),
						new ConfirmationDialogBuilder.ConfirmationDialogClosedListener() {

							@Override
							public void dialogClosed(boolean accepted) {
								if (accepted) {
									update(installerURL);
								} else {
									setDone();
								}
							}
						},
						new ConfirmationDialogBuilder.ConfirmationDialogCheckboxListener() {

							@Override
							public void checkboxChanged(boolean marked) {
								updatePreferences(marked);
							}

							@Override
							public boolean isMarked() {
								return false;
							}
						}, i18N.m("update.donotshowagain"));

	}

	/**
	 * Last step: actually makes the "update", which consists of opening the
	 * browser with the appropriate url
	 * 
	 * @param installerURL
	 *            The URL where the app bundle for the user's platform lives.
	 */
	private void update(String installerURL) {
		controller.getPlatform().browseURL(installerURL);
		setDone();
	}

	// ////////////////////////////
	// / Preferences
	// ///////////////////////////

	/**
	 * Preferences
	 */
	private void updatePreferences(boolean doNotAskAgain) {
		Gdx.app.debug(LOG_TAG, "Updating preferences: updateDisabled="
				+ doNotAskAgain);
		controller.getPreferences().putBoolean(Preferences.UPDATE_DISABLED,
				doNotAskAgain);
	}

	private boolean isUpdateActivated() {
		return !controller.getPreferences().getBoolean(
				Preferences.UPDATE_DISABLED, false);
	}

	/**
	 * Checks local and remove version numbers. Internally makes use of Maven's
	 * {@link es.eucm.ead.editor.control.updatesystem.ComparableVersion}
	 * 
	 * @param localVersion
	 *            The appVersion contained in this app release.json file (e.g.
	 *            2.0.0)
	 * @param remoteVersion
	 *            The appVersion contained in the remote update.json file read
	 *            (e.g. 2.0.1)
	 * @return 0 if localVersion and remoteVersion are equals (no update needed)
	 *         -1 if localVersion is inferior to remote Version (update needed)
	 *         1 if localVersion is superior to remoteVersion (weird case,
	 *         shouldn't happen)
	 */
	private int compareAppVersions(String localVersion, String remoteVersion) {
		ComparableVersion comparableVersion = new ComparableVersion(
				localVersion);
		return comparableVersion
				.compareTo(new ComparableVersion(remoteVersion));
	}

}
