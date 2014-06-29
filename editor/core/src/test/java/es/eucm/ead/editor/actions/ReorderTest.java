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

import es.eucm.ead.editor.control.actions.model.AddScene;
import es.eucm.ead.editor.control.actions.model.ReorderScenes;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests {@link es.eucm.ead.editor.control.actions.model.Reorder} and
 * {@link es.eucm.ead.editor.control.actions.model.ReorderScenes} Created by
 * Javier Torrente on 9/03/14.
 */
public class ReorderTest extends ActionTest {

	@Test
	public void test() {
		initModel();

		String oldSceneOrder = getStreamlinedSceneOrder(); // Should be ""
		// Add scene1
		controller.action(AddScene.class);
		// Add scene2
		controller.action(AddScene.class);
		// Add scene3
		controller.action(AddScene.class);
		String newSceneOrder = getStreamlinedSceneOrder(); // Should be
															// scene0scene1scene2
		assertFalse(oldSceneOrder.equals(newSceneOrder));
		assertEquals("scene0scene1scene2", newSceneOrder);

		// Now, reorder scenes to scene2, scene0, scene1
		controller
				.action(ReorderScenes.class, "scene2", 0, false, Model
						.getComponent(model.getGame(), EditState.class)
						.getSceneorder());
		assertEquals("scene2scene0scene1", getStreamlinedSceneOrder());

		// Now, reorder scenes to scene2, scene1, scene0
		controller
				.action(ReorderScenes.class, "scene1", 1, false, Model
						.getComponent(model.getGame(), EditState.class)
						.getSceneorder());
		assertEquals("scene2scene1scene0", getStreamlinedSceneOrder());

		// Now, try reordering out of bounds. No exception should be thrown, the
		// action fixes the target index to fit into the list
		controller
				.action(ReorderScenes.class, "scene1", 5, false, Model
						.getComponent(model.getGame(), EditState.class)
						.getSceneorder());
		assertEquals("scene2scene0scene1", getStreamlinedSceneOrder());
		controller
				.action(ReorderScenes.class, "scene1", -3, false, Model
						.getComponent(model.getGame(), EditState.class)
						.getSceneorder());
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());

		// Test the action providing the id "scenes" instead of the list
		controller.action(ReorderScenes.class, "scene1", 1, false, "scenes");
		assertEquals("scene2scene1scene0", getStreamlinedSceneOrder());

		// Test the action providing a strange id "s" instead of the list. The
		// action should be executed any way
		controller.action(ReorderScenes.class, "scene1", 2, false, "s");
		assertEquals("scene2scene0scene1", getStreamlinedSceneOrder());

		// Test the action not providing the list. THe action should be able to
		// find the list and execute the reordering.
		controller.action(ReorderScenes.class, "scene1", 1);
		assertEquals("scene2scene1scene0", getStreamlinedSceneOrder());

		// Test the action providing the index of the scene instead of its id.
		controller.action(ReorderScenes.class, 0, 1);
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());

		// Test relative movement
		controller.action(ReorderScenes.class, "scene0", -1, true);
		assertEquals("scene1scene0scene2", getStreamlinedSceneOrder());
		controller.action(ReorderScenes.class, "scene0", -1, true);
		assertEquals("scene0scene1scene2", getStreamlinedSceneOrder());
		controller.action(ReorderScenes.class, "scene0", -1, true);
		assertEquals("scene0scene1scene2", getStreamlinedSceneOrder());
		controller.action(ReorderScenes.class, "scene0", 1, true);
		assertEquals("scene1scene0scene2", getStreamlinedSceneOrder());
		controller.action(ReorderScenes.class, "scene0", 1, true);
		assertEquals("scene1scene2scene0", getStreamlinedSceneOrder());
		controller.action(ReorderScenes.class, "scene0", 1, true);
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
		for (String sceneId : Model.getComponent(model.getGame(),
				EditState.class).getSceneorder()) {
			sceneOrder += sceneId;
		}
		return sceneOrder;
	}

	/**
	 * Add blank stuff to the model
	 */
	private void initModel() {
		// Create empty model
		ModelEntity game = new ModelEntity();
		model.putEntity(ModelEntityCategory.GAME.getCategoryPrefix(), game);
	}
}
