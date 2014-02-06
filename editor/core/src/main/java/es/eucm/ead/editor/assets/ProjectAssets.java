/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import es.eucm.ead.editor.assets.loaders.ProjectLoader;
import es.eucm.ead.editor.assets.loaders.ProjectLoader.ProjectParameter;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.engine.Assets;

/**
 * Extends engine assets to also load editor objects
 */
public class ProjectAssets extends Assets {

	public static final String PROJECT_FILE = "project.json";

	public static final String IMAGES_FOLDER = "images/";

	public static final String BINARY_FOLDER = "binary/";

	private EditorAssets editorAssets;

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public ProjectAssets(Files files, EditorAssets editorAssets) {
		super(files);
		this.editorAssets = editorAssets;
		loadBindings(this.editorAssets.resolve("bindings.json"));
	}

	@Override
	protected void setLoaders() {
		super.setLoaders();
		setLoader(Project.class, new ProjectLoader(this));
	}

	public void loadProject(LoadedCallback callback) {
		load(PROJECT_FILE, Project.class, new ProjectParameter(callback));
	}

	public void toJsonPath(Object object, String path) {
		toJson(object, resolve(path));
	}

	@Override
	public FileHandle resolve(String path) {
		return files
				.absolute((getLoadingPath() == null ? "" : getLoadingPath())
						+ path);
	}

	/**
	 * 
	 * @param path
	 *            the path
	 * @return a file handle for file referenced by an absolute path
	 */
	public FileHandle absolute(String path) {
		return files.absolute(path);
	}

	/**
	 * Copy and loads the asset in the given path to the project folder
	 * 
	 * @param path
	 *            the path
	 * @param type
	 *            the asset type associated to the file
	 * @return the path of the project in which the file was copied
	 */
	public String copyAndLoad(String path, Class<?> type) {
		FileHandle fh = files.absolute(path);
		if (fh.exists()) {
			String folderPath = getFolder(type);
			FileHandle folder = resolve(folderPath);
			String extension = fh.extension();
			if (!"".equals(extension)) {
				extension = "." + extension;
			}
			String name = fh.nameWithoutExtension();
			String fileName = name + extension;
			int count = 1;
			FileHandle dst;
			while ((dst = folder.child(fileName)).exists()) {
				fileName = name + count++ + extension;
			}

			fh.copyTo(dst);

			String projectPath = folderPath + fileName;
			load(projectPath, type);
			return projectPath;
		} else {
			return null;
		}
	}

	private String getFolder(Class<?> clazz) {
		if (clazz == Texture.class) {
			return IMAGES_FOLDER;
		} else {
			return BINARY_FOLDER;
		}
	}
}
