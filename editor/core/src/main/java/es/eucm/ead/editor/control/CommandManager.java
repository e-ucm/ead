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

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.model.ModelEvent;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Default implementation of the {@link CommandManager}.
 */
public class CommandManager {

	/**
	 * Action stacks
	 */
	private final Stack<CommandStack> stacks = new Stack<CommandStack>();

	/**
	 * When this says 'clean', then saving should be useless; queried via
	 * setSaved and isChanged
	 */
	private final DirtyTracker dirtyTracker = new DirtyTracker();

	/**
	 * Default constructor
	 */
	public CommandManager() {
		stacks.push(new CommandStack());
	}

	/**
	 * Add a new stack of commands, used to perform contained tasks such as
	 * those in a modal panel
	 */
	public void addStack() {
		stacks.push(new CommandStack());
	}

	/**
	 * Remove a command stack, optionally canceling changes.
	 * 
	 * @param cancelChanges
	 *            Cancel changes performed on the command stack
	 */
	public void removeCommandStacks(boolean cancelChanges) {
		if (cancelChanges && stacks.peek().canUndo()) {
			stacks.peek().undoCommand(Editor.controller.getModel());
			stacks.pop();
		} else {
			CommandStack as = stacks.pop();
			if (as.getActionHistory() != 0) {
				stacks.peek().increaseActionHistory();
				if (as.canUndo()) {
					stacks.peek().getPerformed().add(as);
				} else {
					clearCommands();
				}
			}
		}
	}

	/**
	 * Perform a command on the game model.
	 */
	public void performCommand(Command action) {
		Gdx.app.debug("CommandManager", "performing: " + action);
		CommandStack currentStack = stacks.peek();
		ModelEvent me = action.performCommand(Editor.controller.getModel());
		if (me != null) {

			//
			// once you do something, you can no longer redo what you had undone
			// FIXME: add tree-undo here?
			// - mutableTreeNode with a command in it
			// - do action = add child to current
			// - undo/redo = traverse graph (redo can choose among
			// possibilities)
			// - keep up to X nodes in graph (LRU-leaf cache?)
			//
			currentStack.getUndone().clear();

			if (action.canUndo()) {
				if (currentStack.getPerformed().isEmpty()
						|| !currentStack.getPerformed().peek().combine(action)) {
					currentStack.getPerformed().push(action);
				}
			} else {
				clearCommands();
			}
			Editor.controller.getModel().fireModelEvent(me);
		} else {
			Gdx.app.error("CommandManager", "action returned null: " + action);
		}
		currentStack.increaseActionHistory();
	}

	/**
	 * Undo the latest command.
	 */
	public void undoCommand() {
		if (!canUndo()) {
			return;
		}
		CommandStack currentStack = stacks.peek();
		Command action = currentStack.getPerformed().peek();
		Gdx.app.debug("CommandManager", "undoing: " + action);
		ModelEvent me = action.undoCommand(Editor.controller.getModel());
		if (me != null) {
			action = currentStack.getPerformed().pop();
			if (action.canRedo()) {
				currentStack.getUndone().push(action);
			} else {
				currentStack.getUndone().clear();
			}
			Editor.controller.getModel().fireModelEvent(me);
		} else {
			Gdx.app.error("CommandManager", "action returned null: " + action);
		}
		currentStack.decreaseActionHistory();
	}

	/**
	 * Redo the latest command.
	 */
	public void redoCommand() {
		if (!canRedo()) {
			return;
		}
		CommandStack currentStack = stacks.peek();
		Command action = currentStack.getUndone().peek();
		Gdx.app.debug("CommandManager", "redoing: " + action);
		ModelEvent me = action.redoCommand(Editor.controller.getModel());
		if (me != null) {
			action = currentStack.getUndone().pop();
			if (action.canUndo()) {
				currentStack.getPerformed().push(action);
			} else {
				clearCommands();
			}
			Editor.controller.getModel().fireModelEvent(me);
		} else {
			Gdx.app.error("CommandManager", "action returned null: " + action);
		}
		currentStack.increaseActionHistory();
	}

	/**
	 * Returns true if there is a command to redo
	 */
	public boolean canRedo() {
		if (!stacks.peek().getUndone().empty()) {
			return stacks.peek().getUndone().peek().canRedo();
		}
		return false;
	}

	/**
	 * Returns true if there is a command to undo
	 */
	public boolean canUndo() {
		if (!stacks.peek().getPerformed().empty()) {
			return stacks.peek().getPerformed().peek().canUndo();
		}
		return false;
	}

	/**
	 * @return true if the game model was modified (any action performed, undone
	 *         or redone after a setSaved).
	 */
	public boolean isChanged() {
		return !dirtyTracker.isClean();
	}

	public void clearCommands() {
		for (CommandStack as : stacks) {
			as.clear();
		}
	}

	/**
	 * Called to indicate that the model has been saved. Immediately after this
	 * call, isChanged will return false. Any additional action will return
	 * isChanged to its "always-false" mode.
	 */
	public void setSaved() {
		dirtyTracker.reset();
	}

	/**
	 * Tracks "dirtyness" (existence of any changes whatsoever) of a game model.
	 * After a reset, things are clean. Things get dirty whenever actions are
	 * performed or undone. Cleanliness can only be achieved after the same
	 * exact action-stack is restored.
	 * 
	 * This keeps references to old actions. Actions must be (relatively)
	 * lightweight, and immutable!
	 */
	private class DirtyTracker {
		private boolean broken;
		private final ArrayList<Command> snapshot = new ArrayList<Command>();

		void reset() {
			broken = false;
			snapshot.clear();
			for (Command c : stacks.peek().getPerformed()) {
				snapshot.add(c);
			}
		}

		boolean isClean() {
			if (broken) {
				return false;
			}
			Stack<Command> performed = stacks.peek().getPerformed();
			if (performed.size() != snapshot.size()) {
				return false;
			}
			for (int i = 0; i < snapshot.size(); i++) {
				if (!performed.get(i).equals(snapshot.get(i))) {
					broken = true;
					return false;
				}
			}
			return true;
		}

		void setDirty() {
			broken = true;
		}
	}
}
