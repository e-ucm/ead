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
import es.eucm.ead.GameStructure;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.Versions;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.editor.assets.EditorGameAssets;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Saves to disk the current game
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None</dd>
 * </dl>
 */
public class Save extends EditorAction {

	public Save() {
		super(false, false);
	}

	@Override
	public void perform(Object... args) {
		save();
		controller.getCommands().updateSavePoint();
	}

	/**
	 * This method needs to be overriden or the first time isEnabled() is called
	 * always returns false, as the Save action is not created and therefore its
	 * enabled property cannot be updated after any other action is executed
	 */
	@Override
	public boolean isEnabled() {
		return controller.getCommands().commandsPendingToSave();
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
		removeAllJsonFilesPersistently();
		for (Map.Entry<String, ModelEntity> entry : controller.getModel()
				.getScenes().entrySet()) {
			String relativePath = relativePathForInteractiveElement(entry
					.getKey());
			controller.getEditorGameAssets().toJsonPath(entry.getValue(),
					relativePath);
		}
	}

	private void updateGameVersions() {
		String appVersion = controller.getAppVersion();
		String modelVersion = "1.0";
		ModelEntity game = controller.getModel().getGame();
		Model.getComponent(game, Versions.class).setAppVersion(appVersion);
		Model.getComponent(game, Versions.class).setModelVersion(modelVersion);
	}

	/**
	 * Removes all json files from disk under the
	 * {@link EditorGameAssets#loadingPath} folder.
	 * 
	 * NOTE: This method should only be invoked from {@link #save()}, before the
	 * model is saved to disk
	 */
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
				if (child.file().getAbsolutePath().toLowerCase()
						.endsWith(GameStructure.JSON_EXTENSION.toLowerCase())) {
					child.delete();
				}
			}

		}

		// Remove the directory if it's empty.
		if (directory.list().length == 0) {
			directory.deleteDirectory();
		}
	}

	/**
	 * Resolves the relative path in the game folder for the given {@code id}.
	 * For example, if {@code id} is "scene0" this method will assume it is a
	 * scene and returns "scenes/scene0.json".
	 * 
	 * Works either for scenes or the game file.
	 * 
	 * @param id
	 *            The id of the {@link ModelEntity} whose relative path is to be
	 *            found (e.g. "scene0").
	 * @return The relative path for the given {@code id} (e.g.
	 *         scenes/scene0.json). Returns {@code null} if the id does not
	 *         correspond to any known {@code ModelEntity}.
	 */
	private String relativePathForInteractiveElement(String id) {
		String jsonRegex = Pattern.quote(GameStructure.JSON_EXTENSION);
		// Game Regex: game(.json)?
		String gameRegex = GameStructure.GAME_FILE + "(" + jsonRegex + ")?";

		Pattern pattern = Pattern.compile(gameRegex);
		Matcher matcher = pattern.matcher(id.toLowerCase());
		if (matcher.matches()) {
			return id.toLowerCase().endsWith(GameStructure.JSON_EXTENSION) ? id
					: id + GameStructure.JSON_EXTENSION;
		}

		// Scene regex: sceneXXX(.json)?
		// Where XXX is an arbitrary length number
		String sceneRegex = "scene\\d+("
				+ Pattern.quote(GameStructure.JSON_EXTENSION) + ")?";
		pattern = Pattern.compile(sceneRegex);

		matcher = pattern.matcher(id.toLowerCase());
		if (matcher.matches()) {
			return controller.getEditorGameAssets().toCanonicalPath(
					GameStructure.SCENES_PATH
							+ (id.toLowerCase().endsWith(
									GameStructure.JSON_EXTENSION) ? id : id
									+ GameStructure.JSON_EXTENSION));
		}
		return null;
	}

}
