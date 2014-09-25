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
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.Commands.CommandListener;
import es.eucm.ead.editor.control.Commands.CommandsStack;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.editor.model.Q;
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
public class Save extends EditorAction implements CommandListener {

	/**
	 * The suffix appended to the files that are being replaced by the newer
	 * version. These temporal files are kept until the updated versions are
	 * correctly written to disk in case some unexpected error happens while
	 * saving and we have to go back to the temporal backup file.
	 */
	public static final String BACKUP_SUFFIX = ".backup";

	/**
	 * To be updated when the Model API Changes (rarely)
	 */
	public static final String MODEL_API_VERSION = "1.0";

	public Save() {
		super(false, false);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		updateEnable(controller.getCommands());
		controller.getCommands().addCommandListener(this);
	}

	@Override
	public void perform(Object... args) {
		save();
	}

	/**
	 * Does the actual saving following the next steps
	 * <ol>
	 * <li>Updates game version codes to those specified by the application (see
	 * <a href="https://github.com/e-ucm/ead/wiki/Model-API-versions"
	 * target="_blank">https://github.com/e-ucm/ead/wiki/Model-API-versions</a>
	 * and <a href="https://github.com/e-ucm/ead/wiki/Releasing"
	 * target="_blank">https://github.com/e-ucm/ead/wiki/Releasing</a>)</li>
	 * <li>Removes all the modified files from the project. That is to ensure
	 * that if an entity was deleted from the model, it is actually removed from
	 * persistent state.</li>
	 * <li>Iterates through the model's entities, and saves the modified
	 * resources to disk.</li>
	 * </ol>
	 */
	private void save() {
		updateGameVersions();
		deleteRemovedResources();
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		for (Map.Entry<String, Resource> nextEntry : controller.getModel()
				.listNamedResources()) {

			Resource resource = nextEntry.getValue();
			if (resource.isModified()) {

				FileHandle oldFile = gameAssets.resolve(nextEntry.getKey());
				FileHandle tmpFile = null;
				boolean oldFileExists = oldFile.exists();
				if (oldFileExists) {
					tmpFile = oldFile.sibling(oldFile.name() + BACKUP_SUFFIX);
					oldFile.moveTo(tmpFile);
				}
				if (resource.getObject() instanceof ModelEntity) {
					ModelEntity currentEntity = (ModelEntity) resource
							.getObject();
					Exporter.createInitComponent(currentEntity);
				}
				gameAssets.toJsonPath(resource.getObject(), nextEntry.getKey());
				if (oldFileExists) {
					tmpFile.delete();
				}
			}
		}
		controller.getCommands().updateSavePoint();
	}

	/**
	 * Deletes the removed resources available in the model if their id ends
	 * with {@link JsonExtension#DOT_JSON}, see
	 * {@link JsonExtension#hasJsonEnd(String)}.
	 */
	private void deleteRemovedResources() {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		for (String name : controller.getModel().getRemovedResources()) {
			if (JsonExtension.hasJsonEnd(name)) {
				gameAssets.resolve(name).delete();
			}
		}
	}

	private void updateGameVersions() {
		String appVersion = controller.getAppVersion();
		ModelEntity game = controller.getModel().getGame();
		Versions versions = Q.getComponent(game, Versions.class);
		versions.setAppVersion(appVersion);
		versions.setModelVersion(MODEL_API_VERSION);
	}

	@Override
	public void doCommand(Commands commands, Command command) {

		updateEnable(commands);
	}

	@Override
	public void undoCommand(Commands commands, Command command) {

		updateEnable(commands);
	}

	@Override
	public void redoCommand(Commands commands, Command command) {

		updateEnable(commands);
	}

	@Override
	public void savePointUpdated(Commands commands, Command savePoint) {

		updateEnable(commands);
	}

	@Override
	public void cleared(Commands commands) {

		updateEnable(commands);
	}

	@Override
	public void contextPushed(Commands commands) {

		updateEnable(commands);
	}

	@Override
	public void contextPopped(Commands commands, CommandsStack poppedContext,
			boolean merge) {
		updateEnable(commands);
	}

	private void updateEnable(Commands commands) {
		setEnabled(commands.commandsPendingToSave());
	}
}
