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
import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.VisibilityComponent;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.processors.VisibilityProcessor;
import es.eucm.ead.engine.systems.VisibilitySystem;
import es.eucm.ead.engine.systems.variables.VariablesSystem;
import es.eucm.ead.schema.data.VariableDef;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link VisibilitySystem} Created by Javier Torrente on 17/04/14.
 */
public class VisibilityTest {

	private VariableDef variableDef = new VariableDef();

	protected GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	private VariablesSystem variablesSystem;

	// These variables are used to determine if the flow structures tested go
	// through the appropriate branch (e.g. "IfThenElse" takes "If" branch if
	// that's the appropriate, etc.)
	private boolean correctBranchCalled = false;
	private boolean somethingCalled = false;

	@Before
	public void setUp() {
		gameLoop = new GameLoop();
		entitiesLoader = new EntitiesLoader(null, gameLoop, null);
		variablesSystem = new VariablesSystem(new Accessor());

		gameLoop.addSystem(variablesSystem);
		gameLoop.addSystem(new VisibilitySystem(gameLoop, variablesSystem));

		entitiesLoader.registerComponentProcessor(Visibility.class,
				new VisibilityProcessor(gameLoop));

		// Add a variable that will be referenced in the expressions of this
		// test
		variableDef.setType(VariableDef.Type.INTEGER);
		variableDef.setInitialValue("-1");
		variableDef.setName("testVariable");
		List<VariableDef> variableDefList = new ArrayList<VariableDef>();
		variableDefList.add(variableDef);
		variablesSystem.registerVariables(variableDefList);
	}

	@Test
	public void testVisibility() {
		reset();

		ModelEntity entity = new ModelEntity();
		Visibility visibility = new Visibility();
		visibility.setCondition("(eq $" + variableDef.getName() + " i1)");
		entity.getComponents().add(visibility);

		entitiesLoader.addEntity(entity);
		IntMap<Entity> entityIntMap = gameLoop.getEntitiesFor(Family
				.getFamilyFor(VisibilityComponent.class));
		ActorEntity actorEntity = (ActorEntity) entityIntMap.entries().next().value;
		assertTrue(actorEntity.getGroup().isVisible());

		gameLoop.update(1);
		assertFalse(actorEntity.getGroup().isVisible());

		variablesSystem.setValue(variableDef.getName(), "i1");
		gameLoop.update(1);
		assertTrue(actorEntity.getGroup().isVisible());

		variablesSystem.setValue(variableDef.getName(), "i0");
		gameLoop.update(1);
		assertFalse(actorEntity.getGroup().isVisible());
	}

	private void reset() {
		correctBranchCalled = false;
		somethingCalled = false;
	}

}
