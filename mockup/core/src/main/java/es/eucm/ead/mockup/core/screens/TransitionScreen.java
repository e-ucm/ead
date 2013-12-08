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
/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.ead.mockup.core.screens;

/**
 * 
 * Helper class that handles the transition between screens.
 *
 */
public class TransitionScreen extends BaseScreen {

	public BaseScreen actualScreen, nextScreen;

	private float elapsedTime;
	private boolean backTrack;

	private final float HALF_TRANSITION_TIME = 0f;

	public TransitionScreen() {
		actualScreen = mockup.showingScreen;
	}

	public BaseScreen initializer(BaseScreen nextScreen) {
		this.actualScreen.hide();
		this.nextScreen = nextScreen;
		this.elapsedTime = HALF_TRANSITION_TIME;
		this.backTrack = false;
		return this;
	}

	@Override
	public void render(float delta) {
		clearColor();
		if (this.backTrack) {
			if (this.elapsedTime >= HALF_TRANSITION_TIME) {
				mockup.showingScreen = this.nextScreen;
			} else {
				this.elapsedTime = this.elapsedTime + delta;
			}
		} else {
			if (elapsedTime <= 0) {
				this.elapsedTime = 0;
				this.backTrack = true;
				this.actualScreen.onHidden();
				this.actualScreen = this.nextScreen;
				this.actualScreen.show();
			} else {
				this.elapsedTime = this.elapsedTime - delta;
			}
		}
		stage.act(delta);
	}

	@Override
	public void draw() {
		this.actualScreen.draw();
	}
}
