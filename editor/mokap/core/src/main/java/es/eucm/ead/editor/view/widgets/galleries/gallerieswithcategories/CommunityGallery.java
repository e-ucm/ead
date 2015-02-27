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
package es.eucm.ead.editor.view.widgets.galleries.gallerieswithcategories;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.Play;
import es.eucm.ead.editor.control.workers.SearchRepo;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.RepoTile;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.engine.gdx.URLTextureLoader;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

public class CommunityGallery extends MyLibraryGallery {

	public CommunityGallery(float rows, int columns, Controller controller) {
		super(rows, columns, controller);
	}

	@Override
	protected void loadContent(String search) {
		if (count < categories.size) {
			controller.action(ExecuteWorker.class, SearchRepo.class, this,
					search, gallery.getPreferredCellWidth(),
					gallery.getPreferredCellHeight(), null,
					(categories.get(count).getCategoryName()));
		}
	}

	@Override
	protected void finishLoadContent() {
		controller.getWorkerExecutor().cancel(SearchRepo.class, this);
		done();
	}

	@Override
	public void loadContents(String search) {
		clear();
		this.search = search;
		if (((MokapPlatform) controller.getPlatform()).isConnected()) {
			super.loadContents(search);
		} else {
			// SHOW WITHOUT CONNECTION
		}
	}

	@Override
	protected void loadThumbnail(Object id, String path) {
		assets.get(
				path,
				Texture.class,
				new URLTextureLoader.URLTextureParameter(controller
						.getLibraryManager()
						.getRepoElementLibraryFolder((RepoElement) id)
						.child(ModelStructure.THUMBNAIL_FILE)), this);
	}

	@Override
	protected void addResultTile(Object... results) {
		addTile(results[0], "", (String) results[1]);
	}

	@Override
	protected Tile createTile(Object id, String title, TextureDrawable thumbnail) {
		return WidgetBuilder.repoTile((RepoElement) id, thumbnail);
	}

	@Override
	protected void prepareGalleryItem(Actor actor, Object id) {
		final RepoElement elem = (RepoElement) id;
		if (elem.getCategoryList().contains(RepoCategories.MOKAPS, true)) {
			actor.addListener(new RepoTile.RepoTileListener() {
				@Override
				public void clickedInLibrary(RepoTileEvent event) {
					controller.action(Play.class,
							controller.getLibraryManager()
									.getRepoElementLibraryFolder(elem).file()
									.getAbsolutePath()
									+ "/" + ModelStructure.CONTENTS_FOLDER);
				}
			});
		}
	}
}
