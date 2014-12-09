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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.widgets.DropdownPane;
import es.eucm.ead.engine.I18N;

public class DropdownPaneTest extends UITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		Table table = new Table();
		table.setFillParent(true);

		DropdownPane drop = new DropdownPane(skin, "page");
		drop.setHead(new Label("HEAD1", skin));
		drop.addToBody(new Label("BODY1", skin));

		DropdownPane drop2 = new DropdownPane(skin, "page");
		drop2.setHead(new Label("HEAD2", skin));
		drop2.addToBody(new Label("BODY2", skin));

		DropdownPane drop3 = new DropdownPane(skin, "page");
		drop3.setHead(new Label("HEAD3", skin));
		drop3.addToBody(new Label("BODY3", skin));

		DropdownPane drop4 = new DropdownPane(skin, "page");
		drop4.setHead(new Label("HEAD4", skin));
		drop4.addToBody(new Label("BODY4", skin));

		DropdownPane drop5 = new DropdownPane(skin, "page");
		drop5.setHead(new Label("HEAD5", skin));
		drop5.addToBody(new Label("BODY5", skin));

		DropdownPane drop6 = new DropdownPane(skin, "page");
		drop6.setHead(new Label("HEAD6", skin));
		drop6.addToBody(new Label("BODY6", skin));

		table.add(drop);
		table.row();
		table.add(drop2);
		table.row();
		table.add(drop3);
		table.row();
		table.add(drop4);
		table.row();
		table.add(drop5);
		table.row();
		table.add(drop6);

		return table;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;
		new LwjglApplication(new DropdownPaneTest(), config);
	}
}