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

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.SceneEditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * 
 * <p>
 * Sets {@link SceneEditState} component position of a given scene.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0] </strong> <em>{@link ModelEntity}</em> the scene whose
 * {@link SceneEditState} component position will be changed.</dd>
 * <dd><strong>args[1] </strong> <em>{@link Float}</em> The new X position.</dd>
 * <dd><strong>args[2] </strong> <em>{@link Float}</em> The new Y position.</dd>
 * </dl>
 * </p>
 * 
 */
public class SetScenePosition extends ModelAction {

	public SetScenePosition() {
		super(true, false, ModelEntity.class, Float.class, Float.class);
	}

	@Override
	public Command perform(Object... args) {
		SceneEditState state = Q.getComponent((ModelEntity) args[0],
				SceneEditState.class);

		CompositeCommand compositeCommand = new CompositeCommand();

		compositeCommand.addCommand(getTransparentFieldCommand(state,
				FieldName.X, (Float) args[1]));
		compositeCommand.addCommand(getTransparentFieldCommand(state,
				FieldName.Y, (Float) args[2]));

		return compositeCommand;
	}

	private FieldCommand getTransparentFieldCommand(Object object,
			String fieldName, Object newValue) {
		return new FieldCommand(object, fieldName, newValue) {
			@Override
			public boolean isTransparent() {
				return true;
			}
		};
	}
}
