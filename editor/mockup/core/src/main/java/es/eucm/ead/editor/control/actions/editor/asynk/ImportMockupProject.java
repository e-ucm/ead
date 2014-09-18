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
package es.eucm.ead.editor.control.actions.editor.asynk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.editor.utils.ZipUtils;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;

/**
 * <p>
 * Imports a given project inside this editor's workspace.
 * </p>
 * 
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None.</dd>
 * </dl>
 * 
 */
public class ImportMockupProject extends BackgroundExecutorAction<FileHandle> {

	private static final String IMPORT_TAG = "ImportMockupProject";

	@Override
	protected String getProcessingI18N() {
		return "project.importing";
	}

	@Override
	protected String getErrorProcessingI18N() {
		return "project.importingError";
	}

	@Override
	protected FileHandle doInBackground() {
		MockupPlatform platform = (MockupPlatform) controller.getPlatform();
		String importProjectPath = platform.getImportProjectPath();

		FileHandle inputProjectZip = controller.getEditorGameAssets().absolute(
				importProjectPath);
		if (!inputProjectZip.exists()) {
			Gdx.app.error(IMPORT_TAG, "The imported project zip : "
					+ importProjectPath + ", does NOT exist!");
			return null;
		}

		FileHandle projectsDir = ProjectsView.MOCKUP_PROJECT_FILE;
		FileHandle outputDir = null;
		int i = 0;
		do {
			outputDir = projectsDir.child("" + (++i));
		} while (outputDir.exists());

		outputDir.mkdirs();

		ZipUtils.unzip(inputProjectZip, outputDir);

		return outputDir;
	}

	@Override
	protected void onPostExecute(FileHandle result) {
		if (result != null) {
			controller.action(OpenMockupGameAsynk.class, result.file()
					.getAbsolutePath());
		}
	}
}
