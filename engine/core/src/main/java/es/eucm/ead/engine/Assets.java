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
package es.eucm.ead.engine;

import java.util.Stack;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Deals with all assets that must be read from a file. Essentially, wraps a
 * {@link AssetManager}, and adds some extra methods. It also controls the
 * loading path
 */
public class Assets implements FileHandleResolver {

	private static final int LOAD_TIME_SLOT_DURATION = 1000 / 30;

	private Files files;

	private AssetManager assetManager;

	private boolean internal;

	private Skin skin;

	private BitmapFont defaultFont;

	private String loadingPath;

	private Stack<String> subgamePaths;

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public Assets(Files files) {
		this.files = files;
		assetManager = new AssetManager(this);
		subgamePaths = new Stack<String>();
	}

	/**
	 * Sets the loading game path
	 * 
	 * @param gamePath
	 *            the game path
	 * @param internal
	 *            if internal is true, game files will be loaded using the
	 *            {@link com.badlogic.gdx.Files.FileType.Internal} type and the
	 *            root of the games will be considered the application
	 *            resources, if false the type will be
	 *            {@link com.badlogic.gdx.Files.FileType.Absolute}, and the game
	 *            path will be considered a path in the local drive
	 */
	public void setGamePath(String gamePath, boolean internal) {
		this.loadingPath = gamePath == null || gamePath.endsWith("/") ? gamePath
				: gamePath + "/";
		this.internal = internal;
	}

	/**
	 * @return the game path
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
		return internal;
	}

	/**
	 * 
	 * @return returns the current skin for the UI
	 */
	public Skin getSkin() {
		if (skin == null) {
			setSkin("default");
		}
		return skin;
	}

	/**
	 * Loads the skin with the given name. It will be necessary to rebuild the
	 * UI to see changes reflected
	 * 
	 * @param skinName
	 *            the skin name
	 */
	public void setSkin(String skinName) {
		String skinFile = "skins/" + skinName + "/skin.json";
		load(skinFile, Skin.class);
		finishLoading();
		this.skin = get(skinFile);
	}

	/**
	 * @return Returns a default font to draw text
	 */
	public BitmapFont getDefaultFont() {
		if (defaultFont == null) {
			defaultFont = new BitmapFont();
		}
		return defaultFont;
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
			FileHandle fh = internal ? files.internal(loadingPath + path)
					: files.absolute(loadingPath + path);
			if (fh.exists()) {
				return fh;
			} else {
				// Fallback: use internal file
				return files.internal(path);
			}
		}
	}

	// WRAPPER around AssetManager

	/**
	 * Adds an asset to the loading queue
	 * 
	 * @param fileName
	 *            the file name
	 * @param type
	 *            the type of the asset
	 */
	public void load(String fileName, Class<?> type) {
		assetManager.load(fileName, type);
	}

	/**
	 * Loads all the assets in the queue
	 */
	public void finishLoading() {
		assetManager.finishLoading();
	}

	/**
	 * @param fileName
	 *            the asset file name
	 * @return the asset
	 */
	public <T> T get(String fileName) {
		return assetManager.get(fileName);
	}

	/**
	 * @param fileName
	 *            the asset file name
	 * @param clazz
	 *            the asset type
	 * @return the asset
	 */
	public <T> T get(String fileName, Class<T> clazz) {
		return assetManager.get(fileName, clazz);
	}

	/**
	 * Updates the AssetManager continuously for the specified number of
	 * milliseconds, yielding the CPU to the loading thread between updates.
	 * This may block for less time if all loading tasks are complete. This may
	 * block for more time if the portion of a single task that happens in the
	 * GL thread takes a long time.
	 * 
	 * @return true if all loading is finished.
	 */
	public boolean update() {
		return assetManager.update(LOAD_TIME_SLOT_DURATION);
	}

	/**
	 * @return If there's assets pending in the loading queue
	 */
	public boolean isLoading() {
		return assetManager.getQueuedAssets() > 0;
	}

	/**
	 * Adds subgame path to load resources
	 * 
	 * @param subgamePath
	 *            the path
	 */
	public void addSubgamePath(String subgamePath) {
		if (!subgamePath.endsWith("/")) {
			subgamePath += "/";
		}
		subgamePaths.add(subgamePath);
		loadingPath += subgamePath;
	}

	/**
	 * Pops a path of a subgame
	 * 
	 * @return returns true if the game popped is the root game
	 */
	public boolean popSubgamePath() {
		if (!subgamePaths.isEmpty()) {
			String subgamePath = subgamePaths.pop();
			loadingPath = loadingPath.substring(0, loadingPath.length()
					- subgamePath.length());
			return false;
		} else {
			return true;
		}
	}
}
