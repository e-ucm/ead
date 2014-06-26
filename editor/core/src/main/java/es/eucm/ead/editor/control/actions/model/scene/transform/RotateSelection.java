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
package es.eucm.ead.editor.control.actions.model.scene.transform;

import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

/**
 * <p>
 * Rotates 90º the current selection
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Type}</em> the type of rotation,
 * clockwise or counter clockwise</dd>
 * </dl>
 */
public class RotateSelection extends TransformSelection {

	public static final float ROTATION = 90;

	public enum Type {
		CLOCKWISE, COUNTER_CLOCKWISE
	}

	public RotateSelection() {
		super(true, false, Type.class);
	}

	@Override
	public Command perform(Object... args) {
		Type type = (Type) args[0];
		CompositeCommand commands = new CompositeCommand();
		for (Object o : controller.getModel().getSelection()) {
			if (o instanceof ModelEntity) {
				ModelEntity modelEntity = (ModelEntity) o;
				float rotation = modelEntity.getRotation();
				commands.addCommand(new FieldCommand(modelEntity,
						FieldName.ROTATION, rotation
								+ (type == Type.CLOCKWISE ? -ROTATION
										: ROTATION)));
			}
		}
		return commands;
	}
}
