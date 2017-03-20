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
package es.eucm.ead.engine.tests.expressions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.builder.ExpressionBuilder;
import es.eucm.ead.engine.*;
import es.eucm.ead.engine.components.TagsComponent;
import es.eucm.ead.engine.expressions.operators.OperationsFactory;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schemax.Layer;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by jtorrente on 28/10/2015.
 */
public class ExpressionBuilderTest {

	private static final String VAR_NAME = "testVar";
	private static final String VAR_NAME2 = "testVar2";
	private static final String ENTITY_TAG1 = "tag1";
	private static final String ENTITY_TAG2 = "tag2";

	private ExpressionBuilder eb;
	private VariablesManager variablesManager;
	private GameLoop gameLoop;

	@Before
	public void init() {
		eb = new ExpressionBuilder();
		Accessor accessor = new Accessor(new HashMap<String, Object>());
		gameLoop = new GameLoop();
		OperationsFactory operationsFactory = new OperationsFactory(gameLoop,
				accessor, new DefaultGameView(gameLoop,
						new GleanerSystemForTest(gameLoop)));
		variablesManager = new VariablesManager(accessor, operationsFactory);
		variablesManager.registerVar(VAR_NAME, 1F, true);
		variablesManager.registerVar(VAR_NAME2, false, true);
	}

	private void testVariableExpression(String exp, Object expected) {
		assertEquals(variablesManager.evaluateExpression(exp), expected);
	}

	private Entity addEntityWithTag(String tag1) {
		Entity entity = new Entity();
		TagsComponent tagsComponent = new TagsComponent();
		tagsComponent.getTags().add(tag1);
		entity.add(tagsComponent);
		gameLoop.addEntity(entity);
		return entity;
	}

	@Test
	public void testAllEntitiesWithTag() {
		addEntityWithTag(ENTITY_TAG1);
		addEntityWithTag(ENTITY_TAG1);
		addEntityWithTag(ENTITY_TAG2);
		Object ob = variablesManager.evaluateExpression(eb
				.allEntitiesWithTag(ENTITY_TAG1));
		assertEquals(Array.class, ob.getClass());
		Array entities = (Array) ob;
		Assert.assertEquals("Number of entities is not as expected", 2,
				entities.size);
	}

	@Test
	public void testEntityWithTag() {
		Entity entity1 = addEntityWithTag(ENTITY_TAG1);
		Object ob = variablesManager.evaluateExpression(eb
				.entityWithTag(ENTITY_TAG1));
		assertEquals(Entity.class, ob.getClass());
		Entity entity2 = (Entity) ob;
		assertTrue("Entities do not match", entity1 == entity2);
	}

	@Test
	public void testVariableComparedTo() {
		// Test int, short, byte, float are supported
		testVariableExpression(eb.variableComparedTo(VAR_NAME, "=", 1), true);
		testVariableExpression(eb.variableComparedTo(VAR_NAME, "=", 1.0F), true);
		// Test an exception is thrown if unsupported numbers are used
		try {
			eb.variableComparedTo(VAR_NAME, "=", 1D);
			fail("An exception should have been thrown");
		} catch (RuntimeException e) {
		}
		try {
			eb.variableComparedTo(VAR_NAME, "=", (short) 1);
			fail("An exception should have been thrown");
		} catch (RuntimeException e) {
		}
		try {
			eb.variableComparedTo(VAR_NAME, "=", 1L);
			fail("An exception should have been thrown");
		} catch (RuntimeException e) {
		}

		// Test wrong operator triggers exception
		try {
			testVariableExpression(eb.variableComparedTo(VAR_NAME, ">>", 1),
					null);
			fail("An exception should have been thrown");
		} catch (RuntimeException e) {
		}

		// Test variables can be used with and without $
		testVariableExpression(eb.variableComparedTo("$" + VAR_NAME, "=", 1),
				true);
	}

	@Test
	public void testVariableLowerThan() {
		testVariableExpression(eb.variableLowerThan(VAR_NAME, 1), false);
	}

	@Test
	public void testVariableLowerEquals() {
		testVariableExpression(eb.variableLowerEquals(VAR_NAME, 1), true);
	}

	@Test
	public void testVariableIsTrue() {
		testVariableExpression(eb.variableIsTrue(VAR_NAME2), false);
	}

	@Test
	public void testVariableIsFalse() {
		testVariableExpression(eb.variableIsFalse(VAR_NAME2), true);
	}

	@Test
	public void testVariableGreaterThan() {
		testVariableExpression(eb.variableGreaterThan(VAR_NAME, 1), false);
	}

	@Test
	public void testVariableGreaterEquals() {
		testVariableExpression(eb.variableGreaterEquals(VAR_NAME, 1), true);
	}

	@Test
	public void testVariableDifferentTo() {
		testVariableExpression(eb.variableDifferentTo(VAR_NAME, 0), true);
		testVariableExpression(eb.variableDifferentTo(VAR_NAME, 1), false);
		testVariableExpression(eb.variableDifferentTo(VAR_NAME2, false), false);
		testVariableExpression(eb.variableDifferentTo(VAR_NAME2, true), true);
	}

	@Test
	public void testLayerSceneContent() {
		Object ob = variablesManager.evaluateExpression(eb.layerSceneContent());
		assertTrue(
				"A layer was not returned",
				ob.getClass().isAssignableFrom(
						DefaultGameView.EngineLayer.class));
		DefaultGameView.EngineLayer eLayer = (DefaultGameView.EngineLayer) ob;
		Assert.assertEquals("Layer returned is not a scene content",
				Layer.SCENE_CONTENT, eLayer.getLayer());
	}

}
