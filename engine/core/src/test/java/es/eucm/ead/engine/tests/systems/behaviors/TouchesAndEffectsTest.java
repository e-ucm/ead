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
package es.eucm.ead.engine.tests.systems.behaviors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.EffectsComponent;
import es.eucm.ead.engine.components.TouchedComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.systems.TouchedSystem;
import es.eucm.ead.engine.systems.behaviors.TouchBehaviorSystem;
import es.eucm.ead.engine.systems.effects.EffectExecutor;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.behaviors.events.Touch.Type;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TouchesAndEffectsTest extends BehaviorTest implements
		MockEffectListener {

	private int executed;

	private int executed1;

	private TestTargetsExecutor testTargetsExecutor;

	@Override
	protected void registerComponentProcessors(
			GameLoop gameLoop,
			Map<Class<? extends ModelComponent>, ComponentProcessor> componentProcessors) {
		componentProcessors.put(Behavior.class,
				new BehaviorsProcessor(gameLoop));
		componentProcessors.put(Tags.class, new TagsProcessor(gameLoop));
	}

	public void addSystems(GameLoop gameLoop) {
		gameLoop.addSystem(new TouchBehaviorSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new TouchedSystem());
	}

	@Test
	public void test() {
		executed = 0;

		ModelEntity modelEntity = new ModelEntity();

		Touch touch = new Touch();
		touch.setType(Type.CLICK);

		Behavior behavior = new Behavior();
		behavior.setEvent(touch);

		Array<Effect> a = new Array<Effect>();
		a.add(new MockEffect(this));
		behavior.setEffects(a);

		modelEntity.getComponents().add(behavior);

		EngineEntity entity = addEntity(modelEntity);

		TouchedComponent touched = new TouchedComponent();
		touched.event(Type.CLICK);

		entity.add(touched);

		gameLoop.update(0);
		gameLoop.update(0);
		assertTrue("Effect wasn't executed", executed == 1);
		gameLoop.update(0);
		gameLoop.update(0);
		assertTrue("Effect executed again. It shouldn't be executed",
				executed == 1);
	}

	private void reset() {
		executed = executed1 = 0;
	}

	@Test
	public void testTargets() {
		reset();
		testTargetsExecutor = new TestTargetsExecutor();

		effectsSystem.registerEffectExecutor(MockEffect1.class,
				testTargetsExecutor);
		// Create entities
		EngineEntity engineEntity1 = addEntityWithTags("tag1", "tag2", "tag3");
		EngineEntity engineEntity2 = addEntityWithTags("tag2", "tag3");
		EngineEntity engineEntity3 = addEntityWithTags("tag3");

		// Add an entity to append effects
		EngineEntity owner = addEntity(new ModelEntity());

		// Test "all" and "this"
		testTargetEffectExecution("(collection btrue)", owner, engineEntity1,
				engineEntity2, engineEntity3, owner);
		testTargetEffectExecution("$_this", owner, owner);

		// Test valid "each entity"
		testTargetEffectExecution(
				"(collection sentity (hastag $entity stag1))", owner,
				engineEntity1);
		testTargetEffectExecution(
				"( collection sanEntity (and (not (hastag $anEntity stag2))  (hastag $anEntity stag3)))",
				owner, engineEntity3);
		// In the next try, since $this = actorEntity3, (not (hastag $_this
		// stag2)) is equivalent to btrue. It is just to test that effectsSystem
		// is able to resolve $_this and $_target at the same time.
		testTargetEffectExecution(
				"( collection (and (not (hastag $_this stag2))  (hastag $entity stag3)))",
				engineEntity3, engineEntity1, engineEntity2, engineEntity3);

		// Test not valid "each entity"
		testTargetEffectException("( collection {(hastag $entity tag1))", owner); // Bad
																					// tag
		testTargetEffectException("i10", owner); // no boolean exp
	}

	private EngineEntity addEntityWithTags(String... tagsToAdd) {
		ModelEntity modelEntity = new ModelEntity();
		if (tagsToAdd != null && tagsToAdd.length > 0) {
			Tags tags = new Tags();
			for (String tag : tagsToAdd) {
				tags.getTags().add(tag);
			}
			modelEntity.getComponents().add(tags);
		}
		return addEntity(modelEntity);
	}

	private void testTargetEffectExecution(String target, Entity owner,
			Entity... expectedTargets) {
		MockEffect1 effect = new MockEffect1();
		effect.setTarget(target);
		Array<Entity> expectedTargetsArray = new Array<Entity>();
		for (Entity expectedTarget : expectedTargets) {
			expectedTargetsArray.add(expectedTarget);
		}
		testTargetsExecutor.expectedTargets = expectedTargetsArray;
		EffectsComponent effectsComponent = gameLoop.addAndGetComponent(owner,
				EffectsComponent.class);
		effectsComponent.getEffectList().add(effect);
		gameLoop.update(0);
		assertEquals(
				"The effect was not executed over all the expected entities",
				0, testTargetsExecutor.expectedTargets.size);
	}

	private void testTargetEffectException(String target, Entity owner,
			Entity... expectedTargets) {
		try {
			testTargetEffectExecution(target, owner, expectedTargets);
			fail("An exception should have been thrown");
		} catch (Exception e) {

		}
	}

	@Override
	public void executed() {
		executed++;
	}

	public class TestTargetsExecutor extends EffectExecutor<MockEffect1> {

		public Array<Entity> expectedTargets;

		@Override
		public void execute(Entity target, MockEffect1 effect) {
			assertTrue(
					"The effect was executed over an entity that was not expected",
					expectedTargets.removeValue(target, true));
		}
	}

	public class MockEffect1 extends MockEffect {

		public MockEffect1() {
			super(new MockEffectListener() {
				@Override
				public void executed() {
					executed1++;
				}
			});
		}
	}
}
