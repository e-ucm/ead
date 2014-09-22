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

import java.util.Map;

import es.eucm.ead.editor.control.Commands.CommandListener;
import es.eucm.ead.editor.control.Commands.CommandsStack;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.Resource;

/**
 * Directly modifies the {@link Resource} resources from the model by setting
 * the {@link Resource#isModified()} flag when changes are detected.
 */
public class ResourceModifiedListener implements CommandListener {

	private Model model;

	public ResourceModifiedListener(Model model) {
		this.model = model;
	}

	@Override
	public void doCommand(Commands commands, Command command) {
		updateModified(command);
	}

	@Override
	public void undoCommand(Commands commands, Command command) {
		updateModified(command);
	}

	@Override
	public void redoCommand(Commands commands, Command command) {
		updateModified(command);
	}

	private void updateModified(Command command) {
		if (!command.isTransparent()) {
			Object res = model.getSelection().getSingle(Selection.RESOURCE);
			if (res != null) {
				String id = res.toString();
				Resource resource = model.getResource(id);
				resource.setModified(true);
			}
		}
	}

	@Override
	public void savePointUpdated(Commands commands, Command savePoint) {
		clearModified();
	}

	@Override
	public void cleared(Commands commands) {
		clearModified();
	}

	private void clearModified() {
		for (Map.Entry<String, Resource> nextEntry : model.listNamedResources()) {
			Resource resource = nextEntry.getValue();
			resource.setModified(false);
		}
	}

	@Override
	public void contextPushed(Commands commands) {

	}

	@Override
	public void contextPopped(Commands commands, CommandsStack poppedContext,
			boolean merge) {

	}

}
