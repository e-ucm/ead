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

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.editor.platform.MockupPlatform.ProjectSentListener;
import es.eucm.ead.editor.utils.ZipUtils;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schemax.GameStructure;

/**
 * <p>
 * Exports the current project to a given location decided by the user.
 * </p>
 * 
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None.</dd>
 * </dl>
 * 
 */
public class ExportMockupProject extends BackgroundExecutorAction<FileHandle> {

	private static final String EXPORT_TAG = "ExportMockupProject";

	@Override
	protected String getProcessingI18N() {
		return "project.exporting";
	}

	@Override
	protected String getErrorProcessingI18N() {
		return "project.exportingError";
	}

	@Override
	protected FileHandle doInBackground() {
		EditorGameAssets editorGameAssets = controller.getEditorGameAssets();
		String loadingPath = editorGameAssets.getLoadingPath();

		if (loadingPath == null || loadingPath.isEmpty()) {
			Gdx.app.error(EXPORT_TAG, "You must open a game before exporting");
			return null;
		}

		FileHandle projectDir = editorGameAssets.resolve(loadingPath);
		if (!projectDir.exists()) {
			Gdx.app.error(EXPORT_TAG, "The project directory : " + loadingPath
					+ ", does NOT exist!");
			return null;
		}

		if (!projectDir.isDirectory()) {
			Gdx.app.debug(EXPORT_TAG, "The project file : " + loadingPath
					+ ", is NOT a directory!");
			return null;
		}

		FileHandle gameFile = projectDir.child(GameStructure.GAME_FILE);

		if (!gameFile.exists() || gameFile.isDirectory()) {
			Gdx.app.debug(EXPORT_TAG, "The project : " + loadingPath
					+ ", does NOT have a " + GameStructure.GAME_FILE
					+ " file! (or, at least, not a valid one)");
			return null;
		}

		// Force a save
		controller.action(ForceSave.class);

		// Copy the current project to a TEMP file
		FileHandle currentProjectFile = editorGameAssets
				.resolve(editorGameAssets.getLoadingPath());

		Documentation doc = Q.getComponent(controller.getModel().getGame(),
				Documentation.class);
		String exportedProjectName = doc.getName();
		if (exportedProjectName != null) {
			exportedProjectName = exportedProjectName.trim();
			if (exportedProjectName.isEmpty()) {
				exportedProjectName = currentProjectFile.name();
			}
		} else {
			exportedProjectName = currentProjectFile.name();
		}

		String tempDirName = currentProjectFile.name() + "_temp";
		FileHandle projectsDir = ProjectsView.MOCKUP_PROJECT_FILE;
		FileHandle tempPath = null;
		int i = 0;
		do {
			tempPath = projectsDir.child(tempDirName + (++i));
		} while (tempPath.exists());

		tempPath.mkdirs();

		currentProjectFile.copyTo(tempPath);

		// Zip the TEMP file
		if (!tempPath.exists()) {
			Gdx.app.error(EXPORT_TAG, "The project ("
					+ currentProjectFile.file().getAbsolutePath()
					+ ") was not copied to the temporal folder: "
					+ tempPath.file().getAbsolutePath());
			return null;
		}
		FileHandle outputZip = projectsDir.child(exportedProjectName
				+ MockupController.EXPORT_EXTENSION);

		ZipUtils.zip(tempPath, outputZip);

		tempPath.deleteDirectory();

		return outputZip;
	}

	@Override
	protected void onPostExecute(final FileHandle result) {
		if (result != null) {
			MockupPlatform platform = (MockupPlatform) controller.getPlatform();
			platform.sendProject(result, controller.getApplicationAssets()
					.getI18N(), new ProjectSentListener() {

				@Override
				public void projectSent(boolean success) {
					if (!success) {
						if (result.exists()) {
							// result.delete();
						}
					}
				}
			});
		}
	}
}
