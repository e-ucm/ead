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
package es.eucm.ead.editor.view.widgets.gallery.repository;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.RepositoryManager;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.repository.info.ItemInfo;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.repo.I18NStrings;
import es.eucm.ead.schema.editor.components.repo.RepoAuthor;

public abstract class InfoGalleryItem extends GalleryItem {

	private static final ChangeListener showInfo = new ChangeListener() {

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			InfoGalleryItem item = (InfoGalleryItem) event.getListenerActor()
					.getUserObject();
			item.info.show(item);
		}

	};

	protected I18N i18n;

	private ItemInfo info;

	private Container<Actor> infoContainer;

	private Array<I18NStrings> tagsArray;
	private String tags;
	private String searchText;

	public InfoGalleryItem(I18N i18n, ItemInfo info, Skin skin,
			String styleName, BaseGallery gallery) {
		super(new Image(), "", false, skin, styleName, false, gallery);
		this.i18n = i18n;

		this.info = info;
		IconButton infoButton = new IconButton("info80x80", 0f, skin,
				"inverted");
		infoButton.setUserObject(this);
		infoButton.addListener(showInfo);
		infoContainer = new Container<Actor>(infoButton).top().right();
		infoContainer.setFillParent(true);
		addActor(infoContainer);
	}

	protected void setTagsArray(Array<I18NStrings> tagsArray) {
		this.tagsArray = tagsArray;
	}

	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		if (searchText == null) {
			searchText = getSimpleName() + " " + getTags();
		}
		return searchText;
	}

	@Override
	public void setThumbnail(Texture asset) {
		super.setThumbnail(asset);
		info.setThumbnail(image.getDrawable(), this);
	}

	public abstract RepoAuthor getAuthor();

	public String getLicense() {
		return "";
	}

	public BaseGallery getGallery() {
		return gallery;
	}

	public Image getImage() {
		return image;
	}

	public String getTags() {
		if (tags == null) {
			tags = buildTags();
		}
		return tags;
	}

	protected abstract String getSimpleName();

	protected String buildTags() {
		String tags = "";
		for (int i = 0; i < tagsArray.size; ++i) {
			tags += RepositoryManager.i18nString(tagsArray.get(i), i18n);
			if (i < tagsArray.size - 1) {
				tags += ", ";
			}
		}
		return tags;
	}
}
