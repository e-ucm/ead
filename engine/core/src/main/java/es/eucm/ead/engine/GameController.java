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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.actors.SceneActor;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.io.SchemaIO;
import es.eucm.ead.engine.triggers.TimeSource;
import es.eucm.ead.engine.triggers.TouchSource;
import es.eucm.ead.engine.triggers.TriggerSource;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Time;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Trigger;
import es.eucm.ead.schema.game.Game;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameController implements TriggerSource {

	private static final int slotTime = 1000 / 30;

	private Assets assets;

	private SchemaIO schemaIO;

	private Factory factory;

	private SceneView sceneView;

	private VarsContext vars;

	private Map<Class<?>, TriggerSource> triggerSources;

	protected Game game;

	private String currentScenePath;

	protected Scene currentScene;

	private SceneActor currentSceneActor;

	private Array<SceneTask> tasks;

	public GameController(Assets assets, SchemaIO schemaIO, Factory factory,
			SceneView sceneView) {
		this.sceneView = sceneView;
		this.assets = assets;
		this.schemaIO = schemaIO;
		this.factory = factory;
		this.vars = new VarsContext();
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
	 * Loads the game from the path set in assets
	 */
	public boolean loadGame() {
		FileHandle gameFile = assets.resolve("game.json");
		if (gameFile.exists()) {
			game = schemaIO.fromJson(Game.class, gameFile);
			vars.registerVariables(game.getVariables());
			loadScene(game.getInitialScene());
			return true;
		} else {
			Gdx.app.error("SceneManager",
					"game.json doesn't exist. Game not loaded.");
			return false;
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

		FileHandle sceneFile = assets.resolve(name);
		if (sceneFile.exists()) {
			currentScenePath = name;
			Scene scene = schemaIO.fromJson(Scene.class, sceneFile);
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
	 * 
	 * @return returns the current vars
	 */
	public VarsContext getVars() {
		return vars;
	}

	@Override
	public void act(float delta) {
		for (TriggerSource triggerSource : triggerSources.values()) {
			triggerSource.act(delta);
		}
		if (isLoading()) {
			boolean done = assets.update(slotTime);
			if (done) {
				executeTasks();
			}
		} else if (tasks.size > 0) {
			executeTasks();
		}
	}

	/**
	 * Sets a scene. All the assets required by the scene must be already
	 * loaded. This method is for internal usage only. Use
	 * {@link GameController#loadScene(String)} to load a scene
	 * 
	 * @param scene
	 *            the scene schema object
	 */
	protected void setScene(Scene scene) {
		currentScene = scene;

		if (currentSceneActor != null) {
			currentSceneActor.free();
		}
		currentSceneActor = factory.getEngineObject(currentScene);
		sceneView.setScene(currentSceneActor);
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
	 * {@link GameController#loadSceneElement(es.eucm.ead.schema.actors.SceneElement)}
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
		return assets.isLoading();
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
	 * Interface for tasks to execute once the scene manager is done loading. We
	 * need these tasks to separate the loading phase from the initialization
	 * phase in actors. We can't initialize an actor until all its resources are
	 * loaded (e.g. a image), so we queue all its assets in the load phase (
	 * {@link GameController#loadScene(String)} and
	 * {@link GameController#loadSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * ) and once all assets are loaded (through
	 * {@link GameController#act(float)} we go to the initialization phase (
	 * {@link GameController#setScene(es.eucm.ead.schema.actors.Scene)} and (
	 * {@link GameController#addSceneElement(es.eucm.ead.schema.actors.SceneElement)}
	 * ), and the new actors appear.
	 */
	public interface SceneTask {
		/**
		 * Executes the task
		 * 
		 * @param sceneManager
		 *            the scene manager
		 */
		void execute(GameController sceneManager);
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
		public void execute(GameController sceneManager) {
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
		public void execute(GameController sceneManager) {
			sceneManager.addSceneElement(sceneElement);
		}
	}
}
