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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.RepoTile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;

public class SearchGallery extends AbstractWidget implements WorkerListener,
		ModelView {

	private Skin skin;

	protected Gallery gallery;

	private ApplicationAssets assets;

	public SearchGallery(float rows, int columns, Controller controller) {
		assets = controller.getApplicationAssets();
		skin = assets.getSkin();
		addActor(gallery = new Gallery(rows, columns,
				skin.get(GalleryStyle.class)));
		gallery.setFillParent(true);
	}

	public void prepare() {
		clear();
	}

	@Override
	public void release() {
		clear();
	}

	@Override
	public void start() {
		clear();
	}

	@Override
	public void clear() {
		assets.clear();
		gallery.clearChildren();
	}

	@Override
	public void result(Object... results) {
		Object firstResult = results[0];
		if (!(firstResult instanceof SearchResponse)) {
			RepoElement elem = (RepoElement) firstResult;
			Pixmap repoThumbnail = (Pixmap) results[1];
			Texture thumbnailTex = new Texture(repoThumbnail);
			addTile(elem, thumbnailTex, repoThumbnail);
			assets.addAsset(thumbnailTex.toString(), Texture.class,
					thumbnailTex);
			assets.addAsset(repoThumbnail.toString(), Pixmap.class,
					repoThumbnail);
		}
	}

	private Cell addTile(RepoElement elem, Texture thumbnailTexture,
			Pixmap thumbnailPixmap) {

		RepoTile tile = WidgetBuilder.repoTile(elem, thumbnailTexture,
				thumbnailPixmap);

		return gallery.add(tile);
	}

	@Override
	public void done() {

	}

	@Override
	public void error(Throwable ex) {

	}

	@Override
	public void cancelled() {

	}

}