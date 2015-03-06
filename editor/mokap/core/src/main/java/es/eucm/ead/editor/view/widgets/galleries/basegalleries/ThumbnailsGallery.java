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
package es.eucm.ead.editor.view.widgets.galleries.basegalleries;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets;

public abstract class ThumbnailsGallery extends AbstractWidget implements
		Assets.AssetLoadedCallback<Texture> {

	protected Assets assets;

	protected Skin skin;

	protected I18N i18N;

	private Button actionButton;

	protected Gallery gallery;

	protected String search;

	protected boolean searchEnabled;

	protected ObjectMap<String, TextureDrawable> pendingTextures = new ObjectMap<String, TextureDrawable>();

	public ThumbnailsGallery(float rows, int columns, Assets assets, Skin skin,
			I18N i18N) {
		this(rows, columns, assets, skin, i18N, skin.get(GalleryStyle.class));
	}

	public ThumbnailsGallery(float rows, int columns, Assets assets, Skin skin,
			I18N i18N, String galleryStyle) {
		this(rows, columns, assets, skin, i18N, skin.get(galleryStyle,
				GalleryStyle.class));
	}

	public ThumbnailsGallery(float rows, int columns, Assets assets, Skin skin,
			I18N i18N, GalleryStyle galleryStyle) {
		this(rows, columns, assets, skin, i18N, galleryStyle, "");
	}

	public ThumbnailsGallery(float rows, int columns, Assets assets, Skin skin,
			I18N i18N, GalleryStyle galleryStyle, String actionIcon) {
		addActor(gallery = new Gallery(rows, columns, galleryStyle));
		if (!actionIcon.equals("")) {
			addActor(actionButton = WidgetBuilder.circleButton(actionIcon));
			prepareActionButton(actionButton);
		}
		this.assets = assets;
		this.skin = skin;
		this.i18N = i18N;
	}

	public abstract void loadContents(String search);

	public boolean isSearchEnabled() {
		return searchEnabled;
	}

	@Override
	public void clear() {
		gallery.clearChildren();
	}

	public Gallery getGallery() {
		return gallery;
	}

	@Override
	public void layout() {
		super.layout();
		setBounds(gallery, 0, 0, getWidth(), getHeight());
		if (actionButton != null && actionButton.getParent() == this) {
			float width = getPrefWidth(actionButton);
			setBounds(actionButton,
					getWidth() - width - WidgetBuilder.dpToPixels(32),
					WidgetBuilder.dpToPixels(32), width,
					getPrefHeight(actionButton));
		}
	}

	public Cell addTile(Object id, String title, String thumbnailPath) {
		TextureDrawable thumbnail = new TextureDrawable();
		pendingTextures.put(thumbnailPath, thumbnail);
		loadThumbnail(id, thumbnailPath);
		Tile tile = createTile(id, title, thumbnail);
		tile.setName(id.toString());
		prepareGalleryItem(tile, id);
		return gallery.add(tile);
	}

	protected Tile createTile(Object id, String title, TextureDrawable thumbnail) {
		return WidgetBuilder.tile(title, thumbnail);
	}

	protected abstract void prepareActionButton(Actor actor);

	public Button getActionButton() {
		return actionButton;
	}

	protected abstract void prepareGalleryItem(Actor actor, Object id);

	protected void loadThumbnail(Object id, String path) {
		assets.get(path, Texture.class, this);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		TextureDrawable drawable = pendingTextures.get(fileName);
		drawable.setTexture(asset);
	}

	public void error(String fileName, Class type, Throwable exception) {
		pendingTextures.remove(fileName);
		Gdx.app.error("ThumbnailGallery", "Impossible to read thumbnail: "
				+ fileName, exception);
	}

}
