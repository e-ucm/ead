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
package es.eucm.ead.editor.view.widgets.editionview.elementcontext;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.IconButton.IconButtonStyle;

public class ContextBar extends Table {

	public ContextBar(Skin skin, IconButton... actors) {
		if (actors.length == 1) {
			IconButton actor = actors[0];
			setUpButtonStyle(skin, actor, "white_single");
		} else if (actors.length > 1) {
			setUpButtonStyle(skin, actors[0], "white_left");
			for (int i = 1; i < actors.length - 1; ++i) {
				setUpButtonStyle(skin, actors[i], "white_center");
			}
			setUpButtonStyle(skin, actors[actors.length - 1], "white_right");
		}
	}

	private void setUpButtonStyle(Skin skin, IconButton button, String style) {
		IconButtonStyle panelStyle = skin.get(style, IconButtonStyle.class);
		if (button.getStyle() != panelStyle) {
			button.setStyle(panelStyle);
		}
		add(button);
	}
}
