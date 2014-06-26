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

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.KeyPressedComponent;
import es.eucm.ead.engine.components.behaviors.events.RuntimeKey;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.systems.KeyPressedSystem;
import es.eucm.ead.engine.systems.behaviors.KeyBehaviorSystem;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Key;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyBehaviorTest extends BehaviorTest implements MockEffectListener {
	Key key;
	private int executed;

	@Override
	protected void registerComponentProcessors(
			GameLoop gameLoop,
			Map<Class<? extends ModelComponent>, ComponentProcessor> componentProcessors) {
		componentProcessors.put(Behavior.class,
				new BehaviorsProcessor(gameLoop));
		componentProcessors.put(Tags.class, new TagsProcessor(gameLoop));
	}

	@Override
	public void addSystems(GameLoop gameLoop) {
		gameLoop.addSystem(new KeyBehaviorSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new KeyPressedSystem());
	}

	private EngineEntity createModelEntityWithKeyboardInteraction(int keycode,
			boolean shift, boolean control, boolean alt) {
		return createModelEntityWithKeyboardInteraction(keycode, shift,
				control, alt, null);
	}

	private EngineEntity createModelEntityWithKeyboardInteraction(int keycode,
			boolean shift, boolean control, boolean alt, String condition) {
		ModelEntity modelEntity = new ModelEntity();
		Behavior behavior = new Behavior();
		key.setKeycode(keycode);
		key.setShift(shift);
		key.setCtrl(control);
		key.setAlt(alt);
		behavior.setEvent(key);
		behavior.getEffects().add(new MockEffect(this));
		modelEntity.getComponents().add(behavior);
		return addEntity(modelEntity);
	}

	@Before
	public void setUp() {
		super.setUp();
		executed = 0;
	}

	@Test
	public void testCorrectKey() {
		key = new Key();
		EngineEntity entity = createModelEntityWithKeyboardInteraction(5, true,
				false, false);
		KeyPressedComponent keyPressedComponent = gameLoop
				.createComponent(KeyPressedComponent.class);
		RuntimeKey runtimeKeyEvent = new RuntimeKey();
		runtimeKeyEvent.setKeycode(5);
		runtimeKeyEvent.setShift(true);
		keyPressedComponent.getKeyEvents().add(runtimeKeyEvent);
		entity.add(keyPressedComponent);
		gameLoop.update(1);
		gameLoop.update(0); // One more cycle so the effect system can actually
		// execute the effects
		assertTrue("Effect wasn't executed", executed == 1);
		assertFalse(
				"Entity shouldn't have a Keyboard Interaction component, since all Keyboard Interactions should be finished",
				entity.hasComponent(KeyPressedComponent.class));
	}

	@Test
	public void testIncorrectKey() {
		key = new Key();

		EngineEntity entity = createModelEntityWithKeyboardInteraction(5, true,
				false, false);
		KeyPressedComponent keyPressedComponent = gameLoop
				.createComponent(KeyPressedComponent.class);
		RuntimeKey runtimeKeyEvent = new RuntimeKey();
		runtimeKeyEvent.setKeycode(5);
		keyPressedComponent.getKeyEvents().add(runtimeKeyEvent);
		entity.add(keyPressedComponent);
		gameLoop.update(1);
		gameLoop.update(0); // One more cycle so the effect system can actually
		// execute the effects
		assertTrue("Effect wasn't executed", executed == 0);
		assertFalse(
				"Entity shouldn't have a Keyboard Interaction component, since all Keyboard Interactions should be finished",
				entity.hasComponent(KeyPressedComponent.class));
	}

	@Test
	public void testMultipleEntities() {
		key = new Key();

		EngineEntity entity = createModelEntityWithKeyboardInteraction(5,
				false, false, false);
		EngineEntity entity1 = createModelEntityWithKeyboardInteraction(5,
				false, false, false);
		KeyPressedComponent keyPressedComponent = gameLoop
				.createComponent(KeyPressedComponent.class);
		RuntimeKey runtimeKeyEvent = new RuntimeKey();
		runtimeKeyEvent.setKeycode(5);

		keyPressedComponent.getKeyEvents().add(runtimeKeyEvent);
		entity.add(keyPressedComponent);

		gameLoop.update(1);
		gameLoop.update(0); // One more cycle so the effect system can actually
		// execute the effects
		assertEquals("Effect wasn't executed", 2, executed);
		assertFalse(
				"Entity shouldn't have a Keyboard Pressed component, since all Keyboard Interactions should be finished",
				entity.hasComponent(KeyPressedComponent.class));

	}

	@Override
	public void executed() {
		executed++;
	}
}
