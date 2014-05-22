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

	protected int oldIndex;

	protected int newIndex;

	/**
	 * Creates an add to list command
	 * 
	 * @param list
	 *            the list in which the element will be added
	 * @param element
	 *            the element to be added to the list
	 * @param index
	 *            the index where the element should be added. {@code -1} adds
	 *            the element at the end of the list
	 */
	protected ListCommand(List list, Object element, int index) {
		this.add = true;
		this.list = list;
		this.element = element;
		this.newIndex = index;
	}

	protected ListCommand(List list, Object e, boolean add) {
		this.add = add;
		this.list = list;
		this.element = e;
		this.newIndex = -1;
	}

	@Override
	public ModelEvent doCommand() {
		if (add) {
			if (newIndex == -1) {
				list.add(element);
				newIndex = list.size() - 1;
			} else {
				list.add(newIndex, element);
			}
			return new ListEvent(Type.ADDED, list, element, newIndex);
		} else {
			oldIndex = list.indexOf(element);
			if (oldIndex == -1) {
				return null;
			}
			list.remove(element);
			return new ListEvent(Type.REMOVED, list, element, oldIndex);
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
			return new ListEvent(Type.REMOVED, list, element, newIndex);
		} else {
			if (oldIndex == -1) {
				return null;
			}
			list.add(oldIndex, element);
			return new ListEvent(Type.ADDED, list, element, oldIndex);
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
		 *            The list in which the command is to be applied
		 * @param e
		 *            The P element to be added to a list by the command
		 */
		public AddToListCommand(List list, Object e) {
			super(list, e, true);
		}

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param list
		 *            The list in which the command should be applied
		 * @param e
		 *            The P element to be added to a list by the command
		 * @param index
		 *            the position to occupy by the element in the list
		 */
		public AddToListCommand(List list, Object e, int index) {
			super(list, e, index);
		}
	}

	public static class RemoveFromListCommand extends ListCommand {

		/**
		 * Constructor for the ListCommand class.
		 * 
		 * @param list
		 *            The list in which the command should be applied
		 * @param e
		 *            The P element to be removed from the list by the command
		 */
		public RemoveFromListCommand(List list, Object e) {
			super(list, e, false);
		}
	}

	public static class ReorderInListCommand extends CompositeCommand {

		/**
		 * Constructor from the command to move an element from one position in
		 * the list, to another. Internally, this generates a
		 * {@link RemoveFromListCommand} and {@link AddToListCommand} where the
		 * specified position
		 * 
		 * @param list
		 *            The list in which the command should be applied
		 * @param element
		 *            The P element to be added to a list by the command
		 * @param newIndex
		 *            the new position to occupy by the element
		 */
		public ReorderInListCommand(List list, Object element, int newIndex) {
			super(new RemoveFromListCommand(list, element),
					new AddToListCommand(list, element, newIndex));
		}

	}

}
