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

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.platform.Platform;

public class MockupApplicationListener extends EditorApplicationListener {

	private final Runnable clearColor = new Runnable() {

		@Override
		public void run() {
			Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		}
	};

	public MockupApplicationListener(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		super.create();
		Gdx.app.postRunnable(clearColor);

	}

	@Override
	public void resize(int width, int height) {
		super.stage.getViewport().update(width, height, true);
	}

	@Override
	protected void initialize() {
	}

	@Override
	public void pause() {
		((MockupController) controller).pause();
	}

	@Override
	public void resume() {
		super.resume();
		Gdx.app.postRunnable(clearColor);
	}

	protected Controller buildController() {
		return new MockupController(this.platform, Gdx.files,
				super.stage.getRoot());
	}
}
