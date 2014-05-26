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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.view.widgets.mockup.Notification;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.GameStructure;

public class LaunchExternalEditorListener extends ClickListener {

	private static final String EFFECTS_TAG = "LaunchExternalEditorListener";
	private static final float DEFAULT_NOTIF_TIMEOUT = 2F;

	private Controller controller;
	private I18N i18n;
	private Notification selectImageNotif, noChangesDetectedNotif;
	private Actor stageActor;

	public LaunchExternalEditorListener(Controller controller, I18N i18n,
			Skin skin, Actor stageActor) {
		this.stageActor = stageActor;
		this.controller = controller;
		this.i18n = i18n;

		selectImageNotif = new Notification(skin).text(i18n
				.m("edition.tool.selectImage"));

		noChangesDetectedNotif = new Notification(skin).text(i18n
				.m("edition.tool.noChanges"));
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		Array<Object> selection = controller.getModel().getSelection();
		if (selection.size == 0) {
			selectImageNotif.show(stageActor.getStage(), DEFAULT_NOTIF_TIMEOUT);
			return;
		}
		Object selectedObject = selection.first();
		if (!(selectedObject instanceof ModelEntity)) {
			selectImageNotif.show(stageActor.getStage(), DEFAULT_NOTIF_TIMEOUT);
			return;
		}
		final ModelEntity selectedElement = (ModelEntity) selectedObject;
		if (Model.hasComponent(selectedElement, Image.class)) {
			final Image oldRenderer = Model.getComponent(selectedElement,
					Image.class);
			final String uri = oldRenderer.getUri();
			final FileHandle imageFile = controller.getEditorGameAssets()
					.resolve(uri);
			if (!imageFile.exists())
				return;
			final long lastModif = imageFile.lastModified();
			final String absImagePath = imageFile.file().getAbsolutePath();
			controller.getPlatform().editImage(i18n, absImagePath,
					new FileChooserListener() {

						@Override
						public void fileChosen(String path) {
							if (!imageFile.exists()) {
								// For some reason our URI got deleted
								// after editing it
								return;
							}

							if (lastModif != imageFile.lastModified()) {
								// The edited image has overridden the
								// previous image.
								ModelEntity newElem = controller
										.getEditorGameAssets().copy(
												selectedElement);
								// We must rename this image in order to be able
								// to reload the image through EditorGameAssets
								FileHandle renamedImg = null;
								int i = 0;
								do {
									renamedImg = imageFile.parent().child(
											imageFile.nameWithoutExtension()
													+ ++i
													+ imageFile.extension());

								} while (renamedImg.exists());
								imageFile.moveTo(renamedImg);
								// Clearing the collider will re-create it with
								// the new image
								Image renderer = Model.getComponent(newElem,
										Image.class);
								changeUriAndClear(renderer, renamedImg.file()
										.getAbsolutePath());

								controller.action(ReplaceEntity.class,
										selectedElement, newElem);
							} else {

								if (path == null) {
									Gdx.app.log(EFFECTS_TAG,
											"returned img path is null...");
									noChangesDetectedNotif.show(
											stageActor.getStage(),
											DEFAULT_NOTIF_TIMEOUT);
									return;
								}

								// The edited image was saved in path
								FileHandle savedFile = controller
										.getEditorGameAssets().absolute(path);

								if (savedFile.exists()) {
									replaceRenderers(controller, path,
											selectedElement, oldRenderer);
								}
							}
						}
					});
		} else {
			Gdx.app.log(EFFECTS_TAG, "the user must select an image first...");
			selectImageNotif.show(stageActor.getStage(), DEFAULT_NOTIF_TIMEOUT);
		}
	}

	/**
	 * Removes the old element from it's parent and adds a copy with the
	 * renderer replaced. Replaces the oldRenderer with the one pointed by
	 * newRendererPath for the selectedElement. Assumes that the newRendererPath
	 * is a correct image file, and that selectedElement actually has the
	 * oldRenderer. If the newRendererPath is not inside the
	 * {@link GameStructure#IMAGES_FOLDER}, this method imports it.
	 * 
	 * @param controller
	 * @param newRendererPath
	 * @param selectedElement
	 * @param oldRenderer
	 */
	private void replaceRenderers(Controller controller,
			String newRendererPath, ModelEntity selectedElement,
			Image oldRenderer) {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		String newPath = gameAssets.copyToProjectIfNeeded(newRendererPath,
				Texture.class);
		if (newPath == null) {
			Gdx.app.log(
					EFFECTS_TAG,
					"something went wrong copying to "
							+ "the project the saved image, probably the path didn't exist");
			return;
		}

		ModelEntity newElem = gameAssets.copy(selectedElement);
		Image renderer = Model.getComponent(newElem, Image.class);
		changeUriAndClear(renderer, newPath);

		controller.action(ReplaceEntity.class, selectedElement, newElem);
	}

	private void changeUriAndClear(Image image, String newUri) {
		image.getCollider().clear();
		image.setUri(newUri);
	}
}
