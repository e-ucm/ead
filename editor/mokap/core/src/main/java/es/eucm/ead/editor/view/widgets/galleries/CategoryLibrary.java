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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.CopyProjectToWorkspace;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.OpenProject;
import es.eucm.ead.editor.control.actions.editor.Play;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.workers.LoadLibraryEntities;
import es.eucm.ead.editor.control.workers.Worker;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery.GalleryStyle;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

public class CategoryLibrary extends ContextMenuGallery implements
		Worker.WorkerListener,
		BackgroundExecutor.BackgroundTaskListener<String> {

	private RepoElement longPressedMokap;

	protected Controller controller;

	protected String category;

	public CategoryLibrary(float rows, int columns, Controller controller) {
		this(rows, columns, "all", controller);
	}

	public CategoryLibrary(float rows, int columns, String category,
			final Controller controller) {
		super(rows, columns, controller.getApplicationAssets(), controller, "");
		this.controller = controller;
		this.category = category;

		searchEnabled = false;

		Button edit = WidgetBuilder.button(SkinConstants.IC_EDIT,
				i18N.m("edit"), SkinConstants.STYLE_CONTEXT);
		edit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (longPressedMokap != null) {
					controller.action(
							CopyProjectToWorkspace.class,
							CategoryLibrary.this,
							controller
									.getLibraryManager()
									.getRepoElementContentsFolder(
											longPressedMokap).path());
				}
			}
		});

		Button play = WidgetBuilder.button(SkinConstants.IC_PLAY,
				i18N.m("play"), SkinConstants.STYLE_CONTEXT);
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (longPressedMokap != null) {
					controller.action(
							Play.class,
							controller
									.getLibraryManager()
									.getRepoElementContentsFolder(
											longPressedMokap).path());
				}
			}
		});

		setContextMenu(edit, play);
	}

	@Override
	protected boolean tileHasContext(Tile tile) {
		longPressedMokap = null;
		if (this.category.equals(RepoCategories.MOKAPS.toString())) {
			Object repoElem = tile.getUserObject();
			if (repoElem instanceof RepoElement) {
				RepoElement repoElement = (RepoElement) repoElem;
				if (controller.getLibraryManager().isDownloaded(repoElement)) {
					longPressedMokap = repoElement;
					return true;
				}
			}
		}
		return false;
	}

	public void changeCategory(String newCategory) {
		this.category = newCategory;
	}

	@Override
	public void loadContents(String string) {
		clear();
		controller.action(ExecuteWorker.class, LoadLibraryEntities.class, this,
				category);
	}

	@Override
	public void start() {
	}

	@Override
	public void result(Object... results) {
		addTile(results[0], (String) results[1], (String) results[2]);
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

	@Override
	protected void prepareActionButton(Actor actor) {

	}

	@Override
	protected void prepareGalleryItem(Actor actor, Object id) {
		if (id instanceof RepoElement) {
			RepoElement selected = (RepoElement) id;
			if (selected.getCategoryList()
					.contains(RepoCategories.MOKAPS, true)) {
				WidgetBuilder.actionOnClick(actor, Play.class,
						controller.getLibraryManager()
								.getRepoElementContentsFolder(selected).path());
				actor.setUserObject(selected);
			}
		}
	}

	@Override
	public void done(BackgroundExecutor backgroundExecutor, String result) {
		controller.action(OpenProject.class, result);
	}
}
