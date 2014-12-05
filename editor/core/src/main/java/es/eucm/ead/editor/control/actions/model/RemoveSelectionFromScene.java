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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildrenFromEntity;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Removes the {@link Selection#SCENE_ELEMENT} selection in the current scene.
 */
public class RemoveSelectionFromScene extends ModelAction {

	@Override
	public Command perform(Object... args) {
		Selection selection = controller.getModel().getSelection();

		CompositeCommand command = new CompositeCommand();

		ModelEntity scene = (ModelEntity) selection
				.getSingle(Selection.EDITED_GROUP);
		Array<ModelEntity> elements = new Array<ModelEntity>();
		for (Object element : selection.get(Selection.SCENE_ELEMENT)) {
			elements.add((ModelEntity) element);
		}

		command.addCommand(controller.getActions()
				.getAction(RemoveChildrenFromEntity.class)
				.perform(scene, elements));

		command.addCommand(controller.getActions()
				.getAction(SetSelection.class)
				.perform(Selection.EDITED_GROUP, Selection.SCENE_ELEMENT));

		return command;
	}
}
