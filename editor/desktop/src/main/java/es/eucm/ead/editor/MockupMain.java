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

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;

public class MockupMain {

	public static void main(String[] args) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 854;
		config.height = 480;
		config.useGL20 = true;
		config.title = "eAdventure Mockup";

		new LwjglApplication(new Editor(new DesktopPlatform() {
			private final Vector2 screenDimensions = new Vector2(960, 600);

			@Override
			public Vector2 getSize() {
				return this.screenDimensions;
			}
		}) {
			@Override
			protected void initialize() {
				super.controller.action(ChangeSkin.NAME, "mockup");
				super.controller.action(ChangeView.NAME, InitialScreen.NAME);
			}

			@Override
			public void render() {
				super.render();
				Table.drawDebug(super.stage);
			}

			@Override
			public void resize(int width, int height) {
				final Vector2 viewport = super.platform.getSize();
				super.stage.setViewport(viewport.x, viewport.y, true);
			}

			@Override
			protected Stage createStage() {
				final Vector2 viewport = super.platform.getSize();
				return new Stage(viewport.x, viewport.y, true);
			}
		}, config);
	}
}
