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
import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.assets.SimpleLoader;
import es.eucm.ead.engine.assets.SimpleLoaderParameters;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.components.Note;
import es.eucm.ead.schema.game.GameMetadata;

/**
 * Extends engine assets to also load editor objects
 */
public class ProjectAssets extends Assets {

	public static final String GAME_METADATA_FILE = "project.json";

	public static final String SCENEMETADATA_PATH = "scenes-editor/";

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
		setLoader(GameMetadata.class, new SimpleLoader<GameMetadata>(this,
				GameMetadata.class) {

			// This is needed since gameMetaData always needs a blank note
			protected void doDependenciesProcessing(GameMetadata object,
					String fileName) {
				if (object.getNotes() == null) {
					object.setNotes(new Note());
				}
			}
		});

		// FIXME Duplicate loaders - we should place a loader that does this
		// note checking thing somewhere
		setLoader(SceneMetadata.class, new SimpleLoader<SceneMetadata>(this,
				SceneMetadata.class) {
			// This is needed since gameMetaData always needs a blank note
			protected void doDependenciesProcessing(SceneMetadata object,
					String fileName) {
				if (object.getNotes() == null) {
					object.setNotes(new Note());
				}
				if (object.getName() == null) {
					// Get the scene id:
					String id = fileName.substring(Math.max(
							fileName.lastIndexOf("\\"),
							fileName.lastIndexOf("/")) + 1, fileName
							.toLowerCase().lastIndexOf(".json"));
					object.setName(id);
				}
			}

		});
	}

	public void loadProject(LoadedCallback callback) {
		if (isLoaded(GAME_METADATA_FILE, GameMetadata.class)) {
			callback.finishedLoading(super.assetManager, GAME_METADATA_FILE,
					GameMetadata.class);
		} else {
			load(GAME_METADATA_FILE, GameMetadata.class,
					new SimpleLoaderParameters<GameMetadata>(callback));
		}
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

	/**
	 * Converts the given scene name (e.g. "scene0") into the relative path of
	 * the file that stores this piece of metadata into disk (e.g.
	 * /scenes-editor/scene0.json)
	 * 
	 * @param id
	 *            The id of the scene (e.g. "scene0"). This is the name of the
	 *            json file that stores the info (e.g. "scene0.json").
	 * @return The string with the internal path of the file (e.g.
	 *         "/scenes-editor/scene0.json")
	 */
	public String convertSceneNameToMetadataPath(String id) {
		return convertNameToPath(id, SCENEMETADATA_PATH, true, false);
	}

	private String getFolder(Class<?> clazz) {
		if (clazz == Texture.class) {
			return IMAGES_FOLDER;
		} else {
			return BINARY_FOLDER;
		}
	}

	/**
	 * Loads the scene with the given name and all its dependencies
	 * 
	 * @param sceneId
	 *            the name of the scene
	 * @param callback
	 *            called once the scene and its dependencies are loaded
	 */
	public void loadSceneMetadata(String sceneId, EditorIO callback) {
		String path = convertSceneNameToMetadataPath(sceneId);
		if (isLoaded(path, SceneMetadata.class)) {
			callback.finishedLoading(assetManager, path, SceneMetadata.class);
		} else {
			load(path, SceneMetadata.class,
					new SimpleLoaderParameters<SceneMetadata>(callback));
		}
	}
}
