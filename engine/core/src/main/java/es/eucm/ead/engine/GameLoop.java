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
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.triggers.TimeSource;
import es.eucm.ead.engine.triggers.TouchSource;
import es.eucm.ead.engine.triggers.TriggerSource;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.actors.hud.Hud;
import es.eucm.ead.schema.behaviors.Time;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Trigger;
import es.eucm.ead.schema.effects.Effect;
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

	private Stack<String> subgamePaths;

	private String currentHud;

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
		subgamePaths = new Stack<String>();
		registerTriggerProducers();
	}

	private void reset() {
		subgamePaths.clear();
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
	public void registerForTrigger(SceneElementEngineObject actor, Trigger event) {
		TriggerSource triggerSource = triggerSources.get(event.getClass());
		if (triggerSource == null) {
			Gdx.app.error("EngineState", "No trigger source found for class "
					+ event.getClass());
		} else {
			triggerSource.registerForTrigger(actor, event);
		}
	}

	@Override
	public void unregisterForAllTriggers(SceneElementEngineObject actor) {
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
		assets.setLoadingPath(path, internal);
		// Start root game
		startSubgame(null, null);
	}

	/**
	 * Loads a subgame
	 * 
	 * @param name
	 *            the name of the subgame
	 * @param effects
	 *            the effects to execute when the subgame ends. The parent for
	 *            the effects will be the root container. Can be null
	 */
	public void startSubgame(String name, List<Effect> effects) {
		if (name != null) {
			String subGamePath = assets.convertSubgameNameToPath(name);
			addSubgamePath(subGamePath);
		} else {
			reset();
		}
		GameState nextGameState = new GameState();
		// If it is not the root game, store the game state
		if (currentGameState != null) {
			if (effects != null) {
				currentGameState.setPostEffects(effects);
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
	 * Ends the current sub game and execute its associated effects, set through
	 * {@link GameLoop#startSubgame(String, java.util.List)}
	 */
	public void endSubgame() {
		if (popSubgamePath()) {
			Gdx.app.exit();
		} else {
			GameState previousGameState = gameStates.pop();
			currentGameState.getVarsContext().copyGlobalsTo(
					previousGameState.getVarsContext());
			// Execute waiting post effects
			for (Effect a : previousGameState.getPostEffects()) {
				sceneView.addEffect(a);
			}
			currentGameState = previousGameState;
			loadGame();
		}
	}

	/**
	 * Adds subgame path to load resources
	 * 
	 * @param subgamePath
	 *            the path
	 */
	public void addSubgamePath(String subgamePath) {
		if (!subgamePath.endsWith("/")) {
			subgamePath += "/";
		}
		subgamePaths.add(subgamePath);
		assets.setLoadingPath(assets.getLoadingPath() + subgamePath);
	}

	/**
	 * Pops a path of a subgame
	 * 
	 * @return returns true if the game popped is the root game
	 */
	public boolean popSubgamePath() {
		if (!subgamePaths.isEmpty()) {
			String loadingPath = assets.getLoadingPath();
			String subgamePath = subgamePaths.pop();
			assets.setLoadingPath(loadingPath.substring(0, loadingPath.length()
					- subgamePath.length()));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Enqueue the current game to be loaded
	 */
	private void loadGame() {
		assets.loadGame(this);
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
					game.getVariablesDefinitions());
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
	 *            the scene's name
	 */
	public void loadScene(String name) {
		assets.loadScene(name, this);
		currentGameState.setCurrentScene(name);
	}

	private void setScene(Scene scene) {
		sceneView.setScene(scene);
		String hud = scene.getHud();
		if (hud == null) {
			sceneView.setHud(null);
		}
		if (hud != null && !hud.equals(currentHud)) {
			sceneView.setHud(assets.get(assets.convertHudNameToPath(hud),
					Hud.class));
		}
		currentHud = hud;
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
	public boolean removeSceneElement(SceneElementEngineObject actor) {
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
