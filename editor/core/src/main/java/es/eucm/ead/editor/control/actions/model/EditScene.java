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

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.scene.SetEditedScene;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Changes the edited scene.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String (Optional)</em> the identifier of
 * edited scene</dd>
 * <dd><strong>args[1]</strong> <em>Boolean (Optional)</em> if the context
 * {@link Selection#EDITED_GROUP} also must be set with the scene<</dd>
 * </dl>
 */
public class EditScene extends EditorAction implements
		AssetLoadedCallback<Object> {

	private boolean editGroup;

	public EditScene() {
		super(true, false, new Class[] {}, new Class[] { String.class },
				new Class[] { String.class, Boolean.class },
				new Class[] { Boolean.class });
	}

	@Override
	public void perform(Object... args) {
		String sceneId = getSceneId(args);
		editGroup = getEditGroup(args);
		ResourceCategory resourceCategory = controller.getModel()
				.getResourceCategory(sceneId);
		if (resourceCategory == ResourceCategory.SCENE) {
			controller.getEditorGameAssets().get(sceneId, Object.class, this);
		} else {
			Gdx.app.error("EditScene", sceneId + " is not a scene.");
		}
	}

	private String getSceneId(Object... args) {
		for (Object arg : args) {
			if (arg instanceof String) {
				return (String) arg;
			}
		}
		return (String) (args.length == 0 ? controller.getModel()
				.getSelection().getSingle(Selection.RESOURCE) : args[0]);
	}

	private boolean getEditGroup(Object... args) {
		for (Object arg : args) {
			if (arg instanceof Boolean) {
				return (Boolean) arg;
			}
		}
		return true;
	}

	@Override
	public void loaded(String fileName, Object asset) {
		controller.action(SetEditedScene.class, fileName, asset, editGroup);
	}
}
