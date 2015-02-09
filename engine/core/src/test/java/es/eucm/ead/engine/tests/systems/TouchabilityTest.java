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
package es.eucm.ead.engine.tests.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.components.TouchabilityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.TouchabilityProcessor;
import es.eucm.ead.engine.systems.TouchabilitySystem;
import es.eucm.ead.schema.components.Touchability;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link TouchabilitySystem}.
 */
public class TouchabilityTest extends EngineTest {

	private String variableDef;

	@Before
	public void setUp() {
		super.setUp();
		gameLoop.addSystem(new TouchabilitySystem(gameLoop, variablesManager));
		componentLoader.registerComponentProcessor(Touchability.class,
				new TouchabilityProcessor(gameLoop));

		// Add a variable that will be referenced in the expressions of this
		// test
		variableDef = "testVariable";
		variablesManager.registerVar(variableDef, -1, false);
	}

	@Test
	public void testTouchability() {

		ModelEntity entity = new ModelEntity();
		Touchability touchability = new Touchability();
		touchability.setCondition("(eq $" + variableDef + " i1)");
		entity.getComponents().add(touchability);

		entitiesLoader.toEngineEntity(entity);
		ImmutableArray<Entity> entityIntMap = gameLoop.getEntitiesFor(Family
				.all(TouchabilityComponent.class).get());
		EngineEntity engineEntity = (EngineEntity) entityIntMap.iterator()
				.next();
		assertTrue(engineEntity.getGroup().isTouchable());

		gameLoop.update(1);
		assertFalse(engineEntity.getGroup().isTouchable());

		variablesManager.setVarToExpression(variableDef, "i1");
		gameLoop.update(1);
		assertTrue(engineEntity.getGroup().isTouchable());

		variablesManager.setVarToExpression(variableDef, "i0");
		gameLoop.update(1);
		assertFalse(engineEntity.getGroup().isTouchable());
	}

}
