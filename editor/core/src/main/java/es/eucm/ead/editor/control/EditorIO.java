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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.game.Game;

import java.util.HashMap;
import java.util.Map;

public class EditorIO implements LoadedCallback {

	private Controller controller;

	private ProjectAssets projectAssets;

	private EditorGame game;

	private Map<String, EditorScene> scenes;

	public EditorIO(Controller controller) {
		this.controller = controller;
		this.projectAssets = controller.getProjectAssets();
		scenes = new HashMap<String, EditorScene>();
	}

	/**
	 * This method starts the loading process of the game project stored in the
	 * given {@code loadingPath}. It should be invoked by
	 * {@link es.eucm.ead.editor.control.Controller#loadGame(String, boolean)}
	 * 
	 * {@link #load(String, boolean)} returns before the whole project is loaded
	 * because of inter-file dependencies: Before loading
	 * {@link es.eucm.ead.editor.assets.ProjectAssets#GAME_FILE}, all
	 * {@link es.eucm.ead.editor.assets.ProjectAssets#SCENES_PATH} have to be
	 * already loaded.
	 * 
	 * The loading process completes once
	 * {@link #finishedLoading(com.badlogic.gdx.assets.AssetManager, String, Class)}
	 * is invoked by {@link es.eucm.ead.editor.assets.EditorSceneLoader} and the
	 * number of scenesMetadata and scenes match (in this case it is assumed
	 * that all sceneMetadatas are already available). Then,
	 * {@link ProjectAssets#loadGame(com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback)}
	 * is invoked, which finishes the loading process.
	 * 
	 * @param loadingPath
	 *            The full path of the project to be loaded. Cannot be null.
	 * @param internal
	 *            Additional parameter required by
	 *            {@link es.eucm.ead.editor.assets.ProjectAssets} to resolve
	 *            files. If true, the root path is the classpath
	 */
	public void load(String loadingPath, boolean internal) {
		game = null;
		scenes.clear();
		projectAssets.setLoadingPath(loadingPath, internal);
		// Game has, as dependencies, all data required
		projectAssets.loadGame(this);
	}

	/**
	 * Convenience method, saves a specified attribute from the {@link Model}.
	 * 
	 * @param target
	 *            {@link Game}, {@link EditorGame}, {@link Map} of Scenes or
	 *            {@link Model} (saves all it's attributes).
	 */
	public void save(Object target) {
		if (target == null)
			return;
		if (target instanceof Game) {
			saveGame(target);
		} else if (target instanceof Map) {
			saveScenes((Map<String, EditorScene>) target);
		} else if (target instanceof Model) {
			saveAll((Model) target);
		} else {
			Gdx.app.error("EditorIO", "Couldn't save " + target.toString());
		}
	}

	/**
	 * Saves the whole model into disk. This method is the one invoked through
	 * the UI (e.g. when the user hits Ctrl+S)
	 * 
	 * @param model
	 *            The model that should be stored into disk
	 */
	public void saveAll(Model model) {
		// First of all, remove all json files persistently from disk
		removeAllJsonFilesPersistently();
		saveGame(model.getGame());
		saveScenes(model.getScenes());
	}

	private void saveGame(Object game) {
		projectAssets.toJsonPath(game, ProjectAssets.GAME_FILE);
	}

	private void saveScenes(Map<String, EditorScene> scenes) {
		for (Map.Entry<String, EditorScene> entry : scenes.entrySet()) {
			projectAssets.toJsonPath(entry.getValue(),
					projectAssets.convertSceneNameToPath(entry.getKey()));
		}
	}

	/**
	 * Removes all json files from disk under the
	 * {@link es.eucm.ead.editor.assets.ProjectAssets#getLoadingPath()} folder.
	 * This includes gamemetadata.json, game.json and any scene.json
	 * 
	 * NOTE: This method should only be invoked from
	 * {@link #saveAll(es.eucm.ead.editor.model.Model)}, before the model is
	 * saved to disk
	 */
	private void removeAllJsonFilesPersistently() {
		String loadingPath = controller.getProjectAssets().getLoadingPath();
		deleteJsonFilesRecursively(controller.getProjectAssets().absolute(
				loadingPath));
	}

	/**
	 * Deletes the json files from a directory recursively
	 * 
	 * @param directory
	 *            The file object pointing to the root directory from where json
	 *            files must be deleted
	 */
	private void deleteJsonFilesRecursively(FileHandle directory) {
		// Delete dir contents
		if (!directory.exists() || !directory.isDirectory())
			return;

		for (FileHandle child : directory.list()) {
			if (child.isDirectory()) {
				deleteJsonFilesRecursively(child);
			} else {
				if ("json".equals(child.extension())) {
					child.delete();
				}
			}

		}

		// Remove the directory if it's empty.
		if (directory.list().length == 0) {
			directory.deleteDirectory();
		}
	}

	@Override
	/**
	 * When this method is invoked by the appropriate loader, model objects (game, gamemetadata, scenes, scenemetadata) are actually initialized.
	 * Loads {@link ProjectAssets#GAME_METADATA_FILE} once all scene metadata are available (see {@link #load(String, boolean)} for more details).
	 */
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == EditorScene.class) {
			String sceneId = projectAssets.resolve(fileName)
					.nameWithoutExtension();
			EditorScene scene = assetManager.get(fileName);
			scenes.put(sceneId, scene);
			// Once scenes have been loaded, load
		} else if (type == EditorGame.class) {
			game = assetManager.get(fileName);
		}

		// If everything is loaded, trigger to load command
		if (projectAssets.isDoneLoading()) {
			controller.command(new ModelCommand(controller.getModel(), game,
					scenes));
		}
	}
}
