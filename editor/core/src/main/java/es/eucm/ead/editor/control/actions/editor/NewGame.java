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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.components.game.GameData;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

import java.io.FileNotFoundException;

/**
 * New game creates an empty game. Expects exactly three parameters: arg[0]: a
 * valid path to a folder where the game should be created (String) arg[1]: a
 * not null {@link ModelEntity} object
 */
public class NewGame extends EditorAction {

	public NewGame() {
		super(true, true, String.class, ModelEntity.class);
	}

	@Override
	public void perform(Object... args) {

		// There should be at least one argument
		// FIXME boilerplate code
		if (args.length < 2) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": This action requires at least two arguments of type String, EditorGame");
		}

		if (args[0] == null || !(args[0] instanceof String)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": This action requires the first argument (args[0]) to be a valid, not null String path for the directory where to create the new game");
		}

		if (args[1] == null || !(args[1] instanceof ModelEntity)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": This action requires the second argument (args[1]) to be a valid, not null Game object");
		}

		// args[0] => Path of the new project
		String path = null;

		path = args[0] != null ? (String) args[0] : "";

		// Check all the slashes are /
		path = controller.getEditorGameAssets().toCanonicalPath(path);

		// FIXME control of null
		ModelEntity game = (ModelEntity) args[1];

		EditorGameAssets editorGameAssets = controller.getEditorGameAssets();
		FileHandle projectFolder = editorGameAssets.absolute(path);

		if (!projectFolder.exists()) {
			projectFolder.mkdirs();
		}

		if (projectFolder.exists()) {
			Model model = controller.getModel();
			// Get the first valid id for scene entities
			String blankSceneId = model.createId(ModelEntityCategory.SCENE);

			GameData gameData = Model.getComponent(game, GameData.class);
			gameData.setInitialScene(blankSceneId);
			EditState editState = Model.getComponent(game, EditState.class);
			editState.setEditScene(blankSceneId);
			editState.getSceneorder().add(blankSceneId);

			model.reset();
			model.putEntity(ModelEntityCategory.GAME.getCategoryPrefix(), game);

			ModelEntity editorScene = controller.getTemplates().createScene(
					blankSceneId);
			model.putEntity(blankSceneId, editorScene);

			editorGameAssets.setLoadingPath(path);
			controller.action(Save.class);
			controller
					.action(OpenGame.class, editorGameAssets.getLoadingPath());
		} else {
			throw new EditorActionException("Impossible to create project",
					new FileNotFoundException(path));
		}
	}
}
