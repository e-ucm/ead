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

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.EditState;

/**
 * Action to reorder the scenes list in {@link EditState} *
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Object}</em> element to be reordered
 * in the list. Can be a string, identifiying the scene, or an Integer, marking
 * the index in the list.</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> the final position of
 * the object in the list.</dd>
 * <dd><strong>args[2]</strong> <em>{@link Boolean}</em> (Optional) if the final
 * position is relative. If true, the object is moved as many spaces in the list
 * as specified by args[1]. If not present, default value is set to false</dd>
 * </dl>
 */
public class ReorderScenes extends ModelAction {

	private Reorder reorder;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		reorder = controller.getActions().getAction(Reorder.class);
	}

	@Override
	public Command perform(Object... args) {
		EditState editState = Model.getComponent(controller.getModel()
				.getGame(), EditState.class);
		String sceneId = args[0] instanceof String ? (String) args[0]
				: editState.getSceneorder().get((Integer) args[0]);
		Integer index = (Integer) args[1];
		Boolean relative = args.length > 2 ? (Boolean) args[2] : false;

		return reorder.perform(editState, editState.getSceneorder(), sceneId,
				index, relative);
	}
}
