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

	protected Controller controller;

	private String name;

	/**
	 * 
	 * @param name
	 *            an unique identifier for the action
	 */
	public EditorAction(String name) {
		this.name = name;
	}

	/**
	 * @param controller
	 *            the main editor controller
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}

	/**
	 * short, descriptive name. Actions are located by their names; "saveAs" or
	 * "launchGame" are typical examples. Action names must be unique.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return true if the action makes sense in the current context; For
	 *         example, you cannot save anything if you do not have anything
	 *         open
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Executes the action with the given arguments
	 * 
	 * @param args
	 *            arguments for the action
	 */
	public abstract void perform(Object... args);
}
