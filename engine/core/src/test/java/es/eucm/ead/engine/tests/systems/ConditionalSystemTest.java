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
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.systems.ConditionalSystem;
import es.eucm.ead.engine.variables.VariablesManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Javier Torrente on 19/04/14.
 */
public class ConditionalSystemTest extends EngineTest {

	private String variableDef;

	private String variableDef2;

	@Before
	public void setUp() {
		super.setUp();
		// Add variables that will be referenced in the expressions of this
		// test
		variableDef = "testVariable";
		variableDef2 = "testVariable2";

		variablesManager.registerVar(variableDef, -1, false);
		variablesManager.registerVar(variableDef2, "a string", false);
	}

	@Test
	public void test() {
		ConditionalSystemForTest conditionalSystemForTest = new ConditionalSystemForTest(
				gameLoop, variablesManager);
		conditionalSystemForTest.testCondition("btrue", true, true);
		conditionalSystemForTest.testCondition("btrue", false, true);

		conditionalSystemForTest.testCondition("true", false, false);
		conditionalSystemForTest.testCondition("true", true, true);

		conditionalSystemForTest.testCondition("$" + variableDef, true, false);
		conditionalSystemForTest.testCondition("$" + variableDef, false, false);

		conditionalSystemForTest.testCondition("$" + variableDef2, true, true);
		conditionalSystemForTest
				.testCondition("$" + variableDef2, false, false);

		conditionalSystemForTest.testCondition(null, false, false);
		conditionalSystemForTest.testCondition(null, true, true);
	}

	public static class ConditionalSystemForTest extends ConditionalSystem {

		private boolean defaultValue;

		public ConditionalSystemForTest(GameLoop engine,
				VariablesManager variablesManager) {
			super(engine, variablesManager, Family.all().get());
		}

		@Override
		public void doProcessEntity(Entity entity, float deltaTime) {
		}

		protected boolean getDefaultValueForCondition() {
			return defaultValue;
		}

		public void testCondition(String expression, boolean defaultValue,
				boolean expectedValue) {
			this.defaultValue = defaultValue;
			assertEquals(
					"The value of the condition and the returned do not match",
					expectedValue, evaluateCondition(expression));
		}
	}
}
