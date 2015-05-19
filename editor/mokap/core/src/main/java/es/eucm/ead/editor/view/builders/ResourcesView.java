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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.*;
import es.eucm.ead.editor.control.actions.model.AddLibraryReference;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.TakePicture;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.CirclesMenu;
import es.eucm.ead.editor.view.widgets.RepoTile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.CategoryLibrary;
import es.eucm.ead.editor.view.widgets.galleries.CategoryRepository;
import es.eucm.ead.editor.view.widgets.galleries.ProjectResourcesGallery;
import es.eucm.ead.editor.view.widgets.galleries.TabsGallery;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * File view. A list with the children of a given file.
 */
public class ResourcesView implements ViewBuilder, BackListener,
		Platform.FileChooserListener {

	private Controller controller;

	private ProjectResourcesGallery projectResources;

	private CategoryLibrary libraryGallery;

	private CategoryRepository repoGallery;

	private TabsGallery tabsGallery;

	private ClickListener back;

	@Override
	public void initialize(Controller control) {
		this.controller = control;
		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		I18N i18N = applicationAssets.getI18N();

		back = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onBackPressed();
			}
		};

		tabsGallery = new TabsGallery(controller.getApplicationAssets()
				.getI18N().m("images"), SkinConstants.IC_GO, skin, i18N);
		tabsGallery.changeColor(SkinConstants.COLOR_IMAGES);
		tabsGallery.getToolbarIcon().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onBackPressed();
			}
		});

		libraryGallery = new CategoryLibrary(2.25f, 3, controller) {
			@Override
			protected void prepareGalleryItem(Actor actor, final Object elem) {
				actor.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						controller.action(AddLibraryReference.class, elem);
						controller.action(ChangeView.class, SceneView.class);
					}
				});
			}
		};
		libraryGallery.changeCategory(RepoCategories.ELEMENTS.toString());

		repoGallery = new CategoryRepository(2.25f, 3, controller) {
			@Override
			protected void prepareGalleryItem(Actor actor, final Object elem) {
				actor.addListener(new RepoTile.RepoTileListener() {
					@Override
					public void clickedInLibrary(RepoTileEvent event) {
						controller.action(AddLibraryReference.class,
								event.getRepoElement());
						controller.action(ChangeView.class, SceneView.class);
					}
				});
			}

		};
		repoGallery.changeCategory(RepoCategories.ELEMENTS.toString());

		projectResources = new ProjectResourcesGallery(2.25f, 3, controller);

		tabsGallery.setTabs(new String[] { i18N.m("community").toUpperCase(),
				i18N.m("my.library").toUpperCase(),
				i18N.m("project").toUpperCase() }, repoGallery, libraryGallery,
				projectResources);

		CirclesMenu circlesMenu = WidgetBuilder.circlesMenu(Align.right,
				new String[] { SkinConstants.IC_CAMERA, SkinConstants.IC_PHOTO,
						SkinConstants.IC_CLOSE }, new Class[] {
						TakePicture.class, ChooseFile.class, null },
				new Object[][] { null, new Object[] { false, this }, null });

		Actor camera = circlesMenu.findActor(SkinConstants.IC_CAMERA);
		camera.addListener(back);
		Actor photo = circlesMenu.findActor(SkinConstants.IC_PHOTO);
		photo.addListener(back);

		Button addButton = WidgetBuilder.button(SkinConstants.STYLE_ADD);
		addButton.pack();
		circlesMenu.pack();
		WidgetBuilder.actionOnClick(
				addButton,
				ShowContextMenu.class,
				addButton,
				circlesMenu,
				-circlesMenu.getWidth() + addButton.getWidth()
						+ WidgetBuilder.dpToPixels(8), 0f);

		tabsGallery.setDefaultActionButton(addButton);

	}

	@Override
	public Actor getView(Object... args) {
		tabsGallery.loadContents();

		return tabsGallery;
	}

	@Override
	public void release(Controller controller) {
	}

	@Override
	public boolean onBackPressed() {
		controller.action(ChangeView.class, SceneView.class);
		return true;
	}

	@Override
	public void fileChosen(String path, Result result) {
		if (path == null || path.trim().isEmpty()) {
			return;
		}

		FileHandle fh = controller.getEditorGameAssets().absolute(path);
		if (ProjectUtils.isSupportedImage(fh)) {
			addElement(path);
		} else if (ProjectUtils.isSupportedText(fh)) {
			Label label = new Label();
			label.setText(fh.readString());
			controller.action(AddLabel.class, label);
		} else {
			controller.action(ShowToast.class, controller
					.getApplicationAssets().getI18N().m("invalid.resource"));
		}
	}

	private void addElement(String elemPath) {
		ModelEntity sceneElement = controller.getTemplates()
				.createSceneElement(elemPath, false);
		controller.action(AddSceneElement.class, sceneElement);
	}
}
