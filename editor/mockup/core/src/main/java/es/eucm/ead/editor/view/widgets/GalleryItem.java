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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GalleryItem extends Table {

	private static final String DELETE_ICON = "recycle24x24";

	private static final ClickListener iconListener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			((GalleryItem) event.getListenerActor().getUserObject())
					.iconClicked();
		};
	};

	private TextField name;

	private GalleryItemStyle style;

	public GalleryItem(Image image, String text, float pad,
			boolean canBeDeleted, Skin skin) {
		this(image, text, pad, pad, canBeDeleted, skin, null);
	}

	public GalleryItem(Image image, String text, boolean canBeDeleted, Skin skin) {
		this(image, text, 0, 0, canBeDeleted, skin, null);
	}

	public GalleryItem(Image image, String text, float padImage, float padText,
			boolean canBeDeleted, Skin skin) {
		this(image, text, padImage, padText, canBeDeleted, skin, null);
	}

	public GalleryItem(Image image, String text, float padImage, float padText,
			boolean canBeDeleted, Skin skin, String nameStyle) {
		super(skin);

		if (nameStyle != null) {
			style = skin.get(nameStyle, GalleryItemStyle.class);
		} else {
			style = skin.get(GalleryItemStyle.class);
		}

		name = new TextField("", style.textStyle);
		name.setMessageText(text);

		Table top = new Table(skin);
		top.setBackground(style.imageBackground);

		if (canBeDeleted) {
			IconButton iconButton = new IconButton(DELETE_ICON, skin);
			iconButton.setUserObject(this);
			iconButton.addListener(iconListener);
			top.add(iconButton).expand().right();
			top.row();
		}

		top.add(image).pad(padImage).expand().center();

		Table bot = new Table(skin);
		bot.setBackground(style.textBackground);
		bot.add(name).pad(padText);

		add(top).expand().fill();
		row();
		add(bot).expandX().fill();
	}

	protected void iconClicked() {
		this.remove();
	}

	static public class GalleryItemStyle {

		public Drawable imageBackground, textBackground;

		public TextFieldStyle textStyle;

		public GalleryItemStyle() {
		}

		public GalleryItemStyle(Drawable imageBackground,
				Drawable textBackground, TextFieldStyle textStyle) {
			this.imageBackground = imageBackground;
			this.textBackground = textBackground;
			this.textStyle = textStyle;
		}

		public GalleryItemStyle(GalleryItemStyle style) {
			this.imageBackground = style.imageBackground;
			this.textBackground = style.textBackground;
			this.textStyle = style.textStyle;
		}
	}
}
