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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;

public class MockupApplicationListener extends EditorApplicationListener {

	public MockupApplicationListener(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		super.create();
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
	}

	@Override
	public void resize(int width, int height) {
		super.stage.getViewport().update(width, height, true);
	}

	@Override
	protected void initialize() {
		controller.action(ChangeView.class, ProjectsView.class);
	}

	@Override
	protected Stage createStage() {
		final Vector2 viewport = super.platform.getSize();
		return new Stage(new ExtendViewport(viewport.x, viewport.y));
	}

	@Override
	protected Controller createController() {
		return new MockupController(this.platform, Gdx.files,
				super.stage.getRoot());
	}
}
