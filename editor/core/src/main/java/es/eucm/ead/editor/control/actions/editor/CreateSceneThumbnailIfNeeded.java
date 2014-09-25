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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Commands.CommandListener;
import es.eucm.ead.editor.control.Commands.CommandsStack;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Creates a thumbnail for a {@link ModelEntity} that is a SCENE only if there
 * have been changes since the last time created..
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link ModelEntity}</em> model entity for</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> width of the thumbnail</dd>
 * <dd><strong>args[2]</strong> <em>{@link Integer}</em> height for the
 * <dd><strong>args[3]</strong> <em>{@link Scaling}</em> Optional. You can also
 * pass the {@link Scaling} that the resulting thumbnail should have. If no
 * scaling is specified, {@link Scaling#stretch} will be used.
 * </dl>
 * 
 * @see CreateThumbnail
 */
public class CreateSceneThumbnailIfNeeded extends CreateThumbnail implements
		CommandListener {

	private Array<ModelEntity> entities;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		entities = new Array<ModelEntity>();
		controller.getCommands().addCommandListener(this);
	}

	@Override
	public void perform(Object... args) {
		ModelEntity modelEntity = (ModelEntity) args[0];
		if (!entities.contains(modelEntity, true)) {
			entities.add(modelEntity);
			super.perform(args);
		}
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
			Model model = controller.getModel();
			Object resource = model.getSelection().getSingle(Selection.SCENE);
			if (resource instanceof ModelEntity) {
				entities.removeValue((ModelEntity) resource, true);
			}
		}
	}

	@Override
	public void savePointUpdated(Commands commands, Command savePoint) {

	}

	@Override
	public void cleared(Commands commands) {

	}

	@Override
	public void contextPushed(Commands commands) {

	}

	@Override
	public void contextPopped(Commands commands, CommandsStack poppedContext,
			boolean merge) {

	}

}
