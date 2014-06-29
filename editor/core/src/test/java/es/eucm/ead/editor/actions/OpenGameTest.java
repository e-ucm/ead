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
package es.eucm.ead.editor.actions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.schema.editor.components.EditState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OpenGameTest extends ActionTest implements
		ModelListener<LoadEvent> {

	private int count;

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().addLoadListener(this);
		count = 0;
		openEmpty();
	}

	@Test
	public void testNoArgs() {
		count = 0;
		platform.pushPath(emptyGamePath.file().getAbsolutePath());
		controller.action(OpenGame.class);
		assertEquals(emptyGamePath.file().getAbsolutePath() + "/",
				controller.getLoadingPath());
		assertEquals(count, 2);
	}

	@Test
	public void testWithPath() {
		count = 0;
		controller.action(OpenGame.class, emptyGamePath.file()
				.getAbsolutePath());
		assertEquals(emptyGamePath.file().getAbsolutePath() + "/",
				controller.getLoadingPath());
		assertEquals(count, 2);
	}

	@Test
	public void testWithInvalidPath() {
		count = 0;
		try {
			controller.action(OpenGame.class, "ñor/ñor");
			fail("An exception should be thrown");
		} catch (EditorActionException e) {

		}
	}

	@Test
	public void testWithNullPath() {
		count = 0;
		// When user cancels file chooser, a null is returned
		platform.pushPath(null);
		controller.action(OpenGame.class);
	}

	@Test
	public void testInvalidProject() {
		count = 0;
		FileHandle invalidGame = FileHandle.tempDirectory("ead-opengame-test-");
		FileHandle gameJsonFile = invalidGame.child(GameStructure.GAME_FILE);
		gameJsonFile.writeString("{width:1200,height:800}", false);
		try {
			controller.action(OpenGame.class, invalidGame.file()
					.getAbsolutePath());
			fail("An exception must be thrown");
		} catch (GdxRuntimeException e) {

		}
		invalidGame.deleteDirectory();
	}

	@Override
	public void modelChanged(LoadEvent event) {
		Model model = event.getModel();
		assertEquals(Model.getComponent(model.getGame(), EditState.class)
				.getEditScene(), "scenes/scene0.json");
		count++;
	}
}
