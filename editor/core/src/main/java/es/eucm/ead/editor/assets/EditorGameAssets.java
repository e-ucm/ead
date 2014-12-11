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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * This asset manager is meant to deal with the game's assets in the editor.
 * That is, for example, the images, game.json and any scene.json file in the
 * game.
 * 
 * This asset manager should only be used in the editor
 * 
 * For managing the own application's assets (e.g. the skin and preferences),
 * use {@link es.eucm.ead.editor.assets.ApplicationAssets} instead.
 */
public class EditorGameAssets extends GameAssets {

	private static final String I18N_PATH = "i18n-engine";

	public static final String EDITOR_BINDINGS = "editor-bindings.json";

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public EditorGameAssets(Files files, ImageUtils imageUtils) {
		super(files, imageUtils);
		getI18N().setI18nPath(I18N_PATH);
		setOutputType(OutputType.json);
		setIgnores();
	}

	/**
	 * Sets components that should not be saved
	 */
	private void setIgnores() {
		IgnoreSerializer ignoreSerializer = new IgnoreSerializer();
		setSerializer(Parent.class, ignoreSerializer);
		setIgnoreUnknownFields(true);
	}

	@Override
	protected FileHandle[] resolveBindings() {
		return new FileHandle[] { files.internal(ENGINE_BINDINGS),
				files.internal(EDITOR_BINDINGS) };
	}

	public void toJsonPath(Object object, String path) {
		toJson(object, null, null, resolve(path));
	}

	@Override
	public FileHandle resolve(String path) {
		if (isGamePathInternal()) {
			return super.resolve(path);
		}
		FileHandle internal = files.internal(path);
		if (checkFileExistence(internal)) {
			return internal;
		}
		return files.absolute((getLoadingPath() == null
				|| path.startsWith(getLoadingPath()) ? "" : getLoadingPath())
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
	private String copyToProject(String path, Class<?> type) {
		FileHandle fh = files.absolute(path);
		return copyToProject(fh, type, true);
	}

	private String copyToProject(FileHandle path, Class<?> type,
			boolean checkExistance) {
		if (checkExistance && !checkFileExistence(path)) {
			return null;
		}
		String folderPath = getFolder(type);
		FileHandle folder = absolute(getLoadingPath() + folderPath);

		FileHandle dst = ProjectUtils.getNonExistentFile(folder,
				path.nameWithoutExtension(), path.extension());

		path.copyTo(dst);

		return folderPath + dst.name();
	}

	/**
	 * Copies and loads the asset in the given path to the project folder only
	 * if they weren't already loaded.
	 * 
	 * @param path
	 *            the path
	 * @param type
	 *            the asset type associated to the file
	 * @return the path of the project in which the file was copied, may be null
	 *         if the path doesn't exist
	 */
	public String copyToProjectIfNeeded(String path, Class<?> type) {
		// If resource path is not loaded
		if (isLoaded(path, type)) {
			return path;
		}
		if (path.startsWith(getLoadingPath())) {
			return path.substring(getLoadingPath().length());
		}
		return copyToProject(path, type);
	}

	/**
	 * Copies and loads the asset in the given path to the project folder
	 * assuming that the file exists and everything it's correct. This is needed
	 * in order to avoid checkping {@link FileHandle#exists()} in Android which
	 * is very slow for Internal files.
	 * 
	 * @param path
	 *            the path
	 * @param type
	 *            the asset type associated to the file
	 * @return the path of the project in which the file was copied, may be null
	 *         if the path doesn't exist
	 */
	public String copyToProjectDirectly(FileHandle path, Class<?> type) {
		return copyToProject(path, type, false);
	}

	private String getFolder(Class<?> clazz) {
		if (clazz == Texture.class) {
			return IMAGES_FOLDER;
		} else if (clazz == Music.class
				|| clazz == com.badlogic.gdx.audio.Sound.class) {
			return SOUNDS_FOLDER;
		} else {
			return null;
		}
	}

	/**
	 * Creates a new {@link Object} from another {@link Object}. This creates a
	 * deep memory copy through JSON serialization of the specified parameter.
	 * 
	 * <pre>
	 * Note: it doesn't copy Objects that are not serialized and ignored by {@link EditorGameAssets}.
	 * E.g. {@link Parent} component.
	 * </pre>
	 * 
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T copy(T entity) {
		Class<T> clazz = (Class<T>) entity.getClass();
		return fromJson(clazz, toJson(entity, clazz));
	}

	/**
	 * Loads the pixmap contained in the given file handle. Remember to call
	 * {@link com.badlogic.gdx.graphics.Pixmap#dispose()} once you are done with
	 * it
	 * 
	 * @param fh
	 *            the file handle
	 * @return a pixmap
	 */
	public Pixmap loadPixmap(FileHandle fh) {
		return new Pixmap(fh);
	}

	// ////////////////////////////////////////////
	// Method for adding assets. THIS SHOULD BE REVISED
	// ////////////////////////////////////////////

	/**
	 * Adds an asset
	 * 
	 * @param fileName
	 *            file name of the asset
	 * @param type
	 *            type of the asset
	 * @param asset
	 *            the asset
	 */
	public <T> void addAsset(String fileName, Class<T> type, T asset) {
		assetManager.addAsset(fileName, type, asset);
	}

	/**
	 * @param path
	 *            path relative to the project to save the object
	 * @param object
	 *            object to save
	 */
	public void save(String path, ModelEntity object) {
		toJsonPath(object, path);
	}

	public FileHandle projectFileHandle() {
		return absolute(getLoadingPath());
	}

	/**
	 * Serializer to ignore classes that shouldn't be saved
	 */
	public class IgnoreSerializer implements Serializer {

		@Override
		public void write(Json json, Object object, Class knownType) {
		}

		@Override
		public Object read(Json json, JsonValue jsonData, Class type) {
			return null;
		}
	}
}
