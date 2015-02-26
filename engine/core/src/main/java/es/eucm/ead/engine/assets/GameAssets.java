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
package es.eucm.ead.engine.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.GameLoader;
import es.eucm.ead.engine.assets.loaders.ScaledTextureLoader;
import es.eucm.ead.schemax.ModelStructure;

/**
 * Manages all game assets. Internally delegates LibGDX
 * {@link com.badlogic.gdx.assets.AssetManager} to do the actual loading.
 * 
 * @see com.badlogic.gdx.assets.AssetManager
 */
public class GameAssets extends Assets implements ModelStructure {

	public static final String ENGINE_BINDINGS = "bindings.json";

	private String loadingPath, referencePath;

	private boolean gamePathInternal;

	protected ImageUtils imageUtils;

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public GameAssets(Files files, ImageUtils imageUtils) {
		super(files);
		this.imageUtils = imageUtils;
		setLoaders();
		loadBindings();
	}

	/**
	 * Sets the root path for the game
	 * 
	 * @param loadingPath
	 *            the loading path
	 */
	public void setLoadingPath(String loadingPath) {
		setLoadingPath(loadingPath, isGamePathInternal());
	}

	/**
	 * Sets the root path for the game, and if it is an internal path
	 * 
	 * @param loadingPath
	 *            the game path
	 * @param internal
	 *            if internal is true, game files will be loaded using the
	 *            internal type and gamePath will be considered a path inside
	 *            the application resources; if false, the type will be
	 *            absolute, and the gamePath will be considered a path in the
	 *            local drive
	 */
	public void setLoadingPath(String loadingPath, boolean internal) {
		this.loadingPath = convertNameToPath(loadingPath, "", false, true);
		this.gamePathInternal = internal;
		clear();
		loadSkin(GameLoader.DEFAULT_SKIN);
	}

	@Override
	public <T> void get(String fileName, Class<T> clazz,
			AssetLoaderParameters<T> parameters, AssetLoadedCallback<T> callback) {
		if (referencePath != null) {
			fileName = referencePath + fileName;
		}
		super.get(fileName, clazz, parameters, callback);
	}

	/**
	 * @return the current loading path
	 */
	public String getLoadingPath() {
		return loadingPath;
	}

	/**
	 * Sets the current path for a referenced entity.
	 */
	public void setReferencePath(String referencePath) {
		if (referencePath != null && !referencePath.endsWith("/")) {
			referencePath += "/";
		}
		this.referencePath = referencePath;
	}

	public String getReferencePath() {
		return referencePath;
	}

	/**
	 * 
	 * @return true if Assets is loading resources files from the internal
	 *         application resources, false if it's loading from the disk
	 */
	public boolean isGamePathInternal() {
		return gamePathInternal;
	}

	/**
	 * Resolves the path following following the file resolver conventions
	 * 
	 * @param path
	 *            the path
	 * @return a file handle pointing the given path. The file may not exist
	 *         (use .exists() to test)
	 */
	@Override
	public FileHandle resolve(String path) {
		FileHandle fileHandle = url(path);
		if (fileHandle != null) {
			return fileHandle;
		}

		if (path.startsWith("/") || path.indexOf(':') == 1) {
			// Absolute file - don't check existence
			fileHandle = files.absolute(path);
			return fileHandle;

		}

		// Internal files. First, try appending loading path
		if (isGamePathInternal()) {
			fileHandle = files.internal(loadingPath + path);
			if (fileHandle != null && checkFileExistence(fileHandle)) {
				return fileHandle;
			}

		}

		// Try internal without appending loading path
		fileHandle = files.internal(path);
		if (fileHandle != null && checkFileExistence(fileHandle)) {
			return fileHandle;
		}

		// Last option: absolute, appending loading path (does not check
		// existence)
		String fullPath = (getLoadingPath() == null
				|| path.startsWith(getLoadingPath()) ? "" : getLoadingPath())
				+ path;

		return files.absolute(fullPath);
	}

	/**
	 * Resolves a file handle inside the project
	 */
	public FileHandle resolveProject(String path) {
		return files.absolute((referencePath == null ? loadingPath
				: referencePath) + path);
	}

	/**
	 * Loads bindings stored in the file
	 * 
	 * @param fileHandle
	 *            file storing the bindings
	 * @return if the bindings loading was completely correct. It might fail if
	 *         the the file is not a valid or a non existing or invalid class is
	 *         found
	 */
	@SuppressWarnings("all")
	public void loadBindings() {
		for (FileHandle bindingsFile : resolveBindings()) {
			if (checkFileExistence(bindingsFile)) {
				Array<String> bindings = fromJson(Array.class, bindingsFile);
				read(bindings);
			}
		}
	}

	protected FileHandle[] resolveBindings() {
		return new FileHandle[] { resolve("bindings.json") };
	}

	/**
	 * Read bindings
	 * 
	 * @param bindings
	 *            a list with bindings
	 * @return if the bindings were correctly read
	 */
	private void read(Array<String> bindings) {
		String schemaPackage = "";
		for (String line : bindings) {
			if (line.contains(".")) {
				schemaPackage = line;
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + line);
					addClassTag(ClassReflection.getSimpleName(schemaClass)
							.toLowerCase(), schemaClass);
				} catch (ReflectionException e) {
					Gdx.app.error(this.getClass().toString(),
							"Error loading bindings", e);
				}
			}
		}
	}

	/**
	 * Set the customized serializers
	 */
	protected void setLoaders() {
		setLoader(Object.class, new JsonLoader<Object>(this, Object.class));
		setLoader(ScaledTexture.class,
				new ScaledTextureLoader(this, imageUtils));
	}

	public String convertSceneNameToPath(String name) {
		return convertNameToPath(name, SCENES_PATH, true, false);
	}

	public String convertSubgameNameToPath(String name) {
		return convertNameToPath(name, SUBGAMES_PATH, false, true);
	}

	public interface ImageUtils {

		/**
		 * Puts in size the size of the image in file handle
		 * 
		 * @return false if the file is not an image. In that case, size will be
		 *         set to -1, -1
		 */
		boolean imageSize(FileHandle fileHandle, Vector2 size);

		/**
		 * @return if the given size can be opened with the current hardware
		 */
		boolean validSize(Vector2 size);

		/**
		 * Scales the image in src to make it loadable by the current hardware.
		 * Result is stored in target.
		 * 
		 * @return the scale applied to src to be loadable ty the current
		 *         hardware
		 */
		float scale(FileHandle src, FileHandle target);

	}
}
