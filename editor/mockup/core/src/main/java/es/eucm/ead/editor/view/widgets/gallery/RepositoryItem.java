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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;

public class RepositoryItem extends GalleryItem implements
		AssetLoadedCallback<Texture> {

	private ModelEntity element;
	private RepoElement documentation;

	public RepositoryItem(Controller controller, ModelEntity element,
			BaseGallery gallery) {
		super(new Image(), "", 0f, 0f, false, controller.getApplicationAssets()
				.getSkin(), null, false, gallery);
		this.element = element;

		documentation = Q.getComponent(element, RepoElement.class);

		((Label) name).setText(getDocumentationName());

		ApplicationAssets assets = controller.getApplicationAssets();
		assets.get(((MockupController) controller).getRepositoryManager()
				.getCurrentLibraryPath() + documentation.getThumbnail(),
				Texture.class, this);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		setThumbnail(asset);
	}

	@Override
	public String getName() {
		return getDocumentationName();
	}

	private String getDocumentationName() {
		if (documentation == null) {
			return " ";
		}
		String name = documentation.getName();
		return name == null ? " " : name;
	}

	public ModelEntity getElement() {
		return element;
	}
}
