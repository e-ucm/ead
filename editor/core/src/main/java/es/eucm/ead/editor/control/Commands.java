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

	private static final int COMMAND = 0, UNDO = 1, REDO = 2, SAVE = 3;

	private Model model;

	private Array<CommandListener> commandListeners;

	private Stack<CommandsStack> commandsStacks;

	private CommandsStack currentCommandsStack;

	/**
	 * Pointer to last saved command
	 */
	private Command savedPoint;

	/**
	 * 
	 * @param model
	 *            the game project model
	 */
	public Commands(Model model) {
		this.model = model;
		commandListeners = new Array<CommandListener>();
		this.commandsStacks = new Stack<CommandsStack>();
	}

	/**
	 * Executes the command. This clears the redo history
	 * 
	 * @param command
	 *            the command
	 */
	public void command(Command command) {
		if (currentCommandsStack != null) {
			currentCommandsStack.command(command);
		} else {
			doCommand(command);
		}
	}

	/**
	 * Undoes the last command
	 */
	public void undo() {
		if (currentCommandsStack != null) {
			currentCommandsStack.undo();
		}
	}

	/**
	 * Executes the last undone command, if any
	 */
	public void redo() {
		if (currentCommandsStack != null) {
			currentCommandsStack.redo();
		}
	}

	/**
	 * @return the current undo history. Could be null if there is no current
	 *         command stack
	 */
	public Stack<Command> getUndoHistory() {
		return currentCommandsStack == null ? null : currentCommandsStack
				.getUndoHistory();
	}

	/**
	 * @return the current redo history. Could be null if there is no current
	 *         command stack
	 */
	public Stack<Command> getRedoHistory() {
		return currentCommandsStack == null ? null : currentCommandsStack
				.getRedoHistory();
	}

	private void doCommand(Command command) {
		ModelEvent modelEvent = command.doCommand();
		model.notify(modelEvent);
	}

	/**
	 * Creates a new context with an independent commands stack. Previous
	 * commands received won't be able to undone until
	 * {@link #popStack(boolean)} is called.
	 */
	public void pushStack() {
		currentCommandsStack = new CommandsStack();
		commandsStacks.push(currentCommandsStack);
		for (CommandListener listener : commandListeners) {
			listener.contextPushed(this);
		}
	}

	/**
	 * Exits the current commands stack, returning to the previous one.
	 * 
	 * @param merge
	 *            if commands of the context left behind must be added at the
	 *            end of the previous commands stack
	 */
	public void popStack(boolean merge) {
		CommandsStack oldCommandsStack = commandsStacks.pop();
		if (!commandsStacks.isEmpty()) {
			currentCommandsStack = commandsStacks.peek();
			if (merge) {
				currentCommandsStack.getUndoHistory().addAll(
						oldCommandsStack.getUndoHistory());
			}
		} else {
			currentCommandsStack = null;
		}

		for (CommandListener listener : commandListeners) {
			listener.contextPopped(this, oldCommandsStack, merge);
		}
	}

	/**
	 * @return the current commands stacks
	 */
	public Stack<CommandsStack> getCommandsStack() {
		return commandsStacks;
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

	private void fire(int type, Command command) {
		for (CommandListener listener : commandListeners) {
			switch (type) {
			case COMMAND:
				listener.doCommand(this, command);
				break;
			case UNDO:
				listener.undoCommand(this, command);
				break;
			case REDO:
				listener.redoCommand(this, command);
				break;
			case SAVE:
				listener.savePointUpdated(this, savedPoint);
				break;
			}
		}
	}

	/**
	 * Indicates that all previous commands has been saved, and the save point
	 * is updated
	 */
	public void updateSavePoint() {
		if (currentCommandsStack != null
				&& !currentCommandsStack.getUndoHistory().isEmpty()) {
			savedPoint = currentCommandsStack.getUndoHistory().peek();
			fire(SAVE, null);
		}
	}

	/**
	 * @return true if the last save point is different from the current history
	 *         point
	 */
	public boolean commandsPendingToSave() {
		return currentCommandsStack != null
				&& !currentCommandsStack.getUndoHistory().isEmpty()
				&& savedPoint != currentCommandsStack.getUndoHistory().peek();
	}

	public void clear() {
		commandsStacks.clear();
		for (CommandListener listener : commandListeners) {
			listener.cleared(this);
		}
	}

	public class CommandsStack {

		private Stack<Command> undoHistory;

		private Stack<Command> redoHistory;

		public CommandsStack() {
			undoHistory = new Stack<Command>();
			redoHistory = new Stack<Command>();
		}

		public Stack<Command> getUndoHistory() {
			return undoHistory;
		}

		public Stack<Command> getRedoHistory() {
			return redoHistory;
		}

		/**
		 * Executes the command. This clears the redo history
		 * 
		 * @param command
		 *            the command
		 */
		public void command(Command command) {
			if (redoHistory.isEmpty() || !command.isTransparent()) {
				redoHistory.clear();
				if (command.canUndo()) {
					if ((undoHistory.isEmpty() && !command.isTransparent())
							|| (!undoHistory.isEmpty() && !undoHistory.peek()
									.combine(command))) {
						undoHistory.add(command);
					}
				}
			}
			doCommand(command);
			fire(COMMAND, command);
		}

		public void undo() {
			if (!undoHistory.isEmpty()) {
				Command command;
				do {
					command = undoHistory.pop();
					redoHistory.add(command);
					model.notify(command.undoCommand());
					fire(UNDO, command);
				} while (command.isTransparent() && !undoHistory.isEmpty());
			}
		}

		public void redo() {
			if (!redoHistory.isEmpty()) {
				Command command = redoHistory.pop();
				undoHistory.add(command);
				doCommand(command);
				fire(REDO, command);

				while (!redoHistory.isEmpty()
						&& redoHistory.peek().isTransparent()) {
					command = redoHistory.pop();
					undoHistory.add(command);
					doCommand(command);
					fire(REDO, command);
				}
			}
		}
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

		/**
		 * The commands save point has been updated
		 * 
		 * @param commands
		 *            the commands object
		 * @param savePoint
		 *            command at the save point
		 */
		void savePointUpdated(Commands commands, Command savePoint);

		/**
		 * The commands were cleared
		 */
		void cleared(Commands commands);

		/**
		 * A context was pushed
		 */
		void contextPushed(Commands commands);

		/**
		 * A context was popped
		 * 
		 * @param merge
		 *            if commands popped were merged
		 */
		void contextPopped(Commands commands, CommandsStack poppedContext,
				boolean merge);

	}
}
