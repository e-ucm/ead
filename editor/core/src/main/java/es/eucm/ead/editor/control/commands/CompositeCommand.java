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

	public CompositeCommand(Command... commands) {
		commandList = new ArrayList<Command>();
		for (Command c : commands) {
			commandList.add(c);
		}
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
	// A composite command can only be undone if all its subcommands can be
	// undone.
	public boolean canUndo() {
		for (Command c : commandList) {
			if (!c.canUndo())
				return false;
		}
		return true;
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
}
