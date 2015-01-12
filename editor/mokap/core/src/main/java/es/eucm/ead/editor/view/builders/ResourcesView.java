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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.Tabs;
import es.eucm.ead.editor.view.widgets.Tabs.TabEvent;
import es.eucm.ead.editor.view.widgets.Tabs.TabListener;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.LibraryGallery;
import es.eucm.ead.editor.view.widgets.galleries.ProjectResourcesGallery;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

/**
 * File view. A list with the children of a given file.
 */
public class ResourcesView implements ViewBuilder, BackListener {

	private Controller controller;

	private LinearLayout view;

	private Container<Actor> container;

	private ProjectResourcesGallery projectResources;

	private LibraryGallery libraryGallery;

	private Tabs tabs;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		I18N i18N = applicationAssets.getI18N();
		view = new LinearLayout(false);
		view.background(controller.getApplicationAssets().getSkin()
				.getDrawable(SkinConstants.DRAWABLE_GRAY_100));
		view.add(buildToolbar(skin, i18N)).expandX();
		view.add(container = new Container<Actor>().fill()).expand(true, true);
		projectResources = new ProjectResourcesGallery(3.15f, 4, controller);
		libraryGallery = new LibraryGallery(3.15f, 4, controller);
	}

	@Override
	public Actor getView(Object... args) {
		updateContent(tabs.getSelectedTabIndex());
		return view;
	}

	@Override
	public void release(Controller controller) {
	}

	private Actor buildTabs(Skin skin, I18N i18N) {
		tabs = new Tabs(skin);
		tabs.setItems(i18N.m("project").toUpperCase(), i18N.m("library")
				.toUpperCase());
		tabs.addListener(new TabListener() {

			@Override
			public void changed(TabEvent event) {
				updateContent(event.getTabIndex());
			}
		});
		return tabs;
	}

	private void updateContent(int index) {
		container.setActor(null);
		switch (index) {
		case 0:
			container.setActor(projectResources);
			libraryGallery.release();
			projectResources.prepare();
			break;
		case 1:
			container.setActor(libraryGallery);
			projectResources.release();
			libraryGallery.prepare();
			break;
		}
	}

	private Actor buildToolbar(Skin skin, I18N i18N) {
		LinearLayout toolbar = new LinearLayout(true).background(skin
				.getDrawable(SkinConstants.DRAWABLE_BROWN_TOOLBAR));
		toolbar.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_GO, null,
				ChangeView.class, SceneView.class));
		toolbar.add(buildTabs(skin, i18N)).expandX();
		return toolbar;
	}

	@Override
	public boolean onBackPressed() {
		controller.action(ChangeView.class, SceneView.class);
		return true;
	}
}
