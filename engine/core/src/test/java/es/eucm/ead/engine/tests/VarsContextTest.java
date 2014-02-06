/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.tests;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.schema.components.VariableDef;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VarsContextTest {
	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
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
	}

	@Test
	public void testCopyGlobals() {

		VariableDef v;
		VarsContext a = new VarsContext();
		VarsContext b = new VarsContext();

		v = new VariableDef();
		v.setName("v1");
		v.setInitialValue("1.0");
		v.setType(VariableDef.Type.FLOAT);
		a.registerVariable(v);

		v = new VariableDef();
		v.setName(VarsContext.GLOBAL_VAR_PREFIX + "v2");
		v.setInitialValue("2.0");
		v.setType(VariableDef.Type.FLOAT);
		a.registerVariable(v);

		a.copyGlobalsTo(b);
		assertEquals(b.getValue(VarsContext.GLOBAL_VAR_PREFIX + "v2"), 2.0f);
		assertEquals(b.getValue("v1"), null);
		a.setValue(VarsContext.GLOBAL_VAR_PREFIX + "v2", 3.0f);

		b.copyGlobalsTo(a);
		assertEquals(b.getValue(VarsContext.GLOBAL_VAR_PREFIX + "v2"), 3.0f);
	}
}
