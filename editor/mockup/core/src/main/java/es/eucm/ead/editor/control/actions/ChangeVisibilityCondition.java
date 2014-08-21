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
package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Change the visibility condition in the current scene element selected </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> new condition</dd>
 * </dl>
 */
public class ChangeVisibilityCondition extends EditorAction {

	public ChangeVisibilityCondition() {
		super(true, false, String.class);
	}

	@Override
	public void perform(Object... args) {
		String condition = (String) args[0];

		ModelEntity modelEntity = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);

		for (ModelComponent component : modelEntity.getComponents()) {
			if (component instanceof Visibility) {
				((Visibility) component).setCondition(condition);
			}
		}
	}

}