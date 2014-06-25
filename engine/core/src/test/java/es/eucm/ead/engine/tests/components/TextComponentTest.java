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
package es.eucm.ead.engine.tests.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.components.controls.TextComponent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TextComponentTest extends EngineTest {

	private TextComponent textComponent;

	private String text;

	@Before
	public void setUp() {
		super.setUp();
		textComponent = new TextComponent() {

			@Override
			protected void updateText(String newText) {
				text = newText;
			}

			@Override
			public Actor getControl() {
				return null;
			}
		};
		textComponent.setVariablesManager(variablesManager);
		variablesManager.setVarToExpression("var", "sñor");
		variablesManager.setVarToExpression("0", "i0");
		variablesManager.setVarToExpression("1", "i1");
		variablesManager.setVarToExpression("2", "i2");
	}

	@Test
	public void testSimpleVariable() {
		textComponent.setText("#$var#");
		assertTrue(textComponent.listensTo("var"));
		assertEquals("ñor", text);
	}

	@Test
	public void testMultipleVariables() {
		textComponent.setText("This is a #$var# with #$0# #$1#!!");
		assertTrue(textComponent.listensTo("var"));
		assertTrue(textComponent.listensTo("0"));
		assertTrue(textComponent.listensTo("1"));
		assertEquals("This is a ñor with 0 1!!", text);

		variablesManager.setVarToExpression("var", "sjar");
		assertEquals("This is a jar with 0 1!!", text);
	}

	@Test
	public void testExpression() {
		textComponent.setText(" #(eq $0 $1)#");
		assertTrue(textComponent.listensTo("0"));
		assertTrue(textComponent.listensTo("1"));
		assertEquals(" false", text);
		variablesManager.setVarToExpression("0", "i1");
		assertEquals(" true", text);

		textComponent.setText(" #(eq $0(+ $1 $1))# tar");
		assertTrue(textComponent.listensTo("0"));
		assertTrue(textComponent.listensTo("1"));
		assertEquals(" false tar", text);
	}

}
