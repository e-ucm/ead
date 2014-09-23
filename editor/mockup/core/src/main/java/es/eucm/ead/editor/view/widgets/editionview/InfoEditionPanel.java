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
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

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
		this.sceneEditor = (Actor) cell.getActor();

		final IconButton camera = new IconButton("camera250x250", 0, skin,
				"inverted");
		Label cameraText = new Label(i18n.m("edition.info.camera_backgorund"),
				skin);
		cameraText.setAlignment(Align.center);
		cameraText.setWrap(true);
		camera.add(cameraText).expandX().fillX();

		final IconButton paint = new IconButton("paint150x150", 0, skin,
				"inverted");
		Label paintText = new Label(i18n.m("edition.info.paint_image"), skin);
		Value smallTextWidth = Value.percentWidth(.3f, this);
		paintText.setWrap(true);
		paintText.setAlignment(Align.center);
		paint.row();
		paint.add(paintText).width(smallTextWidth);

		final IconButton repository = new IconButton("repository150x150", 0,
				skin, "inverted");
		Label repositoryText = new Label(
				i18n.m("edition.info.repository_ideas"), skin);
		repositoryText.setAlignment(Align.center);
		repositoryText.setWrap(true);
		repository.row();
		repository.add(repositoryText).width(smallTextWidth);

		final IconButton gallery = new IconButton("android_gallery150x150", 0,
				skin, "inverted");
		Label galleryText = new Label(i18n.m("edition.info.gallery_image"),
				skin);
		galleryText.setAlignment(Align.center);
		galleryText.setWrap(true);
		gallery.row();
		gallery.add(galleryText).width(smallTextWidth);

		add(camera).colspan(3).expandX().fillX();
		row();
		add(paint).expandY().fillY();
		add(repository).expandY().fillY();
		add(gallery).expandY().fillY();

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
			cell.setActor(null);
			cell.setActor(this);
		}
	}

	@Override
	public void hide() {
		MockupViews.removeHitListener(this, ((MockupController) controller)
				.getRootComponent().getStage());
		cell.setActor(null);
		cell.setActor(sceneEditor);
		sceneEditor.toBack();
	}

}
