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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.mockup.core.control.handlers.ScreenHandler;
import es.eucm.ead.mockup.core.model.Screen;

/**
 * Controlls the handlers, the input...
 */
public class EventController extends InputAdapter {

	private ScreenHandler currentCtr;
	private InputMultiplexer multiplexer;

	public EventController() {

	}

	public void create() {
		this.multiplexer = new InputMultiplexer(Screen.stage, this,
				Engine.stage);
		Gdx.input.setInputProcessor(this.multiplexer);
	}

	/**
	 * Updates the current renderer.
	 * @param delta Elapsed time since the game last updated.
	 */
	public void act(float delta) {
		this.currentCtr.act(delta);
	}

	/**
	 * Changes the current handler to the next one.
	 * Triggers the hide() and show() events.
	 * 
	 * @param next The next handler.
	 */
	public void changeTo(ScreenHandler next) {

		if (currentCtr != null) {
			this.currentCtr.hide();
		}
		this.currentCtr = next;
		this.currentCtr.show();
	}

	public void pause() {
		this.currentCtr.pause();
	}

	public void resume() {
		this.currentCtr.resume();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (isBack(keycode)) {
			this.currentCtr.onBackKeyPressed();
		}
		return true;
	}

	private boolean isBack(int keycode) {
		return keycode == Keys.BACK || keycode == Keys.BACKSPACE;
	}
}
