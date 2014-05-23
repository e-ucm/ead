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
package es.eucm.ead.editor.control.actions.model.scene;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.RootEntityCommand.AddRootEntityCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

/**
 * Creates a new empty scene and sets it as the current edited scene. This
 * actions receives no arguments
 */
public class NewScene extends ModelAction {

	@Override
	public CompositeCommand perform(Object... args) {
		Model model = controller.getModel();

		String id = model.createId(ModelEntityCategory.SCENE);
		ModelEntity scene = controller.getTemplates().createScene(id);

		EditState editState = Model.getComponent(model.getGame(),
				EditState.class);

		CompositeCommand compositeCommand = new CompositeCommand();
		compositeCommand.addCommand(new AddRootEntityCommand(model, id, scene,
				ModelEntityCategory.SCENE));
		compositeCommand.addCommand(new FieldCommand(editState,
				FieldNames.EDIT_SCENE, id));
		compositeCommand.addCommand(new AddToListCommand(editState
				.getSceneorder(), id));

		return compositeCommand;
	}
}
