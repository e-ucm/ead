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
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.GameAssets;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.game.Game;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EditorIO implements LoadedCallback {

	private Controller controller;

	private EditorGameAssets editorGameAssets;

	private EditorGame game;

	private Map<String, EditorScene> scenes;

	public EditorIO(Controller controller) {
		this.controller = controller;
		this.editorGameAssets = controller.getEditorGameAssets();
	}

	/**
	 * This method starts the loading process of the game project stored in the
	 * given {@code loadingPath}. It should be invoked by
	 * {@link es.eucm.ead.editor.control.Controller#loadGame(String, boolean)}
	 * 
	 * {@link #load(String, boolean)} returns before the whole project is loaded
	 * because of inter-file dependencies: Before loading
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#GAME_FILE}, all
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#SCENES_PATH} have to be
	 * already loaded.
	 * 
	 * The loading process completes once
	 * {@link #finishedLoading(com.badlogic.gdx.assets.AssetManager, String, Class)}
	 * is invoked by {@link es.eucm.ead.editor.assets.EditorSceneLoader} and the
	 * number of scenesMetadata and scenes match (in this case it is assumed
	 * that all sceneMetadatas are already available). Then,
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#loadGame(com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback)}
	 * is invoked, which finishes the loading process.
	 * 
	 * @param loadingPath
	 *            The full path of the project to be loaded. Cannot be null.
	 * @param internal
	 *            Additional parameter required by
	 *            {@link es.eucm.ead.editor.assets.EditorGameAssets} to resolve
	 *            files. If true, the root path is the classpath
	 */
	public void load(String loadingPath, boolean internal) {
		game = null;
		scenes = new HashMap<String, EditorScene>();
		editorGameAssets.setLoadingPath(loadingPath, internal);
		// Game has, as dependencies, all data required
		editorGameAssets.loadGame(this);
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
		if (target instanceof EditorGame) {
			saveGame((EditorGame) target);
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

	/**
	 * Saves the given model casted to the basic schema so all the editor's
	 * extra parameters are removed. It is intended for exporting the game for
	 * final release only.
	 * 
	 * First, this method creates a temp directory to store the saved model.
	 * This temp directory is returned so this method can be chained to others
	 * during the exportation process.
	 * 
	 * Second, it casts the {@code model}'s editorgame to
	 * {@link es.eucm.ead.schema.game.Game} and saves it to the temp folder.
	 * 
	 * Finally, it casts the {@code model}'s editorscenes to
	 * {@link es.eucm.ead.schema.actors.Scene} and saves them to the temp
	 * folder.
	 */
	public FileHandle saveGameForExport(Model model) {
		// Temp dir
		FileHandle tempDir = FileHandle.tempDirectory("ead-export-");
		tempDir.mkdirs();

		// Save simplified game
		Game simplifiedGame = (Game) upcastToEngineSchema(model.getGame());
		// Game simplifiedGame = (Game)model.getGame();
		String basePath = editorGameAssets.toCanonicalPath(tempDir.path());
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}
		String gamePath = basePath + EditorGameAssets.GAME_FILE;
		editorGameAssets.toJsonPath(simplifiedGame, gamePath);

		// Save simplified scenes
		String scenesPath = basePath + EditorGameAssets.SCENES_PATH;
		FileHandle scenesFH = new FileHandle(scenesPath);
		scenesFH.mkdirs();
		for (Map.Entry<String, EditorScene> entry : model.getScenes()
				.entrySet()) {
			Scene simplifiedScene = (Scene) upcastToEngineSchema(entry
					.getValue());
			String scenePath = scenesPath + entry.getKey();
			if (!scenePath.toLowerCase().endsWith(".json")) {
				scenePath += ".json";
			}
			editorGameAssets.toJsonPath(simplifiedScene, scenePath);
		}

		// return the tempDir
		return tempDir;
	}

	/**
	 * This method creates a shallow copy of the given object using its
	 * superclass' constructor to instantiate the new object. For example, if
	 * the {@code object} given as an argument is of type
	 * {@link es.eucm.ead.schema.editor.game.EditorGame}, the returned object
	 * will be of type {@link es.eucm.ead.schema.game.Game}. The copy is created
	 * using reflection.
	 * 
	 * This is required by
	 * {@link #saveGameForExport(es.eucm.ead.editor.model.Model)}. The
	 * underlying {@link com.badlogic.gdx.utils.Json} class invokes the object's
	 * getClass() method which always returns the class used to instantiate the
	 * object and therefore simple upcasting does not work.
	 * 
	 * @param object
	 *            The editor's schema object that has to be cloned as an
	 *            engine's schema object
	 * @return The engine's schema object containing a shallow copy of
	 *         {@code object}. May be null if either {@code object} is null or
	 *         if an internal reflection exception is thrown.
	 */
	private Object upcastToEngineSchema(Object object) {
		if (object == null)
			return null;

		final Class clazz = object.getClass().getSuperclass();

		try {
			Object copy = clazz.newInstance();

			for (Field declaredField : clazz.getDeclaredFields()) {
				declaredField.setAccessible(true);
				declaredField.set(copy, declaredField.get(object));
				declaredField.setAccessible(false);
			}

			return copy;
		} catch (InstantiationException e) {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Error while upcasting object of type " + object.getClass()
							+ " to type " + object.getClass().getSuperclass(),
					e);
		} catch (IllegalAccessException e) {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"Error while upcasting object of type " + object.getClass()
							+ " to type " + object.getClass().getSuperclass(),
					e);
		}
		return null;
	}

	private void saveGame(Object game) {
		// Update the appVersion and model version for this game
		((EditorGame) game).setAppVersion(controller.getAppVersion());
		((EditorGame) game).setModelVersion(controller.getModelVersion());
		editorGameAssets.toJsonPath(game, EditorGameAssets.GAME_FILE);
	}

	private void saveScenes(Map<String, EditorScene> scenes) {
		for (Map.Entry<String, EditorScene> entry : scenes.entrySet()) {
			editorGameAssets.toJsonPath(entry.getValue(),
					editorGameAssets.convertSceneNameToPath(entry.getKey()));
		}
	}

	/**
	 * Removes all json files from disk under the
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#getLoadingPath()}
	 * folder.
	 * 
	 * 
	 * NOTE: This method should only be invoked from
	 * {@link #saveAll(es.eucm.ead.editor.model.Model)}, before the model is
	 * saved to disk
	 */
	private void removeAllJsonFilesPersistently() {
		String loadingPath = controller.getEditorGameAssets().getLoadingPath();
		deleteJsonFilesRecursively(controller.getEditorGameAssets().absolute(
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
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == EditorScene.class) {
			String sceneId = editorGameAssets.resolve(fileName)
					.nameWithoutExtension();
			EditorScene scene = assetManager.get(fileName);
			scenes.put(sceneId, scene);
			// Once scenes have been loaded, load
		} else if (type == EditorGame.class) {
			game = assetManager.get(fileName);
		}

		// If everything is loaded, trigger to load command
		if (editorGameAssets.isDoneLoading()) {
			// FIXME commands should only be created in actions
			controller.command(new ModelCommand(controller.getModel(), game,
					scenes));
		}
	}
}
