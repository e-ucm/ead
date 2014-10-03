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
package es.eucm.ead.editor.control.actions.editor.elementeditstate;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.SelectionCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.ElementEditState;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Changes a field of {@link ChangeElementEditState} of all elements in current
 * scene to false
 */
public abstract class ResetElementEditState extends ModelAction {

	protected abstract String getFieldName();

	protected abstract boolean getResetCondition(ElementEditState editState);

	@Override
	public Command perform(Object... args) {

		ModelEntity sceneEntity = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE);

		CompositeCommand command = new CompositeCommand();
		if (sceneEntity != null) {
			resetAll(sceneEntity.getChildren(), command);
		}
		command.addCommand(new SelectionCommand(controller.getModel(),
				Selection.EDITED_GROUP, Selection.SCENE_ELEMENT));
		return command;
	}

	private void resetAll(Array<ModelEntity> entities, CompositeCommand command) {
		for (ModelEntity entity : entities) {
			ElementEditState editState = Q.getComponent(entity,
					ElementEditState.class);
			if (getResetCondition(editState)) {
				command.addCommand(new FieldCommand(editState, getFieldName(),
						false));
			}
			resetAll(entity.getChildren(), command);
		}
	}
}