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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * A widget to represent a value selected from a search. It contains a
 * {@link Label} with the value set and a {@link ImageButton}, that represents
 * the search button.
 */
public class SearchWidget extends LinearLayout {

	private Label label;

	private ImageButton searchButton;

	public SearchWidget(Skin skin) {
		super(true);
		SearchWidgetStyle style = skin.get(SearchWidgetStyle.class);
		background(style.background);

		label = new Label("", new LabelStyle(style.font, style.fontColor));
		ImageButtonStyle buttonStyle = new ImageButtonStyle(
				skin.get(ButtonStyle.class));
		buttonStyle.imageUp = style.searchIcon;
		searchButton = new ImageButton(buttonStyle);

		add(label).expand(true, true);
		add(searchButton);
	}

	public void setText(String text) {
		label.setText(text);
	}

	public ImageButton getSearchButton() {
		return searchButton;
	}

	public static class SearchWidgetStyle {
		public Drawable searchIcon;
		public Drawable background;
		public BitmapFont font;
		public Color fontColor;
	}
}
