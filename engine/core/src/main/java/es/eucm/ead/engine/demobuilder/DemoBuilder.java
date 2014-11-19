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

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.behaviors.events.Init;
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
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.effects.TriggerConversation;
import es.eucm.ead.schema.effects.controlstructures.ControlStructure;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;
import es.eucm.ead.schemax.GameStructure;

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
		addImage(modelEntity, imageUri);
		modelEntity.setX(x);
		modelEntity.setY(y);
		if (parent != null) {
			parent.getChildren().add(modelEntity);
		}
		return this;
	}

	public DemoBuilder addImage(ModelEntity entity, String uri) {
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

	protected Image createImage(String uri) {
		Image image = new Image();
		image.setUri(uri);
		return image;
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
		frame.setRenderer(createImage(frameUri));

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
