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
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.schema.MockComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.physics.VelocityProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.SearchByTagSystem;
import es.eucm.ead.engine.systems.VelocitySystem;
import es.eucm.ead.engine.systems.effects.AddByTagExecutor;
import es.eucm.ead.engine.systems.effects.ChangeVarExecutor;
import es.eucm.ead.engine.systems.variables.VariablesSystem;
import es.eucm.ead.engine.systems.variables.VariablesSystem.VariableListener;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.VariableDef;
import es.eucm.ead.schema.components.VariableDef.Type;
import es.eucm.ead.schema.components.physics.Velocity;
import es.eucm.ead.schema.effects.AddByTag;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests effect that adds a component to entities whose tags match a given
 * expression. See: {@link AddByTag}. Also tests {@link SearchByTagSystem}.
 */
public class AddByTagExecutorTest {

	private EntitiesLoader entitiesLoader;

	private SearchByTagSystem searchByTagSystem;
	private GameLoop gameLoop;

	private AddByTagExecutor executor;

	@Test
	public void test() {
		// Initialization
		MockApplication.initStatics();
		gameLoop = new GameLoop();

		entitiesLoader = new EntitiesLoader(null, gameLoop, null);
		entitiesLoader.registerComponentProcessor(Tags.class,
				new TagsProcessor(gameLoop));
		entitiesLoader.registerComponentProcessor(MockModelComponent1.class,
				new MockProcessor(MockComponent1.class, gameLoop));
		entitiesLoader.registerComponentProcessor(MockModelComponent2.class,
				new MockProcessor(MockComponent2.class, gameLoop));
		entitiesLoader.registerComponentProcessor(MockModelComponent3.class,
				new MockProcessor(MockComponent3.class, gameLoop));

		searchByTagSystem = new SearchByTagSystem(gameLoop);
		gameLoop.addSystem(searchByTagSystem);

		EffectsSystem effectsSystem = new EffectsSystem(gameLoop);
		gameLoop.addSystem(effectsSystem);

		executor = new AddByTagExecutor(entitiesLoader);
		executor.initialize(gameLoop);

		// Create entities with different tags
		ActorEntity actorEntity1 = createAndAddEntityWithTags("tag1,tag2");
		ActorEntity actorEntity2 = createAndAddEntityWithTags("tag1");
		ActorEntity actorEntity3 = createAndAddEntityWithTags("");

		// Add mock components that have an integer value provided as argument.
		executeAddByTag(1, "$tag2", actorEntity1, MockModelComponent1.class);
		executeAddByTag(20, "$tag1", actorEntity2, MockModelComponent2.class);
		executeAddByTag(300, "(and $tag1 $tag2)", actorEntity3,
				MockModelComponent3.class);

		// SearchByTagSystem needs 1 tick to evaluate tag expressions and notify
		// listeners (AddByTagExecutor)
		gameLoop.update(1);

		// Test right components were added to the right entities by checking
		// the total sum of mockcomponents in each entity.
		makeAssertions(321, actorEntity1); // actor1 should have the three
											// components, since it matches the
											// three queries
		makeAssertions(20, actorEntity2); // actor2 should only have the second
											// component.
		makeAssertions(0, actorEntity3); // actor3 does not have tags and should
											// have no components.
	}

	private ActorEntity createAndAddEntityWithTags(String commaSeparatedTags) {
		ModelEntity modelEntity = new ModelEntity();
		if (commaSeparatedTags.length() > 0) {
			Tags tags = new Tags();
			if (commaSeparatedTags.contains(",")) {
				for (String tag : commaSeparatedTags.split(",")) {
					tags.getTags().add(tag);
				}
			} else {
				tags.getTags().add(commaSeparatedTags);
			}
			modelEntity.getComponents().add(tags);
		}
		return entitiesLoader.addEntity(modelEntity);
	}

	private void executeAddByTag(int testValue, String expression,
			ActorEntity owner, Class clazz) {
		AddByTag effect = new AddByTag();
		effect.setTagsExpression(expression);
		MockModelComponent component = null;
		try {
			component = (MockModelComponent) clazz.newInstance();
			component.testValue = testValue;
			effect.setComponent(component);
			executor.execute(owner, effect);
		} catch (Exception e) {
			Gdx.app.error("Exception", "Unexpected exception", e);
		}
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
