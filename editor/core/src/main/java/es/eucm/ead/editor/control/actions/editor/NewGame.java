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
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * New game creates an empty game. Expects exactly three parameters: arg[0]: a
 * valid path to a folder where the game should be created (String) arg[1]: a
 * not null {@link es.eucm.ead.schema.editor.game.EditorGame} object arg[2]: a
 * not null {@link es.eucm.ead.schema.game.Game} object
 */
public class NewGame extends ModelAction {

	/**
	 * The id of the new blank scene each game is created with.
	 */
	public static final String BLANK_SCENE_ID = "scene0";

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

		if (args[1] == null || !(args[1] instanceof EditorGame)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": This action requires the second argument (args[1]) to be a valid, not null EditorGame object");
		}

		// args[0] => Path of the new project
		String path = null;

		path = args[0] != null ? (String) args[0] : new String("");

		// Check all the slashes are /
		path = controller.getEditorGameAssets().toCanonicalPath(path);

		// FIXME control of null
		EditorGame game = (EditorGame) args[1];

		EditorGameAssets editorGameAssets = controller.getEditorGameAssets();
		FileHandle projectFolder = editorGameAssets.absolute(path);

		if (!projectFolder.exists()) {
			projectFolder.mkdirs();
		}

		if (projectFolder.exists()) {
			game.setInitialScene(BLANK_SCENE_ID);
			game.setEditScene(BLANK_SCENE_ID);
			game.getSceneorder().add(BLANK_SCENE_ID);

			Model model = new Model();
			model.setGame(game);

			Map<String, EditorScene> scenes = new HashMap<String, EditorScene>();
			EditorScene editorScene = controller.getTemplates().createScene(
					BLANK_SCENE_ID);
			scenes.put(BLANK_SCENE_ID, editorScene);
			model.setScenes(scenes);

			controller.getModel().setGame(game);
			controller.getModel().setScenes(scenes);

			editorGameAssets.setLoadingPath(path);
			controller.getEditorIO().saveAll(model);
			controller
					.action(OpenGame.class, editorGameAssets.getLoadingPath());
		} else {
			throw new EditorActionException("Impossible to create project",
					new FileNotFoundException(path));
		}
	}
}
