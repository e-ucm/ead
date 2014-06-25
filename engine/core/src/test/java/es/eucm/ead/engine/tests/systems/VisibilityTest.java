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

import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.utils.IntMap;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.components.VisibilityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.VisibilityProcessor;
import es.eucm.ead.engine.systems.VisibilitySystem;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link VisibilitySystem} Created by Javier Torrente on 17/04/14.
 */
public class VisibilityTest extends EngineTest {

	private String variableDef;

	// These variables are used to determine if the flow structures tested go
	// through the appropriate branch (e.g. "IfThenElse" takes "If" branch if
	// that's the appropriate, etc.)
	private boolean correctBranchCalled = false;
	private boolean somethingCalled = false;

	@Before
	public void setUp() {
		super.setUp();
		gameLoop.addSystem(new VisibilitySystem(gameLoop, variablesManager));
		componentLoader.registerComponentProcessor(Visibility.class,
				new VisibilityProcessor(gameLoop));

		// Add a variable that will be referenced in the expressions of this
		// test
		variableDef = "testVariable";
		variablesManager.registerVar(variableDef, -1, false);
	}

	@Test
	public void testVisibility() {
		reset();

		ModelEntity entity = new ModelEntity();
		Visibility visibility = new Visibility();
		visibility.setCondition("(eq $" + variableDef + " i1)");
		entity.getComponents().add(visibility);

		entitiesLoader.toEngineEntity(entity);
		IntMap<Entity> entityIntMap = gameLoop.getEntitiesFor(Family
				.getFamilyFor(VisibilityComponent.class));
		EngineEntity engineEntity = (EngineEntity) entityIntMap.entries()
				.next().value;
		assertTrue(engineEntity.getGroup().isVisible());

		gameLoop.update(1);
		assertFalse(engineEntity.getGroup().isVisible());

		variablesManager.setVarToExpression(variableDef, "i1");
		gameLoop.update(1);
		assertTrue(engineEntity.getGroup().isVisible());

		variablesManager.setVarToExpression(variableDef, "i0");
		gameLoop.update(1);
		assertFalse(engineEntity.getGroup().isVisible());
	}

	private void reset() {
		correctBranchCalled = false;
		somethingCalled = false;
	}

}
