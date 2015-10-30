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
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.expressions.operators.OperationsFactory;
import es.eucm.ead.engine.processors.PersistentGameStateProcessor;
import es.eucm.ead.engine.systems.gamestatepersistence.PersistentGameStateSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.engine.components.PersistentVariable;
import es.eucm.ead.schema.engine.components.PersistentGameState;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link PersistentGameStateSystem} and related elements (processor,
 * model component, engine component) Created by Javier Torrente on 17/04/14.
 */
public class PersistentGameStateTest extends EngineTest {

	public static final String VARIABLE1 = "p1";
	public static final String VARIABLE2 = "p2";

	private PersistentGameStateSystem persistentGameStateSystem;

	@Before
	public void setUp() {
		super.setUp();
		persistentGameStateSystem = new PersistentGameStateSystem(
				variablesManager);
		gameLoop.addSystem(persistentGameStateSystem);
		componentLoader.registerComponentProcessor(PersistentGameState.class,
				new PersistentGameStateProcessor(gameLoop));
	}

	/**
	 * Tests that the model component is successfully converted to an Engine
	 * component, and that this is used to register variables and mark them as
	 * persistent
	 */
	@Test
	public void testComponent() {
		makeVariablePersistent(VARIABLE2, "i100");
		gameLoop.update(1);
		assertFalse(variablesManager.isVariableDefined(VARIABLE1));
		assertEquals(100, variablesManager.getValue(VARIABLE2));
		assertTrue(variablesManager.isVariableDefined(VARIABLE2));

		reset().persistentVar(VARIABLE1, "i3").persistentVar(VARIABLE2, "i5")
				.makeAddEntity();

		gameLoop.update(1);
		assertTrue(variablesManager.isVariableDefined(VARIABLE1));
		assertEquals(3, variablesManager.getValue(VARIABLE1));
		assertEquals(100, variablesManager.getValue(VARIABLE2));
	}

	/**
	 * Tests the {@link PersistentGameStateSystem#save()} and
	 * {@link PersistentGameStateSystem#read()} methods by saving some
	 * persistent variables and then reading them with a new, clean system.
	 */
	@Test
	public void testPersistence() {
		persistentGameStateSystem.deletePersistentState();

		reset().persistentVar("p2", "i19").persistentVar("p1", "i26")
				.makeAddEntity();
		variablesManager.registerVar("v1", 7, true);
		variablesManager.registerVar("p1", 13, true);
		variablesManager.push();// Create local context
		variablesManager.registerVar("v2", 17, false);

		gameLoop.update(0);
		persistentGameStateSystem.save();

		VariablesManager variablesManager2 = new VariablesManager(null,
				new OperationsFactory());
		PersistentGameStateSystem persistentGameStateSystem2 = new PersistentGameStateSystem(
				variablesManager2);
		persistentGameStateSystem2.read();
		assertFalse(variablesManager2.isVariableDefined("v1"));
		assertFalse(variablesManager2.isVariableDefined("v2"));
		assertEquals(13, variablesManager2.getValue("p1"));
		assertEquals(19, variablesManager2.getValue("p2"));

		persistentGameStateSystem.deletePersistentState();
	}

	private List<PersistentVariable> persistentVars = new ArrayList<PersistentVariable>();

	private PersistentGameStateTest reset() {
		persistentVars.clear();
		return this;
	}

	private PersistentGameStateTest persistentVar(String var, String exp) {
		PersistentVariable pv = new PersistentVariable();
		pv.setInitValue(exp);
		pv.setVariable(var);
		persistentVars.add(pv);
		return this;
	}

	private Entity makeAddEntity() {
		PersistentGameState persistentGameState = new PersistentGameState();
		for (PersistentVariable pv : persistentVars) {
			persistentGameState.getPersistentVariables().add(pv);
		}

		ModelEntity entity = new ModelEntity();
		entity.getComponents().add(persistentGameState);

		Entity engineEntity = entitiesLoader.toEngineEntity(entity);
		gameLoop.addEntity(engineEntity);
		return engineEntity;
	}

	private void makeVariablePersistent(String var, String exp) {
		try {
			Method method = PersistentGameStateSystem.class.getDeclaredMethod(
					"makeVariablePersistent", String.class, String.class);
			method.setAccessible(true);
			method.invoke(persistentGameStateSystem, var, exp);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
