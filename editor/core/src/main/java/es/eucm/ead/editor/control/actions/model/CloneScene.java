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
import es.eucm.ead.editor.control.commands.ResourceCommand.AddResourceCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Date;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Duplicates the currently selected scene and appends a suffix to its name in
 * order to be distinguished from the original.
 */
public class CloneScene extends ModelAction {

	@Override
	public Command perform(Object... args) {
		I18N i18n = controller.getApplicationAssets().getI18N();
		ModelEntity scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);

		ModelEntity copy = controller.getEditorGameAssets().copy(scene);

		String sceneName = Q.getComponent(scene, Documentation.class).getName();
		if (sceneName == null) {
			sceneName = "";
		}
		String sceneSuffix = " " + i18n.m("scene.copy").toLowerCase();

		String name = sceneName;
		int index = name.indexOf(sceneSuffix);
		if (index == -1) {
			name += sceneSuffix;
		} else {
			name = name.substring(0, index) + sceneSuffix;
		}

		int cont = 1;
		Model model = controller.getModel();
		for (Model.Resource res : model.getResources(ResourceCategory.SCENE)
				.values()) {
			String resName = Q.getComponent((ModelEntity) res.getObject(),
					Documentation.class).getName();
			if (resName != null && resName.startsWith(name)) {
				++cont;
			}
		}

		if (cont > 1) {
			name += " " + cont;
		}

		String sceneId = model.createId(ResourceCategory.SCENE);
		Q.getComponent(copy, Documentation.class).setName(name);
		Q.getComponent(copy, Date.class).setDate(
				System.currentTimeMillis() + "");

		return new AddResourceCommand(controller.getModel(), sceneId, copy,
				ResourceCategory.SCENE);
	}

}