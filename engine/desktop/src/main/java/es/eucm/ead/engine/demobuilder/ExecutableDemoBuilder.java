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
package es.eucm.ead.engine.demobuilder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.builder.DemoBuilder;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.utils.DesktopImageUtils;
import es.eucm.ead.engine.utils.GeometryUtils;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.shape.Polygon;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;
import es.eucm.utils.gdx.ZipUtils;

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
 * {@link #ExecutableDemoBuilder(String)}).
 */
public abstract class ExecutableDemoBuilder extends DemoBuilder {

	protected GameAssets gameAssets;

	/* To avoid building entities more than once */
	protected boolean built = false;

	/* Convenient container of asset paths - no actual need to use it */
	protected String[] assets;

	/* points to the temp folder where the game is saved */
	protected FileHandle rootFolder;
	/*
	 * relative path of zip file with resources for this game (with no
	 * extension)
	 */
	protected String root;

	// To determine image dimensions
	protected DesktopImageUtils imageUtils;

	/*
	 * Parameter that indicates whether image magick (www.imagemagick.org)
	 * should be used to transform png images to ensure libgdx can read them.
	 * Before this parameter is set to true, the installation path of
	 * imagemagick must be set by invoking the next instruction:
	 * 
	 * ProcessStarter.setGlobalSearchPath(imageMagickDir);
	 * 
	 * e.g.:
	 * 
	 * ProcessStarter.setGlobalSearchPath("C:\Development\ImageMagick");
	 */
	protected boolean convertPNGs = false;

	/**
	 * Creates the object but does not actually build the game. Just creates the
	 * temp folder and unzips the the contents of the file specified by the
	 * relative path {@code root}
	 * 
	 * @param root
	 */
	public ExecutableDemoBuilder(String root) {
		this.root = root;
	}

	public void prepare() {
		imageUtils = new DesktopImageUtils();
		this.gameAssets = new GameAssets(Gdx.files, imageUtils);
	}

	// ///////////////////////////////////////////////////
	// Private methods
	// //////////////////////////////////////////////////
	/**
	 * Creates a model collider for the given image
	 */
	protected Array<Polygon> createSchemaCollider(String imageUri) {

		try {
			Array<Polygon> collider = new Array<Polygon>();
			Pixmap pixmap = new Pixmap(gameAssets.resolve(imageUri));
			Array<Geometry> geometryArray = GeometryUtils.findBorders(pixmap,
					.1, 2);
			for (Geometry geometry : geometryArray) {
				collider.add(GeometryUtils.jtsToSchemaPolygon(geometry));
			}
			pixmap.dispose();
			return collider;
		} catch (GdxRuntimeException e) {
			Gdx.app.error(
					LOG_TAG,
					"An error occurred creating the collider for the next image: "
							+ imageUri
							+ ". Sometimes that's to do with unsupported PNG features. Image properties will be shown.");
			ImgUtils.showImageProperties(gameAssets.resolve(imageUri).path());
			return null;
		}
	}

	protected Dimension getRendererDimension(Renderer component) {
		int width = 0, height = 0;
		if (component instanceof Image) {
			Image image = (Image) component;
			Dimension dim = getImageDimension(image.getUri());
			width = dim.getWidth();
			height = dim.getHeight();
		} else if (component instanceof Frames) {
			Frames frames = (Frames) component;
			for (Frame frame : frames.getFrames()) {
				Dimension frameDim = getRendererDimension(frame.getRenderer());
				width = Math.max(width, frameDim.getWidth());
				height = Math.max(height, frameDim.getHeight());
			}
		} else if (component instanceof States) {
			States states = (States) component;
			for (State state : states.getStates()) {
				Dimension stateDim = getRendererDimension(state.getRenderer());
				width = Math.max(width, stateDim.getWidth());
				height = Math.max(height, stateDim.getHeight());
			}
		}
		Dimension dimension = new Dimension();
		dimension.setWidth(width);
		dimension.setHeight(height);
		return dimension;
	}

	protected Dimension getImageDimension(String imageUri) {
		Dimension dimension = new Dimension();
		Vector2 size = new Vector2();
		imageUtils.imageSize(gameAssets.resolve(imageUri), size);
		dimension.setWidth((int) size.x);
		dimension.setHeight((int) size.y);
		return dimension;
	}

	protected Dimension adjustOrigin(ModelEntity entity) {
		// /////// Entity adjustments
		// Calculate current dimension
		Dimension actualDim = null;
		for (ModelComponent component : entity.getComponents()) {
			if (component instanceof Renderer) {
				actualDim = getRendererDimension((Renderer) component);
				break;
			}
		}

		if (actualDim == null || actualDim.getWidth() == 0
				|| actualDim.getHeight() == 0) {
			return null;
		}

		float actualHeight = actualDim.getHeight();
		float actualWidth = actualDim.getWidth();

		// Center origin
		entity.setOriginX(actualWidth / 2.0F);
		entity.setOriginY(actualHeight / 2.0F);

		return actualDim;
	}

	// ////////////////////////////////////////////////////
	// Public methods for building, saving, and running the game
	// ////////////////////////////////////////////////////

	/**
	 * Builds the game, saves it to disk, and runs it.
	 */
	public void run() {
		final EngineDesktop engine = new EngineDesktop((int) gameWidth,
				(int) gameHeight) {
			@Override
			protected void dispose() {
				ExecutableDemoBuilder.this.clean();
				super.dispose();
			}
		};

		prepare();
		if (!built) {
			build();
		}
		save();
		engine.setSize((int) gameWidth, (int) gameHeight);
		engine.run(rootFolder.file().getAbsolutePath(), false);
		if (debug()) {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					Stage stage = engine.getApplicationListener().getStage();
					stage.setDebugAll(true);
				}
			});
		}
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

	/**
	 * Creates the output folder and extracts contents from the zip. Needed
	 * before building
	 */
	protected void createOutputFolder() {
		rootFolder = FileHandle.tempDirectory(root);
		rootFolder.mkdirs();

		FileHandle zip = gameAssets.resolve(root + ".zip");
		if (zip.exists()) {
			ZipUtils.unzip(zip, rootFolder);

			if (convertPNGs) {
				ImgUtils.convertPNGs(rootFolder);
			}

		}
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
		return ClassLoader.getSystemResourceAsStream(getSnapshotUri());
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

	public boolean debug() {
		return false;
	}

	// //////////////////////////////////////////////////////////
	// Methods for accessing last entities and component
	// /////////////////////////////////////////////////////////

	/**
	 * Creates a game with just one scene configured with the given
	 * {@code backgroundUri} image. The size of the screen (game width and
	 * height) is determined automatically from the size of the image.
	 * 
	 * @param backgroundUri
	 *            Relative uri of the background image of the scene
	 */
	public ExecutableDemoBuilder singleSceneGame(String backgroundUri) {
		Dimension backgroundDim = getImageDimension(backgroundUri);
		super.singleSceneGame(backgroundUri, backgroundDim.getWidth(),
				backgroundDim.getHeight());
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
	public ExecutableDemoBuilder entity(ModelEntity parent, String imageUri,
			VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
		Dimension imageDim = getImageDimension(imageUri);
		super.entity(parent, imageUri, verticalAlign, horizontalAlign,
				(float) imageDim.getWidth(), (float) imageDim.getHeight());
		return this;
	}

	/**
	 * Calculates the width and height of the given {@code entity} and adjusts
	 * its origin to be centered (width/2, height/2).
	 * 
	 * @param entity
	 *            The entity whose origin is to be centered
	 * @return This object, for chaining calls
	 */
	public ExecutableDemoBuilder centerOrigin(ModelEntity entity) {
		adjustOrigin(entity);
		return this;
	}

	/**
	 * Calculates the width and height of the last entity added and adjusts its
	 * origin to be centered (width/2, height/2). Equivalent to
	 * {@code centerOrigin(getLastEntity())}
	 * 
	 * @return This object, for chaining calls
	 */
	public ExecutableDemoBuilder centerOrigin() {
		return centerOrigin(getLastEntity());
	}

	@Override
	protected Image createImage(String uri) {
		Image image = super.createImage(uri);
		image.setCollider(createSchemaCollider(uri));
		return image;
	}

	protected ShapeRenderer rectangle(int width, int height) {
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		shapeRenderer.setShape(rectangle);
		return shapeRenderer;
	}

	protected Parameter param(String name, String value) {
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setValue(value);
		return parameter;
	}
}
