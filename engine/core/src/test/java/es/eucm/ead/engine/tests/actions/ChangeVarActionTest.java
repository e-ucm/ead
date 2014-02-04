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
package es.eucm.ead.engine.tests.actions;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.actions.ChangeVar;
import es.eucm.ead.schema.components.VariableDef;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChangeVarActionTest {

	private MockGame mockGame;
	VarsContext vc;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		mockGame.act();
		vc = mockGame.getGameLoop().getVarsContext();
	}

	@Test
	public void testChangeExistingVar() {

		// make sure the variable exists
		VariableDef v = new VariableDef();
		v.setName("a");
		v.setType(VariableDef.Type.INTEGER);
		v.setInitialValue("" + 0);
		vc.registerVariable(v);
		assertEquals(0, vc.getValue("a"));

		// create action
		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable("a");
		changeVar.setExpression("i42");

		mockGame.addActionToDummyActor(changeVar);
		mockGame.act();

		// check result
		assertEquals((Integer) 42, vc.getValue("a"));
	}

	@Test
	public void testImplicitVarCreation() {

		// make sure the variable does not exist
		assertEquals(false, vc.hasVariable("a"));

		// create action
		ChangeVar changeVar = new ChangeVar();
		changeVar.setVariable("a");
		changeVar.setExpression("s\"hello world\"");

		mockGame.addActionToDummyActor(changeVar);
		mockGame.act();

		// check result
		assertEquals("hello world", vc.getValue("a"));
	}
}
