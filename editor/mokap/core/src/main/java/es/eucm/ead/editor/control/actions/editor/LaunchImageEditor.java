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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

/**
 * <p>
 * Launches an image editor that could be used to add effects to an
 * {@link Image}.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * Not needed. The {@link ModelEntity} is retrieved from the
 * {@link Selection#SCENE_ELEMENT}.
 * </dl>
 */
public class LaunchImageEditor extends EditorAction implements
		FileChooserListener {

	private static final String LOG_TAG = "LaunchExternalEditor";

	private ModelEntity selectedElement;

	private FileHandle imageFile;

	private String savedImageAbsolutePatth;

	public LaunchImageEditor() {
		super(true, false);
	}

	@Override
	public void perform(Object... args) {
		selectedElement = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		Image image = Q.getComponent(selectedElement, Image.class);
		String uri = image.getUri();
		imageFile = controller.getEditorGameAssets().resolve(uri);
		if (!imageFile.exists()) {
			return;
		}
		savedImageAbsolutePatth = imageFile.file().getAbsolutePath();
		I18N i18n = controller.getApplicationAssets().getI18N();
		controller.getPlatform().editImage(i18n, savedImageAbsolutePatth, this);
	}

	@Override
	public void fileChosen(String path) {
		if (path == null) {
			// Something went wrong
			Gdx.app.error(LOG_TAG,
					"Something went wrong retrieving the edited image path.");
			return;
		}
		if (!imageFile.exists()) {
			// For some reason our URI got deleted
			// after editing it
			Gdx.app.error(LOG_TAG, "The edited image path is: " + path
					+ ", but the image doesn't exist.");
			return;
		}

		if (!savedImageAbsolutePatth.equals(imageFile.file().getAbsolutePath())) {
			// The edited image has overridden the
			// previous image.
			// We must rename this image in order to be able
			// to reload the image through EditorGameAssets
			FileHandle renamedImg = ProjectUtils.getNonExistentFile(
					imageFile.parent(), imageFile.nameWithoutExtension(),
					imageFile.extension());
			imageFile.moveTo(renamedImg);
			// Clearing the collider will re-create it with
			// the new image
			replaceRenderers(renamedImg.file().getAbsolutePath(), false);

		} else {

			// The edited image was saved in the provided path
			FileHandle savedFile = controller.getEditorGameAssets().absolute(
					path);

			if (savedFile.exists()) {
				replaceRenderers(savedFile.file().getAbsolutePath(), true);
			}
		}
	}

	/**
	 * Removes the old element from it's parent and adds a copy with the
	 * renderer replaced. Replaces the oldRenderer with the one pointed by
	 * newRendererPath for the selectedElement. Assumes that the newRendererPath
	 * is a correct image file. If the newRendererPath is not inside the
	 * {@link GameStructure#IMAGES_FOLDER}, this method imports it.
	 * 
	 * @param newRendererPath
	 */
	private void replaceRenderers(String newRendererPath, boolean copyToProject) {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		if (copyToProject) {
			newRendererPath = gameAssets.copyToProjectIfNeeded(newRendererPath,
					Texture.class);
			if (newRendererPath == null) {
				Gdx.app.log(
						LOG_TAG,
						"something went wrong copying to "
								+ "the project the saved image, probably the path didn't exist");
				return;
			}
		}

		ModelEntity newElem = gameAssets.copy(selectedElement);
		Image renderer = Q.getComponent(newElem, Image.class);
		changeUriAndClear(renderer, newRendererPath);
		replaceAndSelect(newElem);

	}

	private void replaceAndSelect(ModelEntity newElem) {
		controller.action(ReplaceEntity.class, controller.getModel()
				.getSelection().getSingle(Selection.EDITED_GROUP),
				selectedElement, newElem);
		controller.action(SetSelection.class, Selection.EDITED_GROUP,
				Selection.SCENE_ELEMENT, newElem);
	}

	private void changeUriAndClear(Image image, String newUri) {
		image.getCollider().clear();
		image.setUri(newUri);
	}
}
