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
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.LibraryManager;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.workers.UnzipFile;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.builders.ImportView;
import es.eucm.ead.editor.view.listeners.workers.UnzipFileListener;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;
import java.io.FileFilter;

/**
 * Import a project from a given zip file and open it.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>Class</em> Class of the view to show if the
 * project couldn't be imported.</dd>
 * <dd><strong>args[1]</strong> <em>ErrorCallback</em> invoked if the import
 * went wrong.</dd>
 * </dl>
 */
public class ImportProject extends EditorAction {

	private static final String IMPORT_TAG = "ImportProject";

	public ImportProject() {
		super(true, false, Class.class, OpenLastProject.ErrorCallback.class);
	}

	@Override
	public void perform(Object... args) {

		Class elseView = (Class) args[0];
		OpenLastProject.ErrorCallback callback = args.length == 2 ? (OpenLastProject.ErrorCallback) args[1]
				: null;
		MokapPlatform platform = (MokapPlatform) controller.getPlatform();
		String importProjectPath = (String) platform.getApplicationArguments()[0];
		EditorGameAssets assets = controller.getEditorGameAssets();
		FileHandle inputProjectZip = assets.absolute(importProjectPath);
		if (!inputProjectZip.exists()) {
			Gdx.app.error(IMPORT_TAG, "The imported project zip : "
					+ importProjectPath + ", does NOT exist!");
			handleError(callback,
					OpenLastProject.ErrorCallback.Result.PROJECT_NOT_FOUND,
					elseView, inputProjectZip.path());
			return;
		}

		controller.action(ChangeView.class, ImportView.class);

		FileHandle tempDirectory = FileHandle
				.tempDirectory("mokapProjectUnzip");

		controller.action(ExecuteWorker.class, UnzipFile.class,
				new UnzipProjectListener(tempDirectory, elseView, callback),
				inputProjectZip, tempDirectory);
	}

	private class UnzipProjectListener extends UnzipFileListener {

		private OpenLastProject.ErrorCallback callback;
		private Class elseView;
		private FileHandle projectFolder;

		public UnzipProjectListener(FileHandle outputFolder, Class elseView,
				OpenLastProject.ErrorCallback callback) {
			super(outputFolder);
			this.elseView = elseView;
			this.callback = callback;
		}

		@Override
		public void unzipped() {
			FileHandle rootProject = null;

			FileHandle gameFile = outputFolder.child(ModelStructure.GAME_FILE);
			if (gameFile.exists()) {
				rootProject = outputFolder;
			} else {
				FileHandle[] listChildren = outputFolder.list();
				if (listChildren.length == 1) {
					FileHandle child = listChildren[0];
					if (child.child(ModelStructure.GAME_FILE).exists()) {
						rootProject = child;
					}
				}
			}
			if (rootProject == null) {
				error(null);
				return;
			}

			EditorGameAssets gameAssets = controller.getEditorGameAssets();
			FileHandle loadingDir = gameAssets.absolute(controller
					.getPlatform().getDefaultProjectsFolder());
			projectFolder = ProjectUtils.getNonExistentFile(loadingDir,
					ProjectUtils.createProjectName(), "");

			rootProject.copyTo(projectFolder);
			outputFolder.deleteDirectory();

			FileHandle library = projectFolder
					.child(ModelStructure.LIBRARY_FOLDER);
			FileHandle[] references = library.list(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});

			LibraryManager libraryManager = controller.getLibraryManager();
			for (FileHandle reference : references) {
				FileHandle descriptor = reference
						.child(ModelStructure.DESCRIPTOR_FILE);
				RepoElement elem;
				try {
					elem = gameAssets.fromJson(RepoElement.class, descriptor);
				} catch (SerializationException se) {
					projectFolder.deleteDirectory();

					Gdx.app.error(IMPORT_TAG, "A reference is corrupt: "
							+ reference);
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							handleError(
									callback,
									OpenLastProject.ErrorCallback.Result.PROJECT_CORRUPTED,
									elseView, projectFolder.path());
						}
					});
					return;
				}
				FileHandle elemFolder = libraryManager
						.getRepoElementLibraryFolder(elem);
				if (!elemFolder.exists()) {
					reference.copyTo(elemFolder);
				}
			}
			library.deleteDirectory();

			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					controller.action(OpenProject.class, projectFolder.file()
							.getAbsolutePath());
				}
			});
			MokapPlatform platform = (MokapPlatform) controller.getPlatform();
			clearApplicationArguments(platform);
		}

		@Override
		public void error(Throwable ex) {
			super.error(ex);
			handleError(callback,
					OpenLastProject.ErrorCallback.Result.PROJECT_CORRUPTED,
					elseView, projectFolder.path());
		}

	}

	private void handleError(OpenLastProject.ErrorCallback callback,
			OpenLastProject.ErrorCallback.Result result, Class elseView,
			String projectPath) {
		MokapPlatform platform = (MokapPlatform) controller.getPlatform();
		clearApplicationArguments(platform);
		controller.action(ChangeView.class, elseView);
		if (callback != null) {
			callback.error(result, projectPath);
		}
	}

	private void clearApplicationArguments(MokapPlatform platform) {
		platform.setApplicationArguments((Object[]) null);
	}
}
