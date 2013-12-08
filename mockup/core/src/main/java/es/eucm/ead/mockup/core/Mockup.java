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
package es.eucm.ead.mockup.core;

import static es.eucm.ead.mockup.core.screens.BaseScreen.*;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import es.eucm.ead.mockup.core.facade.IActionResolver;
import es.eucm.ead.mockup.core.screens.BaseScreen;
import es.eucm.ead.mockup.core.screens.Loading;
import es.eucm.ead.mockup.core.screens.Menu;
import es.eucm.ead.mockup.core.screens.TransitionScreen;

public class Mockup implements ApplicationListener {

	/**
	 * Handles transitions and other stuff like 
	 * screen event triggering and
	 * dynamic loading/unloading (TODO part)
	 */
	private TransitionScreen transitionScreen;

	/**
	 * Reference to the actual state.
	 */
	public BaseScreen showingScreen;

	/***
	 * Game States
	 */
	private Loading loading;
	public Menu menu;

	private IActionResolver resolver;

	public Mockup(IActionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);

		// Base screen
		mockup = this;
		sb = new SpriteBatch(35);
		sb.setProjectionMatrix(camera.combined);

		am = new AssetManager();
		BaseScreen.resolver = this.resolver;

		//Screens
		this.menu = new Menu();
		this.loading = new Loading();
		this.loading.create();
		this.showingScreen = loading;

		this.transitionScreen = new TransitionScreen();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		this.showingScreen.render(Gdx.graphics.getDeltaTime());

		this.showingScreen.draw();
	}

	@Override
	public void pause() {
		this.showingScreen.pause();
	}

	@Override
	public void resume() {
		this.showingScreen.resume();
	}

	@Override
	public void dispose() {
		disposeStatics();
	}

	public void setScreen(BaseScreen nextScreen) {
		this.showingScreen = this.transitionScreen.initializer(nextScreen);
	}
}
