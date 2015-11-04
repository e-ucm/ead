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
package es.eucm.ead.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import es.eucm.ead.engine.assets.GameAssets.ImageUtils;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.expressions.operators.OperationsFactory;
import es.eucm.ead.engine.systems.gamestatepersistence.PersistentGameStateSystem;
import es.eucm.ead.engine.variables.VariablesManager;

import java.util.HashMap;

public class EngineApplicationListener implements ApplicationListener {

	protected Stage stage;

	private GameLoop gameLoop;

	private GameAssets gameAssets;

	private ComponentLoader componentLoader;

	private GameLoader gameLoader;

	private DefaultGameView gameView;

	protected ImageUtils imageUtils;

	private PersistentGameStateSystem persistentGameStateSystem;

	public EngineApplicationListener(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}

	@Override
	public void create() {
		// OpenGL settings
		ShaderProgram.pedantic = false;
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		gameLoop = new GameLoop();
		gameView = new DefaultGameView(gameLoop);

		gameAssets = new GameAssets(Gdx.files, imageUtils);

		Accessor accessor = new Accessor(new HashMap<String, Object>());
		OperationsFactory operationsFactory = new OperationsFactory(gameLoop,
				accessor, gameView);
		VariablesManager variablesManager = new VariablesManager(accessor,
				operationsFactory);
		persistentGameStateSystem = new PersistentGameStateSystem(
				variablesManager);
		persistentGameStateSystem.read();

		componentLoader = new ComponentLoader(gameAssets, variablesManager);

		accessor.setComponentLoader(componentLoader);

		EntitiesLoader entitiesLoader = new EntitiesLoader(gameLoop,
				gameAssets, componentLoader);

		gameLoader = new GameLoader(gameAssets, entitiesLoader);

		DefaultEngineInitializer initializer = new DefaultEngineInitializer();
		initializer.init(gameAssets, gameLoop, entitiesLoader, gameView,
				variablesManager, persistentGameStateSystem);

		stage = createStage();
		Gdx.input.setInputProcessor(stage);
		stage.getRoot().addActor(gameView);
		stage.setKeyboardFocus(gameView);
	}

	/**
	 * Creates a Stage as in {@link Stage#Stage()}, but using
	 * {@link PolygonSpriteBatch} instead of {@link SpriteBatch}
	 * 
	 * PolygonSpriteBatch must be used for
	 * {@link es.eucm.ead.engine.components.renderers.SpineActor} to work
	 * properly.
	 */
	protected Stage createStage() {
		Viewport viewport = new ScalingViewport(Scaling.stretch,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				new OrthographicCamera());
		PolygonSpriteBatch batch = new PolygonSpriteBatch();
		Stage newStage = new Stage(viewport, batch);
		return newStage;
	}

	public GameLoop getGameLoop() {
		return gameLoop;
	}

	public GameAssets getGameAssets() {
		return gameAssets;
	}

	public GameLoader getGameLoader() {
		return gameLoader;
	}

	public DefaultGameView getGameView() {
		return gameView;
	}

	/**
	 * @return root stage for the whole game
	 */
	public Stage getStage() {
		return stage;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Load pending resources (if any) from disk
		gameAssets.update();
		// Process input
		stage.act();
		// Process systems
		gameLoop.update(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void pause() {
		persistentGameStateSystem.save();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
