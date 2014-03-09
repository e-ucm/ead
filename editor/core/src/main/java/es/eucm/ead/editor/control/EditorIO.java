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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;

import java.util.HashMap;
import java.util.Map;

public class EditorIO implements LoadedCallback {

	private Controller controller;

	private ProjectAssets projectAssets;

	private GameMetadata gameMetadata;

	private Game game;

	private Map<String, Scene> scenes;

	private Map<String, SceneMetadata> scenesMetadata;

	public EditorIO(Controller controller) {
		this.controller = controller;
		this.projectAssets = controller.getProjectAssets();
		scenes = new HashMap<String, Scene>();
		scenesMetadata = new HashMap<String, SceneMetadata>();
	}

    /**
     * This method starts the loading process of the game project stored in the given {@code loadingPath} (e.g. "C:/Users/Javier Torrente/GIT_REPOS/ead/engine/desktop/src/test/resources/parity2.0/"). It should be invoked by {@link es.eucm.ead.editor.control.Controller#loadGame(String, boolean)}
     *
     * {@link #load(String, boolean)} returns before the whole project is loaded because of inter-file dependencies:
     *      Before loading {@link es.eucm.ead.editor.assets.ProjectAssets#GAME_METADATA_FILE}, all {@link es.eucm.ead.editor.assets.ProjectAssets#SCENES_PATH} have to be already loaded so it can set default values for {@link es.eucm.ead.schema.game.GameMetadata#getSceneorder()} if necessary.
     *
     *  The loading process completes once {@link #finishedLoading(com.badlogic.gdx.assets.AssetManager, String, Class)} is invoked by {@link es.eucm.ead.editor.assets.SceneMetadataLoader} and the number of scenesMetadata and scenes match (in this case it is assumed that all sceneMetadatas are already available). Then, {@link #loadGameMetadata()} is invoked, which finishes the loading process.
     *
     * @param loadingPath   The full path of the project to be loaded (e.g. "C:/Users/Javier Torrente/GIT_REPOS/ead/engine/desktop/src/test/resources/parity2.0/"). Cannot be null.
     * @param internal  Additional parameter required by {@link es.eucm.ead.editor.assets.ProjectAssets} to resolve files
     */
	public void load(String loadingPath, boolean internal) {
		gameMetadata = null;
		game = null;
		scenes.clear();
		scenesMetadata.clear();

		projectAssets.setLoadingPath(loadingPath, internal);
        // NOTE: DO NOT CHANGE THE ORDER IN WHICH THESE FILES ARE LOADED: GAME, SCENES, SCENESMETADATA, GAMEMETADATA!!
        // If GameMetadata is not loaded the last, editor crashes abruptly
		projectAssets.loadGame(this);
		FileHandle scenesPath = projectAssets
				.resolve(ProjectAssets.SCENES_PATH);
		for (FileHandle sceneFile : scenesPath.list()) {
			projectAssets.loadScene(sceneFile.nameWithoutExtension(), this);
		}

        FileHandle scenesMetadataPath = projectAssets
                .resolve(ProjectAssets.SCENEMETADATA_PATH);
        for (FileHandle sceneMetadataFile : scenesMetadataPath.list()) {
            projectAssets.loadSceneMetadata(
                    sceneMetadataFile.nameWithoutExtension(), this);
        }
        //FIXME: I think we should do some error handling here. For example, if "scenes" folder is missing a clean crash is reasonable, but if "scenes-editor" is missing we should just go on.
    }

    /**
     * This method loads {@link es.eucm.ead.editor.assets.ProjectAssets#GAME_METADATA_FILE} after {@link es.eucm.ead.editor.control.EditorIO} has been notified that all scene metadatas have been loaded and it has already retrieved all of them to populate {@link #scenesMetadata}.
     * This method is always invoked by {@link #finishedLoading(com.badlogic.gdx.assets.AssetManager, String, Class)} after {@link #load(String, boolean)} returns
     *
     */
    private void loadGameMetadata(){
        projectAssets.loadGameMetadata(this, game, gameMetadata, scenes, scenesMetadata);
    }

	/**
	 * Convenience method, saves a specified attribute from the {@link Model}.
	 * 
	 * @param target
	 *            {@link Game}, {@link GameMetadata}, {@link Map} of Scenes or
	 *            {@link Model} (saves all it's attributes).
	 */
	public void save(Object target) {
		if (target == null)
			return;
		if (target instanceof Game) {
			saveGame(target);
		} else if (target instanceof GameMetadata) {
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
		removeAllJsonFilesPersistently();
		saveGame(model.getGame());
		saveProject(model.getGameMetadata());
		saveScenes(model.getScenes());
		saveScenesMetadata(model.getScenesMetadata());
	}

	private void saveGame(Object game) {
		projectAssets.toJsonPath(game, ProjectAssets.GAME_FILE);
	}

	private void saveProject(Object project) {
		projectAssets.toJsonPath(project, ProjectAssets.GAME_METADATA_FILE);
	}

	private void saveScenes(Map<String, Scene> scenes) {
		for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
			projectAssets.toJsonPath(entry.getValue(),
					projectAssets.convertSceneNameToPath(entry.getKey()));
		}
	}

	private void saveScenesMetadata(Map<String, SceneMetadata> scenesMetadata) {
		for (Map.Entry<String, SceneMetadata> entry : scenesMetadata.entrySet()) {
			projectAssets.toJsonPath(entry.getValue(), projectAssets
					.convertSceneNameToMetadataPath(entry.getKey()));
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
		if (type == Game.class) {
			game = assetManager.get(fileName);
		} else if (type == Scene.class) {
			String sceneId = projectAssets.resolve(fileName)
					.nameWithoutExtension();
			Scene scene = assetManager.get(fileName);
			scenes.put(sceneId, scene);
            // Once scenes have been loaded, load
		} else if (type == SceneMetadata.class) {
			String sceneId = projectAssets.resolve(fileName)
					.nameWithoutExtension();
			SceneMetadata sceneMetadata = assetManager.get(fileName);
			scenesMetadata.put(sceneId, sceneMetadata);
            // GameMetadata cannot be loaded until ALL scenesMetadata are already available
            if (scenesMetadata.size()==scenes.size()){
                loadGameMetadata();
            }
		} else if (type == GameMetadata.class) {
			gameMetadata = assetManager.get(fileName);
			// Game metadata is the last thing loaded, generate command
			controller.command(new ModelCommand(controller.getModel(), game,
					gameMetadata, scenes, scenesMetadata));

		}
	}
}
