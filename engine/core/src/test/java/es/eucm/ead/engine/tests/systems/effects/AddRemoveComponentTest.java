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
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.systems.effects.AddComponentExecutor;
import es.eucm.ead.engine.systems.effects.RemoveComponentExecutor;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.RemoveComponent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests effects that add or remove a component to entities whose tags match a
 * given expression. See: {@link AddComponent} {@link RemoveComponent}.
 */
public class AddRemoveComponentTest extends EffectTest {

	private AddComponentExecutor addComponentExecutor;

	private RemoveComponentExecutor removeComponentExecutor;

	@Before
	public void setUp() {
		super.setUp();
		gameAssets.addClassTag("mockcomponent1", MockModelComponent1.class);
		gameAssets.addClassTag("mockcomponent2", MockModelComponent2.class);
		gameAssets.addClassTag("mockcomponent3", MockModelComponent3.class);
		componentLoader.registerComponentProcessor(MockModelComponent1.class,
				new MockProcessor(MockComponent1.class, gameLoop));
		componentLoader.registerComponentProcessor(MockModelComponent2.class,
				new MockProcessor(MockComponent2.class, gameLoop));
		componentLoader.registerComponentProcessor(MockModelComponent3.class,
				new MockProcessor(MockComponent3.class, gameLoop));
	}

	@Test
	public void test() {
		addComponentExecutor = new AddComponentExecutor(componentLoader);
		addComponentExecutor.initialize(gameLoop);

		removeComponentExecutor = new RemoveComponentExecutor(componentLoader,
				null);
		removeComponentExecutor.initialize(gameLoop);

		// Create a simple entity
		EngineEntity engineEntity = entitiesLoader
				.toEngineEntity(new ModelEntity());

		// Add mock components that have an integer value provided as argument.
		executeAddComponent(1, engineEntity, MockModelComponent1.class);
		executeAddComponent(20, engineEntity, MockModelComponent2.class);
		executeAddComponent(300, engineEntity, MockModelComponent3.class);

		// Test all three components were added to the entity
		// the total sum of mockcomponents
		makeAssertions(321, engineEntity);

		// Now, remove some of the components
		executeRemoveComponent(engineEntity, "mockcomponent2");
		makeAssertions(301, engineEntity); // actor should have now only two
		// components
		executeRemoveComponent(engineEntity, "mockcomponent4");
		makeAssertions(301, engineEntity); // actor should remain equals since
											// component does not exist
		executeRemoveComponent(engineEntity, "mockcomponent1");
		makeAssertions(300, engineEntity);
	}

	private void executeAddComponent(int testValue, EngineEntity owner,
			Class clazz) {
		AddComponent effect = new AddComponent();
		BasisModelComponent component = null;
		try {
			component = (BasisModelComponent) clazz.newInstance();
			component.testValue = testValue;
			effect.setComponent(component);
			addComponentExecutor.execute(owner, effect);
		} catch (Exception e) {
			Gdx.app.error("Exception", "Unexpected exception", e);
		}
	}

	private void executeRemoveComponent(EngineEntity owner, String tag) {
		RemoveComponent effect = new RemoveComponent();
		effect.setComponent(tag);
		removeComponentExecutor.execute(owner, effect);
	}

	private void makeAssertions(int expectedSum, EngineEntity engineEntity) {
		int actualSum = 0;
		for (Component component : engineEntity.getComponents()) {
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
	public abstract static class BasisModelComponent extends ModelComponent {
		public int testValue;
	}

	public static class MockModelComponent1 extends BasisModelComponent {
	}

	public static class MockModelComponent2 extends BasisModelComponent {
	}

	public static class MockModelComponent3 extends BasisModelComponent {
	}

	private abstract static class MockComponent extends Component {
		public int testValue;
	}

	public static class MockComponent1 extends MockComponent {
	};

	public static class MockComponent2 extends MockComponent {
	};

	public static class MockComponent3 extends MockComponent {
	};

	public static class MockProcessor<S extends BasisModelComponent> extends
			ComponentProcessor<S> {

		private Class clazz;

		public MockProcessor(Class clazz, GameLoop engine) {
			super(engine);
			this.clazz = clazz;
		}

		@Override
		public Component getComponent(BasisModelComponent component) {
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
