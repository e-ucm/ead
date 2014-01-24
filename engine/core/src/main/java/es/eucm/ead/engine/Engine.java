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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import es.eucm.ead.engine.io.SchemaIO;

public class Engine implements ApplicationListener {

	// -- Engine components
	public static Assets assets;
	public static I18N i18n;
	public static Factory factory;
	public static SchemaIO schemaIO;
	public static GameController gameController;
	public static SceneView sceneView;
	public static Stage stage;

	public Engine() {

	}

	/**
	 * Loads the game in the given path. This method will fail if the libgdx
	 * application has not been initialized
	 * 
	 * @param path
	 *            the path where the game is
	 * @param internal
	 *            if the path is internal or absolute
	 */
	public void setLoadingPath(final String path, final boolean internal) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				assets.setGamePath(path, internal);
				gameController.loadGame();
			}
		});
	}

	@Override
	public void create() {
		// OpenGL settings
		ShaderProgram.pedantic = false;
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		assets = new Assets(Gdx.files);
		i18n = new I18N(assets);
		factory = new Factory();
		schemaIO = new SchemaIO(assets, factory);
		// Load bindings
		FileHandle fileHandle = assets.resolve("bindings.json");
		factory.loadBindings(fileHandle);
		schemaIO.loadAlias(fileHandle);
		sceneView = new SceneView(factory);
		gameController = new EngineGameController(assets, schemaIO, sceneView);

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				true);
		stage.addActor(sceneView);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gameController.act(Gdx.graphics.getDeltaTime());
		stage.act();
		stage.draw();
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
