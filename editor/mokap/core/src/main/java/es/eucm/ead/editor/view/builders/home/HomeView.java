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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.editor.Exit;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.workers.SearchRepo;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.galleries.*;
import es.eucm.ead.editor.view.widgets.galleries.gallerieswithcategories.MyLibraryGallery;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.galleries.gallerieswithcategories.CommunityGallery;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;

public class HomeView implements ViewBuilder, BackListener {

	private Controller controller;

	// Main galleries
	private TabsGallery tabsGallery;

	private ProjectsGallery projectsGallery;

	private MyLibraryGallery myLibraryGallery;

	private CommunityGallery communityGallery;

	// Galleries of specific category
	private TabsCategoryGallery tabsCategory;

	private CategoryRepository categoryRepository;

	private CategoryLibrary categoryLibrary;

	private AbstractWidget view = new AbstractWidget();

	private I18N i18N;

	@Override
	public void initialize(Controller control) {
		this.controller = control;
		i18N = controller.getApplicationAssets().getI18N();

		categoryRepository = new CategoryRepository(2.25f, 3, control);

		categoryLibrary = new CategoryLibrary(2.25f, 3, control);

		tabsGallery = new TabsGallery(controller.getApplicationAssets()
				.getI18N().m("application.title"), control
				.getApplicationAssets().getSkin(), i18N);
		tabsCategory = new TabsCategoryGallery(controller
				.getApplicationAssets().getI18N().m("application.title"),
				control.getApplicationAssets().getSkin(), i18N);
		tabsCategory.getToolbarIcon().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				beforeGallery();
			}
		});

		communityGallery = new CommunityGallery(2.25f, 3, control);
		createCategoryInGallery(communityGallery, categoryRepository,
				SkinConstants.IC_WHITE_MOKAP, "mokaps",
				RepoCategories.MOKAPS.toString(), SkinConstants.COLOR_MOKAPS);
		createCategoryInGallery(communityGallery, categoryRepository,
				SkinConstants.IC_IMAGE, "images",
				RepoCategories.ELEMENTS.toString(), SkinConstants.COLOR_IMAGES);

		projectsGallery = new ProjectsGallery(2.25f, 3, control);

		myLibraryGallery = new MyLibraryGallery(2.25f, 3, control);
		createCategoryInGallery(myLibraryGallery, categoryLibrary,
				SkinConstants.IC_WHITE_MOKAP, "mokaps",
				RepoCategories.MOKAPS.toString(), SkinConstants.COLOR_MOKAPS);
		createCategoryInGallery(myLibraryGallery, categoryLibrary,
				SkinConstants.IC_IMAGE, "images",
				RepoCategories.ELEMENTS.toString(), SkinConstants.COLOR_IMAGES);

		tabsGallery.setTabs(new String[] { i18N.m("community").toUpperCase(),
				i18N.m("my.mokaps").toUpperCase(),
				i18N.m("my.library").toUpperCase() }, communityGallery,
				projectsGallery, myLibraryGallery);
	}

	private void clickOnCategory(String title, String category, Color color,
			ThumbnailsGallery gallery) {
		tabsCategory.setTabs(new String[] { i18N.m("new").toUpperCase() },
				gallery);
		tabsCategory.setSearchText(tabsGallery.getSearchText());

		controller.getWorkerExecutor().cancel(SearchRepo.class,
				communityGallery);
		tabsGallery.remove();
		tabsCategory.changeTitle(title);
		tabsCategory.changeColor(color);
		categoryLibrary.changeCategory(category);
		categoryRepository.changeCategory(category);
		view.addActor(tabsCategory);
		tabsCategory.loadContents();
	}

	private void createCategoryInGallery(MyLibraryGallery gallery,
			final ThumbnailsGallery goalGallery, String icon,
			final String textKey, final String category, final Color color) {
		Actor images = gallery.addCategory(i18N.m(textKey), icon, category,
				color);
		images.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				clickOnCategory(i18N.m(textKey), category, color, goalGallery);
			}
		});
	}

	@Override
	public Actor getView(Object... args) {
		controller.getWorkerExecutor().cancelAll();
		controller.getPreferences().putString(Preferences.LAST_OPENED_GAME, "");
		controller.action(ShowInfoPanel.class, TypePanel.INTRODUCTION,
				Preferences.HELP_INTRODUCTION);

		view.addActor(tabsGallery);
		tabsGallery.loadContents();
		projectsGallery.prepare();

		return view;
	}

	@Override
	public void release(Controller controller) {
		controller.getApplicationAssets().clear();
		projectsGallery.release();
	}

	@Override
	public boolean onBackPressed() {
		if (tabsCategory.getParent() == view) {
			beforeGallery();
		} else {
			controller.action(Exit.class, false);
		}
		return true;
	}

	private void beforeGallery() {
		tabsCategory.remove();
		view.addActor(tabsGallery);
		tabsGallery.setSearchText("");
		tabsGallery.loadContents();
	}

}
