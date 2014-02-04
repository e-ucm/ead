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
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.assets.GameLoader.GameParameter;
import es.eucm.ead.engine.assets.SceneLoader.SceneParameter;
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

/**
 * Manages the playing of a Game. Triggers events, keeps variable values and the
 * current scene, can load resources as needed.
 */
public class GameLoop implements TriggerSource, LoadedCallback {

	protected Assets assets;

	protected SceneView sceneView;

	private Map<Class<?>, TriggerSource> triggerSources;

	private Stack<GameState> gameStates;

	private GameState currentGameState;

	public GameLoop() {
		this(new Assets(Gdx.files));
	}

	public GameLoop(Assets assets) {
		this(assets, new SceneView(assets));
	}

	public GameLoop(Assets assets, SceneView sceneView) {
		this.sceneView = sceneView;
		this.assets = assets;
		assets.setGameLoop(this);
		this.gameStates = new Stack<GameState>();
		triggerSources = new LinkedHashMap<Class<?>, TriggerSource>();
		registerTriggerProducers();
	}

	private void reset() {
		gameStates.clear();
		currentGameState = null;
	}

	public Assets getAssets() {
		return assets;
	}

	public SceneView getSceneView() {
		return sceneView;
	}

	private void registerTriggerProducers() {
		registerTriggerProducer(Touch.class, new TouchSource());
		registerTriggerProducer(Time.class, new TimeSource());
	}

	protected <T extends Trigger> void registerTriggerProducer(Class<T> clazz,
			TriggerSource triggerSource) {
		triggerSources.put(clazz, triggerSource);
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
	 * Starts the game loop with the game in the given path
	 * 
	 * @param path
	 *            the path of the game
	 * @param internal
	 *            if the path is internal to the application
	 */
	public void runGame(String path, boolean internal) {
		assets.setGamePath(path, internal);
		// Start root game
		startSubgame(null, null);
	}

	/**
	 * Loads a subgame
	 * 
	 * @param name
	 *            the name of the subgame
	 * @param actions
	 *            the actions to execute when the subgame ends. The parent for
	 *            the actions will be the returning scene in the parent game.
	 *            Can be null
	 */
	public void startSubgame(String name, List<Action> actions) {
		if (name != null) {
			String subGamePath = assets.convertSubgameNameToPath(name);
			assets.addSubgamePath(subGamePath);
		} else {
			reset();
		}
		GameState nextGameState = new GameState();
		// If it is not the root game, store the game state
		if (currentGameState != null) {
			if (actions != null) {
				currentGameState.setPostActions(actions);
			}
			gameStates.push(currentGameState);
			// Copy global vars n¡to new game state
			currentGameState.getVarsContext().copyGlobalsTo(
					nextGameState.getVarsContext());
		}
		currentGameState = nextGameState;
		loadGame();
	}

	/**
	 * Ends the current sub game and execute its associated actions, set through
	 * {@link GameLoop#startSubgame(String, java.util.List)}
	 */
	public void endSubgame() {
		if (assets.popSubgamePath()) {
			Gdx.app.exit();
		} else {
			GameState previousGameState = gameStates.pop();
			currentGameState.getVarsContext().copyGlobalsTo(
					previousGameState.getVarsContext());
			// Execute waiting post actions
			for (Action a : previousGameState.getPostactions()) {
				sceneView.addAction(a);
			}
			currentGameState = previousGameState;
			loadGame();
		}
	}

	/**
	 * Enqueue the current game to be loaded
	 */
	private void loadGame() {
		if (assets.isLoaded("game.json", Game.class)) {
			setGame(assets.get("game.json", Game.class));
		} else {
			assets.load("game.json", Game.class, new GameParameter(this));
		}
	}

	/**
	 * Effectively sets the games, loading the first scene
	 * 
	 * @param game
	 *            the game
	 */
	protected void setGame(Game game) {
		// If a subgame is starting
		if (currentGameState.getCurrentScene() == null) {
			// Load initial variables
			currentGameState.getVarsContext().registerVariables(
					game.getVariables());
			currentGameState.setCurrentScene(game.getInitialScene());
		}

		// Load language
		String lang = currentGameState.getVarsContext().getValue(
				VarsContext.LANGUAGE_VAR);
		assets.getI18N().setLang(lang);

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
		String path = assets.convertSceneNameToPath(name);
		if (assets.isLoaded(path, Scene.class)) {
			setScene(assets.get(path, Scene.class));
		} else {
			assets.load(path, Scene.class, new SceneParameter(this));
		}
		currentGameState.setCurrentScene(name);
	}

	private void setScene(Scene scene) {
		sceneView.setScene(scene);
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
		if (assets.update()) {
			updateTriggerSources(delta);
		}
	}

	protected void updateTriggerSources(float delta) {
		for (TriggerSource triggerSource : triggerSources.values()) {
			triggerSource.act(delta);
		}
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

	public String getCurrentScene() {
		return currentGameState.getCurrentScene();
	}

	@Override
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == Game.class) {
			Game game = (Game) assetManager.get(fileName, type);
			setGame(game);
		} else if (type == Scene.class) {
			Scene scene = (Scene) assetManager.get(fileName, type);
			setScene(scene);
		}
	}
}
