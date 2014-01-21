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
package es.eucm.ead.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.actors.SceneActor;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.game.Game;

import java.util.List;
import java.util.Stack;

/**
 * Scene manager deals with scenes. It's able to load games and scenes. All
 * modifications of the game scene must pass through here. Loading the game,
 * loading a new scene and adding or removing scene elements.
 */
public class SceneManager {

	private static final int LOAD_TIME = 1000 / 30;

	private String currentScenePath;

	protected Scene currentScene;

	private SceneActor currentSceneActor;

	private Array<SceneTask> tasks;

	private Assets assets;

	private Game game;

	private Stack<String> subgamesScenes;

	private Stack<List<Action>> subgamesActions;

	/**
	 * If the scene manager just came back from a subgame
	 */
	private boolean returnedFromSubgame;

	public SceneManager(Assets assets) {
		tasks = new Array<SceneTask>();
		this.assets = assets;
		subgamesActions = new Stack<List<Action>>();
		subgamesScenes = new Stack<String>();
	}

	/**
	 * Loads the game in the current game path, and the initial scene
	 */
	public void loadGame() {
		loadGame(null);
	}

	/**
	 * Loads the game in the current game path
	 * 
	 * @param scene
	 *            the name of the scene to load. If scene is null, then the
	 *            initial scene will be used instead
	 */
	public void loadGame(String scene) {
		FileHandle gameFile = assets.resolve("game.json");
		if (gameFile.exists()) {
			game = Engine.schemaIO.fromJson(Game.class, gameFile);
			Engine.stage.setGameSize(game.getWidth(), game.getHeight());
			loadScene(scene == null ? game.getInitialScene() : scene);
		} else {
			Gdx.app.error("SceneManager",
					"game.json doesn't exist. Game not loaded.");
		}
	}

	/**
	 * 
	 * @return the schema object representing the current game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Loads the scene with the given name. All the resources required by the
	 * scene are queued in the assets manager.
	 * 
	 * @param name
	 *            the scene's name. ".json" is automatically appended if the
	 *            name doesn't end with it, and also is prefixed with "scenes/"
	 *            if it's not already
	 */
	public void loadScene(String name) {
		if (!name.endsWith(".json")) {
			name += ".json";
		}

		if (!name.startsWith("scenes/")) {
			name = "scenes/" + name;
		}

		FileHandle sceneFile = Engine.assets.resolve(name);
		if (sceneFile.exists()) {
			currentScenePath = name;
			Scene scene = Engine.schemaIO.fromJson(Scene.class, sceneFile);
			SetSceneTask st = Pools.obtain(SetSceneTask.class);
			st.setScene(scene);
			// This task won't be executed until all the scene resources are
			// loaded
			addTask(st);
		} else {
			Gdx.app.error("SceneManager", "Scene not found (File " + name
					+ " not found).");
		}
	}

	/**
	 * Sets a scene. All the assets required by the scene must be already
	 * loaded. This method is for internal usage only. Use
	 * {@link SceneManager#loadScene(String)} to load a scene
	 * 
	 * @param scene
	 *            the scene schema object
	 */
	protected void setScene(Scene scene) {
		currentScene = scene;

		if (currentSceneActor != null) {
			currentSceneActor.free();
		}
		currentSceneActor = Engine.factory.getEngineObject(currentScene);
		// If returning from subgame, execute associated actions
		if (returnedFromSubgame) {
			for (Action action : subgamesActions.pop()) {
				currentSceneActor.addAction(action);
			}
			returnedFromSubgame = false;
		}
		Engine.stage.setScene(currentSceneActor);
	}

	/**
	 * 
	 * @return the current scene schema object
	 */
	public Scene getCurrentScene() {
		return currentScene;
	}

	/**
	 * 
	 * @return the path to the json defining the initial state of the current
	 *         scene
	 */
	public String getCurrentScenePath() {
		return currentScenePath;
	}

	/**
	 * Loads the given scene element (and all resources related) and eventually
	 * adds it to the scene
	 * 
	 * @param sceneElement
	 *            the schema object representing the scene element
	 */
	public void loadSceneElement(SceneElement sceneElement) {
		AddSceneElementTask t = Pools.obtain(AddSceneElementTask.class);
		t.setSceneElement(sceneElement);
		addTask(t);
	}

	/**
	 * Effectively adds the scene element to the scene, after all its resources
	 * has been loaded. This method is for internal usage only. Use
	 * {@link SceneManager#loadSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * to add scene elements to the scene
	 * 
	 * @param sceneElement
	 *            the scene element to add
	 */
	protected void addSceneElement(SceneElement sceneElement) {
		currentSceneActor.addActor(sceneElement);
	}

	/**
	 * Removes an actor form the scene
	 * 
	 * @param actor
	 *            the actor to remove
	 * @return if the actor had a parent
	 */
	public boolean removeSceneElement(SceneElementActor actor) {
		return actor.remove();
	}

	/**
	 * Adds a task to be performed once the scene manager is done loading
	 * 
	 * @param task
	 *            the task
	 */
	private void addTask(SceneTask task) {
		tasks.add(task);
	}

	/**
	 * @return if the scene manager is still loading assets
	 */
	public boolean isLoading() {
		return Engine.assets.getQueuedAssets() > 0;
	}

	/**
	 * Updates the scene manager
	 */
	public void act() {
		if (isLoading()) {
			boolean done = Engine.assets.update(LOAD_TIME);
			if (done) {
				executeTasks();
			}
		} else if (tasks.size > 0) {
			executeTasks();
		}
	}

	/** Execute pending tasks, after all assets are loaded **/
	private void executeTasks() {
		for (SceneTask t : tasks) {
			t.execute(this);
			Pools.free(t);
		}
		tasks.clear();
	}

	/**
	 * Resets the current scene into its initial state
	 */
	public void reloadCurrentScene() {
		loadScene(currentScenePath);
	}

	/**
	 * Loads a subgame
	 * 
	 * @param name
	 *            the name of the subgame
	 * @param actions
	 *            the actions to execute when the subgame ends. The parent for
	 *            the actions will be the returning scene in the parent game
	 */
	public void loadSubgame(String name, List<Action> actions) {
		String subgameLoadingPath = "subgames/" + name + "/";
		String subgamePath = subgameLoadingPath + "game.json";
		FileHandle subgame = Engine.assets.resolve(subgamePath);
		if (subgame.exists()) {
			Engine.assets.addSubgamePath(subgameLoadingPath);
			// Add actions and scene to stack. Actions will be executed when
			// endSubgame is called
			subgamesActions.push(actions);
			subgamesScenes.push(currentScenePath);
			loadGame();
		} else {
			Gdx.app.error("SceneManager", name
					+ " does not exist. Subgame not loaded.");
		}
	}

	/**
	 * Ends the current subgame and execute its associated actions, set through
	 * {@link SceneManager#loadSubgame(String, java.util.List)}
	 */
	public void endSubgame() {
		if (Engine.assets.popSubgamePath()) {
			Gdx.app.exit();
		} else {
			String scenePath = subgamesScenes.pop();
			loadGame(scenePath);
			returnedFromSubgame = true;
		}
	}

	/**
	 * Interface for tasks to execute once the scene manager is done loading. We
	 * need these tasks to separate the loading phase from the initialization
	 * phase in actors. We can't initialize an actor until all its resources are
	 * loaded (e.g. a image), so we queue all its assets in the load phase (
	 * {@link SceneManager#loadScene(String)} and
	 * {@link SceneManager#loadSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * ) and once all assets are loaded (through
	 * {@link es.eucm.ead.engine.scene.SceneManager#act()} we go to the
	 * initialization phase (
	 * {@link SceneManager#setScene(es.eucm.ead.schema.actors.Scene)} and (
	 * {@link SceneManager#addSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * ), and the new actors appear.
	 */
	public interface SceneTask {
		/**
		 * Executes the task
		 * 
		 * @param sceneManager
		 *            the scene manager
		 */
		void execute(SceneManager sceneManager);
	}

	/**
	 * Task to set loaded scene
	 */
	public static class SetSceneTask implements SceneTask {

		private Scene scene;

		public void setScene(Scene scene) {
			this.scene = scene;
		}

		@Override
		public void execute(SceneManager sceneManager) {
			sceneManager.setScene(scene);
		}
	}

	/**
	 * Task to add a loaded scene element
	 */
	public static class AddSceneElementTask implements SceneTask {

		private SceneElement sceneElement;

		public void setSceneElement(SceneElement sceneElement) {
			this.sceneElement = sceneElement;
		}

		@Override
		public void execute(SceneManager sceneManager) {
			sceneManager.addSceneElement(sceneElement);
		}
	}
}
