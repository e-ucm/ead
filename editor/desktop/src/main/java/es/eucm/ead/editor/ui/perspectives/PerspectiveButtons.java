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
package es.eucm.ead.editor.ui.perspectives;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

/**
 * Contains all the perspective buttons
 */
public class PerspectiveButtons extends LinearLayout {

	public PerspectiveButtons(Controller controller) {
		super(true);

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();

		add(createButton("home32x32", i18N.m("perspectives.home"), skin));
		add(new Separator(false, skin));
		add(createButton("interface32x32", i18N.m("perspectives.interface"),
				skin));
		add(new Separator(false, skin));
		add(createButton("education32x32", i18N.m("perspectives.education"),
				skin));
		add(new Separator(false, skin));
		add(createButton("testgame32x32", i18N.m("perspectives.testgame"), skin));
		add(new Separator(false, skin));
		add(createButton("export32x32", i18N.m("perspectives.export"), skin));
		add(new Separator(false, skin));
	}

	private Actor createButton(String drawable, String text, Skin skin) {
		IconButton button = new IconButton(skin.getDrawable(drawable), 5, skin);
		button.add(new Label(text, skin, "title")).padLeft(5).padRight(5);
		return button;
	}
}
