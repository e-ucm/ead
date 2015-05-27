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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.RepoTile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.CategoryLibrary;
import es.eucm.ead.editor.view.widgets.galleries.CategoryRepository;
import es.eucm.ead.editor.view.widgets.galleries.ProjectSoundsGallery;
import es.eucm.ead.editor.view.widgets.galleries.TabsGallery;
import es.eucm.ead.editor.view.widgets.selectors.Selector;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.i18n.I18N;

/**
 * Audio view. A list with the audio files from the project, library and
 * repository.
 */
public class SoundsView implements ViewBuilder {

	private Controller controller;

	private Selector.SelectorListener<String> selector;

	private ProjectSoundsGallery projectResources;

	private CategoryLibrary libraryGallery;

	private CategoryRepository repoGallery;

	private BackTabsGallery tabsGallery;

	@Override
	public void initialize(Controller control) {
		this.controller = control;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		I18N i18N = applicationAssets.getI18N();

		tabsGallery = new BackTabsGallery(controller.getApplicationAssets()
				.getI18N().m("sounds"), SkinConstants.IC_GO, skin, i18N);
		tabsGallery.changeColor(SkinConstants.COLOR_SOUNDS);
		tabsGallery.getToolbarIcon().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tabsGallery.onBackPressed();
			}
		});

		libraryGallery = new CategoryLibrary(2.25f, 3, controller) {
			@Override
			protected void prepareGalleryItem(final Actor actor,
					final Object elem) {
				actor.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						processSoundRepoElement((RepoElement) elem);
					}
				});
			}
		};
		libraryGallery.changeCategory(RepoCategories.SOUNDS.toString());

		repoGallery = new CategoryRepository(2.25f, 3, controller) {
			@Override
			protected void prepareGalleryItem(Actor actor, final Object elem) {

				actor.addListener(new RepoTile.RepoTileListener() {
					@Override
					public void clickedInLibrary(RepoTileEvent event) {
						processSoundRepoElement((RepoElement) elem);
					}
				});
			}

		};
		repoGallery.changeCategory(RepoCategories.SOUNDS.toString());

		projectResources = new ProjectSoundsGallery(2.25f, 3, controller) {
			@Override
			protected void selected(String path) {
				selector.selected(path);
			}
		};

		tabsGallery.setTabs(new String[] { i18N.m("community").toUpperCase(),
				i18N.m("my.library").toUpperCase(),
				i18N.m("project").toUpperCase() }, repoGallery, libraryGallery,
				projectResources);

		Button defaultActionButton = WidgetBuilder
				.circleButton(SkinConstants.IC_ADD_SOUND);
		defaultActionButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				projectResources.askForAudio();
			}
		});
		tabsGallery.setDefaultActionButton(defaultActionButton);

	}

	private void processSoundRepoElement(RepoElement element) {
		try {
			FileHandle repoElementContentsFolder = controller
					.getLibraryManager().getRepoElementContentsFolder(element);
			FileHandle entityFile = repoElementContentsFolder
					.child(ModelStructure.ENTITY_FILE);
			ModelEntity modelEntity = controller.getEditorGameAssets()
					.fromJson(ModelEntity.class, entityFile);

			Behavior soundBehavior = Q
					.getComponent(modelEntity, Behavior.class);
			PlaySound playSound = (PlaySound) soundBehavior.getEffects()
					.first();
			String uri = playSound.getUri();
			FileHandle soundUri = repoElementContentsFolder.child(uri);
			String soundPath = controller.getEditorGameAssets()
					.copyToProjectIfNeeded(soundUri.path(), Music.class);
			if (soundPath != null) {
				selector.selected(soundPath);
			} else {
				controller
						.action(ShowToast.class,
								controller.getApplicationAssets().getI18N()
										.m("invalid.resource"));
			}
		} catch (Exception ex) {
			Gdx.app.error("SoundView",
					"Couldn't process a sound from a RepoElement", ex);
			controller.action(ShowToast.class, controller
					.getApplicationAssets().getI18N().m("invalid.resource"));
		}
	}

	@Override
	public Actor getView(Object... args) {
		selector = (Selector.SelectorListener<String>) args[0];
		tabsGallery.loadContents();

		return tabsGallery;
	}

	@Override
	public void release(Controller controller) {
	}

	private class BackTabsGallery extends TabsGallery implements BackListener {

		public BackTabsGallery(String title, String icon, Skin skin, I18N i18N) {
			super(title, icon, skin, i18N);
		}

		@Override
		public boolean onBackPressed() {
			selector.cancelled();
			return true;
		}
	}
}
