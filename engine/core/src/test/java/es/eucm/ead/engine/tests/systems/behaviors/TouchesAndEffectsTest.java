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

import ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.EffectsComponent;
import es.eucm.ead.engine.components.TouchedComponent;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.behaviors.TouchesProcessor;
import es.eucm.ead.engine.systems.behaviors.TouchSystem;
import es.eucm.ead.engine.systems.effects.EffectExecutor;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.data.VariableDef;
import es.eucm.ead.schema.components.behaviors.touches.Touch;
import es.eucm.ead.schema.components.behaviors.touches.Touches;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TouchesAndEffectsTest extends BehaviorTest implements
		MockEffectListener {

	private int executed;

	private int executed1;

	private int executed2;

	private int executed3;

	private TestTargetsExecutor testTargetsExecutor;

	@Override
	protected void registerComponentProcessors(GameLoop gameLoop,
			Map<Class, ComponentProcessor> componentProcessors) {
		componentProcessors.put(Touches.class, new TouchesProcessor(gameLoop));
		componentProcessors.put(Tags.class, new TagsProcessor(gameLoop));
	}

	public void addSystems(GameLoop gameLoop) {
		gameLoop.addSystem(new TouchSystem(gameLoop, variablesSystem));
	}

	@Test
	public void test() {
		executed = 0;

		ModelEntity modelEntity = new ModelEntity();

		Touch touch = new Touch();
		touch.getEffects().add(new MockEffect(this));

		Touches touches = new Touches();
		touches.getTouches().add(touch);

		modelEntity.getComponents().add(touches);

		ActorEntity entity = addEntity(modelEntity);

		TouchedComponent touched = new TouchedComponent();
		touched.touch();

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
		executed = executed1 = executed2 = executed3 = 0;
	}

	@Test
	public void testConditions() {
		effectsSystem.registerEffectExecutor(MockEffect1.class,
				new MockEffectExecutor());
		effectsSystem.registerEffectExecutor(MockEffect2.class,
				new MockEffectExecutor());
		effectsSystem.registerEffectExecutor(MockEffect3.class,
				new MockEffectExecutor());
		reset();

		addVariable("touchToLaunch", VariableDef.Type.INTEGER, "0");

		// This will test conditional effects
		for (int i = 0; i < 100; i++) {
			addVariable("var" + (i + 1), VariableDef.Type.BOOLEAN,
					i % 2 == 0 ? "false" : "true");
		}

		ModelEntity modelEntity = new ModelEntity();

		Touch touch1 = new Touch();
		touch1.setCondition("(eq $touchToLaunch i1)");
		Touch touch2 = new Touch();
		touch2.setCondition("(eq $touchToLaunch i2)");
		Touch touch3 = new Touch();
		touch3.setCondition("(eq $touchToLaunch i2)");
		for (int i = 0; i < 100; i++) {
			MockEffect1 mockEffect1 = new MockEffect1();
			mockEffect1.setCondition("$var" + (i + 1));
			touch1.getEffects().add(mockEffect1);
			MockEffect2 mockEffect2 = new MockEffect2();
			mockEffect2.setCondition("$var" + (i + 1));
			touch2.getEffects().add(mockEffect2);
			MockEffect3 mockEffect3 = new MockEffect3();
			mockEffect3.setCondition("$var" + (i + 1));
			touch3.getEffects().add(mockEffect3);
		}

		Touches touches = new Touches();
		touches.getTouches().add(touch1);
		touches.getTouches().add(touch2);
		touches.getTouches().add(touch3);

		modelEntity.getComponents().add(touches);

		ActorEntity entity = addEntity(modelEntity);

		TouchedComponent touched = new TouchedComponent();
		touched.touch();
		entity.add(touched);

		gameLoop.update(0);
		// No conditions are met, so nothing should happen
		assertTrue("None of the touches should get executed", executed1 == 0
				&& executed2 == 0 && executed3 == 0);
		reset();

		// First touch's condition met
		setVariableValue("touchToLaunch", "i1");
		entity.add(touched);
		gameLoop.update(0);
		gameLoop.update(0);
		assertTrue(executed1 == 50 && executed2 == 0 && executed3 == 0);
		reset();
		// With no more touched components, no effects should be launched
		gameLoop.update(0);
		gameLoop.update(0);
		assertTrue(executed1 == 0 && executed2 == 0 && executed3 == 0);

		// Second and third touches' conditions met. Only the second should be
		// executed (priority).
		setVariableValue("touchToLaunch", "i2");
		entity.add(touched);
		gameLoop.update(0);
		gameLoop.update(0);
		assertTrue(executed1 == 0 && executed2 == 50 && executed3 == 0);
		reset();
		// With no more touched components, no effects should be launched
		gameLoop.update(0);
		gameLoop.update(0);
		assertTrue(executed1 == 0 && executed2 == 0 && executed3 == 0);
	}

	@Test
	public void testTargets() {
		testTargetsExecutor = new TestTargetsExecutor();

		effectsSystem.registerEffectExecutor(MockEffect1.class,
				testTargetsExecutor);
		// Create entities
		ActorEntity actorEntity1 = addEntityWithTags("tag1", "tag2", "tag3");
		ActorEntity actorEntity2 = addEntityWithTags("tag2", "tag3");
		ActorEntity actorEntity3 = addEntityWithTags("tag3");

		// Add an entity to append effects
		ActorEntity owner = addEntity(new ModelEntity());

		// Test "all" and "this"
		testTargetEffectExecution("all", owner, actorEntity1, actorEntity2,
				actorEntity3, owner);
		testTargetEffectExecution("_this", owner, owner);

		// Test valid "each entity"
		testTargetEffectExecution("each _target {(hastag $_target stag1)}",
				owner, actorEntity1);
		testTargetEffectExecution(
				"each _target {(and (not (hastag $_target stag2))  (hastag $_target stag3))}",
				owner, actorEntity3);
		// In the next try, since $this = actorEntity3, (not (hastag $_this
		// stag2)) is equivalent to btrue. It is just to test that effectsSystem
		// is able to resolve $_this and $_target at the same time.
		testTargetEffectExecution(
				"each _target {(and (not (hastag $_this stag2))  (hastag $_target stag3))}",
				actorEntity3, actorEntity1, actorEntity2, actorEntity3);

		// Test not valid "each entity"
		testTargetEffectExecution("each _target {(hastag $_target tag1)}",
				owner); // Bad
						// tag
		testTargetEffectExecution("each _target (hastag $_target stag1)", owner); // No
																					// {}
		testTargetEffectExecution("eachentity {(hastag $_target stag1)}", owner); // eachentity
																					// altogether
	}

	private ActorEntity addEntityWithTags(String... tagsToAdd) {
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
		if (!owner.hasComponent(EffectsComponent.class)) {
			owner.add(new EffectsComponent());
		}
		EffectsComponent effectsComponent = owner
				.getComponent(EffectsComponent.class);
		effectsComponent.getEffectList().add(effect);
		gameLoop.update(0);
		assertEquals(
				"The effect was not executed over all the expected entities",
				0, testTargetsExecutor.expectedTargets.size);
	}

	@Override
	public void executed() {
		executed++;
	}

	public class TestTargetsExecutor extends EffectExecutor<MockEffect1> {

		public Array<Entity> expectedTargets;

		@Override
		public void execute(Entity owner, MockEffect1 effect) {
			assertTrue(
					"The effect was executed over an entity that was not expected",
					expectedTargets.removeValue(owner, true));
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

	public class MockEffect2 extends MockEffect {

		public MockEffect2() {
			super(new MockEffectListener() {
				@Override
				public void executed() {
					executed2++;
				}
			});
		}
	}

	public class MockEffect3 extends MockEffect {

		public MockEffect3() {
			super(new MockEffectListener() {
				@Override
				public void executed() {
					executed3++;
				}
			});
		}
	}

}
