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
package es.eucm.ead.editor.view.builders.gallery;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.RepositoryManager.OnEntityImportedListener;
import es.eucm.ead.editor.control.RepositoryManager.ProgressListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.repository.ImportElement;
import es.eucm.ead.editor.control.actions.editor.repository.UpdateLibraryElements;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.view.builders.EditionView;
import es.eucm.ead.editor.view.builders.LibrariesView;
import es.eucm.ead.editor.view.widgets.Notification;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;
import es.eucm.ead.editor.view.widgets.gallery.GalleryItem;
import es.eucm.ead.editor.view.widgets.gallery.RepositoryItem;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;
import es.eucm.ead.schema.entities.ModelEntity;

public class RepositoryView extends BaseGallery implements ProgressListener,
		OnEntityImportedListener {

	private static final float DEFAULT_NOTIF_TIMEOUT = 2.5F;

	private Notification importingNotif, updatingNotif, errorUpdating,
			errorImporting;

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		this.importingNotif = new Notification(skin).text(i18n
				.m("repository.importing"));
		this.updatingNotif = new Notification(skin).text(i18n
				.m("repository.refreshing"));
		this.errorUpdating = new Notification(skin).text(i18n
				.m("repository.refreshingError"));
		this.errorImporting = new Notification(skin).text(i18n
				.m("repository.importingError"));
	}

	@Override
	protected Actor createPlayButton() {
		Button play = new ToolbarIcon("play80x80", ICON_PAD, iconSize, skin,
				"inverted");
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeView.class, PlayView.class);
			}
		});
		return play;
	}

	@Override
	protected Actor createBackButton() {
		Button back = new ToolbarIcon("back80x80", ICON_PAD, iconSize, skin);
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeView.class, LibrariesView.class);
			}
		});
		return back;
	}

	@Override
	protected int getColumns() {
		return 3;
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
		importingNotif.show(getStage());
		controller.action(ImportElement.class,
				((RepositoryItem) item).getElement(), this);
		view.setTouchable(Touchable.disabled);
	}

	@Override
	public void entityImported(ModelEntity entity, Controller controller) {
		view.setTouchable(Touchable.enabled);
		if (entity != null) {
			controller.action(ChangeView.class, EditionView.class);
		} else {
			errorImporting.show(getStage(), DEFAULT_NOTIF_TIMEOUT);
		}
		importingNotif.hide();
	}

	@Override
	public void finished(boolean succeeded, Controller controller) {
		if (!succeeded) {
			errorUpdating.show(getStage(), DEFAULT_NOTIF_TIMEOUT);
		}
		controller.getBackgroundExecutor().submit(loadingTask, taskListener);
	}

	@Override
	protected void loadItems(Array<GalleryItem> items) {
		items.clear();
		Array<ModelEntity> libElems = ((MockupController) controller)
				.getRepositoryManager().getElements();
		for (ModelEntity repoEntity : libElems) {
			items.add(new RepositoryItem(controller, repoEntity, this));
		}
	}

	private Stage getStage() {
		return topBar.getStage();
	}

	@Override
	public Actor getView(Object... args) {
		((MockupController) controller).getRepositoryManager()
				.setCurrentLibrary(args[0].toString());
		Gdx.app.postRunnable(update);

		return super.view;
	}

	@Override
	public void release(Controller controller) {
		galleryGrid.clear();
		updatingNotif.hide();
		importingNotif.hide();
	}

	private final Runnable update = new Runnable() {

		@Override
		public void run() {
			updatingNotif.show(getStage());
			controller.getBackgroundExecutor().submit(updatingTask,
					taskListener);
		}
	};

	private final BackgroundTask<Boolean> updatingTask = new BackgroundTask<Boolean>() {

		@Override
		public Boolean call() throws Exception {
			controller.action(UpdateLibraryElements.class, RepositoryView.this);
			return false;
		}
	};

	private final BackgroundTask<Boolean> loadingTask = new BackgroundTask<Boolean>() {

		@Override
		public Boolean call() throws Exception {
			RepositoryView.super.getView();
			return true;
		}
	};

	private final BackgroundTaskListener<Boolean> taskListener = new BackgroundTaskListener<Boolean>() {

		@Override
		public void completionPercentage(float percentage) {
		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor, Boolean result) {
			if (result) {
				updatingNotif.hide();
			}
		}

		@Override
		public void error(Throwable e) {
			updatingNotif.hide();
			errorUpdating.show(getStage(), DEFAULT_NOTIF_TIMEOUT);
		}
	};

	@Override
	protected HelpSequence getHelpSequence(Controller controller) {
		return null;
	}
}
