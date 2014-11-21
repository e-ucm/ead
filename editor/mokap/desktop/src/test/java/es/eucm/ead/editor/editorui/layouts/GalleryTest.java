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
package es.eucm.ead.editor.editorui.layouts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.engine.I18N;

public class GalleryTest extends UITest {

	private Gallery gallery;

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {
		gallery = new Gallery(Gdx.graphics.getHeight() / 2.25f, 3, skin);

		for (int i = 0; i < 3; i++) {
			addItem();
		}

		Timer.schedule(new Task() {
			@Override
			public void run() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						addItem();
					}
				});
			}
		}, 1, 1, 50);
		return gallery;
	}

	private void addItem() {
		Pixmap pixmap = new Pixmap(200, 200, Format.RGB888);
		pixmap.setColor(new Color((float) Math.random(), (float) Math.random(),
				(float) Math.random(), 1.0f));
		pixmap.fill();
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		gallery.add(new Image(new TextureDrawable(texture)));
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new GalleryTest(), config);
	}
}
