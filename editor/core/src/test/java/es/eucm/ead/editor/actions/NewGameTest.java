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

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;

import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.Versions;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NewGameTest extends ActionTest implements ModelListener<LoadEvent> {

	private int count;

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().addLoadListener(this);
		count = 0;
	}

	@Test
	public void test() {
		File file = platform.createTempFile(true);
		ModelEntity game = new ModelEntity();
		Model.getComponent(game, EditState.class).setEditScene(
				"scenes/scene0.json");
		Model.getComponent(game, Versions.class).setAppVersion("0.0.0");
		String path = controller.getEditorGameAssets().toCanonicalPath(
				file.getAbsolutePath());
		controller.action(NewGame.class, path, game);
		loadAllPendingAssets();
		assertTrue(controller.getLoadingPath().startsWith(path));
		assertEquals(
				"Two events should have been created: unloaded and loaded",
				count, 2);
	}

	@Test
	public void testInvalidName() {
		try {
			// The \0 : < > are an invalid characters for files in different OS.
			// With this, we ensure the file doesn't exist
			controller.action(NewGame.class, ":<>ñor\0", new ModelEntity());
			fail("An exception should be thrown");
		} catch (EditorActionException e) {

		}
	}

	@Override
	public void modelChanged(LoadEvent event) {
		Model model = event.getModel();
		assertEquals(Model.getComponent(model.getGame(), EditState.class)
				.getEditScene(), "scenes/scene0.json");
		assertEquals(Model.getComponent(model.getGame(), GameData.class)
				.getInitialScene(), "scenes/scene0.json");
		count++;
	}
}
