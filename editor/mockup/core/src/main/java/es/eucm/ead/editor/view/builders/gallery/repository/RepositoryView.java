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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.RepositoryManager.OnEntityImportedListener;
import es.eucm.ead.editor.control.RepositoryManager.ProgressListener;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.repository.ImportElement;
import es.eucm.ead.editor.control.actions.editor.repository.UpdateLibraryElements;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.EditionView;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.repository.info.ItemInfo;
import es.eucm.ead.editor.view.builders.gallery.repository.info.RepositoryInfo;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.repository.RepositoryItem;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;
import es.eucm.ead.schema.entities.ModelEntity;

public class RepositoryView extends BaseGallery implements ProgressListener,
		OnEntityImportedListener {

	private static final float DEFAULT_NOTIF_TIMEOUT = 2.5F;
	private static final int COLUMNS = 3;

	private Toasts toasts;
	private ItemInfo<RepositoryItem> info;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		info = new RepositoryInfo(controller, view);
		toasts = ((MockupViews) controller.getViews()).getToasts();
	}

	@Override
	protected Actor createPlayButton() {
		return null;
	}

	@Override
	protected Actor createBackButton() {
		Button back = new IconButton("back80x80", skin);
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeMockupView.class, LibrariesView.class,
						Transitions.getFadeSlideTransition(topBar, galleryPane,
								false));
			}
		});
		return back;
	}

	@Override
	public int getColumns() {
		return COLUMNS;
	}

	@Override
	protected Actor createToolbarText() {
		return null;
	}

	@Override
	protected String getNewButtonIcon() {
		return null;
	}

	@Override
	public void itemClicked(GalleryItem item) {
		toasts.showNotification(i18n.m("repository.importing"));
		controller.action(ImportElement.class,
				((RepositoryItem) item).getElement(), this);
		view.setTouchable(Touchable.disabled);
	}

	@Override
	public void entityImported(ModelEntity entity, Controller controller) {
		view.setTouchable(Touchable.enabled);
		if (entity != null) {
			toasts.hideNotification();
			controller.action(ChangeMockupView.class, EditionView.class,
					Transitions.getFadeSlideTransition(topBar, galleryPane,
							false));
		} else {
			toasts.showNotification(i18n.m("repository.importingError"),
					DEFAULT_NOTIF_TIMEOUT);
		}
	}

	@Override
	public void finished(boolean succeeded, Controller controller) {
		if (!succeeded) {
			toasts.showNotification("repository.refreshingError",
					DEFAULT_NOTIF_TIMEOUT);
		} else {
			toasts.hideNotification();
		}
		super.getView();
	}

	@Override
	protected void loadItems(Array<GalleryItem> items) {
		items.clear();
		Array<ModelEntity> libElems = ((MockupController) controller)
				.getRepositoryManager().getElements();
		for (ModelEntity repoEntity : libElems) {
			items.add(new RepositoryItem(info, controller, repoEntity, this));
		}
	}

	@Override
	public Actor getView(Object... args) {
		galleryGrid.clear();
		((MockupController) controller).getRepositoryManager()
				.setCurrentLibrary(args[0].toString());
		toasts.showNotification(i18n.m("repository.refreshing"));
		controller.getBackgroundExecutor().submit(updatingTask, taskListener);

		return super.view;
	}

	private final BackgroundTask<Boolean> updatingTask = new BackgroundTask<Boolean>() {

		@Override
		public Boolean call() throws Exception {
			controller.action(UpdateLibraryElements.class, RepositoryView.this);
			return false;
		}
	};

	private final BackgroundTaskListener<Boolean> taskListener = new BackgroundTaskListener<Boolean>() {

		@Override
		public void completionPercentage(float percentage) {
		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor, Boolean result) {
			if (result) {
				toasts.hideNotification();
			}
		}

		@Override
		public void error(Throwable e) {
			toasts.showNotification("repository.refreshingError",
					DEFAULT_NOTIF_TIMEOUT);
			Gdx.app.error("RepositoryView", "Error updating elements", e);
		}
	};

	@Override
	protected HelpSequence getHelpSequence(Controller controller) {
		return null;
	}
}
