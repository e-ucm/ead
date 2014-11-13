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
package es.eucm.ead.editor.editorui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.I18N;

import java.io.File;
import java.io.IOException;

public class AsyncImageTest extends UITest {

	private File file;

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		try {
			file = File.createTempFile("image", "png");
			createImage();
			Image image = WidgetBuilder.asyncImage(file.toString());
			image.setFillParent(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
							createImage();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void createImage() {
		Pixmap pixmap = new Pixmap(200, 200, Format.RGBA8888);
		pixmap.setColor((float) Math.random(), (float) Math.random(),
				(float) Math.random(), 1.0f);
		pixmap.fill();
		PixmapIO.writePNG(
				controller.getApplicationAssets().absolute(
						file.getAbsolutePath()), pixmap);

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				controller.getEditorGameAssets().unload(file.toString());
				controller.getEditorGameAssets().load(file.toString(),
						Texture.class);
			}
		});
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 200;
		config.height = 200;
		new LwjglApplication(new AsyncImageTest(), config);
	}

}
