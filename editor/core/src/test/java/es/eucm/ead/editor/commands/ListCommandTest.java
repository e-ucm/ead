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

import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.schema.behaviors.Behavior;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListCommandTest extends CommandTest {

	private List<Behavior> list;

	@Before
	public void setUp() {
		list = new ArrayList<Behavior>();
	}

	@Test
	public void testAdd() {
		Behavior b = new Behavior();
		AddToListCommand command = new AddToListCommand(list, b);
		command.doCommand();
		assertEquals(list.get(0), b);
		command.undoCommand();
		assertTrue(list.isEmpty());
	}

	@Test
	public void testRemove() {
		Behavior b = new Behavior();
		list.add(b);
		RemoveFromListCommand command = new RemoveFromListCommand(list, b);
		command.doCommand();
		assertTrue(list.isEmpty());
		command.undoCommand();
		assertEquals(list.get(0), b);
	}

	@Test
	public void testRemoveNonExistingItem() {
		Behavior b = new Behavior();
		Behavior b1 = new Behavior();
		list.add(b);
		RemoveFromListCommand command = new RemoveFromListCommand(list, b1);
		command.doCommand();
		assertEquals(list.get(0), b);
		command.undoCommand();
		assertEquals(list.get(0), b);
	}
}
