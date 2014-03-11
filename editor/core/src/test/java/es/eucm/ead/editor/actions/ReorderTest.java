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

import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.Reorder;
import es.eucm.ead.editor.control.actions.ReorderScenes;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link es.eucm.ead.editor.control.actions.Reorder} and
 * {@link es.eucm.ead.editor.control.actions.ReorderScenes} Created by Javier
 * Torrente on 9/03/14.
 */
public class ReorderTest extends EditorActionTest {
	@Override
	protected Class getEditorAction() {
		return Reorder.class;
	}

	@Test
	public void test() {
		initModel();

		String oldSceneOrder = getStreamlinedSceneOrder(); // Should be ""
		// Add scene1
		mockController.action(AddScene.class);
		// Add scene2
		mockController.action(AddScene.class);
		// Add scene3
		mockController.action(AddScene.class);
		String newSceneOrder = getStreamlinedSceneOrder(); // Should be
															// scene0scene1scene2
		assertFalse(oldSceneOrder.equals(newSceneOrder));
		assertEquals("scene0scene1scene2", newSceneOrder);

		// Now, reorder scenes to scene2, scene0, scene1
		mockController.action(ReorderScenes.class, "scene2", 0, false,
				mockModel.getGame().getSceneorder());
		assertEquals("scene2scene0scene1", getStreamlinedSceneOrder());

		// Now, reorder scenes to scene2, scene1, scene0
		mockController.action(ReorderScenes.class, "scene1", 1, false,
				mockModel.getGame().getSceneorder());
		assertEquals("scene2scene1scene0", getStreamlinedSceneOrder());

		// Now, try reordering out of bounds. No exception should be thrown, the
		// action fixes the target index to fit into the list
		mockController.action(ReorderScenes.class, "scene1", 5, false,
				mockModel.getGame().getSceneorder());
		assertEquals("scene2scene0scene1", getStreamlinedSceneOrder());
		mockController.action(ReorderScenes.class, "scene1", -3, false,
				mockModel.getGame().getSceneorder());
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());

		// Test the action providing the id "scenes" instead of the list
		mockController
				.action(ReorderScenes.class, "scene1", 1, false, "scenes");
		assertEquals("scene2scene1scene0", getStreamlinedSceneOrder());

		// Test the action providing a strange id "s" instead of the list. The
		// action should be executed any way
		mockController.action(ReorderScenes.class, "scene1", 2, false, "s");
		assertEquals("scene2scene0scene1", getStreamlinedSceneOrder());

		// Test the action not providing the list. THe action should be able to
		// find the list and execute the reordering.
		mockController.action(ReorderScenes.class, "scene1", 1);
		assertEquals("scene2scene1scene0", getStreamlinedSceneOrder());

		// Test the action providing the index of the scene instead of its id.
		mockController.action(ReorderScenes.class, 0, 1);
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());

		// Test the action providing a bad index of the scene instead of its id.
		// An exception should be thrown
		try {
			mockController.action(ReorderScenes.class, 3, 1);
			assertTrue("Action should not complete", false);
		} catch (EditorActionException e) {
			assertTrue("Exception thrown correctly", true);
		}

		// Test insufficient number of arguments (at least 2)
		try {
			mockController.action(ReorderScenes.class);
			assertTrue("Action should not complete", false);
		} catch (EditorActionException e) {
			assertTrue("Exception thrown correctly", true);
		}

		// Test providing fourth argument neither java.util.List or String for
		// the list. An exception should be thrown
		try {
			mockController.action(ReorderScenes.class, 1, 2, false,
					new Object());
			assertTrue("Action should not complete", false);
		} catch (EditorActionException e) {
			assertTrue("Exception thrown correctly", true);
		}

		// Test providing third argument null. An exception should be thrown
		try {
			mockController.action(ReorderScenes.class, 1, 2, null, "scenes");
			assertTrue("Action should not complete", false);
		} catch (EditorActionException e) {
			assertTrue("Exception thrown correctly", true);
		}

		// Test the action providing not an integer as destiny
		try {
			mockController.action(ReorderScenes.class, 3, "1");
			assertTrue("Action should not complete", false);
		} catch (EditorActionException e) {
			assertTrue("Exception thrown correctly", true);
		}

		// Test relative movement
		mockController.action(ReorderScenes.class, "scene0", -1, true);
		assertEquals("scene1scene0scene2", getStreamlinedSceneOrder());
		mockController.action(ReorderScenes.class, "scene0", -1, true);
		assertEquals("scene0scene1scene2", getStreamlinedSceneOrder());
		mockController.action(ReorderScenes.class, "scene0", -1, true);
		assertEquals("scene0scene1scene2", getStreamlinedSceneOrder());
		mockController.action(ReorderScenes.class, "scene0", 1, true);
		assertEquals("scene1scene0scene2", getStreamlinedSceneOrder());
		mockController.action(ReorderScenes.class, "scene0", 1, true);
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());
		mockController.action(ReorderScenes.class, "scene0", 1, true);
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());
	}

	/**
	 * Returns a string with all the sceneIds in
	 * mockModel.getEditorGame().getSceneorder() concatenated with no spaces or
	 * additional characters
	 * 
	 * @return A streamlined version of scene order
	 */
	private String getStreamlinedSceneOrder() {
		String sceneOrder = "";
		for (String sceneId : mockModel.getGame().getSceneorder()) {
			sceneOrder += sceneId;
		}
		return sceneOrder;
	}

	/**
	 * Add blank stuff to the model
	 */
	private void initModel() {
		// Create empty model
		mockModel.setGame(new EditorGame());
		EditorGame metadata = new EditorGame();
		metadata.setNotes(new Note());
		mockModel.setScenes(new HashMap<String, EditorScene>());
	}
}
