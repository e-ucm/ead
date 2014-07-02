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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.GameLoader;
import es.eucm.ead.schemax.GameStructure;

/**
 * Manages all game assets. Internally delegates LibGDX
 * {@link com.badlogic.gdx.assets.AssetManager} to do the actual loading.
 * 
 * @see com.badlogic.gdx.assets.AssetManager
 */
public class GameAssets extends Assets implements GameStructure {

	public static final String ENGINE_BINDINGS = "bindings.json";

	private String loadingPath;

	private boolean gamePathInternal;

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public GameAssets(Files files) {
		super(files);
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
		loadSkin(GameLoader.DEFAULT_SKIN);
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
		String newLoadingPath = convertNameToPath(loadingPath, "", false, true);
		if (!newLoadingPath.equals(this.loadingPath)
				|| internal != gamePathInternal) {
			this.loadingPath = newLoadingPath;
			this.gamePathInternal = internal;
			clear();
		}
	}

	/**
	 * @return the current loading path
	 */
	public String getLoadingPath() {
		return loadingPath;
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
		if (path.startsWith("/") || path.indexOf(':') == 1) {
			// Absolute file
			return files.absolute(path);
		} else if (loadingPath == null) {
			// If no game path is set, just return an internal file
			return files.internal(path);
		} else {
			// Relative file
			FileHandle fh = gamePathInternal ? files.internal(loadingPath
					+ path) : files.absolute(loadingPath + path);
			if (fh.exists()) {
				return fh;
			} else {
				// Fallback: use internal file
				return files.internal(path);
			}
		}
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
			if (bindingsFile.exists()) {
				Array<Array<String>> bindings = fromJson(Array.class,
						bindingsFile);
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
	private void read(Array<Array<String>> bindings) {
		String schemaPackage = "";
		for (Array<String> entry : bindings) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					addClassTag(ClassReflection.getSimpleName(schemaClass)
							.toLowerCase(), schemaClass);
				} catch (ReflectionException e) {
					Gdx.app.error(this.getClass().getCanonicalName(),
							"Error loading bindings", e);
				}
			}
		}
	}

	/**
	 * @param type
	 *            May be null if the type is unknown.
	 * @param path
	 *            the path of the json file
	 * @return May be null.
	 */
	public <T> T fromJsonPath(Class<T> type, String path) {
		return fromJson(type, resolve(path));
	}

	/**
	 * Set the customized serializers
	 */
	protected void setLoaders() {
		setLoader(Object.class, new JsonLoader<Object>(this, Object.class));
	}

	public String convertSceneNameToPath(String name) {
		return convertNameToPath(name, SCENES_PATH, true, false);
	}

	public String convertSubgameNameToPath(String name) {
		return convertNameToPath(name, SUBGAMES_PATH, false, true);
	}

}
