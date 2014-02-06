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
package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NewGameTest extends EditorActionTest implements
		ModelListener<LoadEvent> {

	private int count;

	@Override
	protected String getEditorAction() {
		return NewGame.NAME;
	}

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().addLoadListener(this);
		count = 0;
	}

	@Test
	public void testNoArgs() {
		controller.action(action);
		loadAllPendingAssets();
		File file = platform.lastTempFile();
		assertTrue(controller.getLoadingPath().startsWith(
				file.getAbsolutePath()));
		assertEquals(count, 1);
	}

	@Test
	public void testWithPath() {
		File file = platform.createTempFile(true);
		controller.action(action, file.getAbsolutePath());
		loadAllPendingAssets();
		assertTrue(controller.getLoadingPath().startsWith(
				file.getAbsolutePath()));
		assertEquals(count, 1);
	}

	@Test
	public void testInvalidName() {
		try {
			// The \0 : < > are an invalid characters for files in different OS.
			// With this, we ensure the file doesn't exist
			controller.action(action, ":<>ñor\0");
			fail("An exception should be thrown");
		} catch (EditorActionException e) {

		}
	}

	@Override
	public void modelChanged(LoadEvent event) {
		Model model = event.getModel();
		assertEquals(model.getProject().getEditScene(), "scene0");
		assertEquals(model.getGame().getInitialScene(), "scene0");
		count++;
	}
}
