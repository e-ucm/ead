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
package es.eucm.ead.editor.editorui.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.MockupController.Dpi;
import es.eucm.ead.editor.editorui.MockupUITest;
import es.eucm.ead.editor.view.widgets.HorizontalToolbar;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiHorizontalToolbar;
import es.eucm.ead.engine.I18N;

public class MultiHorizontalToolbarTest extends MockupUITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		Table table = new Table();
		table.setFillParent(true);

		final MultiHorizontalToolbar multiTop = new MultiHorizontalToolbar(
				skin, "toolbar_top", Color.CYAN);

		HorizontalToolbar topBar1 = new HorizontalToolbar(skin, 40, "white_top");
		topBar1.backgroundColor(Color.YELLOW);
		IconButton icon1 = new IconButton("menu", skin);
		topBar1.leftAdd(icon1);

		HorizontalToolbar topBar2 = new HorizontalToolbar(skin, 40, "white_top");
		topBar2.backgroundColor(Color.ORANGE);
		IconButton icon2 = new IconButton("menu", skin);
		topBar2.rightAdd(icon2);

		HorizontalToolbar topBar3 = new HorizontalToolbar(skin, 40, "white_top");
		topBar3.backgroundColor(Color.RED);
		IconButton icon3 = new IconButton("menu", skin);
		topBar3.leftAdd(icon3);

		final MultiHorizontalToolbar multiBot = new MultiHorizontalToolbar();

		HorizontalToolbar botBar1 = new HorizontalToolbar(skin, 40,
				"white_bottom");
		botBar1.backgroundColor(Color.BLUE);
		IconButton icon4 = new IconButton("menu", skin);
		botBar1.add(icon4);

		HorizontalToolbar botBar2 = new HorizontalToolbar(skin, 40,
				"white_bottom");
		botBar2.backgroundColor(Color.GREEN);
		IconButton icon5 = new IconButton("menu", skin);
		botBar2.add(icon5);

		HorizontalToolbar botBar3 = new HorizontalToolbar(skin, 40,
				"white_bottom");
		botBar3.backgroundColor(Color.GRAY);
		IconButton icon6 = new IconButton("menu", skin);
		botBar3.add(icon6);

		multiTop.addHorizontalToolbar(topBar1, topBar2, topBar3);
		multiBot.addHorizontalToolbar(botBar1, botBar2, botBar3);

		ClickListener topListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				HorizontalToolbar toolbar = (HorizontalToolbar) actor
						.getUserObject();
				multiTop.show(toolbar);
			}
		};

		ClickListener botListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				HorizontalToolbar toolbar = (HorizontalToolbar) actor
						.getUserObject();
				multiBot.show(toolbar);
			}
		};

		icon1.addListener(topListener);
		icon1.setUserObject(topBar2);

		icon2.addListener(topListener);
		icon2.setUserObject(topBar3);

		icon3.addListener(topListener);
		icon3.setUserObject(topBar1);

		icon4.addListener(botListener);
		icon4.setUserObject(botBar2);

		icon5.addListener(botListener);
		icon5.setUserObject(botBar3);

		icon6.addListener(botListener);
		icon6.setUserObject(botBar1);

		table.add(multiTop).expandX().fill();
		table.row();
		table.add().expand().fill();
		table.row();
		table.add(multiBot).expandX().fill();

		return table;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.overrideDensity = MathUtils.round(Dpi.HDPI.getMaxDpi());
		config.title = "TEST";
		new LwjglApplication(new MultiHorizontalToolbarTest(), config);
	}

}