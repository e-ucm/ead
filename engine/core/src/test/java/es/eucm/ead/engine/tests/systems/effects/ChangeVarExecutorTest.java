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

import ashley.core.Entity;
import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.systems.effects.ChangeVarExecutor;
import es.eucm.ead.engine.systems.variables.VariablesSystem;
import es.eucm.ead.schema.data.VariableDef;
import es.eucm.ead.schema.data.VariableDef.Type;
import es.eucm.ead.schema.effects.ChangeVar;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangeVarExecutorTest {

	private ChangeVarExecutor changeVarExecutor;

	private VariablesSystem variablesSystem;

	private boolean fired;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		variablesSystem = new VariablesSystem(new Accessor());
		changeVarExecutor = new ChangeVarExecutor(variablesSystem);
		List<VariableDef> vars = new ArrayList<VariableDef>();
		VariableDef varDef = new VariableDef();
		varDef.setType(Type.BOOLEAN);
		varDef.setName("boolean");
		varDef.setInitialValue("bfalse");
		vars.add(varDef);

		variablesSystem.registerVariables(vars);
	}

	@Test
	public void testSimpleChangeVar() {
		assertFalse(((Boolean) variablesSystem.getValue("boolean")));

		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable("boolean");
		changeVar.setExpression("btrue");
		changeVarExecutor.execute(new Entity(), changeVar);

		variablesSystem.update(0);

		assertTrue((Boolean) variablesSystem.getValue("boolean"));
	}

	@Test
	public void testChangeNonExistingVarDoesNotThrowException() {
		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable("ñor");
		changeVar.setExpression("btrue");
		changeVarExecutor.execute(new Entity(), changeVar);
		variablesSystem.update(0);
	}

	@Test
	public void testInvalidChangeVarDoesNotThrowException() {
		changeVarExecutor.execute(new Entity(), new ChangeVar());
		variablesSystem.update(0);
	}

	@Test
	public void testChangeVarFiresVariableListener() {
		fired = false;
		variablesSystem.addListener(new VariablesSystem.VariableListener() {
			@Override
			public boolean listensTo(String variableName) {
				return "boolean".equals(variableName);
			}

			@Override
			public void variableChanged(String variableName, Object value) {
				fired = true;
			}
		});

		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable("boolean");
		changeVar.setExpression("btrue");
		changeVarExecutor.execute(new Entity(), changeVar);

		variablesSystem.update(0);
		assertTrue(fired);
	}

}
