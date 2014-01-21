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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import es.eucm.ead.mockup.core.control.MockupController;

public class Mockup implements ApplicationListener {

	private MockupController c;

	@Override
	public void create() {
		this.c = new MockupController();
	}

	@Override
	public void resize(int width, int height) {
		this.c.resize(width, height);
	}

	@Override
	public void render() {
		this.c.act(Gdx.graphics.getDeltaTime());
		this.c.draw();
	}

	@Override
	public void pause() {
		this.c.pause();
	}

	@Override
	public void resume() {
		this.c.resume();
	}

	@Override
	public void dispose() {
		this.c.dispose();
	}
}
