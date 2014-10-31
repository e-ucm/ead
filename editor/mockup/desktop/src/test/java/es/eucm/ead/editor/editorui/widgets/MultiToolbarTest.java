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
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiToolbar;
import es.eucm.ead.engine.I18N;

public class MultiToolbarTest extends MockupUITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		Table table = new Table();
		table.setFillParent(true);

		final MultiToolbar multiTop = new MultiToolbar(skin);

		Toolbar topBar1 = new Toolbar(skin, 40, "white_top");
		topBar1.backgroundColor(Color.YELLOW);
		IconButton icon1 = new IconButton("menu", skin);
		topBar1.leftAdd(icon1);

		Toolbar topBar2 = new Toolbar(skin, 40, "white_top");
		topBar2.backgroundColor(Color.ORANGE);
		IconButton icon2 = new IconButton("menu", skin);
		topBar2.rightAdd(icon2);

		Toolbar topBar3 = new Toolbar(skin, 40, "white_top");
		topBar3.backgroundColor(Color.RED);
		IconButton icon3 = new IconButton("menu", skin);
		topBar3.leftAdd(icon3);

		multiTop.addToolbars(topBar1, topBar2, topBar3);

		ClickListener topListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				int index = (Integer) actor.getUserObject();
				multiTop.show(index);
			}
		};

		icon1.addListener(topListener);
		icon1.setUserObject(1);

		icon2.addListener(topListener);
		icon2.setUserObject(2);

		icon3.addListener(topListener);
		icon3.setUserObject(0);

		table.add(multiTop).expandX().fill();
		table.row();
		table.add().expand().fill();

		return table;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.overrideDensity = MathUtils.round(Dpi.HDPI.getMaxDpi());
		config.title = "TEST";
		new LwjglApplication(new MultiToolbarTest(), config);
	}

}