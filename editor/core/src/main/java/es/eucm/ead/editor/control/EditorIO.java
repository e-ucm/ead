/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

public class EditorIO implements LoadedCallback {

	private Controller controller;

	private ProjectAssets projectAssets;

	private Project project;

	private Game game;

	private Map<String, Scene> scenes;

	public EditorIO(Controller controller) {
		this.controller = controller;
		this.projectAssets = controller.getProjectAssets();
		scenes = new HashMap<String, Scene>();
	}

	public void load(String loadingPath, boolean internal) {
		project = null;
		game = null;
		scenes.clear();

		projectAssets.setLoadingPath(loadingPath, internal);
		projectAssets.loadGame(this);
		FileHandle scenesPath = projectAssets
				.resolve(ProjectAssets.SCENES_PATH);
		for (FileHandle sceneFile : scenesPath.list()) {
			projectAssets.loadScene(sceneFile.nameWithoutExtension(), this);
		}
		projectAssets.loadProject(this);
	}

	/**
	 * Convenience method, saves a specified attribute from the {@link Model}.
	 * 
	 * @param target
	 *            {@link Game}, {@link Project}, {@link Map} of Scenes or
	 *            {@link Model} (saves all it's attributes).
	 */
	public void save(Object target) {
		if (target == null)
			return;
		if (target instanceof Game) {
			saveGame(target);
		} else if (target instanceof Project) {
			saveProject(target);
		} else if (target instanceof Map) {
			saveScenes((Map<String, Scene>) target);
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
		removeAllJsonFilesPersistently(model);
		saveGame(model.getGame());
		saveProject(model.getProject());
		saveScenes(model.getScenes());
	}

	private void saveGame(Object game) {
		projectAssets.toJsonPath(game, ProjectAssets.GAME_FILE);
	}

	private void saveProject(Object project) {
		projectAssets.toJsonPath(project, ProjectAssets.PROJECT_FILE);
	}

	private void saveScenes(Map<String, Scene> scenes) {
		for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
			projectAssets.toJsonPath(entry.getValue(),
					projectAssets.convertSceneNameToPath(entry.getKey()));
		}
	}

	/**
	 * Removes all json files from disk. This includes gamemetadata.json,
	 * game.json and any scene.json
	 * 
	 * NOTE: This method should only be invoked from
	 * {@link #saveAll(es.eucm.ead.editor.model.Model)}, before the model is
	 * saved to disk
	 * 
	 * @param model
	 *            The model from which json files will be deleted persistently.
	 */
	public void removeAllJsonFilesPersistently(Model model) {
		removeGameFilePersistently();
		removeProjectFilePersistently();
		removeSceneFilesPersistently(model.getScenes());
	}

	/**
	 * Removes the game file (game.json) from disk persistently. Should only be
	 * invoked from
	 * {@link #removeAllJsonFilesPersistently(es.eucm.ead.editor.model.Model)}
	 */
	private void removeGameFilePersistently() {
		projectAssets.resolve(ProjectAssets.GAME_FILE).delete();
	}

	/**
	 * Removes the project file (gamemetadata.json) from disk persistently.
	 * Should only be invoked from
	 * {@link #removeAllJsonFilesPersistently(es.eucm.ead.editor.model.Model)}
	 */
	private void removeProjectFilePersistently() {
		projectAssets.resolve(ProjectAssets.PROJECT_FILE).delete();
	}

	/**
	 * Removes the project file (gamemetadata.json) from disk persistently.
	 * Should only be invoked from
	 * {@link #removeAllJsonFilesPersistently(es.eucm.ead.editor.model.Model)}
	 * 
	 * @param scenes
	 *            The scenes whose files should be deleted
	 */
	private void removeSceneFilesPersistently(Map<String, Scene> scenes) {
		for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
			projectAssets.resolve(
					projectAssets.convertSceneNameToPath(entry.getKey()))
					.delete();
		}
	}

	@Override
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == Game.class) {
			game = assetManager.get(fileName);
		} else if (type == Scene.class) {
			String sceneName = projectAssets.resolve(fileName)
					.nameWithoutExtension();
			Scene scene = assetManager.get(fileName);
			scenes.put(sceneName, scene);
		} else if (type == Project.class) {
			project = assetManager.get(fileName);
			// Project is the last thing loaded, generate command
			controller.command(new ModelCommand(controller.getModel(), game,
					project, scenes));

		}
	}
}
