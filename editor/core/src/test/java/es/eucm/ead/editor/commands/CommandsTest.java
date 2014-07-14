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

import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;
import org.junit.Before;
import org.junit.Test;

import es.eucm.ead.editor.control.Commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommandsTest extends CommandTest {

	private Commands commands;

	private int counter;

	@Before
	public void setUp() {
		super.setUp();
		commands = new Commands(model);
		commands.pushStack();
		counter = 0;
	}

	@Test
	public void testUndoRedo() {

		MockCommand mockCommand = new MockCommand();

		for (int i = 1; i <= 20; i++) {
			commands.command(mockCommand);
			assertEquals(i, counter);
		}

		for (int i = 19; i > 0; i--) {
			commands.undo();
			assertEquals(i, counter);
		}

		for (int i = 2; i <= 20; i++) {
			commands.redo();
			assertEquals(i, counter);
		}

		for (int i = 0; i < 100; i++) {
			commands.undo();
		}

		assertEquals(counter, 0);

	}

	@Test
	public void testTransparent() {
		commands.command(new MockCommand());
		for (int i = 0; i < 10; i++) {
			commands.command(new TransparentCommand());
		}
		commands.undo();

		assertTrue(commands.getUndoHistory().isEmpty());
		assertEquals(commands.getRedoHistory().size(), 11);

		commands.redo();
		assertTrue(commands.getRedoHistory().isEmpty());
	}

	@Test
	public void testContext() {
		// Without merge
		commands.command(new MockCommand());
		assertFalse(commands.getUndoHistory().isEmpty());
		commands.pushStack();
		assertTrue(commands.getUndoHistory().isEmpty());
		commands.popStack(false);
		assertFalse(commands.getUndoHistory().isEmpty());

		commands.getUndoHistory().clear();
		// Merging
		commands.command(new MockCommand());
		commands.pushStack();
		commands.command(new MockCommand());
		commands.popStack(true);
		assertEquals(commands.getUndoHistory().size(), 2);

		// Pops root context
		try {
			commands.popStack(true);
			commands.popStack(false);
			fail("An exception should have be launched");
		} catch (Exception e) {

		}
	}

	@Test
	public void undoRedoTransparentCommand() {
		// Transparent command are not stacked if no other command is in the
		// stack
		commands.command(new TransparentCommand());
		assertEquals(0, commands.getUndoHistory().size());

		commands.command(new MockCommand());
		for (int i = 0; i < 10; i++) {
			commands.command(new TransparentCommand());
		}

		assertEquals(11, commands.getUndoHistory().size());
		commands.undo();
		assertEquals(0, commands.getUndoHistory().size());
		commands.redo();
		assertEquals(11, commands.getUndoHistory().size());
	}

	private class MockCommand extends Command {

		@Override
		public ModelEvent doCommand() {
			counter++;
			return null;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public ModelEvent undoCommand() {
			counter--;
			return null;
		}

		@Override
		public boolean combine(Command other) {
			return false;
		}
	}

	private static class TransparentCommand extends Command {

		@Override
		public ModelEvent doCommand() {
			return null;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public ModelEvent undoCommand() {
			return null;
		}

		@Override
		public boolean combine(Command other) {
			return false;
		}

		@Override
		public boolean isTransparent() {
			return true;
		}
	}
}
