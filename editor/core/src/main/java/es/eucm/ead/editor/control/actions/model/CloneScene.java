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
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.ResourceCommand.AddResourceCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.Date;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Duplicates the currently selected scene and appends a suffix to its name in
 * order to be distinguished from the original.
 */
public class CloneScene extends ModelAction {

	@Override
	public Command perform(Object... args) {
		ModelEntity scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		ModelEntity copy = controller.getEditorGameAssets().copy(scene);
		Model model = controller.getModel();
		String sceneTitle = Q.getTitle(scene);

		if (sceneTitle != null && !sceneTitle.isEmpty()) {
			Array<String> titles = Pools.obtain(Array.class);
			for (Model.Resource res : model
					.getResources(ResourceCategory.SCENE).values()) {
				String title = Q.getTitle((ModelEntity) res.getObject());
				if (title != null && !title.isEmpty()) {
					titles.add(title);
				}
			}

			Q.setTitle(copy, Q.buildCopyName(sceneTitle, titles));
			titles.clear();
			Pools.free(titles);
		}
		String sceneId = model.createId(ResourceCategory.SCENE);
		Q.getComponent(copy, Date.class).setDate(
				System.currentTimeMillis() + "");

		return new AddResourceCommand(controller.getModel(), sceneId, copy,
				ResourceCategory.SCENE);
	}
}
