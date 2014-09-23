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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.RepositoryManager.ProgressListener;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.repository.UpdateLibraries;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.builders.gallery.RepositoryView;
import es.eucm.ead.editor.view.widgets.GridPanel;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.RepoLibrary;

public class LibrariesView implements ViewBuilder, ProgressListener {

	private static final int COLS = 2;

	private static final float ERROR_NOTIF_TIMEOUT = 3F;
	private static final String IC_GO_BACK = "back80x80";

	private I18N i18n;
	private Controller controller;
	private Table view, topWidgets;
	private Toasts toasts;
	private GridPanel<TextButton> libsGrid;
	private Runnable updateLibraries = new Runnable() {
		@Override
		public void run() {
			toasts.showNotification(i18n.m("repository.refreshing"));
			controller.action(UpdateLibraries.class, LibrariesView.this);
		}
	};

	@Override
	public Actor getView(Object... args) {
		libsGrid.clear();
		Gdx.app.postRunnable(updateLibraries);
		return view;
	}

	@Override
	public void initialize(Controller control) {
		this.controller = control;

		toasts = ((MockupViews) controller.getViews()).getToasts();
		final Skin skin = controller.getApplicationAssets().getSkin();
		i18n = controller.getApplicationAssets().getI18N();
		Vector2 viewport = controller.getPlatform().getSize();

		Button backButton = new ToolbarIcon(IC_GO_BACK, viewport.y
				* BaseGallery.ICON_PAD, viewport.y * BaseGallery.ICON_SIZE,
				skin);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeMockupView.class, EditionView.class);
			}
		});

		final float toolbarSize = controller.getPlatform().getSize().y
				* BaseGallery.TOOLBAR_SIZE;
		topWidgets = new Toolbar(skin, "white_top");
		topWidgets.add(backButton).left().expandY().fill();
		topWidgets.add(i18n.m("repository.selectLibrary")).expandX();

		libsGrid = new GridPanel<TextButton>(COLS,
				BaseGallery.DEFAULT_ENTITY_SPACING);
		libsGrid.addCaptureListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();

				while (!(target instanceof TextButton) && target != libsGrid) {
					target = target.getParent();
				}
				if (target instanceof TextButton) {
					RepoLibrary repoLibrary = (RepoLibrary) (((TextButton) target))
							.getUserObject();
					String targetLib = repoLibrary.getPath();
					controller.action(ChangeMockupView.class,
							RepositoryView.class, targetLib);
				}
			}

		});

		ScrollPane galleryTableScroll = new ScrollPane(libsGrid);
		galleryTableScroll.setScrollingDisabled(true, false);

		view = new Table();
		view.setFillParent(true);
		view.add(topWidgets).expandX().fill().height(toolbarSize);
		view.row();
		view.add(galleryTableScroll).expand().fillX().top();

	}

	@Override
	public void release(Controller controller) {
		toasts.hideNotification();
	}

	@Override
	public void finished(boolean succeeded, Controller controller) {
		if (succeeded) {
			toasts.hideNotification();
			Array<RepoLibrary> libs = ((MockupController) controller)
					.getRepositoryManager().getRepoLibraries();

			if (libs.size == 0)
				return;

			Skin skin = controller.getApplicationAssets().getSkin();

			for (int i = 0; i < libs.size; ++i) {
				TextButton libButton = new TextButton(libs.get(i).getName(),
						skin, "white");
				libButton.setUserObject(libs.get(i));
				libButton.getLabel().setWrap(true);
				libsGrid.addItem(libButton).minHeight(
						BaseGallery.MIN_ITEM_HEIGHT);
			}
		} else {
			toasts.showNotification(i18n.m("repository.refreshingError"),
					ERROR_NOTIF_TIMEOUT);
		}
	}
}