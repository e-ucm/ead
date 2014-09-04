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
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.GameStructure;

/**
 * <p>
 * Launches an external editor that could be used to add effects to an
 * {@link Image}.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * Not needed. The {@link ModelEntity} is retrieved from the
 * {@link Selection#SCENE_ELEMENT}.
 * </dl>
 */
public class LaunchExternalEditor extends EditorAction implements
		FileChooserListener {

	private static final String EFFECTS_TAG = "LaunchExternalEditor";

	private ModelEntity selectedElement;

	private Image image;

	private FileHandle imageFile;

	private long lastModif;

	public LaunchExternalEditor() {
		super(true, false);
	}

	@Override
	public void perform(Object... args) {
		Object elemObject = controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		if (!(elemObject instanceof ModelEntity)) {
			return;
		}
		selectedElement = (ModelEntity) elemObject;
		if (!Q.hasComponent(selectedElement, Image.class)) {
			return;
		}
		image = Q.getComponent(selectedElement, Image.class);
		String uri = image.getUri();
		imageFile = controller.getEditorGameAssets().resolve(uri);
		if (!imageFile.exists())
			return;
		lastModif = imageFile.lastModified();
		String absImagePath = imageFile.file().getAbsolutePath();
		I18N i18n = controller.getApplicationAssets().getI18N();
		controller.getPlatform().editImage(i18n, absImagePath, this);
	}

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
			ModelEntity newElem = controller.getEditorGameAssets().copy(
					selectedElement);
			Q.getComponent(newElem, Parent.class).setParent(
					(ModelEntity) controller.getModel().getSelection()
							.getSingle(Selection.SCENE));
			// We must rename this image in order to be able
			// to reload the image through EditorGameAssets
			FileHandle renamedImg = null;
			int i = 0;
			do {
				renamedImg = imageFile.parent().child(
						imageFile.nameWithoutExtension() + ++i
								+ imageFile.extension());

			} while (renamedImg.exists());
			imageFile.moveTo(renamedImg);
			// Clearing the collider will re-create it with
			// the new image
			Image renderer = Q.getComponent(newElem, Image.class);
			changeUriAndClear(renderer, renamedImg.file().getAbsolutePath());

			controller.action(ReplaceEntity.class, selectedElement, newElem);
			controller.action(SetSelection.class, Selection.SCENE,
					Selection.SCENE_ELEMENT);
		} else {

			// The edited image was saved in path
			FileHandle savedFile = controller.getEditorGameAssets().absolute(
					path);

			if (savedFile.exists()) {
				replaceRenderers(controller,
						savedFile.file().getAbsolutePath(), selectedElement,
						image);
			}
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
		Q.getComponent(newElem, Parent.class).setParent(
				(ModelEntity) controller.getModel().getSelection()
						.getSingle(Selection.SCENE));
		Image renderer = Q.getComponent(newElem, Image.class);
		changeUriAndClear(renderer, newPath);

		controller.action(ReplaceEntity.class, selectedElement, newElem);
		controller.action(SetSelection.class, Selection.SCENE,
				Selection.SCENE_ELEMENT);
	}

	private void changeUriAndClear(Image image, String newUri) {
		image.getCollider().clear();
		image.setUri(newUri);
	}
}
