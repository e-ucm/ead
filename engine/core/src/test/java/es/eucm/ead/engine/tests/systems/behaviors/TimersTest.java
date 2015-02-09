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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.behaviors.TimersComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TimersTest extends BehaviorTest implements MockEffectListener {

	private int executed;

	@Override
	protected void registerComponentProcessors(
			GameLoop gameLoop,
			Map<Class<? extends ModelComponent>, ComponentProcessor> componentProcessors) {
		componentProcessors.put(Behavior.class,
				new BehaviorsProcessor(gameLoop));
	}

	@Override
	public void addSystems(GameLoop gameLoop) {
		gameLoop.addSystem(new TimersSystem(gameLoop, variablesManager));
	}

	private EngineEntity createModelEntityWithTimer(int repeats, float time) {
		return createModelEntityWithTimer(repeats, time, null);
	}

	private EngineEntity createModelEntityWithTimer(int repeats, float time,
			String condition) {

		ModelEntity modelEntity = new ModelEntity();
		Timer timer = new Timer();
		timer.setTime(time);
		timer.setRepeat(repeats);
		timer.setCondition(condition);
		Behavior behavior = new Behavior();

		behavior.setEvent(timer);
		Array<Effect> a = new Array<Effect>();
		a.add(new MockEffect(this));
		behavior.setEffects(a);

		modelEntity.getComponents().add(behavior);

		return addEntity(modelEntity);
	}

	@Before
	public void setUp() {
		super.setUp();
		executed = 0;
	}

	@Test
	public void testOneRepeat() {
		EngineEntity entity = createModelEntityWithTimer(1, 1);
		gameLoop.update(1);
		gameLoop.update(0); // One more cycle so the effect system can actually
							// execute the effects
		assertTrue("Effect wasn't executed", executed == 1);
		assertNull(
				"Entity shouldn't have a timer component, since all timer should be finished",
				entity.getComponent(TimersComponent.class));
	}

	@Test
	public void test10Repeats() {
		int repeats = 10;
		EngineEntity entity = createModelEntityWithTimer(repeats, 1);
		for (int i = 0; i < 10; i++) {
			gameLoop.update(1);
			gameLoop.update(0); // One more cycle so the effect system can
								// actually execute the effects
			assertEquals(i + 1, executed);
		}
		gameLoop.update(10);
		assertTrue("Effect wasn't executed", executed == repeats);
		assertNull(
				"Entity shouldn't have a timer component, since all timer should be finished",
				entity.getComponent(TimersComponent.class));
	}

	@Test
	public void testInfiniteRepeats() {
		createModelEntityWithTimer(-1, 1);
		for (int i = 0; i < 100; i++) {
			gameLoop.update(1);
			gameLoop.update(0); // One more cycle so the effect system can
								// actually execute the effects
			assertEquals(i + 1, executed);
		}
	}

	@Test
	public void testInfiniteRepeatsWithConditions() {
		createModelEntityWithTimer(-1, 1, "(eq (% $var i2) i0)");
		addVariable("var", 0);
		for (int i = 0; i < 100; i++) {
			setVariableValue("var", "i" + i);
			gameLoop.update(1);
		}
		assertEquals(50, executed);
	}

	@Test
	public void testSpareTimeIsUsedInNextRepeat() {
		// Timer must use the spared time of the last update, i.e., if a timer
		// repeats each 1 second, and a delta update of 2 seconds comes, it
		// should execute twice, and not only once, throwing away the remaining
		// 1 second
		EngineEntity entity = createModelEntityWithTimer(4, 1);
		gameLoop.update(2);
		gameLoop.update(0); // A second zero update is needed to give the effect
							// system a second cycle to actually process the
							// effects
		assertEquals(executed, 2);

		gameLoop.update(0.5f);
		gameLoop.update(0);
		assertEquals(executed, 2);
		gameLoop.update(1.0f);
		gameLoop.update(0);
		assertEquals(executed, 3);
		gameLoop.update(0.5f);
		gameLoop.update(0);
		assertEquals(executed, 4);
		assertNull(
				"Entity shouldn't have a timer component, since all timer should be finished",
				entity.getComponent(TimersComponent.class));
	}

	@Override
	public void executed() {
		executed++;
	}
}
