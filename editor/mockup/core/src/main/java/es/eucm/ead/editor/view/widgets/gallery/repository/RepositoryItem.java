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

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.RepositoryManager;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.repository.info.ItemInfo;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.repo.RepoAuthor;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;

public class RepositoryItem extends InfoGalleryItem implements
		AssetLoadedCallback<Texture> {

	private ModelEntity element;
	private RepoElement repoElement;

	public RepositoryItem(ItemInfo<RepositoryItem> info, Controller controller,
			ModelEntity element, BaseGallery gallery) {
		super(controller.getApplicationAssets().getI18N(), info, controller
				.getApplicationAssets().getSkin(), "repository", gallery);
		this.element = element;

		repoElement = Q.getComponent(element, RepoElement.class);

		setTagsArray(repoElement.getTags());
		((Label) name).setText(getSimpleName());

		ApplicationAssets assets = controller.getApplicationAssets();
		assets.get(((MockupController) controller).getRepositoryManager()
				.getCurrentLibraryPath() + repoElement.getThumbnail(),
				Texture.class, this);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		setThumbnail(asset);
	}

	@Override
	protected String getSimpleName() {
		if (repoElement == null) {
			return " ";
		}
		String name = RepositoryManager.i18nString(repoElement.getName(), i18n);
		return name == null ? " " : name;
	}

	public ModelEntity getElement() {
		return element;
	}

	@Override
	public RepoAuthor getAuthor() {
		return repoElement.getAuthor();
	}

	@Override
	public String getDescription() {
		return RepositoryManager.i18nString(repoElement.getDescription(), i18n);
	}

	@Override
	public String getLicense() {
		return repoElement.getLicense().toString();
	}
}
