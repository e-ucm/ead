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

import ashley.core.Component;
import ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.effects.AddComponentExecutor;
import es.eucm.ead.engine.systems.effects.RemoveComponentExecutor;
import es.eucm.ead.engine.systems.variables.VariablesSystem;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.RemoveComponent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests effects that add or remove a component to entities whose tags match a
 * given expression. See: {@link AddComponent} {@link RemoveComponent}.
 */
public class AddRemoveComponentTest {

	private EntitiesLoader entitiesLoader;

	private GameLoop gameLoop;

	private AddComponentExecutor addComponentExecutor;

	private RemoveComponentExecutor removeComponentExecutor;

	@Test
	public void test() {
		// Initialization
		MockApplication.initStatics();
		gameLoop = new GameLoop();

		// Use a gameAssets that knows alias for mockcomponents
		GameAssets gameAssets = new GameAssets(Gdx.files) {
			public void loadBindings() {
				super.loadBindings();
				addClassTag("mockcomponent1", MockModelComponent1.class);
				addClassTag("mockcomponent2", MockModelComponent2.class);
				addClassTag("mockcomponent3", MockModelComponent3.class);
			}
		};
		entitiesLoader = new EntitiesLoader(gameAssets, gameLoop, null);
		entitiesLoader.registerComponentProcessor(MockModelComponent1.class,
				new MockProcessor(MockComponent1.class, gameLoop));
		entitiesLoader.registerComponentProcessor(MockModelComponent2.class,
				new MockProcessor(MockComponent2.class, gameLoop));
		entitiesLoader.registerComponentProcessor(MockModelComponent3.class,
				new MockProcessor(MockComponent3.class, gameLoop));

		VariablesSystem variablesSystem = new VariablesSystem();
		gameLoop.addSystem(variablesSystem);

		EffectsSystem effectsSystem = new EffectsSystem(gameLoop,
				variablesSystem);
		gameLoop.addSystem(effectsSystem);

		addComponentExecutor = new AddComponentExecutor(entitiesLoader);
		addComponentExecutor.initialize(gameLoop);

		removeComponentExecutor = new RemoveComponentExecutor(gameAssets,
				entitiesLoader);
		removeComponentExecutor.initialize(gameLoop);

		// Create a simple entity
		ActorEntity actorEntity = entitiesLoader.addEntity(new ModelEntity());

		// Add mock components that have an integer value provided as argument.
		executeAddComponent(1, actorEntity, MockModelComponent1.class);
		executeAddComponent(20, actorEntity, MockModelComponent2.class);
		executeAddComponent(300, actorEntity, MockModelComponent3.class);

		// Test all three components were added to the entity
		// the total sum of mockcomponents
		makeAssertions(321, actorEntity);

		// Now, remove some of the components
		executeRemoveComponent(actorEntity, "mockcomponent2");
		makeAssertions(301, actorEntity); // actor should have now only two
		// components
		executeRemoveComponent(actorEntity, "mockcomponent4");
		makeAssertions(301, actorEntity); // actor should remain equals since
											// component does not exist
		executeRemoveComponent(actorEntity, "mockcomponent1");
		makeAssertions(300, actorEntity);
	}

	private void executeAddComponent(int testValue, ActorEntity owner,
			Class clazz) {
		AddComponent effect = new AddComponent();
		MockModelComponent component = null;
		try {
			component = (MockModelComponent) clazz.newInstance();
			component.testValue = testValue;
			effect.setComponent(component);
			addComponentExecutor.execute(owner, effect);
		} catch (Exception e) {
			Gdx.app.error("Exception", "Unexpected exception", e);
		}
	}

	private void executeRemoveComponent(ActorEntity owner, String tag) {
		RemoveComponent effect = new RemoveComponent();
		effect.setComponent(tag);
		removeComponentExecutor.execute(owner, effect);
	}

	private void makeAssertions(int expectedSum, ActorEntity actorEntity) {
		int actualSum = 0;
		for (Component component : actorEntity.getComponents()) {
			if (component instanceof MockComponent) {
				actualSum += ((MockComponent) component).testValue;
			}
		}
		assertEquals(
				"The sum does not match the expected. That means not the right components were added",
				expectedSum, actualSum);
	}

	// MockModelComponents, components and processors. Three different
	// components are needed since an entity cannot have more than one component
	// of a given type.
	public abstract static class MockModelComponent extends ModelComponent {
		public int testValue;
	}

	public static class MockModelComponent1 extends MockModelComponent {
	}

	public static class MockModelComponent2 extends MockModelComponent {
	}

	public static class MockModelComponent3 extends MockModelComponent {
	}

	public abstract static class MockComponent extends Component {
		public int testValue;
	}

	public static class MockComponent1 extends MockComponent {
	};

	public static class MockComponent2 extends MockComponent {
	};

	public static class MockComponent3 extends MockComponent {
	};

	public static class MockProcessor extends
			ComponentProcessor<MockModelComponent> {

		private Class clazz;

		public MockProcessor(Class clazz, PooledEngine engine) {
			super(engine);
			this.clazz = clazz;
		}

		@Override
		public Component getComponent(MockModelComponent component) {
			MockComponent mockComponent = null;
			try {
				mockComponent = (MockComponent) clazz.newInstance();
				mockComponent.testValue = component.testValue;
			} catch (Exception e) {
				Gdx.app.error("Exception", "Unexpected exception", e);
			}
			return mockComponent;
		}
	}

}
