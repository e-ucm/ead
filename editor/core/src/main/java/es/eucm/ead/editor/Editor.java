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
package es.eucm.ead.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ShowView;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;

/**
 * Base class for all platform-dependent editors.
 */
public class Editor implements ApplicationListener {

	private static final Color DEFAULT_BACKGROUND_COLOR = new Color(0.2f, 0.2f,
			0.2f, 1.0f);

	/**
	 * Platform-dependent functionality needed in other editor's components.
	 */
	protected Platform platform;

	/**
	 * LibGDX component that represents the whole editor's window.
	 */
	protected Stage stage;

	/**
	 * Mediator and Controller used to manage the editor's functionality.
	 */
	protected Controller controller;

	public Editor(Platform platform) {
		this.platform = platform;
	}

	@Override
	public void create() {
		Gdx.gl.glClearColor(DEFAULT_BACKGROUND_COLOR.r,
				DEFAULT_BACKGROUND_COLOR.g, DEFAULT_BACKGROUND_COLOR.b,
				DEFAULT_BACKGROUND_COLOR.a);
		stage = createStage();
		controller = createController();
		Gdx.input.setInputProcessor(stage);
		initialize();
	}

	protected Stage createStage() {
		return new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				false);
	}

	protected Controller createController() {
		return new Controller(platform, Gdx.files, stage.getRoot());
	}

	protected void initialize() {
		platform.setTitle(controller.getApplicationAssets().getI18N()
				.m("application.title", ""));
		controller.action(ShowView.class, MainBuilder.NAME);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		controller.getEditorGameAssets().update();
		controller.getApplicationAssets().update();
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
