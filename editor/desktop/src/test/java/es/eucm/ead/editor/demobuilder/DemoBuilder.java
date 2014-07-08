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
package es.eucm.ead.editor.demobuilder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.editor.DesktopPlatform;
import es.eucm.ead.editor.utils.GeometryUtils;
import es.eucm.ead.editor.utils.ZipUtils;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.positiontracking.Parallax;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.FieldTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.data.conversation.Node;
import es.eucm.ead.schema.data.shape.Polygon;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.effects.controlstructures.ControlStructure;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schema.effects.controlstructures.IfThenElseIf;
import es.eucm.ead.schema.effects.controlstructures.If;
import es.eucm.ead.schema.effects.controlstructures.While;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * DemoBuilder is a tool for helping make simple demo games quickly. Games
 * created with the tool can be saved automatically to a temp location on disk
 * and run by the engine. Use {@link #build()}, {@link #save()}, {@link #run()}
 * and {@link #clean()} for more info.
 * 
 * Usage: To create a new demo with the tool, create a class that extends
 * DemoBuilder. Then implement {@link #doBuild()} and, perhaps,
 * {@link #assetPaths()}, just in case you want to have all your asset paths
 * defined in one place. You may also consider overriding
 * {@link #getDescription()} and {@link #getSnapshotUri()} if you plan to add
 * the demo to DemoLauncher.
 * 
 * All the resources of the demo (images, sounds, etc.) must be zipped into a
 * file that should be available to DemoBuilder at runtime. DemoBuilder will
 * unzip these files to the temp location of the game. The information required
 * for resolving the location of the zipFile is placed at construction time (see
 * {@link #DemoBuilder(String)}).
 * 
 * The {@link #doBuild()} method is the key. This method must be implemented by
 * the subclass, and there's where the logic for creating all the game entities
 * ({@link #entities}) must be placed. To help building such logic, there are a
 * bunch of different useful methods. Those can be basically structured in two
 * types:
 * 
 * <ul>
 * <li>Those that do not start by <strong>make</strong>. Those methods directly
 * modify the {@link #entities} structure one way or another. Use those methods
 * when you want to make quick editions on entities that you have previously
 * created, for example. Their return type is always {@link DemoBuilder}, so
 * multiple calls can be chained.</li>
 * <li>Methods that start by <strong>make</strong>. These methods create model
 * pieces, but they are not directly added to any part of the model, represented
 * by the {@link #entities} structure. Their return type is the type of the
 * model piece created.</li>
 * </ul>
 * 
 * Example:
 * 
 * <pre>
 *     ChangeVar changeVarEffect = makeChangeVar("var", "i1");
 *     singleSceneGame("images/background.png").entity("images/ball.png", HorizontalAlignment.CENTER, VerticalAlignment.CENTER)touch(makeChangeVarEffect);
 * </pre>
 * 
 * This piece of code creates a game with a single scene that is configured with
 * the given background image, then adds a ball entity to the scene in the
 * center of the screen that when clicked sets variable "var" to one. The size
 * of the game is automatically inferred from the size of the background image.
 * 
 * For more details, you can see as an example PlanesDemo.
 * 
 * Created by Javier Torrente on 30/06/14.
 */
public abstract class DemoBuilder {

	private static final String LOG_TAG = "DemoBuilder";

	public static final String DEFAULT_SCENE_PREF = "scenes/s";
	public static final String JSON = ".json";

	/*
	 * Static stuff needed to use some stuff, like Gdx.app or Pixmap. Should be
	 * used just once
	 */
	private static boolean init = false;

	private static void init() {
		if (!init) {
			LwjglNativesLoader.load();
			MockApplication.initStatics();
		}
	}

	/*
	 * Map with all the entities of the game. Should be "filled in" by doBuild()
	 */
	protected HashMap<String, ModelEntity> entities;

	/* To avoid building entities more than once */
	protected boolean built = false;

	/* Convenient container of asset paths - no actual need to use it */
	protected String[] assets;

	/*
	 * Builder remembers last entities and components added to entities so they
	 * can be retrieved easily
	 */
	protected ModelEntity lastEntity;
	protected ModelEntity lastScene;
	protected ModelComponent lastComponent;

	// Some game properties that come in handy
	protected int sceneCount;
	protected float gameWidth;
	protected float gameHeight;

	protected GameAssets gameAssets;

	/* points to the temp folder where the game is saved */
	protected FileHandle rootFolder;
	/*
	 * relative path of zip file with resources for this game (with no
	 * extension)
	 */
	protected String root;

	// To determine image dimensions
	protected DesktopPlatform platform;

	/**
	 * Creates the object but does not actually build the game. Just creates the
	 * temp folder and unzips the the contents of the file specified by the
	 * relative path {@code root}
	 * 
	 * @param root
	 */
	public DemoBuilder(String root) {
		init();
		this.root = root;
		platform = new DesktopPlatform();
		entities = new HashMap<String, ModelEntity>();
		gameAssets = new GameAssets(Gdx.files);
		sceneCount = 0;
	}

	// ///////////////////////////////////////////////////
	// Private methods
	// //////////////////////////////////////////////////
	/**
	 * Creates a model collider for the given image
	 */
	private Array<Polygon> createSchemaCollider(String imageUri) {
		Array<Polygon> collider = new Array<Polygon>();
		Pixmap pixmap = new Pixmap(gameAssets.resolve(imageUri));
		Array<Geometry> geometryArray = GeometryUtils
				.findBorders(pixmap, .1, 2);
		for (Geometry geometry : geometryArray) {
			collider.add(GeometryUtils.jtsToSchemaPolygon(geometry));
		}
		pixmap.dispose();
		return collider;
	}

	private Dimension getImageDimension(String imageUri) {
		return platform.getImageDimension(gameAssets.resolve(imageUri).read());
	}

	// ////////////////////////////////////////////////////
	// Public methods for building, saving, and running the game
	// ////////////////////////////////////////////////////

	/**
	 * Builds the game, saves it to disk, and runs it.
	 */
	public void run() {
		if (!built) {
			build();
		}
		save();
		EngineDesktop engine = new EngineDesktop((int) gameWidth,
				(int) gameHeight) {
			@Override
			protected void dispose() {
				DemoBuilder.this.clean();
				super.dispose();
			}
		};
		engine.run(rootFolder.file().getAbsolutePath(), false, false);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	/**
	 * Builds all the entities of the game, and sets up assets, but does not
	 * save the game to disk or run the game. Use {@link #save()} or
	 * {@link #run()} instead.
	 * 
	 * @return The hashmap with all the entities of the game
	 */
	public HashMap<String, ModelEntity> build() {
		createOutputFolder();
		assets = assetPaths();
		doBuild();
		built = true;
		return entities;
	}

	/**
	 * Saves all entities to the temp folder ({@link #rootFolder}). Should be
	 * invoked always after {@link #build()}.
	 * 
	 * After this method is invoked, {@link #getRootFolder()} can be used to
	 * determine the location of the folder this game was saved to.
	 */
	public void save() {
		for (Map.Entry<String, ModelEntity> entry : entities.entrySet()) {
			FileHandle fh = rootFolder.child(entry.getKey());
			if (fh.isDirectory()) {
				fh.mkdirs();
			} else if (!fh.parent().exists()) {
				fh.parent().mkdirs();
			}
			Gdx.app.debug(LOG_TAG, "Saving to: " + fh.file().getAbsolutePath());
			gameAssets.toJson(entry.getValue(), null, fh);
		}
	}

	/*
	 * Creates the output folder and extracts contents from the zip. Needed
	 * before building
	 */
	private void createOutputFolder() {
		rootFolder = FileHandle.tempDirectory(root);
		rootFolder.mkdirs();

		gameAssets.setLoadingPath("", true);
		ZipUtils.unzip(gameAssets.resolve(root + ".zip"), rootFolder);

		gameAssets.setLoadingPath(rootFolder.file().getAbsolutePath(), false);
	}

	/**
	 * @return An input stream ready for reading a snapshot image of the game
	 *         for preview, or null if no image is available. By default, it is
	 *         assumed that an image with name {@code root} and png extension
	 *         will be available. Demos can define their snapshot to be in
	 *         another location or with another name by overriding
	 *         {@link #getSnapshotUri()}.
	 */
	public InputStream getSnapshotInputStream() {
		FileHandle imageFileHandle = gameAssets.resolve(getSnapshotUri());
		if (imageFileHandle.exists() && imageFileHandle != null) {
			return imageFileHandle.read();
		}
		return null;
	}

	/**
	 * @return An external FileHandle pointing to the temp folder where the game
	 *         was saved
	 */
	public FileHandle getRootFolder() {
		return rootFolder;
	}

	/**
	 * Deletes the whole temp folder the game was saved to. Should be called
	 * once the game is not planned to be run anymore.
	 */
	public void clean() {
		if (built && rootFolder != null) {
			rootFolder.deleteDirectory();
			built = false;
			entities.clear();
			rootFolder = null;
			lastEntity = lastScene = null;
			lastComponent = null;
			sceneCount = 0;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Protected methods that subclasses may want or need to implement or
	// override
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets up {@link #assets}. Useful to encapsulate asset paths all in one
	 * place
	 */
	protected String[] assetPaths() {
		return new String[] {};
	}

	/**
	 * Does the actual build of the game, creating any entities needed by using
	 * methods available below.
	 */
	protected abstract void doBuild();

	/**
	 * @return A textual description of the demo (for DemoLauncher). By default,
	 *         "No description available is returned". Is strongly encouraged
	 *         for subclasses to override this method.
	 */
	public String getDescription() {
		return "No description available";
	}

	/**
	 * @return The path of the snapshot image for the demo.
	 */
	protected String getSnapshotUri() {
		return root + ".png";
	}

	/**
	 * @return A name of the demo, for the DemoLauncher. By default returns the
	 *         relative path ({@link #root}).
	 */
	public String getName() {
		return root;
	}

	// //////////////////////////////////////////////////////////
	// Methods for accessing last entities and component
	// /////////////////////////////////////////////////////////

	/**
	 * @return The last entity added to {@link #entities}. Could be an entity, a
	 *         scene, or a reusable entity. It is updated each time a new entity
	 *         is added to {@link #entities}.
	 */
	public ModelEntity getLastEntity() {
		return lastEntity;
	}

	/**
	 * @return The last scene added to {@link #entities}. The last scene
	 *         property is updated when {@link #scene(String)} is called.
	 */
	public ModelEntity getLastScene() {
		return lastScene;
	}

	/**
	 * @return The last component added to {@link #entities}
	 */
	public ModelComponent getLastComponent() {
		return lastComponent;
	}

	/**
	 * The last component added to {@link #entities}, casted to the given type.
	 * 
	 * @param clazz
	 *            The type of the component (e.g. MoveTween)
	 * @return The last component added
	 */
	public <T extends ModelComponent> T getLastComponent(Class<T> clazz) {
		return clazz.cast(lastComponent);
	}

	// //////////////////////////////////////////////////////////
	// Methods for building entities (modify the game structure)
	// /////////////////////////////////////////////////////////

	/**
	 * Builds a game of the given size. It does not create any scene, but it
	 * assumes a scene {@value #DEFAULT_SCENE_PREF}0{@value #JSON} will be
	 * available as the first scene.
	 * 
	 * @param width
	 *            Width of the viewport
	 * @param height
	 *            Height of the viewport
	 */
	public DemoBuilder game(int width, int height) {
		gameWidth = width;
		gameHeight = height;

		ModelEntity game = entity().getLastEntity();
		Behavior init = new Behavior();
		init.setEvent(new Init());
		AddEntity loadScene = new AddEntity();
		loadScene.setEntityUri(DEFAULT_SCENE_PREF + sceneCount + JSON);
		loadScene.setTarget("(layer sscene_content)");
		init.getEffects().add(loadScene);
		SetViewport viewport = new SetViewport();
		viewport.setWidth(width);
		viewport.setHeight(height);
		init.getEffects().add(viewport);
		game.getComponents().add(init);
		entities.put(GameStructure.GAME_FILE, game);
		return this;
	}

	/**
	 * Creates a game with just one scene configured with the given
	 * {@code backgroundUri} image. The size of the screen (game width and
	 * height) is determined automatically from the size of the image.
	 * 
	 * @param backgroundUri
	 *            Relative uri of the background image of the scene
	 */
	public DemoBuilder singleSceneGame(String backgroundUri) {
		Dimension backgroundDim = getImageDimension(backgroundUri);
		game(backgroundDim.getWidth(), backgroundDim.getHeight()).scene(
				backgroundUri);
		return this;
	}

	private DemoBuilder entity() {
		lastEntity = new ModelEntity();
		return this;
	}

	/**
	 * Creates a new entity with the given location and image for rendering as a
	 * child of the last entity added to {@link #entities}. See
	 * {@link #entity(ModelEntity, String, float, float)}
	 */
	public DemoBuilder entity(String imageUri, float x, float y) {
		return entity(getLastEntity(), imageUri, x, y);
	}

	/**
	 * Creates a new entity with the given location and image for rendering as a
	 * child of the {@code parent} entity provided.
	 * 
	 * @param parent
	 *            The entity to add this entity to to. Can be null (the entity
	 *            is not added anywhere)
	 * @param imageUri
	 *            The relative uri of the image to serve as renderer
	 * @param x
	 *            The x coordinate for the new child entity
	 * @param y
	 *            The y coordinate for the new child entity
	 */
	public DemoBuilder entity(ModelEntity parent, String imageUri, float x,
			float y) {
		ModelEntity modelEntity = entity().getLastEntity();
		Image image = new Image();
		image.setUri(imageUri);
		image.setCollider(createSchemaCollider(imageUri));
		modelEntity.getComponents().add(image);
		modelEntity.setX(x);
		modelEntity.setY(y);
		if (parent != null) {
			parent.getChildren().add(modelEntity);
		}
		return this;
	}

	/**
	 * Creates a new entity with the given screen alignment as a child of the
	 * {@code parent} entity provided.
	 * 
	 * @param parent
	 *            The entity to add this entity to to
	 * @param imageUri
	 *            The relative uri of the image to serve as renderer
	 * @param verticalAlign
	 *            Can be TOP (sticks the entity to screen top), BOTTOM (sticks
	 *            the entity to screen bottom) or CENTER (places the entity
	 *            vertically centered on the screen).
	 * @param horizontalAlign
	 *            Can be LEFT(sticks the entity to the left of the screen),
	 *            RIGHT (sticks the entity to the right of the screen) or CENTER
	 *            (places the entity horizontally on the screen).
	 */
	public DemoBuilder entity(ModelEntity parent, String imageUri,
			VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
		Dimension imageDim = getImageDimension(imageUri);
		float x = horizontalAlign == HorizontalAlign.LEFT ? 0
				: (horizontalAlign == HorizontalAlign.RIGHT ? gameWidth
						- imageDim.getWidth() : (gameWidth - imageDim
						.getWidth()) / 2.0F);
		float y = verticalAlign == VerticalAlign.DOWN ? 0
				: (verticalAlign == VerticalAlign.UP ? gameHeight
						- imageDim.getHeight() : (gameHeight - imageDim
						.getHeight()) / 2.0F);
		return entity(parent, imageUri, x, y);
	}

	/**
	 * Creates a scene with the given image as background
	 * 
	 * @param imageUri
	 *            The relative uri of the image to serve as renderer for the
	 *            background
	 */
	public DemoBuilder scene(String imageUri) {
		lastScene = entity().getLastEntity();
		lastScene.getChildren().add(entity(imageUri, 0, 0).getLastEntity());
		String sceneId = DEFAULT_SCENE_PREF + (sceneCount++) + JSON;
		entities.put(sceneId, lastScene);
		return this;
	}

	/**
	 * Creates a reusable entity, this is an entity that is not a scene but that
	 * is also saved into a separate file. The entity created will have a
	 * position and image renderer defined
	 * 
	 * @param entityUri
	 *            The uri to save the entity to
	 * @param imageUri
	 *            The relative uri of the image to serve as renderer
	 * @param x
	 *            The x coordinate for the reusable entity
	 * @param y
	 *            The y coordinate for the reusable entity
	 */
	public DemoBuilder reusableEntity(String entityUri, String imageUri,
			float x, float y) {
		ModelEntity modelEntity = entity(null, imageUri, x, y).getLastEntity();
		entities.put(entityUri, modelEntity);
		return this;
	}

	/**
	 * Adds a new frame to the last entity added to {@link #entities}. See
	 * {@link #frame(es.eucm.ead.schema.entities.ModelEntity, String, float)}
	 * for more details.
	 */
	public DemoBuilder frame(String frameUri, float duration) {
		return frame(getLastEntity(), frameUri, duration);
	}

	/**
	 * Adds a new frame to the given {@code modelEntity}. If the entity has no
	 * frame renderer, a new frame renderer is created and added. Any previous
	 * renderers available are converted to frames.
	 * 
	 * @param modelEntity
	 *            The entity to add a frame to
	 * @param frameUri
	 *            The relative uri for the image of the frame
	 * @param duration
	 *            The frame duration
	 */
	public DemoBuilder frame(ModelEntity modelEntity, String frameUri,
			float duration) {
		Frames frames = null;

		Frame frame = new Frame();
		frame.setTime(duration);
		Image image = new Image();
		image.setCollider(createSchemaCollider(frameUri));
		image.setUri(frameUri);
		frame.setRenderer(image);

		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof Frames) {
				frames = ((Frames) modelComponent);
			} else if (modelComponent instanceof Renderer) {
				frames = new Frames();
				frames.setSequence(Frames.Sequence.LINEAR);
				Frame prevFrame = new Frame();
				prevFrame.setRenderer((Renderer) modelComponent);
				prevFrame.setTime(duration);
				frames.getFrames().add(prevFrame);
				modelEntity.getComponents().removeValue(modelComponent, true);
				modelEntity.getComponents().add(frames);
				break;
			}
		}

		if (frames == null) {
			frames = new Frames();
			frames.setSequence(Frames.Sequence.LINEAR);
			modelEntity.getComponents().add(frames);
		}

		frames.getFrames().add(frame);

		lastComponent = frames;

		return this;
	}

	/**
	 * Adds a parallax component to the last entity added to {@link #entities}.
	 * See {@link #parallax(es.eucm.ead.schema.entities.ModelEntity, float)} for
	 * more details.
	 */
	public DemoBuilder parallax(float d) {
		return parallax(getLastEntity(), d);
	}

	/**
	 * Adds a parallax component to the given {@code parent} entity.
	 * 
	 * @param parent
	 *            The entity to add this component to
	 * @param d
	 *            The float value for the parallax (0=horizon, 1=exactly between
	 *            observer and horizon, >1=closer to observer than to horizon).
	 */
	public DemoBuilder parallax(ModelEntity parent, float d) {
		Parallax parallax = new Parallax();
		parallax.setD(d);
		parent.getComponents().add(parallax);
		lastComponent = parallax;
		return this;
	}

	/**
	 * Creates a timer that is looping "ad infinitum". Each {@code time} seconds
	 * it will trigger the {@code effects} provided. The timer is added as a
	 * component of the {@code parent} entity provided.
	 * 
	 * @param parent
	 *            The entity to add this component to
	 * @param time
	 *            Time elapsed between loops, in seconds
	 * @param effects
	 *            Effects to launch each {@code time} seconds.
	 */
	public DemoBuilder infiniteTimer(ModelEntity parent, int time,
			Effect... effects) {
		Timer timer = new Timer();
		timer.setTime(time);
		timer.setRepeat(-1);

		return behavior(parent, timer, effects);
	}

	/**
	 * Creates a new touch behavior with the given {@code effects} and adds it
	 * as component of the last entity added to {@link #entities}. See
	 * {@link #touchBehavior(es.eucm.ead.schema.entities.ModelEntity, es.eucm.ead.schema.effects.Effect...)}
	 * for more details.
	 */
	public DemoBuilder touchBehavior(Effect... effects) {
		return touchBehavior(getLastEntity(), effects);
	}

	/**
	 * Creates a new touch behavior with the given {@code effects} and adds it
	 * as component of the given {@code parent} entity.
	 * 
	 * @param parent
	 *            The entity to add this component to
	 * @param effects
	 *            Effects to launch when the entity is touched.
	 */
	public DemoBuilder touchBehavior(ModelEntity parent, Effect... effects) {
		return behavior(parent, new Touch(), effects);
	}

	/**
	 * Creates a new init behaviour with the given {@code effects} and adds it
	 * as component of the last entity added to {@link #entities}. See
	 * {@link #initBehavior(es.eucm.ead.schema.entities.ModelEntity, es.eucm.ead.schema.effects.Effect...)}
	 * for more details
	 */
	public DemoBuilder initBehavior(Effect... effects) {
		return initBehavior(getLastEntity(), effects);
	}

	/**
	 * Creates a new init behaviour with the given {@code effects} and adds it
	 * as component of given {@code parent} entity.
	 * 
	 * @param parent
	 *            The entity to add this component to
	 * @param effects
	 *            Effects to launch when the entity is loaded.
	 */
	public DemoBuilder initBehavior(ModelEntity parent, Effect... effects) {
		return behavior(parent, new Init(), effects);
	}

	private DemoBuilder behavior(Event event, Effect... effects) {
		return behavior(getLastEntity(), event, effects);
	}

	private DemoBuilder behavior(ModelEntity parent, Event event,
			Effect... effects) {
		Behavior behavior = new Behavior();
		behavior.setEvent(event);
		parent.getComponents().add(behavior);
		for (Effect effect : effects) {
			behavior.getEffects().add(effect);
		}
		lastComponent = behavior;
		return this;
	}

	/**
	 * Adds a new tags component with the given {@code tags} to the last entity
	 * added to {@link #entities}. See
	 * {@link #tags(es.eucm.ead.schema.entities.ModelEntity, String...)} for
	 * more details
	 */
	public DemoBuilder tags(String... tags) {
		return tags(getLastEntity(), tags);
	}

	/**
	 * Adds a new tags component with the given {@code tags} to the given
	 * {@code parent} entity.
	 * 
	 * @param parent
	 *            The entity to add this component to
	 * @param tags
	 *            A list with the tags that are added to the component
	 */
	public DemoBuilder tags(ModelEntity parent, String... tags) {
		Tags tagsComponent = new Tags();
		for (String tag : tags) {
			tagsComponent.getTags().add(tag);
		}
		parent.getComponents().add(tagsComponent);
		lastComponent = tagsComponent;
		return this;
	}

	/**
	 * Creates and adds a tween of the given type with the given properties to
	 * the last entity added to {@link #entities}.
	 */
	public <T extends Tween> DemoBuilder tween(Class<T> clazz, Float delay,
			Integer repeat, Float repeatDelay, Boolean yoyo, Float duration,
			Boolean relative, Tween.EaseEquation easeEquation,
			Tween.EaseType easeType, Float value1, Float value2,
			String component, String field) {
		return tween(getLastEntity(), clazz, delay, repeat, repeatDelay, yoyo,
				duration, relative, easeEquation, easeType, value1, value2,
				component, field);
	}

	/**
	 * Creates and adds a tween of the given type with the given properties to
	 * the given {@code parent} entity.
	 */
	public <T extends Tween> DemoBuilder tween(ModelEntity parent,
			Class<T> clazz, Float delay, Integer repeat, Float repeatDelay,
			Boolean yoyo, Float duration, Boolean relative,
			Tween.EaseEquation easeEquation, Tween.EaseType easeType,
			Float value1, Float value2, String component, String field) {
		Tween tween = makeTween(clazz, delay, repeat, repeatDelay, yoyo,
				duration, relative, easeEquation, easeType, value1, value2,
				component, field);
		parent.getComponents().add(tween);
		lastComponent = tween;
		return this;
	}

	/**
	 * Creates and adds to the given {@code parent} entity a tween that
	 * instantaneously mirrors (x coordinate) the entity. That is, makes
	 * scaleX=-scaleX, whatever the current scale of the entity is.
	 * 
	 * @param parent
	 *            The entity to add this component to
	 */
	public DemoBuilder mirrorEntityTween(ModelEntity parent) {
		ScaleTween mirror = makeMirrorEntityTween(parent.getScaleX());
		parent.getComponents().add(mirror);
		lastComponent = mirror;
		return this;
	}

	/**
	 * Adds the given {@link Parameter} to the last component added to
	 * {@link #entities}.
	 * 
	 * @param param
	 *            {@link Parameter#name}
	 * @param expression
	 *            {@link Parameter#value}
	 */
	public DemoBuilder parameter(String param, String expression) {
		return parameter(getLastComponent(), param, expression);
	}

	/**
	 * Adds the given {@link Parameter} to the given {@code container} object.
	 * 
	 * @param container
	 *            The object to add the parameter to. Can be an {@link Effect}
	 *            or a {@link ModelComponent}.
	 * @param param
	 *            {@link Parameter#name}
	 * @param expression
	 *            {@link Parameter#value}
	 */
	public DemoBuilder parameter(Object container, String param,
			String expression) {
		Parameter parameter = new Parameter();
		parameter.setName(param);
		parameter.setValue(expression);
		if (container instanceof Effect) {
			((Effect) container).getParameters().add(parameter);
		} else if (container instanceof ModelComponent) {
			((ModelComponent) container).getParameters().add(parameter);
		}
		return this;
	}

	/**
	 * Adds the given {@code effect} to the given {@code container}, which can
	 * be {@link Behavior}, {@link Script}, {@link Node} or
	 * {@link ControlStructure}.
	 */
	public DemoBuilder effect(Object container, Effect effect) {
		if (container instanceof Behavior) {
			Behavior behavior = (Behavior) container;
			behavior.getEffects().add(effect);
		} else if (container instanceof Script) {
			Script script = (Script) container;
			script.getEffects().add(effect);
		} else if (container instanceof Node) {
			Node node = (Node) container;
			node.setEffect(effect);
		} else if (container instanceof ControlStructure) {
			ControlStructure controlStructure = (ControlStructure) container;
			controlStructure.getEffects().add(effect);
		}
		return this;
	}

	/**
	 * Adds a ChangeVar effect to the last component added to {@link #entities}
	 * with the given properties and local context.
	 * 
	 * @param variable
	 *            {@link ChangeVar#variable}
	 * @param expression
	 *            {@link ChangeVar#expression}
	 */
	public DemoBuilder changeVar(String variable, String expression) {
		return changeVar(getLastComponent(), variable, expression,
				ChangeVar.Context.LOCAL);
	}

	/**
	 * Adds a ChangeVar effect to the given {@code container} object with the
	 * given properties and local context. See {@link #effect(Object, Effect)}
	 * to see the supported types of containers.
	 * 
	 * @param container
	 *            The object to add this effect to.
	 * @param variable
	 *            {@link ChangeVar#variable}
	 * @param expression
	 *            {@link ChangeVar#expression}
	 */
	public DemoBuilder changeVar(Object container, String variable,
			String expression) {
		effect(container,
				makeChangeVar(variable, expression, ChangeVar.Context.LOCAL));
		return this;
	}

	/**
	 * Adds a ChangeVar effect to the last component added to {@link #entities}
	 * with the given properties.
	 * 
	 * @param variable
	 *            {@link ChangeVar#variable}
	 * @param expression
	 *            {@link ChangeVar#expression}
	 * @param context
	 *            {@link ChangeVar#context}
	 */
	public DemoBuilder changeVar(String variable, String expression,
			ChangeVar.Context context) {
		return changeVar(getLastComponent(), variable, expression, context);
	}

	/**
	 * Adds a ChangeVar effect to the given {@code container} object with the
	 * given properties. See {@link #effect(Object, Effect)} to see the
	 * supported types of containers.
	 * 
	 * @param container
	 *            The object to add this effect to.
	 * @param variable
	 *            {@link ChangeVar#variable}
	 * @param expression
	 *            {@link ChangeVar#expression}
	 * @param context
	 *            {@link ChangeVar#context}
	 */
	public DemoBuilder changeVar(Object container, String variable,
			String expression, ChangeVar.Context context) {
		effect(container, makeChangeVar(variable, expression, context));
		return this;
	}

	/**
	 * Creates an {@link AddComponent} effect with the given {@code target} and
	 * {@code componentToAdd} and adds it to the last component added to
	 * {@link #entities}.
	 */
	public DemoBuilder addComponent(String target, ModelComponent componentToAdd) {
		return addComponent(getLastComponent(), target, componentToAdd);
	}

	/**
	 * Creates an {@link AddComponent} effect with the given {@code target} and
	 * {@code componentToAdd} and adds it to the given {@code parent} container.
	 * For container supported types, see
	 * {@link #effect(Object, es.eucm.ead.schema.effects.Effect)}.
	 */
	public DemoBuilder addComponent(Object parent, String target,
			ModelComponent componentToAdd) {
		effect(parent, makeAddComponent(target, componentToAdd));
		return this;
	}

	// //////////////////////////////////////////////////////////
	// Methods for making model pieces (do not modify entities)
	// /////////////////////////////////////////////////////////
	/**
	 * Creates a {@link ControlStructure} effect of the given type ({@code clazz}
	 * ) with the given {@code condition}
	 * 
	 * @param clazz
	 *            Type of control structure. Could be {@link If},
	 *            {@link IfThenElseIf} or {@link While}.
	 * @param condition
	 *            The string expression that serves as condition.
	 */
	public <T extends ControlStructure> T makeControlStructure(Class<T> clazz,
			String condition) {
		try {
			T controlStructure = ClassReflection.newInstance(clazz);
			if (ClassReflection.getDeclaredField(clazz, "condition") != null) {
				Field field = ClassReflection.getDeclaredField(clazz,
						"condition");
				field.setAccessible(true);
				field.set(controlStructure, condition);
			}
			return controlStructure;
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates an {@link AddEntity} effect with the given entity uri
	 */
	public AddEntity makeAddEntity(String entityUri) {
		AddEntity addEntity = new AddEntity();
		addEntity.setEntityUri(entityUri);
		return addEntity;
	}

	/**
	 * Creates a {@link ChangeVar} effect with the given properties and local
	 * context
	 * 
	 * @param variable
	 *            {@link ChangeVar#variable}
	 * @param expression
	 *            {@link ChangeVar#expression}
	 */
	public ChangeVar makeChangeVar(String variable, String expression) {
		return makeChangeVar(variable, expression, ChangeVar.Context.LOCAL);
	}

	/**
	 * Creates a {@link ChangeVar} effect with the given properties
	 * 
	 * @param variable
	 *            {@link ChangeVar#variable}
	 * @param expression
	 *            {@link ChangeVar#expression}
	 * @param context
	 *            {@link ChangeVar#context}
	 */
	public ChangeVar makeChangeVar(String variable, String expression,
			ChangeVar.Context context) {
		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable(variable);
		changeVar.setExpression(expression);
		changeVar.setContext(context);
		return changeVar;
	}

	/**
	 * Creates a {@link AddComponent} effect
	 * 
	 * @param target
	 *            {@link AddComponent#target}
	 * @param component
	 *            {@link AddComponent#component}
	 */
	public AddComponent makeAddComponent(String target, ModelComponent component) {
		AddComponent addComponent = new AddComponent();
		addComponent.setComponent(component);
		addComponent.setTarget(target);
		return addComponent;
	}

	/**
	 * Creates a {@link ScaleTween} tho mirror an entity on the x axis. It
	 * actually makes scaleX = -currentScale.
	 * 
	 * @param currentScale
	 *            The current scale of the entity
	 */
	public ScaleTween makeMirrorEntityTween(float currentScale) {
		return makeTween(ScaleTween.class, null, null, null, null, 0F, true,
				null, null, -2 * currentScale, null, null, null);
	}

	/**
	 * Makes an empty tween of the given type.
	 * 
	 * @param clazz
	 *            Supported types: {@link ScaleTween}, {@link MoveTween},
	 *            {@link RotateTween}, {@link AlphaTween}, {@link FieldTween}.
	 */
	public <T extends Tween> T makeTween(Class<T> clazz) {
		return makeTween(clazz, null, null, null, null, null, null, null, null,
				null, null, null, null);
	}

	/**
	 * Makes an tween of the given type with the given properties.
	 * 
	 * @param clazz
	 *            Supported types: {@link ScaleTween}, {@link MoveTween},
	 *            {@link RotateTween}, {@link AlphaTween}, {@link FieldTween}.
	 * @param value1
	 *            The first tween-specific parameter. E.g.; "x" value for
	 *            MoveTween, "rotation" value for RotateTween.
	 * @param value2
	 *            The second tween-specific parameter. (Only for MoveTween and
	 *            ScaleTween). E.g.; "y" value for MoveTween.
	 * @param component
	 *            {@link FieldTween#component}
	 * @param field
	 *            {@link FieldTween#field}
	 */
	public <T extends Tween> T makeTween(Class<T> clazz, Float delay,
			Integer repeat, Float repeatDelay, Boolean yoyo, Float duration,
			Boolean relative, Tween.EaseEquation easeEquation,
			Tween.EaseType easeType, Float value1, Float value2,
			String component, String field) {
		try {
			T tween = ClassReflection.newInstance(clazz);
			if (delay != null) {
				tween.setDelay(delay);
			}
			if (repeat != null) {
				tween.setRepeat(repeat);
			}
			if (repeatDelay != null) {
				tween.setRepeatDelay(repeatDelay);
			}
			if (yoyo != null) {
				tween.setYoyo(yoyo);
			}
			if (duration != null) {
				tween.setDuration(duration);
			}
			if (relative != null) {
				tween.setRelative(relative);
			}
			if (easeEquation != null) {
				tween.setEaseEquation(easeEquation);
			}
			if (easeType != null) {
				tween.setEaseType(easeType);
			}
			if (value1 != null) {
				if (MoveTween.class.isAssignableFrom(clazz)) {
					((MoveTween) tween).setX(value1);
				} else if (ScaleTween.class.isAssignableFrom(clazz)) {
					((ScaleTween) tween).setScaleX(value1);
				} else if (RotateTween.class.isAssignableFrom(clazz)) {
					((RotateTween) tween).setRotation(value1);
				} else if (AlphaTween.class.isAssignableFrom(clazz)) {
					((AlphaTween) tween).setAlpha(value1);
				} else if (FieldTween.class.isAssignableFrom(clazz)) {
					((FieldTween) tween).setTarget(value1);
				}
			}
			if (value2 != null) {
				if (MoveTween.class.isAssignableFrom(clazz)) {
					((MoveTween) tween).setY(value2);
				} else if (ScaleTween.class.isAssignableFrom(clazz)) {
					((ScaleTween) tween).setScaleY(value2);
				}
			}
			if (component != null && FieldTween.class.isAssignableFrom(clazz)) {
				((FieldTween) tween).setComponent(component);
			}
			if (field != null && FieldTween.class.isAssignableFrom(clazz)) {
				((FieldTween) tween).setField(field);
			}
			return tween;
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates an expression for retrieving all entities with a given tag.
	 */
	public String makeEntitiesWithTagExp(String tag) {
		return "(collection (hastag $entity s" + tag + "))";
	}

	public enum HorizontalAlign {
		LEFT, CENTER, RIGHT;
	}

	public enum VerticalAlign {
		UP, CENTER, DOWN;
	}

}
