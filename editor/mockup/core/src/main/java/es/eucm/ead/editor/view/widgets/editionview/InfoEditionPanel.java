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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.AddSceneElementFromResource;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.asynk.TakePicture;
import es.eucm.ead.editor.view.builders.LibrariesView;
import es.eucm.ead.editor.view.widgets.HiddenPanel;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.editionview.draw.PaintToolbar;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;

public class InfoEditionPanel extends HiddenPanel {

	private static final float PAD = 30, LITTLE_PAD = 10;

	private Cell cell;

	private Actor sceneEditor;

	private Controller controller;

	private Runnable paintRunnable;

	public InfoEditionPanel(final Controller controller, Skin skin,
			Cell sceneEditorCell, final PaintToolbar paintToolbar) {
		super(skin, "dialog");
		I18N i18n = controller.getApplicationAssets().getI18N();

		this.controller = controller;

		this.cell = sceneEditorCell;
		this.sceneEditor = (Actor) cell.getWidget();

		final IconButton camera = new IconButton("camera250x250", 0, skin,
				"inverted");
		Label cameraText = new Label(i18n.m("edition.info.camera_backgorund")
				+ "\n" + i18n.m("edition.info.camera_picture"), skin);
		cameraText.setAlignment(Align.center);
		cameraText.setFontScale(2f);
		camera.add(cameraText).padLeft(LITTLE_PAD);

		final IconButton paint = new IconButton("paint150x150", 0, skin,
				"inverted");
		Label paintText = new Label(i18n.m("edition.info.paint_image") + "\n"
				+ i18n.m("edition.info.paint_fingers"), skin);
		paintText.setAlignment(Align.center);
		cameraText.setFontScale(1.5f);
		paint.row();
		paint.add(paintText);

		final IconButton repository = new IconButton("repository150x150", 0,
				skin, "inverted");
		Label repositoryText = new Label(
				i18n.m("edition.info.repository_ideas") + "\n"
						+ i18n.m("edition.info.repository_items") + "\n"
						+ i18n.m("edition.info.repository"), skin);
		repositoryText.setAlignment(Align.center);
		cameraText.setFontScale(1.5f);
		repository.row();
		repository.add(repositoryText);

		final IconButton gallery = new IconButton("android_gallery150x150", 0,
				skin, "inverted");
		Label galleryText = new Label(i18n.m("edition.info.gallery_image")
				+ "\n" + i18n.m("edition.info.gallery_device"), skin);
		galleryText.setAlignment(Align.center);
		cameraText.setFontScale(1.5f);
		gallery.row();
		gallery.add(galleryText);

		Table bottom = new Table();
		bottom.add(paint).pad(PAD);
		bottom.add(repository).pad(PAD);
		bottom.add(gallery).pad(PAD);

		add(camera).pad(PAD);
		row();
		add(bottom);

		paintRunnable = new Runnable() {
			@Override
			public void run() {
				paintToolbar.show();
			}

		};

		ChangeListener buttonsListener = new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listenerActor = event.getListenerActor();
				hide();
				if (listenerActor == camera) {
					controller.action(TakePicture.class);
				} else if (listenerActor == repository) {
					controller.action(ChangeMockupView.class,
							LibrariesView.class);
				} else if (listenerActor == gallery) {
					controller.action(AddSceneElementFromResource.class);
				} else if (listenerActor == paint) {
					if (paintToolbar.isShowing()) {
						paintToolbar.hide();
					} else {
						Gdx.app.postRunnable(paintRunnable);

					}
				}
			}
		};

		camera.addListener(buttonsListener);
		repository.addListener(buttonsListener);
		gallery.addListener(buttonsListener);
		paint.addListener(buttonsListener);
	}

	public void show() {
		ModelEntity scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		if (scene.getChildren().size == 0) {
			MockupViews.setUpHiddenPanel(this, ((MockupController) controller)
					.getRootComponent().getStage());
			cell.setWidget(null);
			cell.setWidget(this);
		}
	}

	@Override
	public void hide() {
		MockupViews.removeHitListener(this, ((MockupController) controller)
				.getRootComponent().getStage());
		cell.setWidget(null);
		cell.setWidget(sceneEditor);
		sceneEditor.toBack();
	}

}
