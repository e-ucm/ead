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
package es.eucm.ead.builder;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Reference;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.components.behaviors.events.Key;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.EffectsNode;
import es.eucm.ead.schema.components.positiontracking.Parallax;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.FieldTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.components.tweens.Tween.EaseEquation;
import es.eucm.ead.schema.components.tweens.Tween.EaseType;
import es.eucm.ead.schema.data.Color;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.data.shape.Shape;
import es.eucm.ead.schema.effects.*;
import es.eucm.ead.schema.effects.ChangeVar.Context;
import es.eucm.ead.schema.effects.GoScene.Transition;
import es.eucm.ead.schema.effects.controlstructures.ControlStructure;
import es.eucm.ead.schema.effects.controlstructures.IfThenElseIf;
import es.eucm.ead.schema.effects.controlstructures.ScriptCall;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.*;
import es.eucm.ead.schemax.ModelStructure;

import java.util.HashMap;

/**
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

	protected static final String LOG_TAG = "DemoBuilder";

	/*
	 * A special empty entity is created to hold timers for scheduled effects,
	 * infinite timers, etc on the scene
	 */
	protected static final String SPECIAL_TIMERS_TAG = "_SCENE_TIMERS";

	public static final String LIBRARY_PATH = "library/";
	public static final String WALK = "walk";
	public static final String TALK = "talk";
	public static final String GRAB = "grab";
	public static final String IDLE = "idle";
	public static final String USE = "use";

	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	public static final String DEFAULT_SCENE_PREF = "scenes/s";
	public static final String JSON = ".json";

	/*
	 * Map with all the entities of the game. Should be "filled in" by doBuild()
	 */
	protected HashMap<String, ModelEntity> entities;

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
	protected String lastSceneId;
	protected ModelEntity game;
	protected Behavior initGame;

	// Helper class to create expressions
	protected ExpressionBuilder eb = new ExpressionBuilder();

	/**
	 * @return An object that helps create expressions with Mokap's syntax
	 */
	public ExpressionBuilder getEb() {
		return eb;
	}

	public DemoBuilder() {
		entities = new HashMap<String, ModelEntity>();
		sceneCount = 0;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Protected methods that subclasses may want or need to implement or
	// override
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Does the actual build of the game, creating any entities needed by using
	 * methods available below.
	 */
	protected abstract void doBuild();

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
	 * @return the id of the last scene created through {@link #scene(String)}
	 */
	public String getLastSceneId() {
		return lastSceneId;
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

		game = entity().getLastEntity();
		initGame = new Behavior();
		initGame.setEvent(new Init());
		GoScene loadInitialScene = new GoScene();
		loadInitialScene.setSceneId(DEFAULT_SCENE_PREF + sceneCount + JSON);
		initGame.getEffects().add(loadInitialScene);
		SetViewport viewport = new SetViewport();
		viewport.setWidth(width);
		viewport.setHeight(height);
		initGame.getEffects().add(viewport);
		game.getComponents().add(initGame);
		entities.put(ModelStructure.GAME_FILE, game);
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
	public DemoBuilder singleSceneGame(String backgroundUri, int width,
			int height) {
		game(width, height).scene(backgroundUri);
		return this;
	}

	private DemoBuilder entity() {
		lastEntity = new ModelEntity();
		return this;
	}

	/**
	 * Creates an empty with no renderer in x,y
	 */
	public DemoBuilder entity(float x, float y) {
		return entity(getLastEntity(), null, x, y);
	}

	/**
	 * Creates an empty with no renderer in x,y
	 */
	public DemoBuilder entity(ModelEntity parent, float x, float y) {
		return entity(parent, null, x, y);
	}

	/**
	 * Creates a new entity with the given location and image for rendering as a
	 * child of the last entity added to {@link #entities}. See
	 * {@link #entity(es.eucm.ead.schema.entities.ModelEntity, String, float, float)}
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
	 *            The relative uri of the image to serve as renderer. Can be
	 *            null (no renderer is added)
	 * @param x
	 *            The x coordinate for the new child entity
	 * @param y
	 *            The y coordinate for the new child entity
	 */
	public DemoBuilder entity(ModelEntity parent, String imageUri, float x,
			float y) {
		ModelEntity modelEntity = entity().getLastEntity();
		image(modelEntity, imageUri);
		modelEntity.setX(x);
		modelEntity.setY(y);
		if (parent != null) {
			parent.getChildren().add(modelEntity);
		}
		return this;
	}

	public DemoBuilder libraryEntity(String entityId) {
		lastEntity = new ModelEntity();
		entities.put(LIBRARY_PATH + entityId + "/"
				+ ModelStructure.CONTENTS_FOLDER + ModelStructure.ENTITY_FILE,
				lastEntity);
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
			VerticalAlign verticalAlign, HorizontalAlign horizontalAlign,
			float imageWidth, float imageHeight) {
		float x = horizontalAlign == HorizontalAlign.LEFT ? 0
				: (horizontalAlign == HorizontalAlign.RIGHT ? gameWidth
						- imageWidth : (gameWidth - imageWidth) / 2.0F);
		float y = verticalAlign == VerticalAlign.DOWN ? 0
				: (verticalAlign == VerticalAlign.UP ? gameHeight - imageHeight
						: (gameHeight - imageHeight) / 2.0F);
		return entity(parent, imageUri, x, y);
	}

	public DemoBuilder scene() {
		lastScene = entity().getLastEntity();
		lastSceneId = DEFAULT_SCENE_PREF + (sceneCount++) + JSON;
		entities.put(lastSceneId, lastScene);
		return this;
	}

	/**
	 * Creates a scene with the given image as background
	 * 
	 * @param imageUri
	 *            The relative uri of the image to serve as renderer for the
	 *            background
	 */
	public DemoBuilder scene(String imageUri) {
		scene();
		if (imageUri != null) {
			entity(lastScene, imageUri, 0, 0);
		}
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
	 *            The relative uri of the image to serve as renderer. If
	 *            {@code null}, no renderer image is added to the entity7
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
	 * Just adds an entity created elswhere (passed as argument) to the map of
	 * reusable entities.
	 * 
	 * @param path
	 *            The relative path where the entity should be stored (e.g.
	 *            "animals/dog.json")
	 * @param entity
	 *            The entity to store
	 * @return This DemoBuilder object, for chaining calls
	 */
	public DemoBuilder addReusableEntity(String path, ModelEntity entity) {
		entities.put(path, entity);
		return this;
	}

	/*
	 * Searches in the scene for the empty child entity that holds infinite
	 * timers and delayed effects. If it is not found, it is created and added.
	 * 
	 * See temporalStateChange, delayedEffect and infiniteTimer for more details
	 */
	protected ModelEntity sceneTimers(ModelEntity scene) {
		ModelEntity specialTimers = null;
		for (ModelEntity child : scene.getChildren()) {
			for (ModelComponent component : child.getComponents()) {
				if (!(component instanceof Tags)) {
					continue;
				}
				Tags tags = (Tags) component;
				for (String tag : tags.getTags()) {
					if (SPECIAL_TIMERS_TAG.equals(tag)) {
						specialTimers = child;
						break;
					}
				}
			}
		}

		if (specialTimers == null) {
			specialTimers = new ModelEntity();
			tags(specialTimers, SPECIAL_TIMERS_TAG);
			scene.getChildren().add(specialTimers);
		}
		return specialTimers;
	}

	/*
	 * Equivalent to sceneTimers(getLastScene())
	 */
	private ModelEntity sceneTimers() {
		return sceneTimers(getLastScene());
	}

	public DemoBuilder image(ModelEntity entity, String uri) {
		if (uri != null) {
			entity.getComponents().add(createImage(uri));
		}
		return this;
	}

	public ConversationBuilder conversation(ModelEntity entity, String id) {
		Conversation conversation = new Conversation();
		entity.getComponents().add(conversation);
		conversation.setConversationId(id);
		return new ConversationBuilder(conversation);
	}

	public DemoBuilder image(String uri) {
		image(getLastEntity(), uri);
		return this;
	}

	protected Image createImage(String uri) {
		Image image = new Image();
		image.setUri(uri);
		return image;
	}

	public DemoBuilder states() {
		return states(getLastEntity());
	}

	public DemoBuilder states(ModelEntity modelEntity) {
		States states = new States();
		lastComponent = states;

		for (int i = 0; i < modelEntity.getComponents().size; i++) {
			ModelComponent modelComponent = modelEntity.getComponents().get(i);
			if (modelComponent instanceof Renderer
					&& !(modelComponent instanceof States)) {
				Renderer rendererComp = (Renderer) modelComponent;
				State state = new State();
				state.getStates().add(rendererComp.getClass().getSimpleName());
				state.setRenderer(rendererComp);
				states.getStates().add(state);
				modelEntity.getComponents().removeIndex(i);
				i--;
			}
		}

		modelEntity.getComponents().add(states);
		return this;
	}

	public DemoBuilder state(String... stateTags) {
		return state(getLastEntity(), stateTags);
	}

	public DemoBuilder state(ModelEntity modelEntity, String... stateTags) {
		States states = null;
		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof States) {
				states = (States) modelComponent;
				break;
			}
		}

		if (states == null) {
			states(modelEntity);
		}

		State state = new State();
		lastComponent = state;
		for (String stateTag : stateTags) {
			state.getStates().add(stateTag);
		}
		state.setRenderer(null);
		states.getStates().add(state);
		return this;
	}

	public DemoBuilder state(Renderer renderer, String... tags) {
		States states = (States) lastComponent;
		State state = new State();
		state.setRenderer(renderer);
		state.setStates(new Array<String>(tags));
		states.getStates().add(state);
		return this;
	}

	/**
	 * Adds a blink animation to the last added entity. Equivalent to:
	 * blinkFrameAnimation(getLastEntity(), 4F, 0.1F, frames);
	 * 
	 * @param frames
	 *            The list with the frameUris pointing to the images. Cannot be
	 *            null or zero-length (exception thrown)
	 */
	public DemoBuilder blinkFrameAnimation(String... frames) {
		return blinkFrameAnimation(getLastEntity(), 4F, 0.1F, frames);
	}

	/**
	 * Adds a blink animation to the given parent. Equivalent to:
	 * blinkFrameAnimation(parent, 4F, 0.1F, frames);
	 * 
	 * @param parent
	 *            Entity to add the frames to
	 * @param frames
	 *            The list with the frameUris pointing to the images. Cannot be
	 *            null or zero-length (exception thrown)
	 */
	public DemoBuilder blinkFrameAnimation(ModelEntity parent, String... frames) {
		return blinkFrameAnimation(parent, 4F, 0.1F, frames);
	}

	public DemoBuilder scale(float scale) {
		getLastEntity().setScaleX(scale);
		getLastEntity().setScaleY(scale);
		return this;
	}

	public DemoBuilder rotation(float rotation) {
		getLastEntity().setRotation(rotation);
		return this;
	}

	/**
	 * Adds a frame animation to the last entity added. Equivalent to:
	 * blinkFrameAnimation(getLastEntity(), firstFrameDuration,
	 * otherFramesDuration,frames);
	 * 
	 * The first frame is given a different (longer expected) duration than the
	 * others. When the animation reaches the last frame, it "rewinds" to the
	 * first one (yoyo). This method is useful for creating idle animations
	 * where the character stays still most of the time (first frame), while
	 * from time to time it does something simple like blinking or shaking a
	 * hand (other frames).
	 * 
	 * @param firstFrameDuration
	 *            The duration of the first frame (longer expected), in seconds.
	 * @param otherFramesDuration
	 *            The duration of the other frames, in seconds
	 * @param frames
	 *            The list with the frameUris pointing to the images. Cannot be
	 *            null or zero-length (exception thrown)
	 */
	public DemoBuilder blinkFrameAnimation(float firstFrameDuration,
			float otherFramesDuration, String... frames) {
		return blinkFrameAnimation(getLastEntity(), firstFrameDuration,
				otherFramesDuration, frames);
	}

	/**
	 * Adds a frame animation to the given parent with the given frames. The
	 * first frame is given a different (longer expected) duration than the
	 * others. When the animation reaches the last frame, it "rewinds" to the
	 * first one (yoyo). This method is useful for creating idle animations
	 * where the character stays still most of the time (first frame), while
	 * from time to time it does something simple like blinking or shaking a
	 * hand (other frames).
	 * 
	 * @param parent
	 *            Entity to add the frames to
	 * @param firstFrameDuration
	 *            The duration of the first frame (longer expected), in seconds.
	 * @param otherFramesDuration
	 *            The duration of the other frames, in seconds
	 * @param frames
	 *            The list with the frameUris pointing to the images. Cannot be
	 *            null or zero-length (exception thrown)
	 */
	public DemoBuilder blinkFrameAnimation(ModelEntity parent,
			float firstFrameDuration, float otherFramesDuration,
			String... frames) {
		if (frames == null || frames.length == 0) {
			throw new RuntimeException("frames cannot be null or zero length");
		}

		frame(parent, frames[0], firstFrameDuration);
		for (int i = 1; i < frames.length; i++) {
			frame(parent, frames[i], otherFramesDuration);
		}
		for (int i = frames.length - 2; i >= 1; i--) {
			frame(parent, frames[i], otherFramesDuration);
		}
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
	 * Adds a new frame to the given entity. Equivalent to: frame(modelEntity,
	 * frameUri, Frames.Sequence.LINEAR);
	 */
	public DemoBuilder frame(ModelEntity modelEntity, String frameUri,
			float duration) {
		return frame(modelEntity, frameUri, duration, Frames.Sequence.LINEAR);
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
	 * @param sequence
	 *            The {@link es.eucm.ead.schema.renderers.Frames.Sequence} to
	 *            use (e.g. yoyo, linear, random)
	 */
	public DemoBuilder frame(ModelEntity modelEntity, String frameUri,
			float duration, Frames.Sequence sequence) {
		Frames frames = null;

		Frame frame = new Frame();
		frame.setTime(duration);
		frame.setRenderer(createImage(frameUri));

		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof Frames) {
				frames = ((Frames) modelComponent);
			} else if (modelComponent instanceof Renderer) {
				frames = new Frames();
				frames.setSequence(sequence);
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
			frames.setSequence(sequence);
			modelEntity.getComponents().add(frames);
		}

		frames.getFrames().add(frame);

		lastComponent = frames;

		return this;
	}

	/**
	 * Adds a new frame to the given {@code modelEntity}. If the entity has no
	 * frame renderer, a new frame renderer is created and added. Any previous
	 * renderers available are converted to frames.
	 * 
	 * @param duration
	 *            The frame duration
	 */
	public DemoBuilder frameState(States states, int nTags, float duration,
			String... tagsAndUris) {
		State state = null;
		Frames frames = null;

		frames = new Frames();
		lastComponent = frames;
		frames.setSequence(Frames.Sequence.LINEAR);
		state = new State();
		state.setRenderer(frames);
		states.getStates().add(state);

		for (int i = 0; i < nTags; i++) {
			state.getStates().add(tagsAndUris[i]);
		}

		for (int i = nTags; i < tagsAndUris.length; i++) {
			Frame frame = new Frame();
			frame.setTime(duration);
			String img = tagsAndUris[i];
			if (!img.toLowerCase().endsWith(".png")
					&& !img.toLowerCase().endsWith("jpg")
					&& !img.toLowerCase().endsWith("jpeg")) {
				img += ".png";
			}
			frame.setRenderer(createImage(img));
			frames.getFrames().add(frame);
		}

		return this;
	}

	public DemoBuilder frameState(ModelEntity parent, int nTags,
			float duration, String... tagsAndUris) {
		States states = null;
		for (ModelComponent modelComponent : parent.getComponents()) {
			if (modelComponent instanceof States) {
				states = (States) modelComponent;
				break;
			}
		}

		if (states == null) {
			lastComponent = states = new States();
			parent.getComponents().add(states);
		}

		return frameState(states, nTags, duration, tagsAndUris);
	}

	public DemoBuilder frameState(int nTags, float duration,
			String... tagsAndUris) {
		return frameState(getLastEntity(), nTags, duration, tagsAndUris);
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
	 * Creates a Visibility component and adds it to the given entity (
	 * {@code parent})
	 * 
	 * @param condition
	 *            The condition, in Mokap exp language.
	 * @return This object, for chaining
	 */
	public DemoBuilder visibility(ModelEntity parent, String condition) {
		lastComponent = makeVisibility(condition);
		parent.getComponents().add(lastComponent);
		return this;
	}

	/**
	 * Creates a Visibility component and adds it to the last entity created
	 * 
	 * @param condition
	 *            The condition, in Mokap exp language.
	 * @return This object, for chaining
	 */
	public DemoBuilder visibility(String condition) {
		return visibility(getLastEntity(), condition);
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
	 * Schedules the given set of effects for execution after the specified
	 * number of seconds. The whole cycle (waiting + effect execution) is
	 * repeated the number of times specified (-1 for infinite repetitions).
	 * 
	 * Internally, this results in a timer added to a special empty entity in
	 * the scene.
	 * 
	 * @param delay
	 *            Time elapsed before the effects are triggered, in seconds
	 * @param repeat
	 *            Number of times the effects have to be triggered. If -1,
	 *            effects are scheduled for execution ad infinitum
	 * @param effects
	 *            Effects to launch after {@code delay} seconds.
	 */
	public DemoBuilder scheduleEffects(float delay, int repeat,
			Effect... effects) {
		return behavior(sceneTimers(),
				makeDelayedEffect(delay, repeat, effects).getEvent(), effects);
	}

	/**
	 * Schedules the given set of effects for execution after the specified
	 * number of seconds. The whole cycle (waiting + effect execution) is
	 * repeated ad infinitum
	 * 
	 * This is equivalent to {@code scheduleEffects(delay, -1, effects)}
	 * 
	 * @param delay
	 *            Time duration of the cycle, in seconds
	 * @param effects
	 *            Effects to launch each {@code delay} seconds.
	 */
	public DemoBuilder scheduleEffectsForever(float delay, Effect... effects) {
		return scheduleEffects(delay, -1, effects);
	}

	/**
	 * Creates a simple timer that executes the given {@code effects} after the
	 * specified number of {@code seconds}. The resulting component is added to
	 * the given {@code parent} entity
	 */
	public DemoBuilder simpleTimer(ModelEntity parent, float seconds,
			Effect... effects) {
		lastComponent = makeSimpleTimer(seconds, effects);
		parent.getComponents().add(lastComponent);
		return this;
	}

	/**
	 * Creates a simple timer that executes the given {@code effects} after the
	 * specified number of {@code seconds}. The resulting component is added to
	 * the scene's timers container entity
	 */
	public DemoBuilder simpleTimer(float seconds, Effect... effects) {
		return simpleTimer(sceneTimers(getLastScene()), seconds, effects);
	}

	/**
	 * Creates a new behavior triggered by the stroke of the given key and adds
	 * it to the entity provided
	 * 
	 * @param parent
	 *            Entity to add the component to
	 * @param keyCode
	 *            The code of the key that triggers the effects (see
	 *            {@link com.badlogic.gdx.Input.Keys} for a list of key codes).
	 * @param effects
	 *            Effects to be triggered
	 * @return This object, for chaining
	 */
	public DemoBuilder simpleKeyBehavior(ModelEntity parent, int keyCode,
			Effect... effects) {
		lastComponent = makeSimpleKeyBehavior(keyCode, effects);
		parent.getComponents().add(lastComponent);
		return this;
	}

	/**
	 * Calls {@link #simpleKeyBehavior(ModelEntity, int, Effect...)} passing the
	 * last entity created as first argument.
	 */
	public DemoBuilder simpleKeyBehavior(int keyCode, Effect... effects) {
		return simpleKeyBehavior(getLastEntity(), keyCode, effects);
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

	public DemoBuilder behavior(Event event, Effect... effects) {
		return behavior(getLastEntity(), event, effects);
	}

	public DemoBuilder behavior(ModelEntity parent, Event event,
			Effect... effects) {
		lastComponent = makeBehavior(event, effects);
		parent.getComponents().add(lastComponent);
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

	public DemoBuilder moveTween(ModelEntity parent, float xOffset,
			float yOffset) {
		MoveTween moveTween = new MoveTween();
		moveTween.setX(xOffset);
		moveTween.setY(yOffset);
		moveTween.setRelative(true);
		moveTween.setRepeat(-1);
		moveTween.setYoyo(true);
		moveTween.setDuration(2.0f);
		parent.getComponents().add(moveTween);
		return this;
	}

	/**
	 * Adds the given {@link es.eucm.ead.schema.data.Parameter} to the last
	 * component added to {@link #entities}.
	 * 
	 * @param param
	 *            {@link es.eucm.ead.schema.data.Parameter#name}
	 * @param expression
	 *            {@link es.eucm.ead.schema.data.Parameter#value}
	 */
	public DemoBuilder parameter(String param, String expression) {
		return parameter(getLastComponent(), param, expression);
	}

	/**
	 * Adds the given {@link es.eucm.ead.schema.data.Parameter} to the given
	 * {@code container} object.
	 * 
	 * @param container
	 *            The object to add the parameter to. Can be an
	 *            {@link es.eucm.ead.schema.effects.Effect} or a
	 *            {@link es.eucm.ead.schema.components.ModelComponent}.
	 * @param param
	 *            {@link es.eucm.ead.schema.data.Parameter#name}
	 * @param expression
	 *            {@link es.eucm.ead.schema.data.Parameter#value}
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
	 * be {@link es.eucm.ead.schema.components.behaviors.Behavior},
	 * {@link es.eucm.ead.schema.data.Script},
	 * {@link es.eucm.ead.schema.components.conversation.Node} or
	 * {@link es.eucm.ead.schema.effects.controlstructures.ControlStructure}.
	 */
	public DemoBuilder effect(Effect effect) {
		return effect(getLastComponent(), effect);
	}

	/**
	 * Adds the given {@code effect} to the given {@code container}, which can
	 * be {@link es.eucm.ead.schema.components.behaviors.Behavior},
	 * {@link es.eucm.ead.schema.data.Script},
	 * {@link es.eucm.ead.schema.components.conversation.Node} or
	 * {@link es.eucm.ead.schema.effects.controlstructures.ControlStructure}.
	 */
	public DemoBuilder effect(Object container, Effect effect) {
		if (container instanceof Behavior) {
			Behavior behavior = (Behavior) container;
			behavior.getEffects().add(effect);
		} else if (container instanceof Script) {
			Script script = (Script) container;
			script.getEffects().add(effect);
		} else if (container instanceof EffectsNode) {
			EffectsNode node = (EffectsNode) container;
			node.getEffects().add(effect);
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
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#variable}
	 * @param expression
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#expression}
	 */
	public DemoBuilder changeVar(String variable, String expression) {
		return changeVar(getLastComponent(), variable, expression,
				ChangeVar.Context.LOCAL);
	}

	/**
	 * Adds a ChangeVar effect to the given {@code container} object with the
	 * given properties and local context. See
	 * {@link #effect(Object, es.eucm.ead.schema.effects.Effect)} to see the
	 * supported types of containers.
	 * 
	 * @param container
	 *            The object to add this effect to.
	 * @param variable
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#variable}
	 * @param expression
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#expression}
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
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#variable}
	 * @param expression
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#expression}
	 * @param context
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#context}
	 */
	public DemoBuilder changeVar(String variable, String expression,
			ChangeVar.Context context) {
		return changeVar(getLastComponent(), variable, expression, context);
	}

	/**
	 * Adds a ChangeVar effect to the given {@code container} object with the
	 * given properties. See
	 * {@link #effect(Object, es.eucm.ead.schema.effects.Effect)} to see the
	 * supported types of containers.
	 * 
	 * @param container
	 *            The object to add this effect to.
	 * @param variable
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#variable}
	 * @param expression
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#expression}
	 * @param context
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#context}
	 */
	public DemoBuilder changeVar(Object container, String variable,
			String expression, ChangeVar.Context context) {
		effect(container, makeChangeVar(variable, expression, context));
		return this;
	}

	public DemoBuilder playSound(String sound) {
		playSound(getLastComponent(), sound);
		return this;
	}

	public DemoBuilder playSound(Object container, String sound) {
		effect(container, makePlaySound(sound));
		return this;
	}

	public PlaySound makePlaySound(String sound) {
		PlaySound playSound = new PlaySound();
		playSound.setUri(sound);
		playSound.setLoop(false);
		playSound.setVolume(1.0f);
		return playSound;
	}

	/**
	 * Creates a {@link RemoveComponent} effect that removes the component of
	 * type {@code clazz} from entities identified by Mokap expression
	 * {@code target}. The effect is added to the {@code parent} object provided
	 */
	public DemoBuilder removeComponent(Object parent, String target,
			Class<? extends ModelComponent> clazz) {
		effect(parent, makeRemoveComponent(target, clazz));
		return this;
	}

	/**
	 * Creates a {@link RemoveComponent} effect that removes the component of
	 * type {@code clazz} from entities identified by Mokap expression
	 * {@code target}. The effect is added to the {@code parent} last component
	 * created, but it should be a Behavior
	 */
	public DemoBuilder removeComponent(String target,
			Class<? extends ModelComponent> clazz) {
		effect(getLastComponent(), makeRemoveComponent(target, clazz));
		return this;
	}

	/**
	 * Creates an {@link es.eucm.ead.schema.effects.AddComponent} effect with
	 * the given {@code target} and {@code componentToAdd} and adds it to the
	 * last component added to {@link #entities}.
	 */
	public DemoBuilder addComponent(String target, ModelComponent componentToAdd) {
		return addComponent(getLastComponent(), target, componentToAdd);
	}

	/**
	 * Creates an {@link es.eucm.ead.schema.effects.AddComponent} effect with
	 * the given {@code target} and {@code componentToAdd} and adds it to the
	 * given {@code parent} container. For container supported types, see
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
	 * Creates a Timer that starts automatically (no condition) and launches the
	 * {@code effects} provided in {@code seconds} seconds.
	 */
	public Behavior makeSimpleTimer(float seconds, Effect... effects) {
		Timer timer = new Timer();
		timer.setTime(seconds);
		return makeBehavior(timer, effects);
	}

	/**
	 * Creates a simple behaviour triggered by the press of the given key
	 * 
	 * @param keyCode
	 *            The key that triggers the effects, selected from constants
	 *            declared in: {@link com.badlogic.gdx.Input.Keys}
	 * @param effects
	 *            List with the effects to be triggered
	 * @return The behavior created
	 */
	public Behavior makeSimpleKeyBehavior(int keyCode, Effect... effects) {
		Key key = new Key();
		key.setKeycode(keyCode);
		return makeBehavior(key, effects);
	}

	/**
	 * Creates a touch behaviour
	 * 
	 * @param effects
	 *            List with the effects to be triggered
	 * @return The behavior created
	 */
	public Behavior makeTouchBehavior(Effect... effects) {
		return makeBehavior(new Touch(), effects);
	}

	/**
	 * Creates a behaviour launched upon creation of the container entity
	 * 
	 * @param effects
	 *            List with the effects to be triggered
	 * @return The behavior created
	 */
	public Behavior makeInitBehavior(Effect... effects) {
		return makeBehavior(new Init(), effects);
	}

	public Behavior makeBehavior(Event event, Effect... effects) {
		Behavior behavior = new Behavior();
		behavior.setEvent(event);
		for (Effect effect : effects) {
			behavior.getEffects().add(effect);
		}
		return behavior;
	}

	/**
	 * Creates a behaviour that will trigger the given set of effects after the
	 * given time lapse.
	 * 
	 * @param delay
	 *            The time to wait before triggering the effects (in seconds)
	 * @param repeat
	 *            Number of times the effects have to be triggered
	 * @param effects
	 *            The set of effects to trigger.
	 * @return A Behavior component
	 */
	public Behavior makeDelayedEffect(float delay, int repeat,
			Effect... effects) {
		Timer t = new Timer();
		t.setCondition("btrue");
		t.setRepeat(repeat);
		t.setTime(delay);
		return makeBehavior(t, effects);
	}

	/**
	 * Creates a behaviour that will trigger the given set of effects after the
	 * given time lapse. Effects are triggered exactly once.
	 * 
	 * @param delay
	 *            The time to wait before triggering the effects (in seconds)
	 * @param effects
	 *            The set of effects to trigger.
	 * @return A Behavior component
	 */
	public Behavior makeDelayedEffect(float delay, Effect... effects) {
		return makeDelayedEffect(delay, 1, effects);
	}

	/**
	 * Creates a component that makes the container entity visible only when the
	 * given {@code condition} is true
	 * 
	 * @param condition
	 *            The condition, in Mokap exp language
	 */
	public Visibility makeVisibility(String condition) {
		Visibility visibility = new Visibility();
		visibility.setCondition(condition);
		return visibility;
	}

	/**
	 * Creates a
	 * {@link es.eucm.ead.schema.effects.controlstructures.ControlStructure}
	 * effect of the given type ({@code clazz} ) with the given
	 * {@code condition}
	 * 
	 * @param clazz
	 *            Type of control structure. Could be
	 *            {@link es.eucm.ead.schema.effects.controlstructures.If},
	 *            {@link es.eucm.ead.schema.effects.controlstructures.IfThenElseIf}
	 *            or {@link es.eucm.ead.schema.effects.controlstructures.While}.
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
	 * Creates an {@link es.eucm.ead.schema.effects.AddEntity} effect with the
	 * given entity uri
	 */
	public AddEntity makeAddEntity(String entityUri) {
		AddEntity addEntity = new AddEntity();
		addEntity.setEntityUri(entityUri);
		return addEntity;
	}

	/**
	 * Creates a {@link es.eucm.ead.schema.effects.ChangeVar} effect with the
	 * given properties and local context
	 * 
	 * @param variable
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#variable}
	 * @param expression
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#expression}
	 */
	public ChangeVar makeChangeVar(String variable, String expression) {
		return makeChangeVar(variable, expression, ChangeVar.Context.LOCAL);
	}

	/**
	 * Creates a {@link es.eucm.ead.schema.effects.ChangeVar} effect with the
	 * given properties
	 * 
	 * @param variable
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#variable}
	 * @param expression
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#expression}
	 * @param context
	 *            {@link es.eucm.ead.schema.effects.ChangeVar#context}
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
	 * Creates an effect that will remove the component from the given type, if
	 * any, from an entity or group of entities
	 * 
	 * @param target
	 *            Optional argument (can be null) that identifies, through a
	 *            Mokap expression, the entity or entities affected
	 * @param clazz
	 *            The type of component to remove
	 * @return The effect created
	 */
	public RemoveComponent makeRemoveComponent(String target,
			Class<? extends ModelComponent> clazz) {
		RemoveComponent removeComponent = new RemoveComponent();
		removeComponent.setComponent(clazz.getSimpleName().toLowerCase());
		if (target != null) {
			removeComponent.setTarget(target);
		}
		return removeComponent;
	}

	/**
	 * Creates an effect that will remove the component from the given type, if
	 * any, from the entity that contains the effect
	 * 
	 * @param clazz
	 *            The type of component to remove
	 * @return The effect created
	 */
	public RemoveComponent makeRemoveComponent(
			Class<? extends ModelComponent> clazz) {
		return makeRemoveComponent(null, clazz);
	}

	/**
	 * Creates a {@link es.eucm.ead.schema.effects.AddComponent} effect
	 * 
	 * @param target
	 *            {@link es.eucm.ead.schema.effects.AddComponent#target}
	 * @param component
	 *            {@link es.eucm.ead.schema.effects.AddComponent#component}
	 */
	public AddComponent makeAddComponent(String target, ModelComponent component) {
		AddComponent addComponent = new AddComponent();
		addComponent.setComponent(component);
		addComponent.setTarget(target);
		return addComponent;
	}

	/**
	 * Creates a {@link es.eucm.ead.schema.components.tweens.ScaleTween} tho
	 * mirror an entity on the x axis. It actually makes scaleX = -currentScale.
	 * 
	 * @param currentScale
	 *            The current scale of the entity
	 */
	public ScaleTween makeMirrorEntityTween(float currentScale) {
		return makeTween(ScaleTween.class, null, null, null, null, 0F, true,
				null, null, -2 * currentScale, null, null, null);
	}

	public RotateTween makeRotateTween(float rotation, float duration) {
		return makeTween(RotateTween.class, null, null, null, null, duration,
				true, EaseEquation.QUINT, EaseType.INOUT, rotation, null, null,
				null);
	}

	/**
	 * Makes an empty tween of the given type.
	 * 
	 * @param clazz
	 *            Supported types:
	 *            {@link es.eucm.ead.schema.components.tweens.ScaleTween},
	 *            {@link es.eucm.ead.schema.components.tweens.MoveTween},
	 *            {@link es.eucm.ead.schema.components.tweens.RotateTween},
	 *            {@link es.eucm.ead.schema.components.tweens.AlphaTween},
	 *            {@link es.eucm.ead.schema.components.tweens.FieldTween}.
	 */
	public <T extends Tween> T makeTween(Class<T> clazz) {
		return makeTween(clazz, null, null, null, null, null, null, null, null,
				null, null, null, null);
	}

	/**
	 * Makes an tween of the given type with the given properties.
	 * 
	 * @param clazz
	 *            Supported types:
	 *            {@link es.eucm.ead.schema.components.tweens.ScaleTween},
	 *            {@link es.eucm.ead.schema.components.tweens.MoveTween},
	 *            {@link es.eucm.ead.schema.components.tweens.RotateTween},
	 *            {@link es.eucm.ead.schema.components.tweens.AlphaTween},
	 *            {@link es.eucm.ead.schema.components.tweens.FieldTween}.
	 * @param value1
	 *            The first tween-specific parameter. E.g.; "x" value for
	 *            MoveTween, "rotation" value for RotateTween.
	 * @param value2
	 *            The second tween-specific parameter. (Only for MoveTween and
	 *            ScaleTween). E.g.; "y" value for MoveTween.
	 * @param component
	 *            {@link es.eucm.ead.schema.components.tweens.FieldTween#component}
	 * @param field
	 *            {@link es.eucm.ead.schema.components.tweens.FieldTween#field}
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

	public TriggerConversation makeTriggerConversation(String conversationId,
			int startingNodeId) {
		TriggerConversation triggerConversation = new TriggerConversation();
		triggerConversation.setNodeId(startingNodeId);
		triggerConversation.setConversationId(conversationId);
		return triggerConversation;
	}

	public GoScene makeGoScene(String sceneId, Transition transition,
			float duration) {
		return makeGoScene(sceneId, transition, duration, false);
	}

	public GoScene makeGoScene(String sceneId, Transition transition,
			float duration, boolean updateGameLoop) {
		GoScene goScene = new GoScene();
		goScene.setSceneId(sceneId);
		goScene.setTransition(transition);
		goScene.setDuration(duration);
		goScene.setUpdateGameLoop(updateGameLoop);
		return goScene;
	}

	public Color makeColor(float r, float g, float b, float a) {
		Color color = new Color();
		color.setR(r);
		color.setG(g);
		color.setB(b);
		color.setA(a);
		return color;
	}

	/**
	 * Creates an effect that will change one of the four color attributes (red,
	 * green, blue, alpha) of all the entities found to have the given tag.
	 * 
	 * @param tag
	 *            The tag that identifies the entities
	 * @param colorComponent
	 *            "r", "g", "b", or "a". Lowercase and uppercase allowed.
	 * @param value
	 *            float value between 0 and 1 that will be given to the
	 *            particular color attribute
	 * @return The effect
	 */
	public Effect makeChangeColorAttributeEffect(String tag,
			String colorComponent, float value) {
		if (!colorComponent.equals("a") && !colorComponent.equals("A")
				&& !colorComponent.equals("r") && !colorComponent.equals("R")
				&& !colorComponent.equals("g") && !colorComponent.equals("G")
				&& !colorComponent.equals("b") && !colorComponent.equals("B")) {
			throw new RuntimeException("The selected color attribute: "
					+ colorComponent
					+ " is not supported. Only a, r, g, b accepted");
		}
		ChangeEntityProperty setColor = new ChangeEntityProperty();
		setColor.setTarget(eb.entityWithTag(tag));
		setColor.setProperty("group.color." + colorComponent);
		setColor.setExpression("f" + value);
		return setColor;
	}

	/**
	 * Creates an effect that will change the color of all the entities that
	 * have the given tag
	 * 
	 * @param tag
	 *            The tag that identifies entities to be affected by the effect
	 * @param r
	 *            The red component in range [0,1]
	 * @param g
	 *            The green component in range [0,1]
	 * @param b
	 *            The blue component in range [0,1]
	 * @param a
	 *            The alpha component in range [0,1]
	 * @return The effect created
	 */
	public Effect makeChangeColorEffect(String tag, Float r, Float g, Float b,
			Float a) {
		Script script = new Script();
		if (r != null) {
			script.getEffects()
					.add(makeChangeColorAttributeEffect(tag, "r", r));
		}
		if (g != null) {
			script.getEffects()
					.add(makeChangeColorAttributeEffect(tag, "g", g));
		}
		if (b != null) {
			script.getEffects()
					.add(makeChangeColorAttributeEffect(tag, "b", b));
		}
		if (a != null) {
			script.getEffects()
					.add(makeChangeColorAttributeEffect(tag, "a", a));
		}
		ScriptCall scriptCall = new ScriptCall();
		scriptCall.setScript(script);
		return scriptCall;
	}

	public DemoBuilder color(float r, float g, float b, float a) {
		getLastEntity().setColor(makeColor(r, g, b, a));
		return this;
	}

	public DemoBuilder emptyRectangle(int width, int height) {
		emptyRectangle(width, height, false);
		return this;
	}

	public DemoBuilder emptyRectangle(int width, int height, boolean hitAll) {
		EmptyRenderer emptyRenderer = new EmptyRenderer();
		Rectangle rectangle = new Rectangle();
		emptyRenderer.setShape(rectangle);
		emptyRenderer.setHitAll(hitAll);
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		getLastEntity().getComponents().add(emptyRenderer);
		return this;
	}

	public DemoBuilder origin(float originX, float originY) {
		getLastEntity().setOriginX(originX);
		getLastEntity().setOriginY(originY);
		return this;
	}

	public DemoBuilder frames(float frameTime, String... frameUris) {
		getLastEntity().getComponents().add(makeFrames(frameTime, frameUris));
		return this;
	}

	public DemoBuilder frames(float frameTime, String prefix, String suffix,
			int init, int end, int numberCharacters) {
		getLastEntity().getComponents().add(
				makeFrames(frameTime, prefix, suffix, init, end,
						numberCharacters));
		return this;
	}

	public DemoBuilder name(String name) {
		getLastEntity().setName(name);
		return this;
	}

	/**
	 * Builds a {@link PreloadEntity} effect that will load in the background
	 * the given entity, plus all the binary resources referenced in it (e.g.
	 * images and sounds). When the process is complete, the given set of
	 * post-effects will be launched. This can be used as a callback, or to
	 * schedule background loading tasks in separate chunks.
	 * 
	 * @param entityUri
	 *            The path of the entity to be loaded in the background (e.g.
	 *            scenes/s1.json)
	 * @param effects
	 *            The set of effects to be launched once all the resources of
	 *            the entity have been loaded (i.e. callback)
	 */
	public PreloadEntity makePreloadEntity(String entityUri, Effect... effects) {
		PreloadEntity preloadEntity = new PreloadEntity();
		preloadEntity.setEntityUri(entityUri);
		for (Effect effect : effects) {
			preloadEntity.getEffects().add(effect);
		}
		return preloadEntity;
	}

	/**
	 * Creates a {@link PreloadEntity} object using
	 * {@link #makePreloadEntity(String, Effect...)} and adds it to the last
	 * component created, which is expected to be a Behavior.
	 * 
	 * @param entityUri
	 *            The path of the entity to be loaded in the background (e.g.
	 *            scenes/s1.json)
	 * @param effects
	 *            The set of effects to be launched once all the resources of
	 *            the entity have been loaded (i.e. callback)
	 * @return This DemoBuilder object, so calls can be chained
	 */
	public DemoBuilder preloadEntity(String entityUri, Effect... effects) {
		return effect(makePreloadEntity(entityUri, effects));
	}

	/**
	 * Creates a {@link PreloadEntity} object using
	 * {@link #makePreloadEntity(String, Effect...)} and adds it to the given
	 * container. For a list of types of containers supported, see
	 * {@link #effect(Object, Effect)}.
	 * 
	 * @param container
	 *            The object the effect will be added to
	 * @param entityUri
	 *            The path of the entity to be loaded in the background (e.g.
	 *            scenes/s1.json)
	 * @param effects
	 *            The set of effects to be launched once all the resources of
	 *            the entity have been loaded (i.e. callback)
	 * @return This DemoBuilder object, so calls can be chained
	 */
	public DemoBuilder preloadEntity(Object container, String entityUri,
			Effect... effects) {
		return effect(container, makePreloadEntity(entityUri, effects));
	}

	public DemoBuilder reference(String referenceUri) {
		Reference reference = new Reference();
		reference.setFolder(referenceUri + "/");
		reference.setEntity(ModelStructure.CONTENTS_FOLDER
				+ ModelStructure.ENTITY_FILE);
		getLastEntity().getComponents().add(reference);
		return this;
	}

	public Frames makeFrames(float frameTime, String prefix, String suffix,
			int init, int end, int numberCharacters) {
		String[] uris = new String[end - init + 1];
		for (int i = init; i <= end; i++) {
			String number = i + "";
			while (number.length() < numberCharacters) {
				number = "0" + number;
			}
			uris[i - init] = prefix + number + suffix;
		}
		return makeFrames(frameTime, uris);
	}

	public Frames makeFrames(float frameTime, String... frameUris) {
		Frames frames = new Frames();
		for (String uri : frameUris) {
			Frame frame = new Frame();
			Image image = new Image();
			image.setUri(uri);
			frame.setRenderer(image);
			frame.setTime(frameTime);
			frames.getFrames().add(frame);
		}
		return frames;
	}

	/**
	 * Creates a renderer for a {@link SpineAnimation}.
	 * 
	 * @param uri
	 *            Names of the files, without any extension, as produced by the
	 *            Spine software package. Example: "skeleton". Cannot be null.
	 * @param initialState
	 *            Optional argument. If set, changes the initial animation used
	 *            by the engine. The value provided must be a valid spine state
	 *            defined in the file.
	 * @return The SpineAnimation object
	 */
	public SpineAnimation makeSpine(String uri, String initialState) {
		SpineAnimation spine = new SpineAnimation();
		if (initialState != null) {
			spine.setInitialState(initialState);
		}
		spine.setUri(uri);
		return spine;
	}

	/**
	 * Creates a {@link SpineAnimation} renderer component with the arguments
	 * provided using {@link #makeSpine(String, String)} and adds it to the
	 * {@code entity} given as an argument.
	 */
	public DemoBuilder spine(ModelEntity entity, String uri, String initialState) {
		lastComponent = makeSpine(uri, initialState);
		entity.getComponents().add(lastComponent);
		return this;
	}

	/**
	 * Creates a {@link SpineAnimation} renderer component with the arguments
	 * provided using {@link #makeSpine(String, String)} and adds it to the last
	 * entity created.
	 * 
	 * Equivalent to {@code spine(getLastEntity(), uri, initialState}
	 */
	public DemoBuilder spine(String uri, String initialState) {
		return spine(getLastEntity(), uri, initialState);
	}

	public DemoBuilder circle(int radius) {
		getLastEntity().getComponents().add(
				buildShapeRenderer(makeCircle(radius)));
		return this;
	}

	public ShapeRenderer buildShapeRenderer(Shape shape) {
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setShape(shape);
		return shapeRenderer;
	}

	public Circle makeCircle(int radius) {
		Circle circle = new Circle();
		circle.setRadius(radius);
		return circle;
	}

	public ChangeState makeChangeState(String target, String state) {
		ChangeState changeState = new ChangeState();
		changeState.setStateTag(state);
		changeState.setTarget(target);
		return changeState;
	}

	public IfThenElseIf makeIfElse(String condition, Effect ifEffect,
			Effect elseEffect) {
		Array<Effect> ifEffects = new Array<Effect>();
		ifEffects.add(ifEffect);

		Array<Effect> elseEffects = new Array<Effect>();
		if (elseEffect != null) {
			elseEffects.add(elseEffect);
		}
		return makeIfElse(condition, ifEffects, elseEffects);
	}

	public IfThenElseIf makeIfElse(String condition, Array<Effect> ifEffects,
			Array<Effect> elseEffects) {
		IfThenElseIf ifThen = new IfThenElseIf();
		ifThen.setCondition(condition);
		ifThen.setEffects(ifEffects);
		ifThen.setElse(elseEffects);
		return ifThen;
	}

	public void initVar(String var, String expression) {
		initGame.getEffects().add(
				makeChangeVar(var, expression, Context.GLOBAL));
	}

	public enum HorizontalAlign {
		LEFT, CENTER, RIGHT
	}

	public enum VerticalAlign {
		UP, CENTER, DOWN
	}

}
