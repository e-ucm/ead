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

import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.commands.SelectionCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by angel on 22/05/14.
 */
public class SelectionCommandTest extends CommandTest {

	@Test
	public void testSelection() {
		Array<Object> selection = new Array<Object>();

		Object selected = new Object();
		selection.add(selected);

		SelectionCommand selectionCommand = new SelectionCommand(model,
				selection);

		selectionCommand.doCommand();

		assertSame(model.getSelection().first(), selected);
		selectionCommand.undoCommand();
		assertEquals(model.getSelection().size, 0);
	}

	@Test
	public void testCombine() {
		Array<Object> selection = new Array<Object>();
		Object selected1 = new Object();
		selection.add(selected1);

		SelectionCommand selectionCommand1 = new SelectionCommand(model,
				selection);

		Array<Object> selection2 = new Array<Object>();
		Object selected2 = new Object();
		selection2.add(selected2);

		SelectionCommand selectionCommand2 = new SelectionCommand(model,
				selection2);

		selectionCommand1.doCommand();
		selectionCommand2.doCommand();

		assertSame(model.getSelection().first(), selected2);
		selectionCommand1.undoCommand();
		assertEquals(model.getSelection().size, 0);
	}
}
