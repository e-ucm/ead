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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import es.eucm.ead.engine.actions.AbstractVideoAction;
import es.eucm.ead.engine.io.SchemaIO;
import es.eucm.ead.engine.scene.SceneManager;
import es.eucm.ead.engine.triggers.TouchSource;

public class Engine implements ApplicationListener {

	public static EngineStage stage;
	public static Assets assets;
	public static SceneManager sceneManager;
	public static Engine engine;
	public static Factory factory;
	public static SchemaIO schemaIO;
	public static VarsContext vars;

	private EventListener eventListener;
	private String path;
	private boolean internal;

	public Engine() {

	}

	public Engine(String path, boolean internal) {
		setLoadingPath(path, internal);
	}

	public EventListener getEventListener() {
		return eventListener;
	}

	public void setLoadingPath(String path, boolean internal) {
		this.path = path;
		this.internal = internal;
		if (assets != null) {
			assets.setGamePath(path, internal);
		}
	}

	@Override
	public void create() {
		// OpenGL settings
		ShaderProgram.pedantic = false;
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		// VarsContext
		vars = new VarsContext();

		// Set global statics
		engine = this;
		factory = createFactory();

		assets = new Assets(Gdx.files);
		assets.setGamePath(path, internal);

		schemaIO = createJsonIO();
		sceneManager = createSceneManager(assets);

		eventListener = createEventListener();
		stage = createStage();
		Gdx.input.setInputProcessor(stage);

		loadBindings();

		// Start
		sceneManager.loadGame();
	}

	public boolean loadBindings() {
		BindingsLoader bindingsLoader = new BindingsLoader();
		bindingsLoader.addBindingListener(factory);
		bindingsLoader.addBindingListener(schemaIO);
		return bindingsLoader.load(assets.resolve("bindings.json"));
	}

	// Method to override if desired
	protected Factory createFactory() {
		return new Factory();
	}

	protected SceneManager createSceneManager(Assets assets) {
		return new SceneManager(assets);
	}

	protected EngineStage createStage() {
		return new EngineStage(Gdx.graphics.getWidth(), Gdx.graphics
				.getHeight(), true);
	}

	protected EventListener createEventListener() {
		return new TouchSource();
	}

	protected SchemaIO createJsonIO() {
		return new SchemaIO();
	}

	@Override
	public void resize(int width, int height) {
		stage.resize(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		sceneManager.act();
		if (!sceneManager.isLoading()) {
			stage.act();
			stage.draw();
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
