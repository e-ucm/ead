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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.RepositoryManager.OnThumbnailAvailableListener;
import es.eucm.ead.editor.control.actions.editor.repository.GetLibraryThumbnail;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.repository.info.ItemInfo;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.RepoAuthor;
import es.eucm.ead.schema.editor.components.RepoLibrary;
import es.eucm.ead.schema.editor.components.RepoLicense;

public class LibraryItem extends InfoGalleryItem implements
		AssetLoadedCallback<Texture>, OnThumbnailAvailableListener {

	private RepoLibrary repoLibrary;

	public LibraryItem(ItemInfo<LibraryItem> info, Controller controller,
			RepoLibrary repoLibrary, BaseGallery gallery) {
		super(info, controller.getApplicationAssets().getSkin(), "library",
				gallery);

		this.repoLibrary = repoLibrary;
		setTagsArray(repoLibrary.getTags());
		((Label) name).setText(getSimpleName());

		controller.action(GetLibraryThumbnail.class, this, repoLibrary);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		setThumbnail(asset);
	}

	protected String getSimpleName() {
		if (repoLibrary == null) {
			return " ";
		}
		String name = repoLibrary.getName();
		return name == null ? " " : name;
	}

	@Override
	public RepoAuthor getAuthor() {
		return repoLibrary.getAuthor();
	}

	@Override
	public String getDescription() {
		return repoLibrary.getDescription();
	}

	@Override
	public String getLicense() {
		String ret = "";
		Array<RepoLicense> licenses = repoLibrary.getLicenses();
		for (int i = 0; i < licenses.size; ++i) {
			ret += licenses.get(i).toString();
			if (i < licenses.size - 1) {
				ret += ", ";
			}
		}
		return ret;
	}

	public RepoLibrary getRepoLibrary() {
		return repoLibrary;
	}

	@Override
	public void thumbnailAvailable(String thumbnailPath, Controller controller) {
		if (thumbnailPath != null) {
			ApplicationAssets assets = controller.getApplicationAssets();
			assets.get(thumbnailPath, Texture.class, this);
		}
	}
}
