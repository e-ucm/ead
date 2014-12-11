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
package es.eucm.ead.engine.mock;

import com.badlogic.gdx.Input.Buttons;
import es.eucm.ead.engine.EngineApplicationListener;
import es.eucm.ead.engine.GameLoop;

public class MockGame {

	private EngineApplicationListener engineApplicationListener;

	private MockApplication application;

	public GameLoop getGameLoop() {
		return engineApplicationListener.getGameLoop();
	}

	public MockGame() {
		this("testgame");
	}

	public MockGame(String path) {
		engineApplicationListener = new EngineApplicationListener(
				new MockImageUtils());
		application = new MockApplication(engineApplicationListener, 800, 600);
		application.start();
		engineApplicationListener.getGameLoader().loadGame(path, true);
	}

	public void act() {
		act(1);
	}

	public void act(int loops) {
		for (int i = 0; i < loops; i++) {
			application.act();
			engineApplicationListener.getGameAssets().finishLoading();
		}
	}

	/**
	 * Generate a press event in the given screen coordinates
	 */
	public void press(int x, int y) {
		engineApplicationListener.getStage().touchDown(x, y, 0, Buttons.LEFT);
	}

	/**
	 * Generate a release event in the given screen coordinates
	 */
	public void release(int x, int y) {
		engineApplicationListener.getStage().touchUp(x, y, 0, Buttons.LEFT);
	}

	/**
	 * Generates a click event in the given screen coordinates
	 */
	public void click(int x, int y) {
		press(x, y);
		release(x, y);
	}

	public MockApplication getApplication() {
		return application;
	}

	public EngineApplicationListener getEngineApplicationListener() {
		return engineApplicationListener;
	}
}
