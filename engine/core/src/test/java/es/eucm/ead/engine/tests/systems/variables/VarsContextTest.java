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
package es.eucm.ead.engine.tests.systems.variables;

import es.eucm.ead.engine.*;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.schema.data.VariableDef;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class VarsContextTest {

	public static final String COMPLEX_VAR = "complexVar";

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
	}

	private VariablesManager buildVariablesManager() {
		GameAssets gameAssets = new GameAssets(new MockFiles());
		ComponentLoader componentLoader = new ComponentLoader(gameAssets);
		VariablesManager variablesManager = new VariablesManager(
				componentLoader, null);
		return variablesManager;
	}

	@Test
	public void testInvalidUserDefinedVar() {
		VariablesManager variablesManager = buildVariablesManager();

		VariableDef variableDef = new VariableDef();
		variableDef.setName("_var");
		List<VariableDef> vars = new ArrayList<VariableDef>();
		vars.add(variableDef);
		try {
			variablesManager.registerVariables(vars);
			fail("An exception should be thrown because variableDef starts with _");
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testVars() {
		VarsContext vars = new VarsContext();

		VariableDef v = new VariableDef();
		v.setName("var");
		v.setInitialValue("1.0");
		v.setType(VariableDef.Type.FLOAT);
		vars.registerVariable(v);

		assertEquals(vars.getVariable("var").getType(), Float.class);

		// Value doesn't change, because it's an invalid type for the variable
		vars.setValue("var", 50);
		assertEquals(vars.getValue("var"), 1.0f);
		vars.setValue("var", 50.0f);
		assertEquals(vars.getValue("var"), 50.0f);

		// Test complex types
		vars.registerVariable(COMPLEX_VAR, new B(1, 2), A.class);
		assertTrue(vars.getValue(COMPLEX_VAR) instanceof A);
		assertEquals(1, ((A) (vars.getValue(COMPLEX_VAR))).a);
		assertEquals(2, ((B) (vars.getValue(COMPLEX_VAR))).b);

		vars.setValue(COMPLEX_VAR, new C(3, 4));
		assertTrue(vars.getValue(COMPLEX_VAR) instanceof A);
		assertEquals(3, ((A) (vars.getValue(COMPLEX_VAR))).a);
		assertEquals(4, ((C) (vars.getValue(COMPLEX_VAR))).c);
		vars.setValue(COMPLEX_VAR, null);
		assertNull(vars.getValue(COMPLEX_VAR));
	}

	@Test
	public void testLocalContexts() {
		VariablesManager variablesManager = buildVariablesManager();
		// Test pop() throws an exception as there is no local
		// context created
		try {
			variablesManager.pop();
			fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			assertTrue(true);
		}

		// Test variables are resolved well when different contexts are present
		// in the stack
		variablesManager.push().registerVar("testVar1", 5).push()
				.registerVar("testVar1", 10);
		assertEquals(10, variablesManager.getValue("testVar1"));
		variablesManager.pop();
		assertEquals(5, variablesManager.getValue("testVar1"));
		variablesManager.push();
		assertEquals(5, variablesManager.getValue("testVar1"));
	}

	public abstract static class A {
		public int a;

		public A(int a) {
			this.a = a;
		}
	}

	public static class B extends A {
		public int b;

		public B(int a, int b) {
			super(a);
			this.b = b;
		}
	}

	public static class C extends A {
		public int c;

		public C(int a, int c) {
			super(a);
			this.c = c;
		}
	}
}
