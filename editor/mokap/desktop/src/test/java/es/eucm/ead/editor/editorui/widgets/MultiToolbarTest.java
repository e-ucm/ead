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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.MokapController.Dpi;
import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiToolbar;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class MultiToolbarTest extends UITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		Table table = new Table();
		table.setFillParent(true);

		final MultiToolbar multiTop = new MultiToolbar(skin);

		LinearLayout insertBar = new LinearLayout(true);

		IconButton redo = new IconButton("ic_redo", 0, skin, "toolbar");
		insertBar.add(redo);

		IconButton undo = new IconButton("ic_undo", 0, skin, "toolbar");
		insertBar.add(undo);

		IconButton compose = new IconButton("ic_compose", 0, skin, "toolbar");
		insertBar.add(compose);

		IconButton menu = new IconButton("ic_menu", 0, skin, "toolbar");
		insertBar.add(menu);

		insertBar.addSpace();

		IconButton paste = new IconButton("ic_paste", 0, skin, "toolbar");
		insertBar.add(paste);

		IconButton add = new IconButton("ic_add", 0, skin, "toolbar");
		insertBar.add(add);

		LinearLayout transformBar = new LinearLayout(true);

		IconButton redo2 = new IconButton("ic_redo", 0, skin, "toolbar");
		transformBar.add(redo2);

		IconButton undo2 = new IconButton("ic_undo", 0, skin, "toolbar");
		transformBar.add(undo2);

		IconButton check = new IconButton("ic_check", 0, skin, "toolbar");
		transformBar.add(check);

		transformBar.addSpace();

		IconButton group = new IconButton("ic_group", 0, skin, "toolbar");
		transformBar.add(group);

		IconButton edit = new IconButton("ic_edit", 0, skin, "toolbar");
		transformBar.add(edit);

		IconButton toBack = new IconButton("ic_to_back", 0, skin, "toolbar");
		transformBar.add(toBack);

		IconButton toFront = new IconButton("ic_to_front", 0, skin, "toolbar");
		transformBar.add(toFront);

		IconButton moreTrans = new IconButton("ic_more", 0, skin, "toolbar");
		transformBar.add(moreTrans);

		LinearLayout paintBar = new LinearLayout(true);

		IconButton redo3 = new IconButton("ic_redo", 0, skin, "toolbar");
		paintBar.add(redo3);

		IconButton undo3 = new IconButton("ic_undo", 0, skin, "toolbar");
		paintBar.add(undo3);

		IconButton circle = new IconButton("ic_circle", 0, skin, "toolbar");
		paintBar.add(circle);

		IconButton brush = new IconButton("ic_brush", 0, skin, "toolbar");
		paintBar.add(brush);

		IconButton save = new IconButton("ic_check", 0, skin, "toolbar");
		paintBar.add(save);

		paintBar.addSpace();

		IconButton cancel = new IconButton("ic_close", 0, skin, "toolbar");
		paintBar.add(cancel);

		multiTop.addToolbars(insertBar, transformBar, paintBar);

		ClickListener topListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				int index = (Integer) actor.getUserObject();
				multiTop.show(index);
			}
		};

		compose.addListener(topListener);
		compose.setUserObject(1);

		check.addListener(topListener);
		check.setUserObject(2);

		save.addListener(topListener);
		save.setUserObject(0);

		cancel.addListener(topListener);
		cancel.setUserObject(0);

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