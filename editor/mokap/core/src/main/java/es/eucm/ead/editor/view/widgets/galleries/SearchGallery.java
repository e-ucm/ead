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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.workers.CopyEntityResources;
import es.eucm.ead.editor.control.workers.DownloadFile;
import es.eucm.ead.editor.control.workers.UnzipFile;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.listeners.workers.CopyEntityResourcesListener;
import es.eucm.ead.editor.view.listeners.workers.DownloadFileListener;
import es.eucm.ead.editor.view.listeners.workers.UnzipFileListener;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.Cell;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;
import es.eucm.ead.schema.entities.ModelEntity;

public class SearchGallery extends AbstractWidget implements WorkerListener,
		ModelView {

	private Skin skin;

	protected Gallery gallery;

	private ApplicationAssets assets;

	private Controller controller;

	public SearchGallery(float rowHeight, int columns, Controller controller) {
		this.controller = controller;
		assets = controller.getApplicationAssets();
		skin = assets.getSkin();
		addActor(gallery = new Gallery(rowHeight, columns,
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
			repoThumbnail.dispose();
			addTile(elem, thumbnailTex);
			assets.addAsset(thumbnailTex.toString(), Texture.class,
					thumbnailTex);
		}
	}

	private Cell addTile(RepoElement elem, Texture thumbnailTexture) {

		TextureDrawable thumbnail = new TextureDrawable(thumbnailTexture);
		Image image = new Image(thumbnail);
		image.setName(elem.getId());

		String title = Q.getRepoElementName(elem);
		Tile tile = WidgetBuilder.tile(image, title);
		prepareGalleryItem(tile, elem);

		return gallery.add(tile);
	}

	private void prepareGalleryItem(Actor actor, final RepoElement elem) {
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				EditorGameAssets editorGameAssets = controller
						.getEditorGameAssets();
				final FileHandle loadingPath = editorGameAssets
						.absolute(editorGameAssets.getLoadingPath());
				final FileHandle tempOutputFolder = FileHandle
						.tempDirectory("downloadedRepoElem");
				final FileHandle contentsZip = tempOutputFolder
						.child("contents");
				controller.action(ExecuteWorker.class, DownloadFile.class,
						new DownloadFileListener(tempOutputFolder) {

							@Override
							public void downloaded() {
								controller.action(ExecuteWorker.class,
										UnzipFile.class, new UnzipFileListener(
												tempOutputFolder) {

											@Override
											public void unzipped() {
												controller
														.action(ExecuteWorker.class,
																CopyEntityResources.class,
																new CopyEntityResourcesListener(
																		tempOutputFolder) {

																	@Override
																	public void entityCopied(
																			ModelEntity entity) {
																		controller
																				.action(AddSceneElement.class,
																						entity);
																		controller
																				.action(ChangeView.class,
																						SceneView.class);
																	}
																},
																tempOutputFolder,
																loadingPath);
											}
										}, contentsZip, tempOutputFolder);
							}
						}, elem.getContentsUrl(), contentsZip.file()
								.getAbsolutePath());

			}
		});
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