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
package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.events.ModelEvent;

/**
 * Commands define tasks that can be performed over the game model. This part is
 * used to allow the easy implementation of undo and re-do mechanisms.
 */
public abstract class Command {

	private String resourceModified;

	/**
	 * Do the actual work. Returns a model event if it could be performed, null
	 * in other case.
	 * 
	 * @return True if the action was performed correctly
	 */
	public abstract ModelEvent doCommand();

	/**
	 * Returns true if the action can be undone
	 * 
	 * @return True if the action can be undone
	 */
	public abstract boolean canUndo();

	/**
	 * Undo the work done by the action. Returns true if it could be undone,
	 * false in other case.
	 * 
	 * @return a model event if it could be performed, null in other case.
	 */
	public abstract ModelEvent undoCommand();

	/**
	 * @return true if this command directly modifies a resource in the game
	 */
	public abstract boolean modifiesResource();

	/**
	 * Combines this action with other similar action (if possible). Useful for
	 * combining simple changes such as characters typed in the same field.
	 * 
	 * @param other
	 *            The other action with which this action can be combined if
	 *            possible
	 * @return true if the actions were combined
	 */
	public boolean combine(Command other) {
		return false;
	}

	/**
	 * When a command is transparent, is automatically undone/redone along with
	 * its previous/next command.
	 */
	public boolean isTransparent() {
		return false;
	}

	/**
	 * @return the id of the resource modified. If {@code null} is returned, it
	 *         modifies the default selected resource
	 */
	public String getResourceModified() {
		return resourceModified;
	}

	public void setResourceModified(String resourceModified) {
		this.resourceModified = resourceModified;
	}
}
