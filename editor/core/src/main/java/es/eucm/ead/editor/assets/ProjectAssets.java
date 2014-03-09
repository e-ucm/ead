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
import es.eucm.ead.engine.Assets;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;

import java.util.Map;

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
        // GameMetadata and SceneMetadata need specific loaders since they have to set default values to the model
		setLoader(GameMetadata.class, new GameMetadataLoader(this));
        setLoader(SceneMetadata.class, new SceneMetadataLoader(this));
    }

    /**
     * Loads the game metadata file. This method should be invoked only from {@link es.eucm.ead.editor.control.EditorIO#loadGameMetadata()}.
     * Since there are inter-file dependencies between scenes and gamemetadata (see {@link es.eucm.ead.editor.control.EditorIO#load(String, boolean)} for more details), this method needs access to the model: scenes, scenemetadata, game and gamemetadata
     * @param callback  callback.finishedLoading() is invoked after the object requested to load is available
     * @param game  {@link es.eucm.ead.editor.control.EditorIO#game}
     * @param gameMetadata  {@link es.eucm.ead.editor.control.EditorIO#gameMetadata}
     * @param scenes {@link es.eucm.ead.editor.control.EditorIO#scenes}
     * @param scenesMetadata {@link es.eucm.ead.editor.control.EditorIO#scenesMetadata}
     */
	public void loadGameMetadata(LoadedCallback callback, Game game, GameMetadata gameMetadata, Map<String, Scene> scenes, Map<String, SceneMetadata> scenesMetadata) {
		if (isLoaded(GAME_METADATA_FILE, GameMetadata.class)) {
			callback.finishedLoading(super.assetManager, GAME_METADATA_FILE,
					GameMetadata.class);
		} else {
			load(GAME_METADATA_FILE, GameMetadata.class,
					new LoaderParametersWithModel<GameMetadata>(callback, game, gameMetadata, scenes, scenesMetadata));
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
	 * Loads the scene metadata with the given id and all its dependencies
	 * 
	 * @param sceneId
	 *            the id of the scene (e.g. "scene0")
	 * @param callback
	 *            called once the scene metadata file and its dependencies are loaded
	 */
	public void loadSceneMetadata(String sceneId, LoadedCallback callback) {
		String path = convertSceneNameToMetadataPath(sceneId);
		if (isLoaded(path, SceneMetadata.class)) {
			callback.finishedLoading(assetManager, path, SceneMetadata.class);
		} else {
			load(path, SceneMetadata.class,
					new LoaderParametersWithModel<SceneMetadata>(callback));
		}
	}
}
