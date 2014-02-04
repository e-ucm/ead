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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.assets.GameLoader;
import es.eucm.ead.engine.assets.SceneLoader;
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
import java.util.Stack;

/**
 * Deals with all assets that must be read from a file. Essentially, wraps a
 * {@link AssetManager}, and adds some extra methods. It also controls the
 * loading path
 */
public class Assets extends Json implements FileHandleResolver {

	private static final int LOAD_TIME_SLOT_DURATION = 1000;

	private Files files;

	private AssetManager assetManager;

	private I18N i18n;

	private Skin skin;

	private BitmapFont defaultFont;

	private String loadingPath;

	private boolean internal;

	private Stack<String> subgamePaths;

	private Json json;

	private GameLoop gameLoop;

	private Map<Class<?>, Class<?>> relations;

	private Array<AssetDescriptor> dependencies;

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
		json = new Json();
		subgamePaths = new Stack<String>();
		relations = new HashMap<Class<?>, Class<?>>();
		dependencies = new Array<AssetDescriptor>();
		json = new Json();
		setLoaders();
		loadBindings(resolve("bindings.json"));
	}

	public void setGameLoop(GameLoop gameLoop) {
		this.gameLoop = gameLoop;
	}

	public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(
			Class<T> type, AssetLoader<T, P> loader) {
		assetManager.setLoader(type, loader);
	}

	public I18N getI18N() {
		return i18n;
	}

	/**
	 * Sets the loading game path
	 * 
	 * @param gamePath
	 *            the game path
	 * @param internal
	 *            if internal is true, game files will be loaded using the
	 *            internal type and the root of the games will be considered the
	 *            application resources, if false the type will be absolute, and
	 *            the game path will be considered a path in the local drive
	 */
	public void setGamePath(String gamePath, boolean internal) {
		setLoadingPath(gamePath == null || gamePath.endsWith("/") ? gamePath
				: gamePath + "/");
		this.internal = internal;
	}

	private void setLoadingPath(String loadingPath) {
		this.loadingPath = loadingPath;
		// Loading path changed, all assets file name are invalid, and must be
		// cleared
		clear();
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
		String skinFile = skinName;
		if (!skinFile.endsWith(".json")) {
			skinFile = "skins/" + skinName + "/skin.json";
		}
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
		load(fileName, type, null);
	}

	public <T> void load(String fileName, Class<T> type,
			AssetLoaderParameters<T> parameter) {
		assetManager.load(fileName, type, parameter);
	}

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
	 * Clear and disposes all loaded assets
	 */
	public void clear() {
		Gdx.app.debug("Assets", "Clearing " + assetManager.getDiagnostics());
		assetManager.clear();
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
		setLoadingPath(loadingPath + subgamePath);
	}

	/**
	 * Pops a path of a subgame
	 * 
	 * @return returns true if the game popped is the root game
	 */
	public boolean popSubgamePath() {
		if (!subgamePaths.isEmpty()) {
			String subgamePath = subgamePaths.pop();
			setLoadingPath(loadingPath.substring(0, loadingPath.length()
					- subgamePath.length()));
			return false;
		} else {
			return true;
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
	public void loadBindings(FileHandle fileHandle) {
		try {
			Array<Array<String>> bindings = json.fromJson(Array.class,
					fileHandle);
			read(bindings);
		} catch (SerializationException e) {
			Gdx.app.error("Assets", fileHandle.path()
					+ " doesn't contain a valid bindings file");
		}
	}

	private boolean read(Array<Array<String>> bindings) {
		String schemaPackage = "";
		String corePackage = "";
		for (Array<String> entry : bindings) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
				corePackage = entry.size == 1 ? null : entry.get(1);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					Class coreClass = null;
					if (entry.size == 2) {
						coreClass = corePackage == null ? null
								: ClassReflection.forName(corePackage + "."
										+ entry.get(1));
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
		relations.put(schemaClass, engineClass);
		addClassTag(alias, schemaClass);
	}

	/**
	 * @param clazz
	 *            a schema class
	 * @return Returns true if the given schema class has a correspondent engine
	 *         class
	 */
	public boolean containsRelation(Class<?> clazz) {
		return relations.containsKey(clazz);
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
		Class<?> clazz = relations.get(element.getClass());
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
	public <T> T newObject(Class<T> clazz) {
		try {
			return ClassReflection.newInstance(clazz);
		} catch (ReflectionException e) {
			Gdx.app.error("Assets", "Impossible to create new object", e);
			return null;
		}
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

	public <T> void addDependency(String fileName, Class<T> clazz) {
		addDependency(new AssetDescriptor<T>(fileName, clazz));
	}

	public void addDependency(AssetDescriptor assetDescriptor) {
		if (!contains(dependencies, assetDescriptor)) {
			dependencies.add(assetDescriptor);
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

	public Array<AssetDescriptor> popDependencies() {
		Array<AssetDescriptor> assetDescriptors = new Array<AssetDescriptor>();
		for (AssetDescriptor assetDescriptor : dependencies) {
			assetDescriptors.add(assetDescriptor);
		}
		dependencies.clear();
		return assetDescriptors;
	}

	public String convertSceneNameToPath(String name) {
		String path = name;
		if (!path.endsWith(".json")) {
			path += ".json";
		}

		if (!path.startsWith("scenes/")) {
			path = "scenes/" + path;
		}
		return path;
	}

	public String convertSubgameNameToPath(String name) {
		String path = name;
		if (!path.endsWith("/")) {
			path += "/";
		}
		if (!path.startsWith("subgames/")) {
			path = "subgames/" + path;
		}
		return path;
	}

}
