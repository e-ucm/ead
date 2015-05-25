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
package es.eucm.ead.editor.control.workers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.components.Reference;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;
import es.eucm.utils.gdx.ZipUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

/**
 * Exports the project to a .zip file. The .zip file will be added to the
 * {@link es.eucm.ead.schemax.ModelStructure#EXPORT_PATH}.
 * <dl>
 * <dt><strong>The input arguments are</strong></dt>
 * <dd><strong>None</strong>
 * </dl>
 * <dl>
 * <dt><strong>The result argument is</strong></dt>
 * <dd><strong>args[0]</strong> <em>Float</em> the completion value, ranging
 * from 0 to 1. The last result is a {@link com.badlogic.gdx.files.FileHandle}
 * to the zipped project.
 * </dl>
 */
public class ExportProject extends StatesWorker {

	private static final String EXPORT_TAG = "ExportProject";

	@Override
	public Class<? extends WorkerState> getInitialState() {
		return CopyProject.class;
	}

	public static class CopyProject extends WorkerState {

		private FileHandle currentProject, tempDirectory;
		private FilenameFilter noMetadata = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return !ModelStructure.METADATA_PATH.startsWith(filename);
			}

		};

		@Override
		public void init(Object... args) {
			String exportProjectPath = controller.getLoadingPath();
			EditorGameAssets assets = controller.getEditorGameAssets();
			currentProject = assets.absolute(exportProjectPath);
			if (!currentProject.isDirectory()) {
				Gdx.app.error(EXPORT_TAG, "Project trying to export: "
						+ currentProject + ", does NOT exist!");
				end();
			}
		}

		@Override
		public void step() {
			tempDirectory = FileHandle.tempDirectory("mokapProjectCopy");
			copyTo(currentProject, tempDirectory);
			setCompletion(1f);
			setNextState(CopyProjectReferences.class, tempDirectory);
		}

		@Override
		public void cancelled() {
			if (tempDirectory != null && tempDirectory.exists()) {
				tempDirectory.deleteDirectory();
			}
		}

		@Override
		public float getWeight() {
			return 0.3f;
		}

		public void copyTo(FileHandle orig, FileHandle dest) {
			if (!dest.exists()) {
				dest.mkdirs();
			}
			copyDirectory(orig, dest);
		}

		private void copyDirectory(FileHandle sourceDir, FileHandle destDir) {
			destDir.mkdirs();
			FileHandle[] files = sourceDir.list(noMetadata);
			for (int i = 0, n = files.length; i < n; i++) {
				FileHandle srcFile = files[i];
				FileHandle destFile = destDir.child(srcFile.name());
				if (srcFile.isDirectory()) {
					copyDirectory(srcFile, destFile);
				} else {
					copyFile(srcFile, destFile);
				}
			}
		}

		private void copyFile(FileHandle source, FileHandle dest) {
			try {
				dest.write(source.read(), false);
			} catch (Exception ex) {
				throw new GdxRuntimeException("Error copying source file: "
						+ source.file() + " (" + source.type()
						+ ")\n" //
						+ "To destination: " + dest.file() + " (" + dest.type()
						+ ")", ex);
			}
		}
	}

	public static class CopyProjectReferences extends WorkerState {

		private FileHandle tempDirectory, projectLibraryFolder, tempLibFolder;
		private Array<Reference> references;
		private float totalReferences;

		public CopyProjectReferences() {
			references = new Array<Reference>();
		}

		@Override
		public void init(Object... args) {
			references.clear();
			tempDirectory = (FileHandle) args[0];
			tempLibFolder = tempDirectory
					.child(ModelStructure.EXPORT_LIBRARY_FOLDER);
			projectLibraryFolder = controller.getLibraryManager()
					.getLibraryFolder();

			Map<String, Model.Resource> resources = controller.getModel()
					.getResources(ResourceCategory.SCENE);
			for (Model.Resource resource : resources.values()) {
				ModelEntity scene = (ModelEntity) resource.getObject();
				for (ModelEntity child : scene.getChildren()) {
					if (Q.hasComponent(child, Reference.class)) {
						Reference reference = Q.getComponent(child,
								Reference.class);
						if (!hasReference(reference)) {
							references.add(reference);
						}
					}
				}
			}
			totalReferences = references.size;
		}

		private boolean hasReference(Reference ref) {
			for (Reference reference : references) {
				if (reference.getFolder().equals(ref.getFolder())
						&& reference.getEntity().equals(ref.getEntity())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void step() {
			setCompletion(1f - references.size / totalReferences);
			if (references.size == 0) {
				setNextState(CompressingProject.class, tempDirectory);
				return;
			}
			Reference reference = references.removeIndex(0);
			FileHandle childFolder = projectLibraryFolder.child(reference
					.getFolder());
			childFolder.copyTo(tempLibFolder.child(childFolder.name()));
		}

		@Override
		public void cancelled() {
			if (tempDirectory != null && tempDirectory.exists()) {
				tempDirectory.deleteDirectory();
			}
		}

		@Override
		public float getWeight() {
			return 0.35f;
		}
	}

	public static class CompressingProject extends WorkerState {

		private FileHandle projectZip, tempDirectory;

		@Override
		public void init(Object... args) {
			tempDirectory = (FileHandle) args[0];
			projectZip = null;
		}

		@Override
		public void step() {
			if (projectZip != null) {
				result(projectZip);
				end();
				return;
			}

			FileHandle exportFolder = controller.getEditorGameAssets()
					.resolveProject(ModelStructure.EXPORT_PATH);
			exportFolder.emptyDirectory();
			String untitled = controller.getApplicationAssets().getI18N()
					.m("untitled");
			String projectName = Q.getTitle(controller.getModel().getGame(),
					untitled);
			if (!projectName.equals(untitled)) {
				projectName = projectName.replaceAll("[^a-zA-Z0-9\\._\\s]+",
						"_");
			}
			projectZip = exportFolder.child(projectName + ".zip");

			ZipUtils.zip(tempDirectory, projectZip);
			tempDirectory.deleteDirectory();
			setCompletion(1f);
		}

		@Override
		public void cancelled() {
			deleteDirectory(tempDirectory);
			deleteDirectory(projectZip);
		}

		private void deleteDirectory(FileHandle directory) {
			if (directory != null && directory.exists()) {
				directory.deleteDirectory();
			}
		}

		@Override
		public float getWeight() {
			return 0.35f;
		}
	}
}
