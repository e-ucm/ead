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
package es.eucm.ead.mockup.core.control.handlers;

import es.eucm.ead.mockup.core.model.Screen;
import es.eucm.ead.mockup.core.model.Screens;

/**
 * It's responsible for updating the screen.
 * 
 * TEMPORAL: Also handles other events like touchDown, touchDragged or 
 * keyPressed(to handle Keys.Back button event on devices).
 */
public class ScreenHandler extends Screen {
	
	/**
	 * Used to go to navigate to the previous screen when the
	 * Kays.Back button is pressed.
	 * (When {onBackKeyPressed()} is triggered) 
	 */
	private Screens previousScreen;

	/**
	 * Updates the screen.
	 * @param delta elapsed time since the last time.
	 */
	public void act(float delta) {

	}

	public void pause() {

	}

	public void resume() {

	}

	/**
	 * previousScreen must be configured or onBackKeyPressed() will throw an IllegalStateException.
	 * You could also override {onBackKeyPressed()} method instead.
	 * 
	 * @param previousScreen
	 */
	protected void setPreviousScreen(Screens previousScreen) {
		this.previousScreen = previousScreen;
	}

	/**
	 * Executed when BACK key was pressed.
	 * If previousScreen is null an {@link IllegalStateException IllegalStateException} will be thrown.
	 */
	public void onBackKeyPressed() {
		if (previousScreen == null) {
			throw new IllegalStateException(
					"previousScreen is null in "
							+ this.getClass().getSimpleName()
							+ " please configure previousScreen via {setPreviousScreen(Screens previousScreen)} method"
							+ " or @Override {onBackKeyPressed()}");
		}
		mockupController.changeTo(previousScreen);
	}
}
