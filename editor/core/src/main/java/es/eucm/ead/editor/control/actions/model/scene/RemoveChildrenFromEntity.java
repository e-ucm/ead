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
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Removes children from an entity
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link ModelEntity}</em> The parent of the
 * entities</dd>
 * <dd><strong>args[n>0]</strong> <em>{@link ModelEntity}</em> Each subsequent
 * argument will be considered a child to be removed from the parent entity.</dd>
 * </dl>
 */
public class RemoveChildrenFromEntity extends ModelAction {

	@Override
	public boolean validate(Object... args) {
		if (args.length < 2) {
			return false;
		}

		for (Object o : args) {
			if (!(o instanceof ModelEntity)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public CompositeCommand perform(Object... args) {
		CompositeCommand compositeCommand = new CompositeCommand();
		ModelEntity parent = (ModelEntity) args[0];
		for (int i = 1; i < args.length; i++) {
			ModelEntity child = (ModelEntity) args[i];
			if (parent.getChildren().contains(child)) {
				compositeCommand.addCommand(new RemoveFromListCommand(parent
						.getChildren(), child));
			}
		}
		return compositeCommand;
	}
}
