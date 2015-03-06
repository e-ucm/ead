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
package es.eucm.ead.engine.tests.systems.effects;

import aurelienribon.tweenengine.Tween;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.components.TweensComponent;
import es.eucm.ead.engine.components.behaviors.TimersComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.tweens.TweensProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.RemoveEntitiesSystem;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.effects.AddComponentExecutor;
import es.eucm.ead.engine.systems.effects.AddEntityExecutor;
import es.eucm.ead.engine.systems.effects.RemoveEntityExecutor;
import es.eucm.ead.engine.systems.tweens.FieldAccessor;
import es.eucm.ead.engine.systems.tweens.FieldAccessor.FieldWrapper;
import es.eucm.ead.engine.systems.tweens.GroupAccessor;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.BaseTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.MoveTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.ScaleTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.TimelineCreator;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.FieldTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.RemoveEntity;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link AddEntity} effects but also serves as a good test bed for
 * {@link es.eucm.ead.engine.systems.RemoveEntitiesSystem}, {@link RemoveEntity}
 * and {@link TimersSystem}.
 * 
 * Created by Javier Torrente on 31/05/14.
 */
public class AddEntityTest extends EngineTest implements EntityListener {

	private Map<Class, BaseTweenCreator> creators;

	private AddEntityExecutor addEntityExecutor;

	private int count;
	private FileHandle tempFile;
	private Json json;

	@BeforeClass
	public static void setUpTweens() {
		Tween.registerAccessor(Group.class, new GroupAccessor());
		Tween.registerAccessor(FieldWrapper.class, new FieldAccessor());
	}

	@Before
	public void setup() {
		super.setUp();
		creators = new HashMap<Class, BaseTweenCreator>();
		ScaleTweenCreator scaleTweenCreator = new ScaleTweenCreator();
		creators.put(ScaleTween.class, scaleTweenCreator);
		MoveTweenCreator moveTweenCreator = new MoveTweenCreator();
		creators.put(MoveTween.class, moveTweenCreator);
		TimelineCreator timelineCreator = new TimelineCreator(creators);
		creators.put(Timeline.class, timelineCreator);

		TweenSystem tweenSystem = new TweenSystem();
		for (Map.Entry<Class, BaseTweenCreator> entry : creators.entrySet()) {
			tweenSystem.registerBaseTweenCreator(entry.getKey(),
					entry.getValue());
		}
		gameLoop.addSystem(tweenSystem);

		TweensProcessor tweensProcessor = new TweensProcessor(gameLoop);
		componentLoader.registerComponentProcessor(AlphaTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(FieldTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(MoveTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(RotateTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(ScaleTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(Timeline.class,
				tweensProcessor);

		TimersSystem timersSystem = new TimersSystem(gameLoop, variablesManager);
		gameLoop.addSystem(timersSystem);
		EffectsSystem effectsSystem = new EffectsSystem(gameLoop,
				variablesManager, gameAssets);
		addEntityExecutor = new AddEntityExecutor(entitiesLoader,
				variablesManager);
		effectsSystem
				.registerEffectExecutor(AddEntity.class, addEntityExecutor);
		effectsSystem.registerEffectExecutor(AddComponent.class,
				new AddComponentExecutor(componentLoader));
		effectsSystem.registerEffectExecutor(RemoveEntity.class,
				new RemoveEntityExecutor());
		gameLoop.addSystem(effectsSystem);

		gameLoop.addSystem(new RemoveEntitiesSystem(gameLoop, variablesManager));

		gameLoop.addEntityListener(this);

		count = 0;

		// Create json and temp file for entity storage
		tempFile = FileHandle.tempFile("ead-test");
		json = new Json();
	}

	@After
	public void cleanTempFile() {
		tempFile.delete();
	}

	@Test
	public void testSimpleAdd() {
		AddEntity addEntity = new AddEntity();
		// Create entity and get it copied to temp path
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(10);
		modelEntity.setY(10);
		json.toJson(modelEntity, null, null, tempFile);
		addEntity.setEntityUri(tempFile.path());
		EngineEntity parentEntity = entitiesLoader
				.toEngineEntity(new ModelEntity());
		// Check "newest entity" points to null
		assertNull(variablesManager
				.getValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR));
		addEntityExecutor.execute(parentEntity, addEntity);
		gameAssets.getAssetManager().finishLoading();
		gameLoop.update(0);
		assertEquals("There should be 2 entities", 2, count);
		// Get added entity
		EngineEntity entityAdded = (EngineEntity) parentEntity.getGroup()
				.getChildren().get(0).getUserObject();
		// Now "newestEntity" should point to the entity that was added
		assertEquals("Newest entity did not update", entityAdded,
				variablesManager
						.getValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR));
		assertEquals("Entity added should have no components", 0, entityAdded
				.getComponents().size());
		// Check x and y are not overriden
		assertEquals("X and Y should be those specified in the entity", 10,
				entityAdded.getGroup().getX(), 0.001F);
		assertEquals("X and Y should be those specified in the entity", 10,
				entityAdded.getGroup().getY(), 0.001F);
		// Check the entity is still there after a long time
		gameLoop.update(1000);
		gameLoop.update(1000);
		assertEquals("There should be 2 entities yet", 2, count);
	}

	@Test
	public void testOverrideXY() {
		AddEntity addEntity = new AddEntity();
		addEntity.setX(20);
		addEntity.setY(20);
		// Create entity and get it copied to temp path
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(10);
		modelEntity.setY(10);
		json.toJson(modelEntity, null, null, tempFile);
		addEntity.setEntityUri(tempFile.path());
		EngineEntity parentEntity = entitiesLoader
				.toEngineEntity(new ModelEntity());
		addEntityExecutor.execute(parentEntity, addEntity);
		gameAssets.getAssetManager().finishLoading();
		gameLoop.update(0);
		// Get added entity
		EngineEntity entityAdded = (EngineEntity) parentEntity.getGroup()
				.getChildren().get(0).getUserObject();
		// Check x and y are overriden by those specified in the effect
		assertEquals("X and Y should be those specified in the effect", 20,
				entityAdded.getGroup().getX(), 0.001F);
		assertEquals("X and Y should be those specified in the effect", 20,
				entityAdded.getGroup().getY(), 0.001F);
	}

	@Test
	public void testTempEntity() {
		AddEntity addEntity = new AddEntity();
		addEntity.setDuration(2);
		ModelEntity modelEntity = new ModelEntity();
		json.toJson(modelEntity, null, null, tempFile);
		addEntity.setEntityUri(tempFile.path());
		EngineEntity parentEntity = entitiesLoader
				.toEngineEntity(new ModelEntity());
		// Check "newest entity" points to null
		assertNull(variablesManager
				.getValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR));
		addEntityExecutor.execute(parentEntity, addEntity);
		gameAssets.getAssetManager().finishLoading();
		gameLoop.update(0);
		assertEquals("There should be 2 entities", 2, count);
		// Get added entity
		EngineEntity entityAdded = (EngineEntity) parentEntity.getGroup()
				.getChildren().get(0).getUserObject();
		// Now "newestEntity" should point to the entity that was added
		assertEquals("Newest entity did not update", entityAdded,
				variablesManager
						.getValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR));
		assertNotNull(
				"Entity added should have a timers component to get the entity removed",
				entityAdded.getComponent(TimersComponent.class));
		// Check the entity gets removed after 2 seconds
		gameLoop.update(1);
		assertEquals("There should be 2 entities yet", 2, count);
		gameLoop.update(1);
		gameLoop.update(1);
		assertEquals("There should be only 1 entity", 1, count);
		// Check "newest entity" points to null again
		assertNull(variablesManager
				.getValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR));
	}

	@Test
	public void testInAnimation() {
		AddEntity addEntity = new AddEntity();
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(-10);
		json.toJson(modelEntity, null, null, tempFile);
		addEntity.setEntityUri(tempFile.path());

		MoveTween inAnimation = new MoveTween();
		inAnimation.setRelative(false);
		inAnimation.setX(50);
		inAnimation.setDuration(1F);
		addEntity.setAnimationIn(inAnimation);

		MoveTween outAnimation = new MoveTween();
		outAnimation.setRelative(false);
		outAnimation.setY(50);
		outAnimation.setDuration(1);
		addEntity.setAnimationOut(outAnimation);

		EngineEntity parentEntity = entitiesLoader
				.toEngineEntity(new ModelEntity());
		addEntityExecutor.execute(parentEntity, addEntity);
		gameAssets.getAssetManager().finishLoading();
		assertEquals("There should be 2 entities", 2, count);
		// Get added entity
		EngineEntity entityAdded = (EngineEntity) parentEntity.getGroup()
				.getChildren().get(0).getUserObject();
		assertNotNull("Entity added should have a tweens component",
				entityAdded.getComponent(TweensComponent.class));
		gameAssets.getAssetManager().finishLoading();
		gameLoop.update(0);
		// Check animation is active
		gameLoop.update(0.5F);
		assertEquals("The entity is not moving!", 20, entityAdded.getGroup()
				.getX(), 0.001F);
		assertEquals("The out animation should not have been launched!", 0,
				entityAdded.getGroup().getY(), 0.001F);
		// Check animation completes
		gameLoop.update(0.5F);
		assertEquals("The entity is not moving!", 50, entityAdded.getGroup()
				.getX(), 0.001F);
		assertEquals("The out animation should not have been launched!", 0,
				entityAdded.getGroup().getY(), 0.001F);
		// Check the entity is not removed, out animation is not launched and in
		// animation has stopped already
		gameLoop.update(1000);
		assertEquals("The entity should not be moving!", 50, entityAdded
				.getGroup().getX(), 0.001F);
		assertEquals("The out animation should not have been launched!", 0,
				entityAdded.getGroup().getY(), 0.001F);
		assertEquals("There should be 2 entities yet", 2, count);
	}

	@Test
	public void testInOutAnimation() {
		AddEntity addEntity = new AddEntity();
		addEntity.setDuration(2);
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(-10);
		json.toJson(modelEntity, null, null, tempFile);
		addEntity.setEntityUri(tempFile.path());

		MoveTween inAnimation = new MoveTween();
		inAnimation.setRepeat(0);
		inAnimation.setYoyo(false);
		inAnimation.setRelative(false);
		inAnimation.setX(50);
		inAnimation.setDuration(1F);
		addEntity.setAnimationIn(inAnimation);

		MoveTween outAnimation = new MoveTween();
		outAnimation.setRepeat(0);
		outAnimation.setYoyo(false);
		outAnimation.setRelative(false);
		outAnimation.setY(50);
		outAnimation.setX(50);
		outAnimation.setDuration(1);
		addEntity.setAnimationOut(outAnimation);

		EngineEntity parentEntity = entitiesLoader
				.toEngineEntity(new ModelEntity());
		addEntityExecutor.execute(parentEntity, addEntity);
		gameAssets.getAssetManager().finishLoading();
		assertEquals("There should be 2 entities", 2, count);
		// Get added entity
		EngineEntity entityAdded = (EngineEntity) parentEntity.getGroup()
				.getChildren().get(0).getUserObject();
		assertNotNull("Entity added should have a tweens component",
				entityAdded.getComponent(TweensComponent.class));
		assertNotNull("Entity added should have a timers component",
				entityAdded.getComponent(TimersComponent.class));
		gameLoop.update(0);
		// Check animation is active
		gameLoop.update(0.5F);
		assertEquals("The entity is not moving!", 20, entityAdded.getGroup()
				.getX(), 0.001F);
		assertEquals("The out animation should not have been launched!", 0,
				entityAdded.getGroup().getY(), 0.001F);
		// Check in animation completes
		gameLoop.update(0.5F);
		assertEquals("The entity is not moving!", 50, entityAdded.getGroup()
				.getX(), 0.001F);
		assertEquals("The out animation should not have been launched!", 0,
				entityAdded.getGroup().getY(), 0.001F);
		// Check out animation starts after 2 seconds - not earlier
		gameLoop.update(2);
		assertEquals("There should be 2 entities yet", 2, count);
		assertEquals("The entity is not moving!", 50, entityAdded.getGroup()
				.getX(), 0.001F);
		assertEquals("The out animation should not have been launched!", 0,
				entityAdded.getGroup().getY(), 0.001F);
		// Animation starts
		gameLoop.update(0.5F);
		assertEquals("There should be 2 entities yet", 2, count);
		assertEquals("The entity should not be moving on the X!", 50,
				entityAdded.getGroup().getX(), 0.001F);
		assertEquals("The out animation should have started", 25, entityAdded
				.getGroup().getY(), 0.001F);
		// Out animation Completes - the entity has to be removed
		gameLoop.update(0.49F);
		assertEquals("There should be 2 entities yet", 2, count);
		assertEquals("The entity should not be moving!", 50, entityAdded
				.getGroup().getX(), 0.001F);
		assertEquals("The entity should almost be there", 50, entityAdded
				.getGroup().getY(), 0.001F);
		gameLoop.update(0.01F);
		gameLoop.update(0.01F);
		assertEquals("There should be one entity only", 1, count);
	}

	@Test
	public void testAnimationDuration() {
		Timeline timeline1 = new Timeline();
		timeline1.setMode(Timeline.Mode.PARALLEL);

		Timeline timeline2 = new Timeline();
		timeline2.setMode(Timeline.Mode.SEQUENCE);
		ScaleTween scaleTween = new ScaleTween();
		scaleTween.setDelay(10);
		scaleTween.setDuration(20);
		timeline2.getChildren().add(scaleTween);

		MoveTween moveTween = new MoveTween();
		moveTween.setRepeat(10);
		moveTween.setDuration(7);
		moveTween.setDelay(6);
		moveTween.setRepeatDelay(13);
		timeline2.getChildren().add(moveTween);

		MoveTween moveTween2 = new MoveTween();
		moveTween2.setRepeat(-1);
		moveTween2.setDuration(7);
		moveTween2.setDelay(6);
		moveTween2.setRepeatDelay(13);

		MoveTween moveTween3 = new MoveTween();
		moveTween3.setDuration(10000);
		timeline1.getChildren().add(moveTween3);

		timeline1.getChildren().add(timeline2);
		checkDuration(moveTween);
		checkDuration(scaleTween);
		checkDuration(moveTween2);
		checkDuration(moveTween3);
		checkDuration(timeline2);
		checkDuration(timeline1);
	}

	private void checkDuration(BaseTween baseTween) {
		aurelienribon.tweenengine.BaseTween runtimeTween = creators.get(
				baseTween.getClass()).createTween(new EngineEntity(gameLoop),
				baseTween);
		runtimeTween.build();
		float expectedDuration = runtimeTween.getFullDuration();
		assertEquals("duration is not the expected", expectedDuration,
				getDuration(baseTween), 0.001F);
	}

	private float getDuration(BaseTween baseTween) {
		return TweenSystem.getAnimationFullDuration(baseTween);
	}

	@Override
	public void entityAdded(Entity entity) {
		count++;
	}

	@Override
	public void entityRemoved(Entity entity) {
		count--;
	}
}
