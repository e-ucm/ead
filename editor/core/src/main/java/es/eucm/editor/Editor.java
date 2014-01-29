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
package es.eucm.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.I18N;
import es.eucm.editor.control.ViewController;
import es.eucm.ead.editor.platform.Platform;

public class Editor implements ApplicationListener {

	public static Assets assets;
	public static Stage stage;
	public static Platform platform;
	public static Preferences preferences;
	public static I18N i18n;
	public static ViewController viewController;
	public static Controller controller;

	public Editor(Platform platform) {
		Editor.platform = platform;
	}

	@Override
	public void create() {
		// OpenGL settings
		ShaderProgram.pedantic = false;
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		assets = new Assets(Gdx.files);
		assets.setGamePath(
				"/home/eva/repositories/ead/engine/desktop/src/test/resources/techdemo",
				false);
		i18n = new I18N(assets);

		// Load preferences
		preferences = new Preferences(assets);

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				false);

		// Load UI
		viewController = new ViewController(assets, stage.getRoot(), i18n);
		viewController.showView("game");

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		ScissorStack.getViewport();
		Table.drawDebug(stage);
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
