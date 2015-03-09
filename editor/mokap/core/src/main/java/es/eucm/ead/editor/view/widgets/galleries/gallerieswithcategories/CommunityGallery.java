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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.Play;
import es.eucm.ead.editor.control.workers.SearchRepo;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.RepoTile;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.gdx.URLTextureLoader;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

public class CommunityGallery extends MyLibraryGallery {

	private static final float TOP_PAD = WidgetBuilder.dpToPixels(48);

	private LinearLayout tryReconnect;

	public CommunityGallery(float rows, int columns, Controller controller) {
		super(rows, columns, controller);
		searchEnabled = true;

		tryReconnect = new LinearLayout(false);
		tryReconnect.clear();
		tryReconnect.add(new Label(i18N.m("no.connection"), skin)).centerX()
				.marginTop(TOP_PAD);

		Button reconnect = new WidgetBuilder()
				.circleButton(SkinConstants.IC_REFRESH);
		reconnect.setTransform(true);
		reconnect.setOrigin(reconnect.getWidth() * 0.5f,
				reconnect.getHeight() * 0.5f);
		reconnect.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Object search = tryReconnect.getUserObject();
				loadContents(search == null ? "" : search.toString());
				event.getListenerActor().addAction(
						Actions.sequence(Actions.touchable(Touchable.disabled),
								Actions.rotateBy(-360, 2f),
								Actions.touchable(Touchable.enabled)));
			}
		});
		tryReconnect.add(reconnect).centerX().marginTop(TOP_PAD);
		tryReconnect.addSpace();
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
			tryReconnect.remove();
			super.loadContents(search);
		} else {
			tryReconnect.setUserObject(search);
			addActor(tryReconnect);
		}
	}

	@Override
	public void layout() {
		super.layout();
		tryReconnect.setBounds(0, 0, getWidth(), getHeight());
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
