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
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.control.appdata.BugReport;
import es.eucm.network.requests.Request;
import es.eucm.network.requests.RequestCallback;
import es.eucm.network.requests.Response;

/**
 * Tracks user interaction with the editor. Do not worry, we do not work for the
 * NSA. We just want to know how our editor is used to improve it.
 * 
 * This tracker contains method to register several events in the editor.
 */
public class Tracker {

	/**
	 * The number of actions on top of the action log stack that will be sent
	 * out along with an exception during bug reporting.
	 */
	private static final int ACTION_NUMBER_BUGREPORTING = 40;

	/**
	 * If the tracking is enabled
	 */
	private boolean enabled;

	/**
	 * The URL used to post bug reports. Should be provided by
	 * {@link es.eucm.ead.editor.control.Controller}, who has access to it
	 * through the {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} file
	 * where it is stored.
	 */
	private String bugReportURL;

	/**
	 * This object needs the controller to access
	 * {@link es.eucm.ead.editor.control.Actions#getLoggedActions(int)}, and
	 * {@link es.eucm.ead.editor.assets.ApplicationAssets#toJson(Object)} for
	 * retrieving and serializing actions for bug reporting; and also
	 * {@link Controller#getRequestHelper()} to make requests to the bug
	 * reporting backend.
	 */
	private Controller controller;

	/**
	 * Constructor. Receives the url of the backend used for bug reporting. If
	 * {@link #bugReportURL} is null, the bug report feature will be disabled.
	 * 
	 * It also receives the controller to get access to some stuff (see comment
	 * above).
	 * 
	 */
	public Tracker(String bugReportURL, Controller controller) {
		this.bugReportURL = bugReportURL;
		this.controller = controller;
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
	 * Connects to our backend to notify a bug. In this context, a bug is
	 * defined by an unhandled exception.
	 * 
	 * A bug report contains the exception and also the stack of actions the
	 * user performed on this session.
	 * 
	 * @param e
	 *            The unhnandled exception.
	 */
	public void reportBug(Throwable e) {
		// If there's no available url for bug reporting, do nothing
		if (bugReportURL != null) {
			// Create bug report
			BugReport bugReport = new BugReport();
			bugReport.setActionsLog(controller.getActions().getLoggedActions(
					ACTION_NUMBER_BUGREPORTING));
			bugReport.setThrowable(e);
			bugReport.setExceptionTimestamp(System.currentTimeMillis() + "");
			// Get the json
			String json = null;
			try {
				json = controller.getApplicationAssets().toJson(bugReport);
			} catch (SerializationException e1) {
				Gdx.app.error(
						this.getClass().getCanonicalName(),
						"An exception thrown while serializing the bug report. The report could not be sent.",
						e1);
			}

			// If json is valid and no SerializationException was thrown, create
			// the request
			if (json != null) {
				// Create request
				Request request = new Request();
				request.setMethod("POST");
				request.setUri(bugReportURL);
				request.setEntity(json);

				// Send request
				controller.getRequestHelper().send(request, bugReportURL,
						new RequestCallback() {
							@Override
							public void error(Request request,
									Throwable throwable) {
								// TODO Implement once the user flow of bug
								// reporting has been defined
							}

							@Override
							public void success(Request request,
									Response response) {
								// TODO Implement once the user flow of bug
								// reporting has been defined
							}
						});
				Gdx.app.debug(this.getClass().getCanonicalName(),
						"Bug sent successfully", e);
			}
		} else {
			Gdx.app.error(
					this.getClass().getCanonicalName(),
					"Bug could not be reported since the bug reporting URL is not properly defined",
					e);
		}
	}

}
