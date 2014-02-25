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
package es.eucm.ead.editor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;

public class Mockup extends Editor {

	private static final int WIDTH = 1100;
	private static final int HEIGHT = 700;

	public Mockup(Platform platform) {
		super(platform);
	}

	@Override
	public void render() {
		super.render();
		Table.drawDebug(super.stage);
	}

	@Override
	public void resize(int width, int height) {
		super.stage.setViewport(WIDTH, HEIGHT, true);
	}

	@Override
	protected Stage createStage() {
		return new Stage(WIDTH, HEIGHT, true);
	}

	@Override
	protected void initialize() {
		super.controller.action(ChangeSkin.NAME, "mockup");
		super.controller.action(ChangeView.NAME, InitialScreen.NAME);
	}
}
