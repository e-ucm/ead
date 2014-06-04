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
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.MusicLoader.MusicParameter;
import com.badlogic.gdx.assets.loaders.PixmapLoader.PixmapParameter;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;

import es.eucm.ead.engine.I18N;

/**
 * Abstract class for managing assets. In this context, any file required for
 * the application to run is considered an asset. That includes game json files,
 * images, sounds, skins, i18n files, etc.
 * 
 * This particular class is meant to be the superclass for all classes dealing
 * with assets. Provides basic functionality to load i18n
 * 
 * Known subclasses: Assets (Abstract class for asset management) | |__
 * ApplicationAssets (deals with editor's own resources, like its skin and i18n
 * files, images, etc.) |__ GameAssets (deals with resources for the particular
 * game being played, like images, scene and game files, sounds etc). | |__
 * EditorGameAssets (Extends Game assets for managing game resources in the
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

	/**
	 * Current skin to create widgets
	 */
	private Skin skin;

	public Assets(Files files) {
		this.files = files;
		assetManager = new AssetManager(this);
		i18n = new I18N(this);
	}

	/**
	 * @return returns the i18n module. It is created when this Assets object is
	 *         built.
	 */
	public I18N getI18N() {
		return i18n;
	}

	/**
	 * @return returns the current skin to create controls
	 */
	public Skin getSkin() {
		return skin;
	}

	/**
	 * Loads the skin in the given path. Once this skin is loaded, it can be
	 * recovered through {@link Assets#getSkin()}
	 * 
	 * @param pathWithoutExtension
	 *            path to json file defining the skin (without json extension)
	 */
	public void loadSkin(String pathWithoutExtension) {
		String skinJson = pathWithoutExtension + ".json";
		SkinParameter skinParameter = new SkinParameter(pathWithoutExtension
				+ ".atlas");
		if (!isLoaded(skinJson, Skin.class))
			get(skinJson, Skin.class, skinParameter,
					new AssetLoadedCallback<Skin>() {
						@Override
						public void loaded(String fileName, Skin asset) {
							skin = asset;
						}
					}, true);
	}

	/**
	 * Convenient method for ensuring that a given path is in canonical form (
	 * only "/" and no "\").
	 * 
	 * @param path
	 *            The absolute or relative path that may contain \ slashes. May
	 *            be null.
	 * @return The path with all "\" replaced by "/", or null if {@code path} is
	 *         null.
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
	 * Adds the given asset to the loading queue of the assets. It will be
	 * loaded and passed to the callback eventually.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link GameAssets})
	 * @param clazz
	 *            the type of the asset.
	 * @param callback
	 *            to be called with the loaded asset
	 */
	public <T> void get(String fileName, Class<T> clazz,
			AssetLoadedCallback<T> callback) {
		get(fileName, clazz, null, callback, false);
	}

	/**
	 * Adds the given asset to the loading queue of the Assets.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link GameAssets})
	 * @param clazz
	 *            the type of the asset.
	 * @param callback
	 *            to be called with the loaded asset
	 * @param forceLoading
	 *            the asset is loaded right away. If false, it will be loaded at
	 *            some point in the future in the main thread
	 */
	public <T> void get(String fileName, Class<T> clazz,
			AssetLoadedCallback<T> callback, boolean forceLoading) {
		get(fileName, clazz, null, callback, forceLoading);
	}

	/**
	 * Adds the given asset to the loading queue of the Assets.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link GameAssets})
	 * @param clazz
	 *            the type of the asset.
	 * @param parameters
	 *            asset params
	 * @param assetLoadedCallback
	 *            to be called with the loaded asset
	 */
	public <T> void get(String fileName, Class<T> clazz,
			AssetLoaderParameters<T> parameters,
			AssetLoadedCallback<T> assetLoadedCallback) {
		get(fileName, clazz, parameters, assetLoadedCallback, false);
	}

	/**
	 * Adds the given asset to the loading queue of the Assets.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link GameAssets})
	 * @param clazz
	 *            the type of the asset.
	 * @param parameters
	 *            asset params
	 * @param callback
	 *            to be called with the loaded asset
	 * @param forceLoading
	 *            the asset is loaded right away. If false, it will be loaded at
	 *            some point in the future in the main thread
	 */
	public <T> void get(String fileName, Class<T> clazz,
			AssetLoaderParameters<T> parameters,
			AssetLoadedCallback<T> callback, boolean forceLoading) {
		if (assetManager.isLoaded(fileName, clazz)) {
			callback.loaded(fileName, assetManager.get(fileName, clazz));
		} else {
			if (parameters == null) {
				parameters = getDefaultParameters(clazz, callback);
			} else {
				parameters.loadedCallback = new AssetParameters<T>(callback);
			}
			assetManager.load(fileName, clazz, parameters);
			if (forceLoading) {
				assetManager.finishLoading();
			}
		}
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

	/**
	 * @see AssetManager#get(String, Class)
	 */

	/**
	 * Clear and disposes all loaded assets. This method is only used from
	 * {@link GameAssets} right now.
	 */
	public void clear() {
		Gdx.app.debug(this.getClass().getCanonicalName(), "Clearing "
				+ assetManager.getDiagnostics());
		assetManager.clear();
	}

	public class AssetManager extends com.badlogic.gdx.assets.AssetManager {

		public AssetManager(FileHandleResolver resolver) {
			super(resolver);
		}

		@Override
		public <T> void addAsset(String fileName, Class<T> type, T asset) {
			super.addAsset(fileName, type, asset);
		}
	}

	private AssetLoaderParameters getDefaultParameters(Class clazz,
			AssetLoadedCallback callback) {
		AssetLoaderParameters parameters;
		AssetParameters loadedCallback = new AssetParameters(callback);
		if (clazz == BitmapFont.class) {
			parameters = new BitmapFontParameter();
		} else if (clazz == Music.class) {
			parameters = new MusicParameter();
		} else if (clazz == Pixmap.class) {
			parameters = new PixmapParameter();
		} else if (clazz == Sound.class) {
			parameters = new SoundParameter();
		} else if (clazz == TextureAtlas.class) {
			parameters = new TextureAtlasParameter();
		} else if (clazz == Texture.class) {
			parameters = new TextureParameter();
		} else {
			parameters = loadedCallback;
		}
		parameters.loadedCallback = loadedCallback;
		return parameters;
	}

	public interface AssetLoadedCallback<T> {

		void loaded(String fileName, T asset);

	}

	public class AssetParameters<T> extends AssetLoaderParameters<T> implements
			LoadedCallback {

		private AssetLoadedCallback<T> assetLoadedCallback;

		public AssetParameters(AssetLoadedCallback<T> assetLoadedCallback) {
			this.assetLoadedCallback = assetLoadedCallback;
		}

		@Override
		public void finishedLoading(
				com.badlogic.gdx.assets.AssetManager assetManager,
				String fileName, Class aClass) {
			Object asset = assetManager.get(fileName, aClass);
			assetLoadedCallback.loaded(fileName, (T) asset);
		}
	}

	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	public synchronized void dispose() {
		Gdx.app.debug(this.getClass().getCanonicalName(), "Disposing.");
		assetManager.dispose();
	}
}
