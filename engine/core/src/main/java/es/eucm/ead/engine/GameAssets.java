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
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.GameStructure;
import es.eucm.ead.engine.assets.SimpleLoaderParameters;
import es.eucm.ead.engine.assets.SimpleLoader;
import es.eucm.ead.engine.assets.serializers.*;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.renderers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all game assets. Internally delegates LibGDX
 * {@link com.badlogic.gdx.assets.AssetManager} to do the actual loading.
 * 
 * @see com.badlogic.gdx.assets.AssetManager
 */
public class GameAssets extends Assets implements GameStructure {

	private BitmapFont defaultFont;

	private String loadingPath;

	private boolean gamePathInternal;

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
	public GameAssets(Files files) {
		super(files);

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
	 * {@link GameAssets#getEngineObject(Object)}
	 * 
	 * @param gameLoop
	 *            the game loop
	 */
	public void setGameLoop(GameLoop gameLoop) {
		this.gameLoop = gameLoop;
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
		return gamePathInternal;
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
	 * Loads the game all its dependent assets (including the initial scene)
	 * 
	 * @param callback
	 *            called once the game and its dependencies are loaded
	 */
	public void loadGame(LoadedCallback callback) {
		if (isLoaded(GAME_FILE, Game.class)) {
			callback.finishedLoading(assetManager, GAME_FILE, Game.class);
		} else {
			load(GAME_FILE, Game.class, new SimpleLoaderParameters<Game>(
					callback));
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
			load(path, Scene.class, new SimpleLoaderParameters<Scene>(callback));
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
					Gdx.app.error(this.getClass().getCanonicalName(),
							"Error loading bindings", e);
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
			Gdx.app.error(this.getClass().getCanonicalName(),
					"No actor for class" + element.getClass()
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
	 * {@link GameAssets#newObject(Class)}
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
		// FIXME The way in which scene elements are parsed is a bit ... weird.
		// This should be revised.
		setSerializer(SceneElement.class, new SceneElementSerializer(this));
		setSerializer(AtlasImage.class, new SimpleSerializer<AtlasImage>(this,
				"uri", TextureAtlas.class));
		setSerializer(Image.class, new UriTextureSerializer<Image>(this));
		setSerializer(NinePatch.class,
				new UriTextureSerializer<NinePatch>(this));
		setSerializer(TextStyle.class, new SimpleSerializer<TextStyle>(this,
				"font", BitmapFont.class));
		setSerializer(Text.class, new TextSerializer(this));
		// Second, set loaders
		setLoader(Game.class, new SimpleLoader<Game>(this, Game.class));
		setLoader(Scene.class, new SimpleLoader<Scene>(this, Scene.class));
		setLoader(TextStyle.class, new SimpleLoader<TextStyle>(this,
				TextStyle.class));
	}

	/**
	 * Method used by serialiazers to store its dependencies. Later, they will
	 * be recovered through {@link GameAssets#popDependencies()}
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
	 * be recovered through {@link GameAssets#popDependencies()}
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

}
