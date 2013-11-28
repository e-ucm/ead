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
package es.eucm.ead.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.core.io.JsonIO;
import es.eucm.ead.core.listeners.SceneElementInputListener;
import es.eucm.ead.core.scene.loaders.SceneLoader;
import es.eucm.ead.core.scene.SceneManager;
import es.eucm.ead.core.scene.loaders.TextLoader;
import es.eucm.ead.schema.actors.Scene;

public class EAdEngine implements ApplicationListener {

	public static EngineStage stage;
	public static AssetManager assetManager;
	public static SceneManager sceneManager;
	public static EAdEngine engine;
	public static Factory factory;
	public static JsonIO jsonIO;

	private FileResolver fileResolver;
	private EventListener eventListener;

	private Array<BindListener> bindListeners;

	public EAdEngine(String path) {
		bindListeners = new Array<BindListener>();
		fileResolver = new FileResolver();
		setLoadingPath(path);
	}

	public EventListener getEventListener() {
		return eventListener;
	}

	public void setLoadingPath(String path) {
		fileResolver.setPath(path);
	}

	public void addBindListener(BindListener bindListener) {
		bindListeners.add(bindListener);
	}

	@Override
	public void create() {
		// OpenGL settings
		ShaderProgram.pedantic = false;
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		// Set global statics
		engine = this;
		factory = createFactory();

		assetManager = new AssetManager(fileResolver);
		addAssetLoaders(assetManager, fileResolver);

		jsonIO = createJsonIO(fileResolver);
		sceneManager = createSceneManager(assetManager);

		stage = createStage();
		Gdx.input.setInputProcessor(stage);

		addBindListener(factory);
		addBindListener(jsonIO);
		loadBinds();

		eventListener = createEventListener();
		// Start
		sceneManager.loadGame();
	}

	/**
	 * Add asset loaders to load new assets
	 * 
	 * @param assetManager
	 *            the asset manager
	 * @param fileResolver
	 *            the file resolver used by the asset manager
	 */
	private void addAssetLoaders(AssetManager assetManager,
			FileResolver fileResolver) {
		// Scene Loader
		assetManager.setLoader(Scene.class, new SceneLoader(fileResolver));
		// Text loader
		assetManager.setLoader(String.class, new TextLoader(fileResolver));
	}

	@SuppressWarnings("all")
	/**
	 * Loads all the classes binds
	 */
	public void loadBinds() {
		Json json = new Json();
		Array<Array<String>> binds = json.fromJson(Array.class, Gdx.files
				.internal("binds.json"));
		String schemaPackage = "";
		String corePackage = "";
		for (Array<String> entry : binds) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
				corePackage = entry.size == 1 ? null : entry.get(1);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					Class coreClass = corePackage == null ? null
							: ClassReflection.forName(corePackage + "."
									+ entry.get(1));
					bind(entry.get(0).toLowerCase(), schemaClass, coreClass);
				} catch (ReflectionException e) {
					Gdx.app.error("LoadBinds", "Error loading binds", e);
				}
			}
		}
	}

	/** Call bind listeners **/
	private void bind(String alias, Class schemaClass, Class coreClass) {
		for (BindListener bindListener : bindListeners) {
			bindListener.bind(alias, schemaClass, coreClass);
		}
	}

	// Method to override if desired
	protected Factory createFactory() {
		return new Factory();
	}

	protected SceneManager createSceneManager(AssetManager assetManager) {
		return new SceneManager(assetManager);
	}

	protected EngineStage createStage() {
		return new EngineStage(Gdx.graphics.getWidth(), Gdx.graphics
				.getHeight(), true);
	}

	protected EventListener createEventListener() {
		return new SceneElementInputListener();
	}

	protected JsonIO createJsonIO(FileResolver fileResolver) {
		return new JsonIO();
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

	public interface BindListener {
		void bind(String alias, Class schemaClass, Class coreClass);
	}
}
