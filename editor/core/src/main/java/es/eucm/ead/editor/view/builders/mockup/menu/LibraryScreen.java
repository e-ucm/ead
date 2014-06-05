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
package es.eucm.ead.editor.view.builders.mockup.menu;

import java.util.List;

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

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.RepositoryManager;
import es.eucm.ead.editor.control.RepositoryManager.ProgressListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.builders.mockup.gallery.RepositoryGallery;
import es.eucm.ead.editor.view.widgets.mockup.Notification;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid;
import es.eucm.ead.engine.I18N;

public class LibraryScreen implements ViewBuilder, ProgressListener {

	private static final float MIN_LIBRARY_HEIGHT = 165F;
	private static final String IC_GO_BACK = "ic_goback";
	private static final int COLS = 2;

	private Controller controller;
	private Table view, topWidgets;
	private final RepositoryManager repoManager = new RepositoryManager();
	private Notification refreshingNotif, errorReftreshing;
	private GalleryGrid<TextButton> libsGrid;

	@Override
	public Actor getView(Object... args) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				refreshingNotif.show(view.getStage());
				repoManager.updateLibraries(LibraryScreen.this, controller);
			}
		});
		return view;
	}

	@Override
	public void initialize(Controller control) {
		this.controller = control;

		final Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18n = controller.getApplicationAssets().getI18N();
		Vector2 viewport = controller.getPlatform().getSize();

		this.refreshingNotif = new Notification(skin).text(
				i18n.m("general.mockup.repository.refreshing"))
				.createUndefinedProgressBar();
		this.errorReftreshing = new Notification(skin).text(i18n
				.m("general.mockup.repository.refreshingError"));

		Button backButton = new IconButton(viewport, skin, IC_GO_BACK,
				controller, ChangeView.class, SceneEdition.class);

		topWidgets = new Table(skin).left().top();
		topWidgets.add(backButton).left();
		topWidgets.add(i18n.m("general.mockup.repository.selectLibrary"))
				.expandX();// TODO i18n

		libsGrid = new GalleryGrid<TextButton>(skin, COLS, null, view, false,
				null);
		libsGrid.debug();
		libsGrid.addCaptureListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();

				while (!(target instanceof TextButton) && target != libsGrid) {
					target = target.getParent();
				}
				if (target instanceof TextButton) {
					String targetLib = ((TextButton) target).getText()
							.toString();
					controller.action(ChangeView.class,
							RepositoryGallery.class, repoManager, targetLib);
				}
			}

		});

		final ScrollPane galleryTableScroll = new ScrollPane(libsGrid);
		galleryTableScroll.setScrollingDisabled(true, false);

		view = new Table().debug();
		view.setFillParent(true);
		view.add(topWidgets).expandX().fillX();
		view.row();
		view.add(galleryTableScroll).expand().fillX().top();

	}

	@Override
	public void release(Controller controller) {
		refreshingNotif.hide();
	}

	@Override
	public void finished(boolean succeeded, Controller controller) {
		refreshingNotif.hide();
		if (succeeded) {
			List<String> libs = repoManager.getLibraries();

			if (libs.isEmpty())
				return;

			libsGrid.clear();
			final Skin skin = controller.getApplicationAssets().getSkin();

			for (int i = 0; i < libs.size(); ++i) {
				TextButton lib = new TextButton(libs.get(i), skin);
				lib.getLabel().setWrap(true);
				libsGrid.addItem(lib).minHeight(MIN_LIBRARY_HEIGHT);
			}
		} else {
			errorReftreshing.show(view.getStage(), 2);
		}
	}
}
