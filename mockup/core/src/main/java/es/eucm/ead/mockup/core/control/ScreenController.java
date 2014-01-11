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
package es.eucm.ead.mockup.core.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.mockup.core.control.screens.AbstractScreen;

/**
 * Controlls the handlers, the input...
 */
public class ScreenController extends InputAdapter {

	/**
	 * Change this color to change the color wich the screen is cleared with.
	 */
	public static Color CLEAR_COLOR = Color.GREEN;

	private AbstractScreen currentScreen;
	private InputMultiplexer multiplexer;

	public void create() {
		this.multiplexer = new InputMultiplexer(AbstractScreen.stage, this,
				Engine.stage);
		Gdx.input.setInputProcessor(this.multiplexer);
	}

	/**
	 * Updates the current renderer.
	 * @param delta Elapsed time since the game last updated.
	 */
	public void act(float delta) {
		this.currentScreen.act(delta);
	}

	/**
	 * Clears the screen and draws the current renderer.
	 */
	public void draw() {
		clearColor();
		this.currentScreen.draw();
	}

	private void clearColor() {
		GLCommon gl = Gdx.gl20;
		gl.glClearColor(CLEAR_COLOR.r, CLEAR_COLOR.g, CLEAR_COLOR.b,
				CLEAR_COLOR.a);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Changes the current handler to the next one.
	 * Triggers the hide() and show() events.
	 * 
	 * @param next The next handler.
	 */
	public void changeTo(AbstractScreen next) {
		this.currentScreen.hide();
		this.currentScreen = next;
		this.currentScreen.show();
	}

	public void pause() {
		this.currentScreen.pause();
	}

	public void resume() {
		this.currentScreen.resume();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (isBack(keycode)) {
			this.currentScreen.onBackKeyPressed();
		}
		return true;
	}

	private boolean isBack(int keycode) {
		return keycode == Keys.BACK || keycode == Keys.BACKSPACE;
	}

	public void setCurrentScreen(AbstractScreen currentCtr) {
		this.currentScreen = currentCtr;
	}
}
