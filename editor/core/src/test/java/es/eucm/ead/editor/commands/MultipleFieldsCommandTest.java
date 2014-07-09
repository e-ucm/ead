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
package es.eucm.ead.editor.commands;

import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import es.eucm.ead.editor.control.commands.MultipleFieldsCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultipleFieldsCommandTest extends CommandTest implements
		FieldListener {

	private boolean toggle;

	private int count;

	@Test
	public void testCommand() {
		ModelEntity entity = new ModelEntity();
		model.addFieldListener(entity, this);

		MultipleFieldsCommand command = new MultipleFieldsCommand(entity, false)
				.field(FieldName.X, 10).field(FieldName.Y, 10);

		count = 0;
		toggle = true;
		model.notify(command.doCommand());
		assertEquals(count, 2);

		assertEquals((int) entity.getX(), 10);
		assertEquals((int) entity.getY(), 10);

		toggle = false;
		model.notify(command.undoCommand());
		assertEquals(count, 4);
	}

	@Test
	public void testCombine() {
		ModelEntity entity = new ModelEntity();
		model.addFieldListener(entity, this);

		MultipleFieldsCommand command = new MultipleFieldsCommand(entity, true)
				.field(FieldName.X, 10).field(FieldName.Y, 10);
		MultipleFieldsCommand command2 = new MultipleFieldsCommand(entity, true)
				.field(FieldName.X, 20).field(FieldName.Y, 20);
		MultipleFieldsCommand command3 = new MultipleFieldsCommand(entity, true)
				.field(FieldName.X, 50).field(FieldName.ROTATION, 20);
		assertTrue(command.combine(command2));
		assertFalse(command.combine(command3));

		count = 0;
		toggle = true;
		model.notify(command.doCommand());
		assertEquals(count, 2);
		assertEquals((int) entity.getX(), 20);
		assertEquals((int) entity.getY(), 20);

	}

	@Override
	public boolean listenToField(String fieldName) {
		return true;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		if (FieldName.X.equals(event.getField())) {
			assertTrue(toggle);
		} else if (FieldName.Y.equals(event.getField())) {
			assertFalse(toggle);
		}
		count++;
		toggle = !toggle;
	}
}
