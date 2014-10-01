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

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * <p>
 * Performs an action over the {@link Selection#SCENE_ELEMENT}.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None</dd>
 * </dl>
 */
public abstract class SceneElementSelection extends ModelAction {

	@Override
	public Command perform(Object... args) {
		CompositeCommand compositeCommand = new CompositeCommand();

		Selection selection = controller.getModel().getSelection();
		Object sceneObject = selection.getSingle(Selection.SCENE);
		if (sceneObject instanceof ModelEntity) {
			ModelEntity scene = (ModelEntity) sceneObject;
			Object[] elements = selection.get(Selection.SCENE_ELEMENT);
			for (Object elemObject : elements) {
				if (elemObject instanceof ModelEntity) {
					ModelEntity element = (ModelEntity) elemObject;
					compositeCommand.addCommand(getCommand(scene, element));
				}
			}
			return compositeCommand;
		}
		return null;
	}

	public abstract Command getCommand(ModelEntity scene,
			ModelEntity sceneElement);
}
