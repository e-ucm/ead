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
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;

/**
 * Tracks user interaction with the editor. Do not worry, we do not work for the
 * NSA. We just want to know how our editor is used to improve it.
 * 
 * This tracker contains method to register several events in the editor.
 */
public class Tracker implements PreferenceListener {

	/**
	 * Path to activate installations in Bugr
	 */
	private static final String ACTIVATE_PATH = "/api/activate";

	private Controller controller;

	protected String cid;

	protected String bugrURL;

	/**
	 * If the tracking is enabled
	 */
	private boolean enabled;

	public Tracker(Controller controller) {
		this.controller = controller;
		// Read preferences
		Preferences preferences = controller.getPreferences();
		setEnabled(preferences.getBoolean(Preferences.TRACKING_ENABLED));
		preferences.addPreferenceListener(Preferences.TRACKING_ENABLED, this);

		bugrURL = controller.getReleaseInfo().getBugReportURL();
		if (bugrURL == null) {
			enabled = false;
		} else {
			loadClientId();
		}
	}

	/**
	 * Loads the client identifier from preferences. If it does not exists,
	 * obtains one from bugr.
	 */
	protected void loadClientId() {
		String clientId = controller.getPreferences().getString(
				Preferences.CLIENT_ID);
		if ("".equals(clientId) || clientId == null) {
			HttpRequest request = new HttpRequest("POST");
			request.setUrl(bugrURL + ACTIVATE_PATH);
			// Obtain an unique id
			Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
				@Override
				public void handleHttpResponse(HttpResponse httpResponse) {
					if (httpResponse.getStatus().getStatusCode() == 200) {
						cid = httpResponse.getResultAsString();
						controller.getPreferences().putString(
								Preferences.CLIENT_ID, cid);
						// Start session with recently acquired cid
						startSession();
					} else {
						Gdx.app.error("Tracker",
								"Impossible to activate this installation. Server said:\n"
										+ httpResponse.getResultAsString());
					}
				}

				@Override
				public void failed(Throwable throwable) {
					Gdx.app.error("Tracker",
							"Impossible to activate this installation.",
							throwable);
				}

				@Override
				public void cancelled() {

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
	public void actionPerformed(Class clazz, boolean performed) {
		if (enabled) {
			actionPerformedImpl(clazz, performed);
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
	 */
	protected void actionPerformedImpl(Class clazz, boolean performed) {
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

	public void changeView(String simpleName) {
		Gdx.app.debug("Tracker", "Change view " + simpleName);
	}

	/**
	 * A new scene was created
	 */
	public void newScene() {
	}

	/**
	 * A button was pressed
	 * 
	 * @param label
	 *            label of the button
	 */
	public void buttonPressed(String label) {
	}
}
