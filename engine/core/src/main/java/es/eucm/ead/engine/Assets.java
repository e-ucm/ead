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
package es.eucm.ead.engine;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Abstract class for managing assets. In this context, any file required for
 * the application to run is considered an asset. That includes, of course, game
 * json files, images, sounds, etc., but also i18n files and files required by
 * the application itself, like the skin files.
 * 
 * This particular class is meant to be the superclass for all classes dealing
 * with assets. Provides basic functionality like this:
 * 
 * - Creating and making available an {@link es.eucm.ead.engine.I18N} object for
 * handling internationalization (see {@link #getI18N()}). - Loading assets
 * (handles the whole load&update process). See:
 * {@link #setLoader(Class, com.badlogic.gdx.assets.loaders.AssetLoader)},
 * {@link #load(String, Class, com.badlogic.gdx.assets.AssetLoaderParameters)},
 * {@link #isDoneLoading()} and {@link #isLoaded(String, Class)} - Getting
 * assets, converted to their associated object type (e.g. "/scenes/scene0.json"
 * => {@link es.eucm.ead.schema.actors.Scene}). See {@link #get(String)} and
 * {@link #get(String, Class)}. - Clearing assets
 * 
 * A new Assets handler extending {@link Assets} must be created for each
 * particular context. For example, both engine and editor extends
 * {@link Assets} to deal with game resources, but there's also an assets
 * handler (ApplicationAssets) to deal with resources of the editor like the
 * skin or the i18n files.
 * 
 * Known subclasses: Assets (Abstract class for asset management) | |__
 * ApplicationAssets (deals with editor's own resources, like its skin and i18n
 * files, images, etc.) |__ EngineAssets (deals with resources for the
 * particular game being played, like images, scene and game files, sounds etc).
 * | |__ EditorAssets (Extends Engine assets for managing game resources in the
 * editor).
 * 
 */
public abstract class Assets extends Json implements FileHandleResolver {

	/**
	 * Default time slot for loading assets.
	 */
	private static final int LOAD_TIME_SLOT_DURATION = 1000;

	/**
	 * LibGDX asset manager.
	 */
	protected AssetManager assetManager;

	protected Files files;
	private I18N i18n;

	public Assets(Files files) {
		this.files = files;
		assetManager = new AssetManager(this) {

		};
		i18n = new I18N(this);
	}

	// ////////////////////////////////////////////
	// METHODS FOR DEALING WITH PATHS AS STRINGS
	// ////////////////////////////////////////////

	/**
	 * Convenient method for ensuring that a given path has not windows slashes
	 * (\).
	 * 
	 * This method should be invoked each time a NEW PROJECT is created or an
	 * EXISTING PROJECT is loaded.
	 * 
	 * When recent projects are dealt with in the editor, this method should be
	 * used as well.
	 * 
	 * @param path
	 *            The absolute or relative path that may contain Windows slashes
	 *            (e.g. "\scenes\scene0.json", "C:\Users\A user\eadprojects\A
	 *            project\" or "C:\Users\A user/eadprojects\A project/"). May be
	 *            null.
	 * @return The path with all "\" replaced by "/", or null if {@code path} is
	 *         null. (e.g. "/scenes/scene0.json",
	 *         "C:/Users/A user/eadprojects/A project/" or
	 *         "C:/Users/A user/eadprojects/A project/")
	 */
	public String toCanonicalPath(String path) {
		return path == null ? null : path.replaceAll("\\\\", "/");
	}

	/**
	 * Internal method that translates the name of a resource (a String) to a
	 * path that can be dealt with for resolving and loading the resource.
	 * Examples: "scene0" => "/scenes/scene0.json" "default" =>
	 * /skins/default/skin.json@
	 * 
	 * @param name
	 *            The name to be translated to a path (e.g. "scene0")
	 * @param prefix
	 *            A prefix to be appended to the name. Appended only if
	 *            {@code name} does not start with {@code prefix}.
	 * @param addJsonExtension
	 *            True if a ".json" extension should be added to the
	 *            {@code name}. Appended only if {@code name} does not ends with
	 *            ".json" or ".JSON".
	 * @param addSlash
	 *            True if a final slash "/" should be appended at the end, false
	 *            otherwise. The slash is only appended if {@code name} does not
	 *            ends with "/".
	 * @return The name converted to path: E.g.: path = prefix + name + ".json"
	 *         or path = prefix + name + "/
	 */
	protected String convertNameToPath(String name, String prefix,
			boolean addJsonExtension, boolean addSlash) {
		String path = (name == null ? "" : name);
		if (addJsonExtension && !path.toLowerCase().endsWith(".json")) {
			path += ".json";
		}

		if (addSlash && !path.endsWith("/")) {
			path += "/";
		}

		if (!path.startsWith(prefix)) {
			path = prefix + path;
		}
		return path;
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

	// ////////////////////////////////////////////
	// METHODS FOR LOADING ASSETS
	// ////////////////////////////////////////////
	/**
	 * Sets a new {@link com.badlogic.gdx.assets.loaders.AssetLoader} for the
	 * given type. This is needed for any object type that can be stored to and
	 * loaded from a file (e.g. scene.json, game.json). Otherwise Assets does
	 * not know how to load the file and convert it to an object of the model.
	 * 
	 * @param type
	 *            the type of the asset
	 * @param loader
	 *            the loader
	 */
	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(
			Class<T> type, AssetLoader<T, P> loader) {
		assetManager.setLoader(type, loader);
	}

	/**
	 * Adds the given asset to the loading queue of the Assets.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link EngineAssets})
	 * @param type
	 *            the type of the asset.
	 * @param parameter
	 *            parameters for the AssetLoader.
	 */
	public <T> void load(String fileName, Class<T> type,
			AssetLoaderParameters<T> parameter) {
		assetManager.load(fileName, type, parameter);
	}

	/**
	 * @param fileName
	 *            the file name of the asset
	 * @return whether the asset is already loaded
	 */
	public boolean isLoaded(String fileName, Class<?> type) {
		return assetManager.isLoaded(fileName, type);
	}

	/**
	 * Forces load of all the assets in the queue. This method blocks until all
	 * resources scheduled for loading are done.
	 */
	public void finishLoading() {
		assetManager.finishLoading();
	}

	/**
	 * @return false if there are assets pending in the assets queue, true if
	 *         the queue is clean.
	 */
	public boolean isDoneLoading() {
		return assetManager.getQueuedAssets() == 0;
	}

	/**
	 * Updates Assets continuously for the specified number of milliseconds,
	 * yielding the CPU to the loading thread between updates. This may block
	 * for less time if all loading tasks are complete. This may block for more
	 * time if the portion of a single task that happens in the GL thread takes
	 * a long time.
	 * 
	 * @return true if all loading is finished.
	 */
	public boolean update() {
		return assetManager.update(LOAD_TIME_SLOT_DURATION);
	}

	// ////////////////////////////////////////////
	// METHODS FOR GETTING ASSETS
	// ////////////////////////////////////////////
	/**
	 * Gets the resource identified by the given {@code fileName} as an object
	 * of the model. Example: "scene0.json" =>
	 * {@link es.eucm.ead.schema.actors.Scene}
	 * 
	 * @param fileName
	 *            the asset file name (e.g. "scene0.json")
	 * @return the asset (e.g. {@link es.eucm.ead.schema.actors.Scene})
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

	// ////////////////////////////////////////////
	// I18N
	// ////////////////////////////////////////////
	/**
	 * @return returns the i18n module. It is created when this Assets object is
	 *         built.
	 */
	public I18N getI18N() {
		return i18n;
	}

	// ////////////////////////////////////////////
	// CLEARING RESOURCES
	// ////////////////////////////////////////////

	/**
	 * Clear and disposes all loaded assets. This method is only used from
	 * {@link es.eucm.ead.engine.EngineAssets} right now.
	 */
	public void clear() {
		Gdx.app.debug(this.getClass().getCanonicalName(), "Clearing "
				+ assetManager.getDiagnostics());
		assetManager.clear();
	}

	/**
	 * Internal class that wraps {@link com.badlogic.gdx.assets.AssetManager}
	 * Now this is only needed to allow adding assets to the asset manager
	 * without loading them first (only invoked when adding a scene in the
	 * editor).
	 */
	protected class AssetManager extends com.badlogic.gdx.assets.AssetManager {

		public AssetManager(FileHandleResolver resolver) {
			super(resolver);
		}

		@Override
		public <T> void addAsset(String fileName, Class<T> type, T asset) {
			super.addAsset(fileName, type, asset);
		}
	}
}
