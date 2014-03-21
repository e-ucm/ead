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
package es.eucm.ead.editor.widgets;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.engine.mock.MockFiles;

public abstract class AbstractWidgetTest extends EditorTest implements
		ApplicationListener {

	private Stage stage;

	private AbstractWidget widget;

	private static final String SKIN = "skins/default/skin.json";

	@Override
	public void create() {
		mockPlatform = new MockPlatform();
		mockController = new Controller(mockPlatform, new MockFiles(),
				new Group());
		mockModel = mockController.getModel();
		stage = new Stage(new ScreenViewport());
		AssetManager assetManager = new AssetManager();
		assetManager.load(SKIN, Skin.class);
		assetManager.finishLoading();
		widget = createWidget(mockController);
		stage.addActor(widget);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		float wWidth = Math.max(widget.getWidth(),
				Math.min(widget.getPrefWidth(), width));
		float wHeight = Math.max(widget.getHeight(),
				Math.min(widget.getPrefHeight(), height));
		widget.setBounds(width / 2 - wWidth / 2, height / 2 - wHeight / 2,
				wWidth, wHeight);

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

	public abstract AbstractWidget createWidget(Controller controller);
}