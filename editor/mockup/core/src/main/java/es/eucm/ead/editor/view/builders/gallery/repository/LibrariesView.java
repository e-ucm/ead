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
package es.eucm.ead.editor.view.builders.gallery.repository;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.RepositoryManager.ProgressListener;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.repository.UpdateLibraries;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.EditionView;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.repository.info.ItemInfo;
import es.eucm.ead.editor.view.builders.gallery.repository.info.LibraryInfo;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.repository.LibraryItem;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;
import es.eucm.ead.schema.editor.components.repo.RepoLibrary;

public class LibrariesView extends BaseGallery implements ProgressListener {

	private static final int COLS = 2;

	private static final float ERROR_NOTIF_TIMEOUT = 3F;
	private static final String IC_GO_BACK = "back80x80";

	private Toasts toasts;
	private ItemInfo<LibraryItem> info;

	@Override
	public Actor getView(Object... args) {
		galleryGrid.clear();
		toasts.showNotification(i18n.m("repository.refreshing"));
		controller.action(UpdateLibraries.class, LibrariesView.this);
		return view;
	}

	@Override
	public void initialize(Controller control) {
		super.initialize(control);
		toasts = ((MockupViews) controller.getViews()).getToasts();
		info = new LibraryInfo(control, view);
	}

	@Override
	public void release(Controller controller) {
		super.release(controller);
		toasts.hideNotification();
	}

	@Override
	public void finished(boolean succeeded, Controller controller) {
		if (succeeded) {
			toasts.hideNotification();
			super.getView();
		} else {
			toasts.showNotification(i18n.m("repository.refreshingError"),
					ERROR_NOTIF_TIMEOUT);
		}
	}

	@Override
	protected void loadItems(Array<GalleryItem> items) {
		items.clear();
		Array<RepoLibrary> libs = ((MockupController) controller)
				.getRepositoryManager().getRepoLibraries();
		for (RepoLibrary repoLibrary : libs) {
			LibraryItem libraryItem = new LibraryItem(info, controller,
					repoLibrary, this);
			items.add(libraryItem);
		}
	}

	@Override
	protected Actor createPlayButton() {
		return null;
	}

	@Override
	protected Actor createBackButton() {
		Button back = new IconButton(IC_GO_BACK, skin);
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeMockupView.class, EditionView.class,
						Transitions.getFadeSlideTransition(topBar, galleryPane,
								false));
			}
		});
		return back;
	}

	@Override
	public void itemClicked(GalleryItem item) {
		RepoLibrary repoLibrary = ((LibraryItem) item).getRepoLibrary();
		String targetLib = repoLibrary.getPath();
		controller.action(ChangeMockupView.class, RepositoryView.class,
				Transitions.getFadeSlideTransition(topBar, galleryPane, true),
				targetLib);
	}

	@Override
	public int getColumns() {
		return COLS;
	}

	@Override
	protected Actor createToolbarText() {
		return new Label(i18n.m("repository.selectLibrary"), skin);
	}

	@Override
	protected String getNewButtonIcon() {
		return null;
	}

	@Override
	protected HelpSequence getHelpSequence(Controller controller) {
		return null;
	}
}