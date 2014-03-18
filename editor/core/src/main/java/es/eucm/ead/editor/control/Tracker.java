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

/**
 * Tracks user interaction with the editor. Do not worry, we do not work for the
 * NSA. We just want to know how our editor is used to improve it.
 * 
 * This tracker contains method to register several events in the editor.
 */
public class Tracker {

	/**
	 * If the tracking is enabled
	 */
	private boolean enabled;

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

}
