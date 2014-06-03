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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.commands.SelectionCommand.SetEditionContextCommand;
import es.eucm.ead.editor.control.commands.SelectionCommand.SetSelectionCommand;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by angel on 22/05/14.
 */
public class SelectionCommandTest extends CommandTest {

	@Test
	public void testSetSelection() {
		Array<Object> selection = new Array<Object>();

		Object selected = new Object();
		selection.add(selected);

		SetSelectionCommand selectionCommand = new SetSelectionCommand(model,
				selection);

		selectionCommand.doCommand();

		assertSame(model.getSelection().first(), selected);
		selectionCommand.undoCommand();
		assertEquals(model.getSelection().size, 0);
	}

	@Test
	public void testSetSelectionCombine() {
		Array<Object> selection = new Array<Object>();
		Object selected1 = new Object();
		selection.add(selected1);

		SetSelectionCommand selectionCommand1 = new SetSelectionCommand(model,
				selection);

		Array<Object> selection2 = new Array<Object>();
		Object selected2 = new Object();
		selection2.add(selected2);

		SetSelectionCommand selectionCommand2 = new SetSelectionCommand(model,
				selection2);

		selectionCommand1.doCommand();
		selectionCommand2.doCommand();

		assertSame(model.getSelection().first(), selected2);
		selectionCommand1.undoCommand();
		assertEquals(model.getSelection().size, 0);
	}

	@Test
	public void testSetContext() {
		Object context = new Object();
		Array<Object> selection = new Array<Object>();
		selection.add(new Object());
		model.setSelection(selection);

		SetEditionContextCommand contextCommand = new SetEditionContextCommand(
				model, context);

		SelectionEvent event = contextCommand.doCommand();

		assertSame(model.getEditionContext(), context);
		assertEquals(Type.EDITION_CONTEXT_UPDATED, event.getType());
		assertEquals(context, event.getEditionContext());
		assertTrue(event.getSelection().size == 0);
		event = contextCommand.undoCommand();
		assertNull(model.getEditionContext());
		assertEquals(Type.EDITION_CONTEXT_UPDATED, event.getType());
		assertNull(event.getEditionContext());
		assertTrue(event.getSelection().size == 1);
	}

	@Test
	public void testCombineContext() {
		Object context1 = new Object();
		Object context2 = new Object();
		Array<Object> selection = new Array<Object>();
		selection.add(new Object());

		SetSelectionCommand setSelectionCommand = new SetSelectionCommand(
				model, selection);

		SetEditionContextCommand contextCommand1 = new SetEditionContextCommand(
				model, context1);
		SetEditionContextCommand contextCommand2 = new SetEditionContextCommand(
				model, context2);

		setSelectionCommand.combine(contextCommand1);
		setSelectionCommand.combine(contextCommand2);

		SelectionEvent selectionEvent = setSelectionCommand.doCommand();
		assertEquals(selectionEvent.getType(), Type.EDITION_CONTEXT_UPDATED);
		assertTrue(selectionEvent.getSelection().size == 0);
		assertEquals(selectionEvent.getEditionContext(), context2);
		assertEquals(model.getEditionContext(), context2);

	}
}
