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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;

import java.util.Stack;

/**
 * Implements the commands stack
 */
public class Commands {

	private static final int COMMAND = 0, UNDO = 1, REDO = 2;

	private Array<CommandListener> commandListeners;

	private Stack<Command> undoHistory;

	private Stack<Command> redoHistory;

	private Model model;

	/**
	 * 
	 * @param model
	 *            the game project model
	 */
	public Commands(Model model) {
		this.model = model;
		commandListeners = new Array<CommandListener>();
		undoHistory = new Stack<Command>();
		redoHistory = new Stack<Command>();
	}

	/**
	 * Adds a command listener that listens to all command operations performed
	 * by this object
	 * 
	 * @param commandListener
	 *            the listener
	 */
	public void addCommandListener(CommandListener commandListener) {
		commandListeners.add(commandListener);
	}

	public Stack<Command> getUndoHistory() {
		return undoHistory;
	}

	public Stack<Command> getRedoHistory() {
		return redoHistory;
	}

	private void notify(int type, Command command) {
		for (CommandListener l : commandListeners) {
			switch (type) {
			case COMMAND:
				l.doCommand(this, command);
				break;
			case UNDO:
				l.undoCommand(this, command);
				break;
			case REDO:
				l.redoCommand(this, command);
				break;
			}
		}
	}

	/**
	 * Executes the command. This clears the redo history
	 * 
	 * @param command
	 *            the command
	 */
	public void command(Command command) {
		redoHistory.clear();

		if (command.canUndo()) {
			if (undoHistory.isEmpty() || !undoHistory.peek().combine(command)) {
				undoHistory.add(command);
			}
		}
		doCommand(command);
		notify(COMMAND, command);
	}

	/**
	 * Undoes the last command
	 */
	public void undo() {
		if (!undoHistory.isEmpty()) {
			Command command = undoHistory.pop();
			redoHistory.add(command);
			model.notify(command.undoCommand());
			notify(UNDO, command);
		}
	}

	/**
	 * Executes the last undone command, if any
	 */
	public void redo() {
		if (!redoHistory.isEmpty()) {
			Command command = redoHistory.pop();
			undoHistory.add(command);
			doCommand(command);
			notify(REDO, command);
		}
	}

	private void doCommand(Command command) {
		ModelEvent modelEvent = command.doCommand();
		model.notify(modelEvent);
	}

	public interface CommandListener {

		/**
		 * A command is executed
		 * 
		 * @param commands
		 *            the commands object
		 * @param command
		 *            the command executed
		 */
		void doCommand(Commands commands, Command command);

		/**
		 * A command is undone
		 * 
		 * @param commands
		 *            the commands object
		 * @param command
		 *            the command undone
		 */
		void undoCommand(Commands commands, Command command);

		/**
		 * A command is redone
		 * 
		 * @param commands
		 *            the commands object
		 * @param command
		 *            the command redone
		 */
		void redoCommand(Commands commands, Command command);
	}
}
