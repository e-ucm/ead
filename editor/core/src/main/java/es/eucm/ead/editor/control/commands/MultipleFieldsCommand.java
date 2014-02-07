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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.MultipleEvent;

public class MultipleFieldsCommand extends Command {

	private Object target;

	private Array<FieldCommand> commands;

	private boolean combine;

	public MultipleFieldsCommand(Object target, boolean combine) {
		this.target = target;
		this.combine = combine;
		this.commands = new Array<FieldCommand>();
	}

	public MultipleFieldsCommand field(String fieldName, Object value) {
		commands.add(new FieldCommand(target, fieldName, value, true));
		return this;
	}

	@Override
	public ModelEvent doCommand() {
		MultipleEvent event = new MultipleEvent(target);
		for (FieldCommand command : commands) {
			event.addEvent(command.doCommand());
		}
		return event;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		MultipleEvent event = new MultipleEvent(target);
		// Undo commands in inverse order
		for (int i = commands.size - 1; i >= 0; i--) {
			event.addEvent(commands.get(i).undoCommand());
		}
		return event;
	}

	@Override
	public boolean combine(Command other) {
		if (combine && other instanceof MultipleFieldsCommand) {
			MultipleFieldsCommand c = (MultipleFieldsCommand) other;
			if (c.target == this.target
					&& c.commands.size == this.commands.size) {
				// Check if they can be combined
				for (int i = 0; i < commands.size; i++) {
					FieldCommand c1 = commands.get(i);
					FieldCommand c2 = c.commands.get(i);
					if (!c1.getFieldName().equals(c2.getFieldName())) {
						return false;
					}
				}
				// Now, combine
				for (int i = 0; i < commands.size; i++) {
					FieldCommand c1 = commands.get(i);
					FieldCommand c2 = c.commands.get(i);
					c1.combine(c2);
				}
				return true;
			}
			this.combine = c.combine;
		}
		return false;
	}
}
