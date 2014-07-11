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

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EnabledOnloadAction;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.Versions;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.JsonExtension;

import java.util.Map;

/**
 * <p>
 * Saves to disk the current game
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None</dd>
 * </dl>
 */
public class Save extends EnabledOnloadAction {

	/**
	 * To be updated when the Model API Changes (rarely)
	 */
	public static final String MODEL_API_VERSION = "1.0";

	public Save() {
		super(true, false);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
	}

	@Override
	public void perform(Object... args) {
		save();
		controller.getCommands().updateSavePoint();
	}

	/**
	 * Does the actual saving following the next steps
	 * <ol>
	 * <li>Updates game version codes to those specified by the application (see
	 * <a href="https://github.com/e-ucm/ead/wiki/Model-API-versions"
	 * target="_blank">https://github.com/e-ucm/ead/wiki/Model-API-versions</a>
	 * and <a href="https://github.com/e-ucm/ead/wiki/Releasing"
	 * target="_blank">https://github.com/e-ucm/ead/wiki/Releasing</a>)</li>
	 * <li>Removes all json files from the project. That is to ensure that if an
	 * entity was deleted from the model, it is actually removed from presistent
	 * state.</li>
	 * <li>Iterates through the model's entities, and saves them to disk. For
	 * each entity, it determines its relative path inside the project.</li>
	 * </ol>
	 */
	private void save() {
		updateGameVersions();
		updateEditState();
		removeAllJsonFilesPersistently();
		for (Map.Entry<String, Object> nextEntry : controller.getModel()
				.listNamedResources()) {
			Object resource = nextEntry.getValue();
			if (resource instanceof ModelEntity) {
				ModelEntity currentEntity = (ModelEntity) resource;
				Exporter.createInitComponent(currentEntity);
			}
			controller.getEditorGameAssets().toJsonPath(resource,
					nextEntry.getKey());
		}
		controller.getCommands().updateSavePoint();
	}

	private void updateGameVersions() {
		String appVersion = controller.getAppVersion();
		ModelEntity game = controller.getModel().getGame();
		Q.getComponent(game, Versions.class).setAppVersion(appVersion);
		Q.getComponent(game, Versions.class).setModelVersion(MODEL_API_VERSION);
	}

	private void updateEditState() {
		if (controller.getViews().getCurrentView() != null) {
			EditState editState = Q.getComponent(controller.getModel()
					.getGame(), EditState.class);
			editState.setView(controller.getViews().getCurrentView().getClass()
					.getName());
			editState.getArguments().clear();
			editState.getArguments().addAll(
					controller.getViews().getCurrentArgs());
		}
	}

	private void removeAllJsonFilesPersistently() {
		String loadingPath = controller.getEditorGameAssets().getLoadingPath();
		deleteJsonFilesRecursively(controller.getEditorGameAssets().absolute(
				loadingPath));
	}

	/**
	 * Deletes the json files from a directory recursively
	 * 
	 * @param directory
	 *            The file object pointing to the root directory from where json
	 *            files must be deleted
	 */
	private void deleteJsonFilesRecursively(FileHandle directory) {
		// Delete dir contents
		if (!directory.exists() || !directory.isDirectory())
			return;

		for (FileHandle child : directory.list()) {
			if (child.isDirectory()) {
				deleteJsonFilesRecursively(child);
			} else {
				if (JsonExtension.hasJsonExtension(child.extension())) {
					child.delete();
				}
			}

		}

		// Remove the directory if it's empty.
		if (directory.list().length == 0) {
			directory.deleteDirectory();
		}
	}
}
