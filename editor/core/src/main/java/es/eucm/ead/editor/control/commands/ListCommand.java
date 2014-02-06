package es.eucm.ead.editor.control.commands;

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

import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.ListEvent.Type;
import es.eucm.ead.editor.model.events.ModelEvent;

import java.util.List;

/**
 * Contains subclasses for adding to, removing from, and reordering elements in
 * lists. Changing existing elements can be achieved via the suitable
 * ChangeFieldCommand
 */
public abstract class ListCommand extends Command {

	private boolean add;
	/**
	 * The list in which the added elements will be placed.
	 */
	protected List list;

	/**
	 * The element to be added to the list.
	 */
	protected Object element;

	protected int index;

	protected ListCommand(List list, Object e, boolean add) {
		this.add = add;
		this.list = list;
		this.element = e;
	}

	@Override
	public ModelEvent doCommand() {
		if (add) {
			list.add(element);
			index = list.size() - 1;
			return new ListEvent(Type.ADDED, list, element, index);
		} else {
			index = list.indexOf(element);
			list.remove(element);
			return new ListEvent(Type.REMOVED, list, element, index);
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		if (add) {
			list.remove(element);
			return new ListEvent(Type.REMOVED, list, element, index);
		} else {
			if (index == -1) {
				return null;
			}
			list.add(index, element);
			return new ListEvent(Type.ADDED, list, element, index);
		}
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}

	public static class AddToListCommand extends ListCommand {

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param list
		 *            The EAdList in which the command is to be applied
		 * @param e
		 *            The P element to be added to a list by the command
		 */
		public AddToListCommand(List list, Object e) {
			super(list, e, true);
		}
	}

	public static class RemoveFromListCommand extends ListCommand {

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param list
		 *            The EAdList in which the command is to be applied
		 * @param e
		 *            The P element to be removed from the list by the command
		 */
		public RemoveFromListCommand(List list, Object e) {
			super(list, e, false);
		}
	}

}
