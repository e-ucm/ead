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
package es.eucm.ead.editor.view.widgets.gallery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.widgets.IconButton;

public abstract class GalleryItem extends Button {

	private static final float MAX_PERCENT_WIDTH = .3F;
	private static final String DELETE_ICON = "recycle";

	private static final ClickListener iconListener = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();
			GalleryItem item = ((GalleryItem) actor.getUserObject());
			if (actor.hit(x, y, true) instanceof IconButton) {
				item.gallery.deleteItem(item);
			} else {
				item.gallery.itemClicked(item);
			}
		};
	};

	protected Actor name;

	private GalleryItemStyle style;

	protected Image image;

	protected BaseGallery gallery;

	protected Skin skin;
	protected Table top;

	public GalleryItem(Image image, String text, boolean canBeDeleted,
			Skin skin, BaseGallery gallery) {
		this(image, text, canBeDeleted, skin, null, true, gallery);
	}

	public GalleryItem(Image image, String text, float padImage, float padText,
			boolean canBeDeleted, Skin skin, BaseGallery gallery) {
		this(image, text, canBeDeleted, skin, null, true, gallery);
	}

	public GalleryItem(Image image, String text, boolean canBeDeleted,
			Skin skin, String nameStyle, boolean editableName,
			BaseGallery gallery) {
		super(skin.get("galleryItem", ButtonStyle.class));
		this.skin = skin;
		this.gallery = gallery;
		if (nameStyle != null) {
			style = skin.get(nameStyle, GalleryItemStyle.class);
		} else {
			style = skin.get(GalleryItemStyle.class);
		}

		if (editableName) {
			TextField nameTf = new TextField("", style.textStyle);
			nameTf.setFocusTraversal(false);
			nameTf.setMessageText(text);
			name = nameTf;
		} else {
			Label nameLabel = new Label(text, skin);
			name = nameLabel;
		}

		top = new Table();
		top.setBackground(style.imageBackground);

		if (canBeDeleted) {
			IconButton iconButton = new IconButton(DELETE_ICON, skin);
			Container<Actor> icon = new Container<Actor>(iconButton).top()
					.right();
			icon.setFillParent(true);
			top.addActor(icon);
		}
		top.addCaptureListener(iconListener);
		top.setTouchable(Touchable.enabled);
		top.setUserObject(this);

		this.image = image;
		image.setScaling(Scaling.fit);
		image.setDrawable(skin.getDrawable("new_project80x80"));
		top.add(image).maxSize(Gdx.graphics.getWidth() * MAX_PERCENT_WIDTH);

		Container<Actor> bot = new Container<Actor>(name);
		bot.setBackground(style.textBackground);

		add(top).expand().fill();
		row();
		add(bot).expandX().fill();
	}

	public void deleteItem() {
	}

	public String getName() {
		if (name instanceof TextField) {
			return ((TextField) name).getText();
		} else {
			return ((Label) name).getText().toString();
		}
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

	public void setThumbnail(Texture asset) {
		image.setDrawable(new TextureRegionDrawable(new TextureRegion(asset)));
	}
}
