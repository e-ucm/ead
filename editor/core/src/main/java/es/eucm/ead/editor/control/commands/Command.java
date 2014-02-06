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
package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.events.ModelEvent;

/**
 * Commands define tasks that can be performed over the game model. This part is
 * used to allow the easy implementation of undo and re-do mechanisms.
 */
public abstract class Command {

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
	 * Combines this action with other similar action (if possible). Useful for
	 * combining simple changes such as characters typed in the same field.
	 * 
	 * @param other
	 *            The other action with which this action can be combined if
	 *            possible
	 * @return true if the actions were combined
	 */
	public abstract boolean combine(Command other);

}
