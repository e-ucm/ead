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
package es.eucm.ead.editor.control;

import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;

import java.util.Stack;

public class Commands {

	private Stack<Command> undoHistory;

	private Stack<Command> redoHistory;

	private Controller controller;

	private Model model;

	public Commands(Controller controller) {
		this.controller = controller;
		this.model = controller.getModel();
		undoHistory = new Stack<Command>();
		redoHistory = new Stack<Command>();
	}

	public void command(Command command) {
		redoHistory.clear();

		if (command.canUndo()) {
			if (undoHistory.isEmpty() || !undoHistory.peek().combine(command)) {
				undoHistory.add(command);
			}
		}

		doCommand(command);
	}

	public void undo() {
		if (!undoHistory.isEmpty()) {
			Command command = undoHistory.pop();
			redoHistory.add(command);
			controller.notify(command.undoCommand());
		}
	}

	public void redo() {
		if (!redoHistory.isEmpty()) {
			Command command = redoHistory.pop();
			undoHistory.add(command);
			doCommand(command);
		}
	}

	private void doCommand(Command command) {
		ModelEvent modelEvent = command.doCommand();
		controller.notify(modelEvent);
	}
}
