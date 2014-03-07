/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;

/**
 * Encapsulates an editor task which can be invoked from different contexts. For
 * example, "undo", "redo", "save as", "open" and "run" will all be actions.
 * Notice that the some actions may require additional input from the user
 * before actually doing much; for instance, "save as" will likely pop up a
 * dialog before saving anything.
 * 
 * Actions are the only first line of interaction, exposing editor APIs to the
 * GUI user. They delegate all the actual heavy lifting to the actual editor
 * APIs.
 * 
 * @author mfreire
 */
public abstract class EditorAction {

	private Array<EditorActionListener> listeners;

	protected Controller controller;

	private boolean enabled;

	/**
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 */
	public EditorAction(boolean initialEnable) {
		this.listeners = new Array<EditorActionListener>();
		enabled = initialEnable;
	}

	/**
	 * Creates the action initially enabled
	 */
	public EditorAction() {
		this(true);
	}

	/**
	 * @param controller
	 *            the main editor controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}

	/**
	 * @return true if the action makes sense in the current context; For
	 *         example, you cannot save anything if you do not have anything
	 *         open
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            Sets if this actions is enabled and can be invoked by the user
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (EditorActionListener listener : listeners) {
			listener.enabledChanged(getClass(), this.enabled);
		}
	}

	public void addListener(EditorActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Executes the action with the given arguments
	 * 
	 * @param args
	 *            arguments for the action
	 */
	public abstract void perform(Object... args);

	public interface EditorActionListener {

		/**
		 * The state of the action changed
		 * 
		 * @param actionClass
		 *            the action class
		 * @param enable
		 *            if the action is enable
		 */
		void enabledChanged(Class actionClass, boolean enable);
	}
}
