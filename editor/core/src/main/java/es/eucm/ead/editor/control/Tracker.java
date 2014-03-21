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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.appdata.BugReport;
import es.eucm.network.Method;
import es.eucm.network.requests.*;

/**
 * Tracks user interaction with the editor. Do not worry, we do not work for the
 * NSA. We just want to know how our editor is used to improve it.
 * 
 * This tracker contains method to register several events in the editor.
 */
public class Tracker implements PreferenceListener {

	/**
	 * The number of actions on top of the action log stack that will be sent
	 * out along with an exception during bug reporting.
	 */
	private static final int ACTION_NUMBER_BUGREPORTING = 40;

	/**
	 * Path to send bugs to Bugr
	 */
	private static final String BUG_PATH = "/api/bug";

	/**
	 * Path to activate installations in Bugr
	 */
	private static final String ACITAVATE_PATH = "/api/activate";

	private Controller controller;

	protected RequestHelper requestHelper;

	protected String cid;

	protected String bugrURL;

	/**
	 * If the tracking is enabled
	 */
	private boolean enabled;

	public Tracker(Controller controller) {
		this.controller = controller;
		this.bugrURL = controller.getReleaseInfo().getBugReportURL();
		requestHelper = controller.getRequestHelper();
		loadClientId(controller.getPreferences());
		// Read preferences
		Preferences preferences = controller.getPreferences();
		setEnabled(preferences.getBoolean(Preferences.TRACKING_ENABLED));
		preferences.addPreferenceListener(Preferences.TRACKING_ENABLED, this);

	}

	/**
	 * Loads the client identifier from preferences. It if does not exists,
	 * obtains one from bugr
	 * 
	 * @param preferences
	 *            the editor preferences
	 */
	private void loadClientId(Preferences preferences) {
		String clientId = preferences.getString(Preferences.CLIENT_ID);
		// Obtain an unique id
		if (clientId == null) {
			requestHelper.url(bugrURL + ACITAVATE_PATH).method(Method.POST)
					.send(new RequestCallback() {
						@Override
						public void error(Request request, Throwable throwable) {
							Gdx.app.error(
									"Tracker",
									"Impossible to activate this installation.",
									throwable);
						}

						@Override
						public void success(Request request, Response response) {
							cid = response.getContent();
							controller.getPreferences().putString(
									Preferences.CLIENT_ID, cid);
							// Start session with recently acquired cid
							startSession();

						}
					});
		} else {
			this.cid = clientId;
		}
	}

	/**
	 * 
	 * @return if the tracker is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets if the tracker is enabled
	 * 
	 * @param enabled
	 *            if tracker is enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * The user just started a session with the editor
	 */
	public void startSession() {
		if (enabled) {
			startSessionImpl();
		}

	}

	/**
	 * The user finished a session with the editor
	 */
	public void endSession() {
		if (enabled) {
			endSessionImpl();
		}
	}

	/**
	 * The user performs an action
	 * 
	 * @param action
	 *            the action performed (represented by a serialized string)
	 */
	public void actionPerformed(String action) {
		if (enabled) {
			actionPerformedImpl(action);
		}
	}

	/**
	 * Actual implementation for start session signal. To be implemented by
	 * inheriting classes
	 */
	protected void startSessionImpl() {
		Gdx.app.debug("Tracker", "Session started");
	}

	/**
	 * Actual implementation for end session signal. To be implemented by
	 * inheriting classes
	 */
	protected void endSessionImpl() {
		Gdx.app.debug("Tracker", "Session ended");
	}

	/**
	 * Actual implementation of action performed. To be implement by inheriting
	 * classes
	 * 
	 * @param action
	 *            the action performed (represented by a serialized string)
	 */
	protected void actionPerformedImpl(String action) {
		Gdx.app.debug("Tracker", action);
	}

	/**
	 * The user enabled the tracker
	 */
	protected void trackerEnabled() {
		Gdx.app.debug("Tracker", "Tracker enabled by user");
	}

	/**
	 * The user disabled the tracking
	 */
	protected void trackerDisabled() {
		Gdx.app.debug("Tracker", "Tracker disabled by user");
	}

	/**
	 * Connects to our backend to notify a bug. In this context, a bug is
	 * defined by an unhandled exception.
	 * 
	 * A bug report contains the exception and also the stack of actions the
	 * user performed on this session.
	 * 
	 * @param e
	 *            The unhandled exception.
	 */
	public void reportBug(Throwable e) {
		// If there's no available url for bug reporting, do nothing
		if (bugrURL != null) {
			// Create bug report
			BugReport bugReport = new BugReport();
			bugReport.setActionsLog(controller.getActions().getLoggedActions(
					ACTION_NUMBER_BUGREPORTING));
			bugReport.setThrowable(e);

			requestHelper.url(bugReport + BUG_PATH).post(bugReport,
					new RequestCallback() {
						@Override
						public void error(Request request, Throwable throwable) {
							Gdx.app.error("Tracker", "Error sending bug",
									throwable);
						}

						@Override
						public void success(Request request, Response response) {
							Gdx.app.debug("Tracker", "Bug sent successfully");
						}
					});

		}
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		boolean newEnabled = (newValue instanceof Boolean)
				&& (Boolean) newValue;
		if (isEnabled() != newEnabled) {
			if (isEnabled()) {
				trackerDisabled();
				endSession();
			} else {
				trackerEnabled();
				startSession();
			}
			setEnabled(newEnabled);
		}
	}
}
