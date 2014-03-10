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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.commands.ListCommand;

import java.util.List;

/**
 * Action for reordering elements on a list.
 * 
 * This action expects at least 2 arguments, although optional third and fourth
 * arguments can also be provided. If less than two arguments are provided, an {
 * {@link es.eucm.ead.editor.control.actions.EditorActionException} is thrown.
 * 
 * The first argument (args[0]) is the element of the list that has to be
 * reordered. It can either be any of the objects contained in the list (
 * {@code args[2]}), a string, an Integer, or null. Any other type will cause an
 * {{@link es.eucm.ead.editor.control.actions.EditorActionException} to be
 * thrown. If args[0] is null or a String, the action relies on
 * {@link #findObjectById(String)} to find the object. This is intended for
 * subclasses that want to ask the user, search the list according to a String
 * id, etc. See {@link es.eucm.ead.editor.control.actions.ReorderScenes} for an
 * example. If args[0] it's an integer, it will be assumed this is the position
 * in the list where the object actually is. A range check will be performed,
 * throwing an {@link es.eucm.ead.editor.control.actions.EditorActionException}
 * if the given index is less than 0 or if it exceeds the size of the list.
 * 
 * The second argument (args[1]) is the final position of the object in the
 * list. It can be either relative or absolute depending on an optional args[2]
 * value. Only Integers are supported. Null values or any other type will cause
 * an {@link es.eucm.ead.editor.control.actions.EditorActionException} to be
 * thrown. A range check is performed, but in contrast to args[0], if the
 * destiny position does not fit in the list, it is fixed silently and no
 * exception is thrown.
 * 
 * The third optional argument (args[2]) is a boolean that indicates whether the
 * reordering is relative. If the reordering is relative (when args[2] is true),
 * the object is moved as many spaces in the list as specified by args[1] (the
 * destiny position is calculated depending on args[1] and the position it had
 * previously in the list). If args[2] is false or is not present (default
 * value), args[1] is considered an absolute position.
 * 
 * The fourth optional argument (args[3]) may be the list. It is optional since
 * subclasses may know what list has to be used. It can be either a
 * {@link java.util.List}, a {@link java.lang.String} or null. Should this
 * argument be of any other type, an {
 * {@link es.eucm.ead.editor.control.actions.EditorActionException} is thrown.
 * If args[3] is null or a String, the action relies on
 * {@link #findListById(String)} to find the list. This is intended for
 * subclasses that want to ask the user, search the list according to a String
 * id, etc. See {@link es.eucm.ead.editor.control.actions.ReorderScenes} for an
 * example.
 * 
 * 
 * Created by Javier Torrente on 9/03/14.
 */
public class Reorder extends EditorAction {
	@Override
	public void perform(Object... args) {
		// (1) There should be at least two arguments (initial position, destiny
		// position)
		if (args.length < 2) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": cannot execute reorder action with less than 2 arguments");
		}

		// (2) Now, get the list
		List list = null;
		// If third argument not exists or it's null or a String, the list has
		// to be found. In this case, findListById is invoked. Subclasses should
		// override this method if they want to support this feature
		if (args.length < 4 || args[3] == null || args[3] instanceof String) {
			list = findListById((args.length < 4 || args[3] == null) ? null
					: (String) args[3]);
		}
		// If the list is not to be found, it should be an instance of
		// java.util.List. Otherwise, throw exception
		else {
			if (!(args[3] instanceof List)) {
				throw new EditorActionException(
						"Error in action "
								+ this.getClass().getCanonicalName()
								+ ": the fourth argument (args[3]) must be either an object of type java.util.List or a String (id for finding the list) ");
			} else {
				list = (List) args[3];
			}
		}

		// At this point, if the list is still null, return without doing
		// anything else
		if (list == null)
			return;

		// (3) Now, get the object that has to be reordered
		Object elementToBeReordered = null;
		// If first argument is null or a String, elementToBeReordered has to be
		// found. In this case, findObjectById is invoked. Subclasses should
		// override this method if they want to support this feature
		if (args[0] == null || args[0] instanceof String) {
			elementToBeReordered = findObjectById(args[0] == null ? null
					: (String) args[0]);
		}
		// If the first argument is an instance of Integer, make range check and
		// retrieve object directly from its position in the list
		else if (args[0] instanceof Integer) {
			int elementIndex = ((Integer) args[0]).intValue();
			if (elementIndex < 0 || elementIndex >= list.size()) {
				throw new EditorActionException(
						"Error in action "
								+ this.getClass().getCanonicalName()
								+ ": Range check failed. The first argument (args[0]) represents an integer outside the range of the list (args[2]) - it's either <0 or >=list.size");
			} else {
				elementToBeReordered = list.get(elementIndex);
			}
		}
		// If the object has not to be found, it must be in the list. If not
		// present, return without doing anything else
		else {
			boolean elementInList = list.contains(args[0]);
			if (!elementInList) {
				for (Object o : list) {
					if (o == args[0]) {
						elementInList = true;
						break;
					}
				}
			}

			if (elementInList) {
				elementToBeReordered = args[0];
			}
		}

		// At this point, if elementToBeReordered is still null, return without
		// doing anything else
		if (elementToBeReordered == null)
			return;
		// Get the initial position of the element. This is required to check
		// that initialPosition!=destinyPosition before creating the command
		int sourcePosition = list.indexOf(elementToBeReordered);

		// (4) Get if the reodering is absolute (default) or relative
		boolean relative = false;
		if (args.length >= 3) {
			if (!(args[2] instanceof Boolean)) {
				throw new EditorActionException(
						"Error in action "
								+ this.getClass().getCanonicalName()
								+ ": Third argument (args[2]) is null or has not a valid type. Only Boolean supported");
			} else {
				relative = ((Boolean) args[2]).booleanValue();
			}
		}

		// (5) Get the destiny index for the element
		int destinyPosition = -1;
		// If the second argument is not a valid integer, throw an exception.
		// Otherwise assign destinyPosition
		if (args[1] == null || !(args[1] instanceof Integer)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": The second argument (args[1]) does not represent a valid integer number.");
		} else {
			destinyPosition = ((Integer) args[1]).intValue();
		}
		// If destinyPosition is relative, adjust according to sourcePosition
		if (relative)
			destinyPosition += sourcePosition;

		// Range check: adjust destinyPosition if necessary so it fits within
		// the range of the list
		if (destinyPosition < 0) {
			destinyPosition = 0;
		} else if (destinyPosition >= list.size()) {
			destinyPosition = list.size() - 1;
		}

		// (5) Create command (if applicable)
		if (sourcePosition != destinyPosition) {
			controller.command(new ListCommand.ReorderInListCommand(list,
					elementToBeReordered, destinyPosition));
		}
	}

	/**
	 * This method finds the object that represents the list that holds the
	 * element that is to be reordered, given its id (e.g. "scenes").
	 * {@link #findListById(String)} is only invoked when the fourth argument
	 * (args[3]) of the {@link es.eucm.ead.editor.control.actions.Reorder}
	 * action is missining, is null or a String (id). If
	 * {@link #findListById(String)} returns null, no exception is thrown and
	 * the action does not modify the model.
	 * 
	 * By default, this method returns {@code null} and has no effect on the
	 * action. It is provided as a convenient stub for subclasses that may want
	 * to feed the action with identifiers instead of with the whole list. See
	 * {@link es.eucm.ead.editor.control.actions.ReorderScenes} for more
	 * details.
	 * 
	 * @param id
	 *            The identifier for seeking the list containing the element
	 *            that has to be reordered (e.g. "scenes"). It may be
	 *            {@code null} if the id is unknown for this action (e.g.
	 *            arg[3]==null or args.length<4). Subclasses overriding this
	 *            method may want to ask the user to provide the id of the list
	 *            if {@code id} is null.
	 * @return The {@link java.util.List} whose id matches {@code id}.
	 *         {@code null} is returned by default if the subclass extending
	 *         {@link es.eucm.ead.editor.control.actions.Reorder} does not
	 *         override this method. It may be null if the list is not found. If
	 *         the object returned is null, then nothing happens (neither
	 *         exception nor modification of the model).
	 */
	protected List findListById(String id) {
		return null;
	}

	/**
	 * This method finds the object to be reordered, given its id (e.g.
	 * "scene0"). {@link #findObjectById(String)} is only invoked when the first
	 * argument (args[0]) of the
	 * {@link es.eucm.ead.editor.control.actions.Reorder} action is null or a
	 * String (in that case, it assumes the string contains the identifier of
	 * the element). If {@link #findObjectById(String)} returns null, no
	 * exception is thrown and the action does not modify the model.
	 * 
	 * By default, this method returns {@code null} and has no effect on the
	 * action. It is provided as a convenient stub for subclasses that may want
	 * to feed the action with identifiers instead of with the object
	 * (convenient for macro recording, for example). See
	 * {@link es.eucm.ead.editor.control.actions.ReorderScenes} for more
	 * details.
	 * 
	 * @param id
	 *            The identifier for seeking the object to be reordered (e.g.
	 *            "scene0"). It may be {@code null} if the id is unknown to this
	 *            action (arg[0]==null). Subclasses overriding this method may
	 *            want to ask the user to provide the id of object if {@code id}
	 *            is null.
	 * @return The Object whose id matches {@code id}. {@code null} is returned
	 *         by default if the subclass extending
	 *         {@link es.eucm.ead.editor.control.actions.Reorder} does not
	 *         override this method. It may be null if the object is not found.
	 *         If the object returned is null, then nothing happens (neither
	 *         exception nor modification of the model).
	 */
	protected Object findObjectById(String id) {
		return null;
	}
}
