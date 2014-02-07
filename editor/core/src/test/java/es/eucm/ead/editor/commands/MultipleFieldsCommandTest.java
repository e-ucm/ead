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
package es.eucm.ead.editor.commands;

import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import org.junit.Test;

import es.eucm.ead.editor.control.commands.MultipleFieldsCommand;
import es.eucm.ead.schema.components.Transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultipleFieldsCommandTest extends CommandTest implements
		FieldListener {

	private boolean toggle;

	private int count;

	@Test
	public void testCommand() {
		Transformation t = new Transformation();
		model.addFieldListener(t, this);

		MultipleFieldsCommand command = new MultipleFieldsCommand(t, false)
				.field("x", 10).field("y", 10);

		count = 0;
		toggle = true;
		model.notify(command.doCommand());
		assertEquals(count, 2);

		assertEquals((int) t.getX(), 10);
		assertEquals((int) t.getY(), 10);

		toggle = false;
		model.notify(command.undoCommand());
		assertEquals(count, 4);
	}

	@Test
	public void testCombine() {
		Transformation t = new Transformation();
		model.addFieldListener(t, this);

		MultipleFieldsCommand command = new MultipleFieldsCommand(t, true)
				.field("x", 10).field("y", 10);
		MultipleFieldsCommand command2 = new MultipleFieldsCommand(t, true)
				.field("x", 20).field("y", 20);
		MultipleFieldsCommand command3 = new MultipleFieldsCommand(t, true)
				.field("x", 50).field("rotation", 20);
		assertTrue(command.combine(command2));
		assertFalse(command.combine(command3));

		count = 0;
		toggle = true;
		model.notify(command.doCommand());
		assertEquals(count, 2);
		assertEquals((int) t.getX(), 20);
		assertEquals((int) t.getY(), 20);

	}

	@Override
	public boolean listenToField(String fieldName) {
		return true;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		if ("x".equals(event.getField())) {
			assertTrue(toggle);
		} else if ("y".equals(event.getField())) {
			assertFalse(toggle);
		}
		count++;
		toggle = !toggle;
	}
}
