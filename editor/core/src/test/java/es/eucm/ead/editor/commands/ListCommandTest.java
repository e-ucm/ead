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

import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.ReorderInListCommand;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ListCommandTest extends CommandTest {

	private List<ModelEntity> list;

	@Before
	public void setUp() {
		list = new ArrayList<ModelEntity>();
	}

	@Test
	public void testAdd() {
		ModelEntity entity = new ModelEntity();
		entity.setX(10);
		AddToListCommand command = new AddToListCommand(list, entity);
		ListEvent event = (ListEvent) command.doCommand();
		assertEquals(list.get(0), entity);
		// Check also event was formed as expected
		testListEvent(event, entity, 0, ListEvent.Type.ADDED);

		ListEvent event2 = (ListEvent) command.undoCommand();
		assertTrue(list.isEmpty());
		// Check also event was formed as expected
		testListEvent(event2, entity, 0, ListEvent.Type.REMOVED);
	}

	@Test
	public void testAddSpecificIndex() {
		ModelEntity entity1 = new ModelEntity();
		entity1.setX(1);
		ModelEntity entity2 = new ModelEntity();
		entity1.setX(2);
		ModelEntity entity3 = new ModelEntity();
		entity1.setX(3);
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);

		ModelEntity newEntity = new ModelEntity();
		newEntity.setX(10);

		AddToListCommand command = new AddToListCommand(list, newEntity, 1);
		ListEvent event = (ListEvent) command.doCommand();
		assertEquals(list.get(0), entity1);
		assertEquals(list.get(1), newEntity);
		assertEquals(list.get(2), entity2);
		assertEquals(list.get(3), entity3);
		// Check also event was formed as expected
		testListEvent(event, newEntity, 1, ListEvent.Type.ADDED);
	}

	@Test
	public void testRemove() {
		ModelEntity entity = new ModelEntity();
		entity.setX(10);
		list.add(entity);
		RemoveFromListCommand command = new RemoveFromListCommand(list, entity);
		ListEvent event = (ListEvent) command.doCommand();
		assertTrue(list.isEmpty());
		testListEvent(event, entity, 0, ListEvent.Type.REMOVED);

		ListEvent event2 = (ListEvent) command.undoCommand();
		assertEquals(entity, list.get(0));
		testListEvent(event2, entity, 0, ListEvent.Type.ADDED);
	}

	@Test
	public void testRemoveNonExistingItem() {
		ModelEntity entity = new ModelEntity();
		ModelEntity entity1 = new ModelEntity();
		list.add(entity);
		RemoveFromListCommand command = new RemoveFromListCommand(list, entity1);
		command.doCommand();
		assertEquals(list.get(0), entity);
		command.undoCommand();
		assertEquals(list.get(0), entity);
	}

	@Test
	public void testReorderList() {
		ModelEntity entity1 = new ModelEntity();
		ModelEntity entity2 = new ModelEntity();
		ModelEntity entity3 = new ModelEntity();
		ModelEntity entity4 = new ModelEntity();
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		list.add(entity4);

		ReorderInListCommand command = new ReorderInListCommand(list, entity4,
				0);
		command.doCommand();
		assertEquals(list.indexOf(entity4), 0);
		command.undoCommand();
		assertEquals(list.indexOf(entity4), 3);

		ReorderInListCommand command2 = new ReorderInListCommand(list, entity1,
				2);
		command2.doCommand();
		assertEquals(list.indexOf(entity1), 2);
		command2.undoCommand();
		assertEquals(list.indexOf(entity1), 0);
	}

	private void testListEvent(ListEvent event, ModelEntity expectedElement,
			int expectedIndex, ListEvent.Type expectedType) {
		assertEquals(list, event.getTarget());
		assertEquals(expectedElement, event.getElement());
		assertEquals(expectedIndex, event.getIndex());
		assertEquals(expectedType, event.getType());
	}
}
