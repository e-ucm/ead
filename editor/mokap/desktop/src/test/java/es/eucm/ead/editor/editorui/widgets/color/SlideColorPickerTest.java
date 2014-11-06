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
package es.eucm.ead.editor.editorui.widgets.color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorEvent;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorListener;
import es.eucm.ead.engine.I18N;

public class SlideColorPickerTest extends UITest {

	private Color clearColor = new Color();

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		Table table = new Table() {
			@Override
			public void act(float delta) {
				super.act(delta);
				Gdx.gl20.glClearColor(clearColor.r, clearColor.g, clearColor.b,
						clearColor.a);
			}
		};
		table.setFillParent(true);

		SlideColorPicker slideColorPicker = new SlideColorPicker(skin);
		slideColorPicker.addListener(new ColorListener() {
			@Override
			public void colorChanged(ColorEvent event) {
				clearColor.set(event.getColor());
			}
		});
		slideColorPicker.initialize();
		clearColor.set(slideColorPicker.getPickedColor());
		table.add(slideColorPicker);

		return table;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 600;
		config.height = 300;
		config.title = "SlideColorPickerTest";
		new LwjglApplication(new SlideColorPickerTest(), config);
	}

}