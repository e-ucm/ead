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
package es.eucm.ead.editor.view.widgets.galleries;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;

/**
 * Created by angel on 18/11/14.
 */
public abstract class ThumbnailsGallery extends AbstractWidget implements
		AssetLoadedCallback<Texture> {

	private Assets assets;

	private Skin skin;

	private I18N i18N;

	protected ObjectMap<String, TextureDrawable> pendingTextures = new ObjectMap<String, TextureDrawable>();

	protected Gallery gallery;

	private Button add;

	public ThumbnailsGallery(float rowHeight, int columns, Assets assets,
			Skin skin, I18N i18N) {
		addActor(gallery = new Gallery(rowHeight, columns));
		addActor(add = WidgetBuilder.button(SkinConstants.STYLE_ADD));
		prepareAddButton(add);
		this.assets = assets;
		this.skin = skin;
		this.i18N = i18N;
	}

	@Override
	public void clear() {
		gallery.clearChildren();
		pendingTextures.clear();
	}

	@Override
	public void layout() {
		setBounds(gallery, 0, 0, getWidth(), getHeight());
		float width = getPrefWidth(add);
		setBounds(add, getWidth() - width - WidgetBuilder.dpToPixels(32),
				WidgetBuilder.dpToPixels(32), width, getPrefHeight(add));
	}

	public void addTile(String id, String title, String thumbnailPath) {
		Image image;
		if (thumbnailPath == null) {
			image = new Image(skin, SkinConstants.DRAWABLE_LOGO);
			image.setScaling(Scaling.fit);
		} else {
			TextureDrawable thumbnail = new TextureDrawable();
			image = new Image(thumbnail);
			pendingTextures.put(thumbnailPath, thumbnail);
			assets.get(thumbnailPath, Texture.class, this);
			image.setName(thumbnailPath);
		}
		title = title == null || "".equals(title) ? i18N.m("untitled") : title;
		Tile tile = WidgetBuilder.tile(image, title);
		tile.setName(id);
		prepareGalleryItem(tile, id);
		gallery.add(tile);
	}

	protected abstract void prepareAddButton(Actor actor);

	protected abstract void prepareGalleryItem(Actor actor, String id);

	@Override
	public void loaded(String fileName, Texture asset) {
		TextureDrawable drawable = pendingTextures.get(fileName);
		drawable.setTexture(asset);
	}
}
