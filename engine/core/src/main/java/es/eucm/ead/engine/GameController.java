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
package es.eucm.ead.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.io.SchemaIO;
import es.eucm.ead.engine.triggers.TimeSource;
import es.eucm.ead.engine.triggers.TouchSource;
import es.eucm.ead.engine.triggers.TriggerSource;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Time;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Trigger;
import es.eucm.ead.schema.game.Game;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GameController implements TriggerSource {

	protected Assets assets;

	protected SchemaIO schemaIO;

	protected SceneView sceneView;

	private Map<Class<?>, TriggerSource> triggerSources;

	private Array<SceneTask> tasks;

	private Stack<GameState> gameStates;

	private GameState currentGameState;

	public GameController(Assets assets, SchemaIO schemaIO, SceneView sceneView) {
		this.sceneView = sceneView;
		this.assets = assets;
		this.schemaIO = schemaIO;
		this.gameStates = new Stack<GameState>();
		tasks = new Array<SceneTask>();
		triggerSources = new LinkedHashMap<Class<?>, TriggerSource>();
		registerTriggerProducers();
	}

	protected void registerTriggerProducers() {
		triggerSources.put(Touch.class, new TouchSource());
		triggerSources.put(Time.class, new TimeSource());
	}

	@Override
	public void registerForTrigger(SceneElementActor actor, Trigger event) {
		TriggerSource triggerSource = triggerSources.get(event.getClass());
		if (triggerSource == null) {
			Gdx.app.error("EngineState", "No trigger source found for class "
					+ event.getClass());
		} else {
			triggerSource.registerForTrigger(actor, event);
		}
	}

	@Override
	public void unregisterForAllTriggers(SceneElementActor actor) {
		for (TriggerSource triggerSource : triggerSources.values()) {
			triggerSource.unregisterForAllTriggers(actor);
		}
	}

	/**
	 * Loads the game in the current game path, and the initial scene
	 */
	public boolean loadGame() {
		FileHandle gameFile = assets.resolve("game.json");
		if (gameFile.exists()) {
			Game game = schemaIO.fromJson(Game.class, gameFile);
			loadGame(game);
			return true;
		} else {
			Gdx.app.error("GameController",
					"game.json doesn't exist. Game not loaded.");
			return false;
		}
	}

	protected void loadGame(Game game) {
		// If there is no game state for the game, create a new one
		if (currentGameState == null) {
			currentGameState = new GameState(game.getInitialScene());
			VarsContext vars = currentGameState.getVarsContext();
			vars.registerVariables(game.getVariables());
		} else {
			currentGameState = gameStates.pop();
			// Execute waiting actions
			for (Action a : currentGameState.getPostactions()) {
				sceneView.addAction(a);
			}
			currentGameState.getPostactions().clear();
		}
		loadScene(currentGameState.getCurrentScene());
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
		String path = convertToPath(name);
		FileHandle sceneFile = assets.resolve(path);
		if (sceneFile.exists()) {
			currentGameState.setCurrentScene(name);
			Scene scene = schemaIO.fromJson(Scene.class, sceneFile);
			SetSceneTask st = Pools.obtain(SetSceneTask.class);
			st.setScene(scene);
			// This task won't be executed until all the scene resources are
			// loaded
			addTask(st);
		} else {
			Gdx.app.error("GameController", "Scene not found (File " + path
					+ " not found).");
		}
	}

	private String convertToPath(String name) {
		String path = name;
		if (!path.endsWith(".json")) {
			path += ".json";
		}

		if (!path.startsWith("scenes/")) {
			path = "scenes/" + path;
		}
		return path;
	}

	/**
	 * 
	 * @return returns the current vars
	 */
	public VarsContext getVarsContext() {
		return currentGameState.getVarsContext();
	}

	@Override
	public void act(float delta) {
		if (isLoading() || tasks.size > 0) {
			boolean done = assets.update();
			if (done) {
				executeTasks();
			}
		} else {
			for (TriggerSource triggerSource : triggerSources.values()) {
				triggerSource.act(delta);
			}
		}
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
		return assets.isLoading();
	}

	/** Execute pending tasks, after all assets are loaded **/
	private void executeTasks() {
		for (SceneTask t : tasks) {
			t.execute(sceneView);
			Pools.free(t);
		}
		tasks.clear();
	}

	/**
	 * Resets the current scene into its initial state
	 */
	public void reloadCurrentScene() {
		loadScene(currentGameState.getCurrentScene());
	}

	/**
	 * @param sceneElement
	 *            the target scene element
	 * @return Returns the actor that wraps the given scene element
	 */
	public Actor getSceneElement(SceneElement sceneElement) {
		return sceneView.getCurrentScene().getSceneElement(sceneElement);
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
			currentGameState.getPostactions().addAll(actions);
			gameStates.push(currentGameState);
			currentGameState = null;
			loadGame();
		} else {
			Gdx.app.error("GameController", name
					+ " doesn't exist. Subgame not loaded.");
		}
	}

	/**
	 * Ends the current subgame and execute its associated actions, set through
	 * {@link GameController#loadSubgame(String, java.util.List)}
	 */
	public void endSubgame() {
		if (Engine.assets.popSubgamePath()) {
			Gdx.app.exit();
		} else {
			loadGame();
		}
	}

	public String getCurrentScene() {
		return currentGameState.getCurrentScene();
	}

	/**
	 * Interface for tasks to execute once the game controller is done loading.
	 * We need these tasks to separate the loading phase from the initialization
	 * phase in actors. We cannot initialize an actor until all its resources
	 * are loaded (e.g. a image), so we queue all its assets in the load phase (
	 * {@link GameController#loadScene(String)} and
	 * {@link GameController#loadSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * ) and once all assets are loaded (through
	 * {@link GameController#act(float)} we go to the initialization phase (
	 * {@link SceneView#setScene(es.eucm.ead.schema.actors.Scene)} and (
	 * {@link SceneView#addSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * ), and the new actors appear.
	 */
	public interface SceneTask {
		/**
		 * Executes the task
		 * 
		 * @param sceneView
		 *            the scene manager
		 */
		void execute(SceneView sceneView);
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
		public void execute(SceneView sceneView) {
			sceneView.setScene(scene);
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
		public void execute(SceneView sceneView) {
			sceneView.addSceneElement(sceneElement);
		}
	}
}
