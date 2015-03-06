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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.engine.mock.MockFiles;

public abstract class AbstractWidgetTest extends EditorTest implements
		ApplicationListener {

	private Stage stage;
	private AbstractWidget widget;
	protected Array<String> statusMessages = new Array<String>();
	protected BitmapFont statusFont;
	private boolean fillWindow;

	public void setFillWindow(boolean fillWindow) {
		this.fillWindow = fillWindow;
	}

	@Override
	public void create() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		platform = new MockPlatform();
		controller = new Controller(platform, new MockFiles(), new Group(),
				new Group());
		model = controller.getModel();
		stage = new Stage(new ScreenViewport());
		widget = createWidget(controller);
		stage.addActor(widget);
		Gdx.input.setInputProcessor(stage);

		statusFont = new BitmapFont();
		statusFont.setColor(Color.LIGHT_GRAY);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		if (fillWindow) {
			widget.setBounds(0, 0, width, height);
		} else {
			float wWidth = Math.min(width,
					Math.max(widget.getPrefWidth(), widget.getWidth()));
			float wHeight = Math.min(height,
					Math.max(widget.getPrefHeight(), widget.getHeight()));
			widget.setBounds(width / 2 - wWidth / 2, height / 2 - wHeight / 2,
					wWidth, wHeight);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		controller.act(Gdx.graphics.getDeltaTime());
		stage.act();
		stage.draw();
		float x = 10;
		float y = stage.getHeight();
		SpriteBatch batch = (SpriteBatch) stage.getBatch();
		batch.begin();
		for (String s : statusMessages) {
			statusFont.draw(batch, s, x, y);
			y -= 14;
		}
		batch.end();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		statusFont.dispose();
		stage.dispose();
	}

	public abstract AbstractWidget createWidget(Controller controller);
}
