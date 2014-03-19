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
import es.eucm.ead.editor.control.actions.Update;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.editor.control.appdata.ReleasePlatformInfo;
import es.eucm.ead.editor.control.appdata.UpdateInfo;
import es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder;
import es.eucm.network.requests.Request;
import es.eucm.network.requests.RequestHelper;
import es.eucm.network.requests.ResourceCallback;

/**
 * This system deals with auto-updates of the application. It is supposed to
 * live in a separate Thread that is controlled by the
 * {@link es.eucm.ead.editor.control.Controller}.
 * 
 * The update process goes through a total of 4 phases (see {@link #run()} for
 * more details): 1) Tries to retrieve the update.json file with info about the
 * latest release remotely. This actually generates a network request and
 * suspends the thread until a response is obtained from
 * {@link es.eucm.network.requests.RequestHelper}. 2) If (1) succeeds, it
 * compares the remote app version read with the one stored in this application.
 * If local version < remote version, the process follows 3) If update needed,
 * asks the user for a confirmation through a dialog. The thread is suspended
 * until the user confirms or denies the update. 4) If the user confirms the
 * update, the update system requests the {@link es.eucm.ead.editor.control.Controller} to open a browser
 * with the page for downloading the new application bundle. This is done
 * through action {@link es.eucm.ead.editor.control.actions.Update}
 * 
 * Created by Javier Torrente on 17/03/14.
 */
public class UpdateSystem extends Thread {

	/**
	 * Wait for 20 seconds for the update.json.
	 */
	private static final long TIMEOUT = 20000;

	// Attributes needed from the controller
	// Just to show a confirmation dialog
	private Controller controller;
	// To compare with the remote UpdateInfo object
	private ReleaseInfo releaseInfo;
	// To make the network request for getting updateInfo
	private RequestHelper requestHelper;

	/**
	 * The remote update.json object read, or null if not available
	 */
	private UpdateInfo updateInfo;

	/**
	 * The url that should be opened if user confirms update (
	 * {@link #userConfirmedUpdate})
	 */
	private String installerURL;
	private boolean userConfirmedUpdate;

	/**
	 * To suspend the thread when: a) the request to get update.json has been
	 * created but response has not been received yet b) the update operation is
	 * pending from user's approval
	 */
	private Object monitor;

	// Constructor
	public UpdateSystem(ReleaseInfo releaseInfo, RequestHelper requestHelper,
			Controller controller) {
		this.controller = controller;
		this.releaseInfo = releaseInfo;
		this.requestHelper = requestHelper;
		this.updateInfo = null;
		this.installerURL = null;
		this.monitor = new Object();
		this.userConfirmedUpdate = false;
	}

	@Override
	public void run() {
		// First, try to retrieve the update.json file
		if (downloadUpdateInfo()) {
			/**
			 * Suspend this thread until
			 * {@link es.eucm.ead.editor.control.updatesystem.UpdateSystem.UpdateInfoCallback}
			 * is notified about the update.json loading process results
			 */
			if (updateInfo == null) {
				pauseUpdate(TIMEOUT);
			}

			// Once updateInfo is available, check if update is needed
			if (checkUpdateNeeded()) {
				// Wait until user confirms update
				askUserConfirmation();
				pauseUpdate();
				if (userConfirmedUpdate) {
					openDownloadURL();
				}
			}
		}
	}

	// Phase 1: fetch update.json
	private boolean downloadUpdateInfo() {
		boolean requestCreated = false;
		if (updateInfo == null) {
			// Try to download update.json. If updateURL is not present, disable
			// the update system
			if (releaseInfo.getUpdateURL() != null) {
				Request request = new Request();
				request.setUri(releaseInfo.getUpdateURL());
				request.setMethod("get");
				Gdx.app.debug(this.getClass().getCanonicalName(),
						"Trying to retrieve update.json from url:"
								+ releaseInfo.getUpdateURL());
				requestHelper.get(request, releaseInfo.getUpdateURL(),
						new UpdateInfoCallback(), String.class, false);
				requestCreated = true;
			} else {
				Gdx.app.debug(this.getClass().getCanonicalName(),
						"The update.json url is null. The update system will be disabled.");
				requestCreated = false;
			}
		}
		return requestCreated;
	}

	// Phase 2: Once update.json has been retrieved, check if localVersion <
	// remoteVersion
	private boolean checkUpdateNeeded() {
		boolean updateNeeded = false;
		if (updateInfo != null) {
			if (updateInfo.getVersion() != null) {
				if (compareAppVersions(releaseInfo.getAppVersion(),
						updateInfo.getVersion()) < 0) {
					Gdx.app.debug(this.getClass().getCanonicalName(),
							"This application is outdated. Checking if an url for the update is available.");
					// Iterate through platforms
					for (ReleasePlatformInfo platform : updateInfo
							.getPlatforms()) {
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
							this.getClass().getCanonicalName(),
							"This application is up to date. No update is needed. The update system will be disabled.");
				}
			} else {
				Gdx.app.debug(
						this.getClass().getCanonicalName(),
						"The update.json has a null version number. The update system will be disabled.");
			}
		}
		return updateNeeded;
	}

	// Phase 3: If update is required, ask for user confirmation
	private void askUserConfirmation() {
		if (installerURL != null) {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Asking the user to confirm download from:" + installerURL);
			controller.getViews().showDialog(
					ConfirmationDialogBuilder.class.getCanonicalName(),
					new ConfirmationDialogBuilder.ConfirmationDialogListener() {

						@Override
						public void dialogClosed(boolean accepted) {
							userConfirmedUpdate = accepted;
							resumeUpdate();
						}
					});
		}

	}

	// Phase 4: if user confirms the operation, start the update
	private void openDownloadURL() {
		if (userConfirmedUpdate) {
			controller.action(Update.class, installerURL);
		}
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

	// Methods for pausing and resuming the update process
	private void resumeUpdate() {
		synchronized (monitor) {
			monitor.notify();
		}
	}

	private void pauseUpdate() {
		pauseUpdate(-1);
	}

	private void pauseUpdate(long timeout) {
		synchronized (monitor) {
			try {
				if (timeout > 0)
					monitor.wait(timeout);
				else
					monitor.wait();
			} catch (InterruptedException e) {
				Gdx.app.debug(this.getClass().getCanonicalName(),
						"Exception while pausing the update process", e);
			}
		}
	}

	/**
	 * Callback that is passed to the
	 * {@link es.eucm.network.requests.RequestHelper} to be notified once
	 * update.json has been retrieved.
	 */
	private class UpdateInfoCallback implements ResourceCallback<String> {

		@Override
		public void error(Throwable e) {
			Gdx.app.debug(
					this.getClass().getCanonicalName(),
					"Error fetching update.json. UpdateSystem will be disabled",
					e);
			resumeUpdate();
		}

		@Override
		public void success(String data) {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Update.json fetched and read: " + data);
			try {
				updateInfo = controller.getApplicationAssets().fromJson(
						UpdateInfo.class, data);
			} catch (SerializationException e) {
				Gdx.app.error(this.getClass().getCanonicalName(),
						"An error occurred while reading update.json from "
								+ releaseInfo.getUpdateURL()
								+ ". The update system will be disabled.", e);
				updateInfo = null;
			}
			resumeUpdate();
		}
	}
}
