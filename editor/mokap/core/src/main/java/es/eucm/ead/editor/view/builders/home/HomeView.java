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
package es.eucm.ead.editor.view.builders.home;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.Exit;
import es.eucm.ead.editor.control.actions.editor.Play;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.workers.FeaturedElements;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.RepoTile.RepoTileListener;
import es.eucm.ead.editor.view.widgets.Tabs;
import es.eucm.ead.editor.view.widgets.Tabs.TabEvent;
import es.eucm.ead.editor.view.widgets.Tabs.TabListener;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.ProjectsGallery;
import es.eucm.ead.editor.view.widgets.galleries.RepoGallery;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.response.SearchResponse;
import es.eucm.ead.schemax.ModelStructure;

public class HomeView implements ViewBuilder, BackListener, WorkerListener {

	private Controller controller;

	private LinearLayout view;

	private Container<Actor> content;

	private ProjectsGallery projectsGallery;

	private RepoGallery repoGallery;

	private Tabs tabs;

	@Override
	public void initialize(Controller control) {
		this.controller = control;
		view = new LinearLayout(false);
		view.add(buildToolbar()).expandX();
		view.background(controller.getApplicationAssets().getSkin()
				.getDrawable(SkinConstants.DRAWABLE_GRAY_100));
		view.add(content = new Container<Actor>().fill()).expand(true, true);

		projectsGallery = new ProjectsGallery(1.65f, 3, control);

		repoGallery = new RepoGallery(1.65f, 3, control);
		repoGallery.addListener(new RepoTileListener() {

			@Override
			public void clickedInLibrary(RepoTileEvent event) {
				controller.action(Play.class, controller.getLibraryManager()
						.getRepoElementLibraryFolder(event.getRepoElement())
						.file().getAbsolutePath()
						+ "/" + ModelStructure.CONTENTS_FOLDER);
			}

		});
	}

	private void search() {
		controller.action(ExecuteWorker.class, FeaturedElements.class, this,
				"all");
	}

	@Override
	public Actor getView(Object... args) {
		controller.getWorkerExecutor().cancelAll();
		updateContent(tabs.getSelectedTabIndex());
		controller.getPreferences().putString(Preferences.LAST_OPENED_GAME, "");
		controller.action(ShowInfoPanel.class, TypePanel.INTRODUCTION,
				Preferences.HELP_INTRODUCTION);
		return view;
	}

	@Override
	public void release(Controller controller) {
		controller.getApplicationAssets().clear();
	}

	@Override
	public boolean onBackPressed() {
		controller.action(Exit.class, false);
		return true;
	}

	private void updateContent(int index) {
		switch (index) {
		case 0:
			content.setActor(projectsGallery);
			projectsGallery.load();
			break;
		case 1:
			content.setActor(repoGallery);
			repoGallery.clear();
			search();
			break;
		}
	}

	private Actor buildToolbar() {
		Skin skin = controller.getApplicationAssets().getSkin();
		LinearLayout topRow = new LinearLayout(true);
		topRow.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_MOKAP, null));
		topRow.add(
				WidgetBuilder.label(controller.getApplicationAssets().getI18N()
						.m("application.title"), SkinConstants.STYLE_TOOLBAR))
				.marginLeft(WidgetBuilder.dpToPixels(8));

		LinearLayout toolbar = new LinearLayout(false);
		toolbar.add(topRow).expandX();
		toolbar.add(buildTabs()).left();
		toolbar.background(skin.getDrawable(SkinConstants.DRAWABLE_TOOLBAR));
		toolbar.backgroundColor(skin.getColor(SkinConstants.COLOR_BROWN_MOKA));
		return toolbar;
	}

	private Actor buildTabs() {
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		tabs = new Tabs(skin);
		tabs.setItems(i18N.m("my.mokaps").toUpperCase(), i18N.m("community")
				.toUpperCase());
		tabs.addListener(new TabListener() {

			@Override
			public void changed(TabEvent event) {
				updateContent(event.getTabIndex());
			}
		});
		return tabs;
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		Object firstResult = results[0];
		if (!(firstResult instanceof SearchResponse)) {
			RepoElement elem = (RepoElement) firstResult;
			if (controller.getLibraryManager().isMokap(elem)) {
				Pixmap repoThumbnail = (Pixmap) results[1];
				Texture thumbnailTex = new Texture(repoThumbnail);
				repoGallery.add(elem, repoThumbnail, thumbnailTex);
			}
		}
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
