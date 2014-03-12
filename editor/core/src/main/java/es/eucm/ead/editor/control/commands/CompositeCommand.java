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
import es.eucm.ead.editor.model.events.MultipleEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenient class for grouping commands that need to be always undone and
 * redone together.
 * 
 * This is the typical case of an action that needs to create several commands.
 * For example, DeleteScene needs to generate three commands: 1) To delete the
 * scene from the scenes map 2) To change the editScene field, in case the scene
 * deleted was being edited. 3) To change the initialScene, in case the scene
 * deleted was the initial
 * 
 * All those three commands should always be undone altogether, and therefore
 * they must be placed into a CompositeCommand
 * 
 * Created by Javier Torrente on 3/03/14.
 */
public class CompositeCommand extends Command {

	protected List<Command> commandList;

    protected UndoBehavior undoBehaviour;

    /**
     * Creates a Composite Command with an arbitrary number of commands that will be executed in order.
     * When this constructor is used, the CompositeCommand determines if it can be undone by checking
     * the list of commands (it will be undoable if all its subcommands are undoable)
     * @param commands  The list of commands to execute in order
     */
    public CompositeCommand(Command... commands){
        this (UndoBehavior.INHERIT_FROM_COMMANDLIST, commands);
    }

    /**
     * Creates a Composite Command with an arbitrary number of commands that will be executed in order.
     * When this constructor is used, the CompositeCommand determines if it can be undone following the
     * next algorithm:
     *
     * If {@code undoBehavior} is {@link es.eucm.ead.editor.control.commands.CompositeCommand.UndoBehavior#CANNOT_UNDO},
     * then this command cannot be undone.
     *
     * If {@code undoBehavior} is {@link es.eucm.ead.editor.control.commands.CompositeCommand.UndoBehavior#INHERIT_FROM_COMMANDLIST},
     * it will iterate through the subcommands. If any of the subcommands is not undoable, this command will not be undoable.
     * If all the subcomands are undoable, this command will  be undoable.
     *
     * the list of commands (it will be undoable if all its subcommands are undoable)
     * @param commands  The list of commands to execute in order
     */
	public CompositeCommand(UndoBehavior undoBehavior, Command... commands) {
		commandList = new ArrayList<Command>();
		for (Command c : commands) {
			commandList.add(c);
		}
        this.undoBehaviour = undoBehavior;
	}

	public CompositeCommand(List<Command> commands) {
		commandList = commands;
	}

	@Override
	public ModelEvent doCommand() {
		MultipleEvent multipleEvent = new MultipleEvent();
		for (int i = 0; i < commandList.size(); i++) {
			multipleEvent.addEvent(commandList.get(i).doCommand());
		}
		return multipleEvent;
	}

	@Override
	// By default, a composite command can only be undone if all its subcommands can be
	// undone. However, it is possible to pass Composite Commands an UndoBehaviour value to override this behaviour.
    // If UndoBehaviour.CANNOT_UNDO is passed, this command will not be undoable.
	public boolean canUndo() {
        if (undoBehaviour==UndoBehavior.CANNOT_UNDO) return false;
        else {
            for (Command c : commandList) {
                if (!c.canUndo())
                    return false;
            }
            return true;
        }
	}

	@Override
	public ModelEvent undoCommand() {
		MultipleEvent multipleEvent = new MultipleEvent();
		for (int i = commandList.size() - 1; i >= 0; i--) {
			multipleEvent.addEvent(commandList.get(i).undoCommand());
		}
		return multipleEvent;
	}

	@Override
	public boolean combine(Command other) {
		// Does not make any sense to combine CompositeCommands since they are
		// meant for complex actions.
		return false;
	}

    /**
     * To specify if the composite command should check subcomand list to see
     * if it can be undone ({@link #INHERIT_FROM_COMMANDLIST}), or if it
     * should just not be undoable ({@link #CANNOT_UNDO})
     */
    public enum UndoBehavior{
        CANNOT_UNDO("cannotUndoAlways"),
        INHERIT_FROM_COMMANDLIST("inheritFromCommandList");

        private String name;
        private UndoBehavior(String name){
            this.name = name;
        }

        public String toString(){
            return name;
        }
    }
}
