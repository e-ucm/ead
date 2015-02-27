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
package es.eucm.ead.editor.view.widgets.galleries.gallerieswithcategories;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class CategoryButton extends LinearLayout {

	private static final float PAD = WidgetBuilder.dpToPixels(16);

	private String buttonText;

	private String categoryName;

	private Color color;

	public CategoryButton(String icon, String buttonText, String categoryName,
			Color color, Skin skin) {
		super(true, skin.getDrawable(SkinConstants.DRAWABLE_BUTTON));
		backgroundColor(color);

		this.buttonText = buttonText;
		this.categoryName = categoryName;
		this.color = color;

		add(new IconButton(icon, skin, SkinConstants.STYLE_TOOLBAR))
				.marginLeft(PAD).marginRight(PAD);
		add(new Label(buttonText, skin, SkinConstants.STYLE_TOOLBAR)).expandX();
	}

	public Color getColor() {
		return color;
	}

	public String getButtonText() {
		return buttonText;
	}

	public String getCategoryName() {
		return categoryName;
	}
}
