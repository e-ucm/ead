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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.I18N.Lang;

/**
 * Panel with skins and languages settings
 */
public class HiddenLateralOptionsPanel extends HiddenPanel {

	public HiddenLateralOptionsPanel(Controller controller, Skin skin) {
		super(skin);

		setVisible(false);

		I18N i18n = controller.getEditorAssets().getI18N();

		Label skins = new Label(i18n.m("general.mockup.skins").toUpperCase(),
				skin);
		String skinStyle = "default-radio", lineString = "- - - - - - - - - - - - -";
		CheckBox skinDefault = new CheckBox(
				i18n.m("general.mockup.skins.default"), skin, skinStyle);
		skinDefault.setChecked(true);
		CheckBox skinN2 = new CheckBox(i18n.m("general.mockup.skins.funny"),
				skin, skinStyle);
		CheckBox skinN3 = new CheckBox(i18n.m("general.mockup.skins.pro"),
				skin, skinStyle);
		Label line = new Label(lineString, skin);
		Label languages = new Label(i18n.m("menu.editor.language")
				.toUpperCase(), skin);

		Table root = new Table(skin);
		ScrollPane sp = new ScrollPane(root, skin);
		sp.setupFadeScrollBars(0f, 0f);
		sp.setScrollingDisabled(true, false);
		root.add(skins);
		root.row();
		root.add(skinDefault).left();
		root.row();
		root.add(skinN2).left();
		root.row();
		root.add(skinN3).left();
		root.row();
		root.add(line);
		root.row();
		root.add(languages);

		new ButtonGroup(skinDefault, skinN2, skinN3);
		ButtonGroup languagesGroup = new ButtonGroup();
		for (Lang lang : i18n.getAvailable()) {
			CheckBox lan = new CheckBox(lang.name, skin, skinStyle);
			languagesGroup.add(lan);
			root.row();
			root.add(lan).left();
		}

		this.add(sp);
	}
}
