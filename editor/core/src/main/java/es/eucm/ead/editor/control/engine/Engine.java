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
package es.eucm.ead.editor.control.engine;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.processors.EditorImageProcessor;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.DefaultEngineInitializer;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.Layer;

/**
 * Contains an instance of the engine
 */
public class Engine {

	private EntitiesLoader entitiesLoader;

	private GameLoop gameLoop;

	private FacadeGameView gameView;

	private GameLoader gameLoader;

	public Engine(Controller controller) {
		this.gameLoop = new GameLoop();
		gameLoop.setPlaying(false);
		this.gameView = new FacadeGameView();

		EditorGameAssets editorGameAssets = controller.getEditorGameAssets();

		ComponentLoader componentLoader = new ComponentLoader(editorGameAssets);
		VariablesManager variablesManager = new VariablesManager(gameLoop,
				componentLoader, gameView);
		this.entitiesLoader = new EntitiesLoader(gameLoop, editorGameAssets,
				componentLoader);
		gameLoader = new GameLoader(editorGameAssets, entitiesLoader, gameView,
				variablesManager);

		DefaultEngineInitializer initializer = new DefaultEngineInitializer();
		initializer.init(editorGameAssets, gameLoop, entitiesLoader, gameView,
				variablesManager);

		componentLoader.registerComponentProcessor(
				Image.class,
				new EditorImageProcessor(gameLoop, editorGameAssets, controller
						.getShapeRenderer()));
	}

	public EntitiesLoader getEntitiesLoader() {
		return entitiesLoader;
	}

	public GameLoop getGameLoop() {
		return gameLoop;
	}

	public GameLoader getGameLoader() {
		return gameLoader;
	}

	/**
	 * Sets the current game view. Changes affecting game view in the engine
	 * will effect the passed argument
	 */
	public void setGameView(GameView gameView) {
		this.gameView.setGameView(gameView);
	}

	/**
	 * Updates the engine
	 */
	public void update(float delta) {
		gameLoop.update(delta);
	}

	/**
	 * Plays the engine, with the given game
	 */
	public void play(ModelEntity game) {
		for (Layer layer : Layer.values()) {
			gameView.clearLayer(layer, true);
		}
		gameLoader.loadGame(game);
		gameLoop.setPlaying(true);
	}

	/**
	 * Stops the engine
	 */
	public void stop() {
		gameLoop.setPlaying(false);
	}
}
