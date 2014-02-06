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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.assets.GameLoader;
import es.eucm.ead.engine.assets.GameLoader.GameParameter;
import es.eucm.ead.engine.assets.SceneLoader;
import es.eucm.ead.engine.assets.SceneLoader.SceneParameter;
import es.eucm.ead.engine.assets.serializers.AtlasImageSerializer;
import es.eucm.ead.engine.assets.serializers.ImageSerializer;
import es.eucm.ead.engine.assets.serializers.NinePatchSerializer;
import es.eucm.ead.engine.assets.serializers.SceneElementSerializer;
import es.eucm.ead.engine.assets.serializers.TextSerializer;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.renderers.AtlasImage;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.NinePatch;
import es.eucm.ead.schema.renderers.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Deals with all assets that must be read from a file. Essentially, wraps a
 * {@link AssetManager}, and adds some extra methods. It also controls the
 * loading path
 */
public class Assets extends Json implements FileHandleResolver {

	public static final String GAME_FILE = "game.json";

	public static final String SCENES_PATH = "scenes/";

	public static final String SUBGAMES_PATH = "subgames/";

	private static final int LOAD_TIME_SLOT_DURATION = 1000;

	protected Files files;

	protected AssetManager assetManager;

	private I18N i18n;

	private BitmapFont defaultFont;

	private String loadingPath;

	private boolean gamePathinternal;

	private GameLoop gameLoop;

	/**
	 * Relations between schema classes and engine classes
	 */
	private Map<Class<?>, Class<?>> engineRelations;

	/**
	 * Pending dependencies added after reading a json file
	 */
	private Array<AssetDescriptor> pendingDependencies;

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public Assets(Files files) {
		this.files = files;
		i18n = new I18N(this);
		assetManager = new AssetManager(this);
		engineRelations = new HashMap<Class<?>, Class<?>>();
		pendingDependencies = new Array<AssetDescriptor>();
		setLoaders();
		FileHandle bindings = resolve("bindings.json");
		if (bindings.exists()) {
			loadBindings(bindings);
		}
	}

	/**
	 * Sets the game loop in the assets. The game loop is set here to set it
	 * back to all engine objects created through
	 * {@link Assets#getEngineObject(Object)}
	 * 
	 * @param gameLoop
	 *            the game loop
	 */
	public void setGameLoop(GameLoop gameLoop) {
		this.gameLoop = gameLoop;
	}

	/**
	 * @return returns the i18n module
	 */
	public I18N getI18N() {
		return i18n;
	}

	/**
	 * 
	 * @return the libgdx asset manager
	 */
	public AssetManager getAssetManager() {
		return assetManager;
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
		this.gamePathinternal = internal;
		// Loading path changed, all assets become invalid, and must be
		// cleared
		clear();
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
		return gamePathinternal;
	}

	/**
	 * Sets a new {@link AssetLoader} for the given type.
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
	 * @return Returns the default font to draw text
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
			FileHandle fh = gamePathinternal ? files.internal(loadingPath
					+ path) : files.absolute(loadingPath + path);
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
	 * Loads the game all its dependent assets (including the initial scene)
	 * 
	 * @param callback
	 *            called once the game and its dependencies are loaded
	 */
	public void loadGame(LoadedCallback callback) {
		if (isLoaded(GAME_FILE, Game.class)) {
			callback.finishedLoading(assetManager, GAME_FILE, Game.class);
		} else {
			load(GAME_FILE, Game.class, new GameParameter(callback));
		}
	}

	/**
	 * Loads the scene with the given name and all its dependencies
	 * 
	 * @param name
	 *            the name of the scene
	 * @param callback
	 *            called once the scene and its dependencies are loaded
	 */
	public void loadScene(String name, LoadedCallback callback) {
		String path = convertSceneNameToPath(name);
		if (isLoaded(path, Scene.class)) {
			callback.finishedLoading(assetManager, path, Scene.class);
		} else {
			load(path, Scene.class, new SceneParameter(callback));
		}
	}

	/**
	 * Adds an asset to the loading queue
	 * 
	 * @param fileName
	 *            the file name
	 * @param type
	 *            the type of the asset
	 */
	public void load(String fileName, Class<?> type) {
		load(fileName, type, null);
	}

	/**
	 * Adds the given asset to the loading queue of the Assets.
	 * 
	 * @param fileName
	 *            the file name (interpretation depends on {@link Assets})
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
	 * @return whether the asset is loaded
	 */
	public boolean isLoaded(String fileName, Class<?> type) {
		return assetManager.isLoaded(fileName, type);
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
	 * Clear and disposes all loaded assets
	 */
	public void clear() {
		Gdx.app.debug("Assets", "Clearing " + assetManager.getDiagnostics());
		assetManager.clear();
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
	public void loadBindings(FileHandle fileHandle) {
		Array<Array<String>> bindings = fromJson(Array.class, fileHandle);
		read(bindings);
	}

	/**
	 * Read bindings
	 * 
	 * @param bindings
	 *            a list with bindings
	 * @return if the bindings were correctly read
	 */
	private boolean read(Array<Array<String>> bindings) {
		String schemaPackage = "";
		String enginePackage = "";
		for (Array<String> entry : bindings) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
				enginePackage = entry.size == 1 ? null : entry.get(1);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					Class coreClass = null;
					if (enginePackage != null) {
						coreClass = enginePackage == null ? null
								: ClassReflection
										.forName(enginePackage
												+ "."
												+ (entry.size == 2 ? entry
														.get(1) : entry.get(0)
														+ "EngineObject"));
					}
					bind(ClassReflection.getSimpleName(schemaClass)
							.toLowerCase(), schemaClass, coreClass);
				} catch (ReflectionException e) {
					Gdx.app.error("Assets", "Error loading bindings", e);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Binds a schema class with an engine class
	 * 
	 * @param alias
	 *            alias of the class
	 * @param schemaClass
	 *            the schema class
	 * @param engineClass
	 *            the engine class wrapping the schema class
	 */
	public void bind(String alias, Class<?> schemaClass,
			Class<? extends EngineObject> engineClass) {
		engineRelations.put(schemaClass, engineClass);
		addClassTag(alias, schemaClass);
	}

	/**
	 * Builds an engine object from an schema object
	 * 
	 * @param element
	 *            the schema object
	 * @return an engine object representing the schema object
	 */
	@SuppressWarnings("unchecked")
	public <S, T extends EngineObject> T getEngineObject(S element) {
		Class<?> clazz = engineRelations.get(element.getClass());
		if (clazz == null) {
			Gdx.app.error("Assets", "No actor for class" + element.getClass()
					+ ". Null is returned");
			return null;
		} else {
			T a = (T) newObject(clazz);
			a.setGameLoop(gameLoop);
			a.setSchema(element);
			return a;
		}
	}

	/**
	 * Returns the element to the objects pool. Be careful to ensure that
	 * nothing refers to this object, because it will be eventually returned by
	 * {@link Assets#newObject(Class)}
	 * 
	 * @param o
	 *            the object that is not longer used
	 */
	public <T extends EngineObject> void free(T o) {
		if (o != null) {
			Pools.free(o);
		}
	}

	/**
	 * Creates a new instance of the given class
	 * 
	 * @param clazz
	 *            the clazz
	 * @param <T>
	 *            the type of the element returned
	 * @return an instance of the given class
	 */
	protected <T> T newObject(Class<T> clazz) {
		return Pools.obtain(clazz);
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
		// First, set serializers
		setSerializer(AtlasImage.class, new AtlasImageSerializer(this));
		setSerializer(Image.class, new ImageSerializer(this));
		setSerializer(Text.class, new TextSerializer(this));
		setSerializer(SceneElement.class, new SceneElementSerializer(this));
		setSerializer(NinePatch.class, new NinePatchSerializer(this));
		// Second, set loaders
		setLoader(Game.class, new GameLoader(this));
		setLoader(Scene.class, new SceneLoader(this));
	}

	/**
	 * Method used by serialiazers to store its dependencies. Later, they will
	 * be recovered through {@link es.eucm.ead.engine.Assets#popDependencies()}
	 * 
	 * @param fileName
	 * @param clazz
	 * @param <T>
	 */
	public <T> void addDependency(String fileName, Class<T> clazz) {
		addDependency(new AssetDescriptor<T>(fileName, clazz));
	}

	/**
	 * 
	 * Method used by serialiazers to store its dependencies. Later, they will
	 * be recovered through {@link es.eucm.ead.engine.Assets#popDependencies()}
	 * 
	 * @param assetDescriptor
	 */
	public void addDependency(AssetDescriptor assetDescriptor) {
		if (!contains(pendingDependencies, assetDescriptor)) {
			pendingDependencies.add(assetDescriptor);
		}
	}

	private boolean contains(Array<AssetDescriptor> list, AssetDescriptor asset) {
		for (AssetDescriptor a : list) {
			if (a.fileName.equals(asset.fileName) && a.type.equals(asset.type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return returns the dependencies pending to be treated
	 */
	public Array<AssetDescriptor> popDependencies() {
		Array<AssetDescriptor> assetDescriptors = new Array<AssetDescriptor>();
		for (AssetDescriptor assetDescriptor : pendingDependencies) {
			assetDescriptors.add(assetDescriptor);
		}
		pendingDependencies.clear();
		return assetDescriptors;
	}

	public String convertSceneNameToPath(String name) {
		return convertNameToPath(name, SCENES_PATH, true, false);
	}

	public String convertSubgameNameToPath(String name) {
		return convertNameToPath(name, SUBGAMES_PATH, false, true);
	}

	protected String convertNameToPath(String name, String prefix,
			boolean addJsonExtension, boolean addSlash) {
		String path = (name == null ? "" : name);
		if (addJsonExtension && !path.endsWith(".json")) {
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

}
