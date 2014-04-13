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

import javax.swing.JFrame;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.engine.utils.SwingEDTUtils;

public class MockupMain {

	public static void main(String[] args) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 854;
		config.height = 480;
		config.forceExit = true;
		config.title = "eAdventure Mockup";

		final DesktopPlatform platform = new DesktopPlatform() {
			private final Vector2 screenDimensions = new Vector2(960, 600);

			@Override
			public Vector2 getSize() {
				return this.screenDimensions;
			}

			@Override
			public void askForFile(final FileChooserListener listener) {
				Gdx.input.getTextInput(new TextInputListener() {

					@Override
					public void input(String text) {
						listener.fileChosen(text);
					}

					@Override
					public void canceled() {
					}

				}, "File path!", "");
			}
		};
		final LwjglFrame frame = new LwjglFrame(new Editor(platform) {

			@Override
			protected void initialize() {
				super.controller.action(ChangeView.class, InitialScreen.NAME);
			}

			@Override
			public void render() {
				super.render();
				Table.drawDebug(super.stage);
			}

			@Override
			public void resize(int width, int height) {
				super.stage.getViewport().update(width, height, true);
			}

			@Override
			protected Controller createController() {
				return new Controller(super.platform, Gdx.files,
						super.stage.getRoot()) {
					@Override
					protected ApplicationAssets createApplicationAssets(
							Files files) {
						return new ApplicationAssets(files, "mockup");
					}
				};
			}

			@Override
			protected Stage createStage() {
				final Vector2 viewport = super.platform.getSize();
				return new Stage(new ExtendViewport(viewport.x, viewport.y));
			}
		}, config);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		platform.setFrame(frame);

		// set visible calls create()
		SwingEDTUtils.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
