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

import es.eucm.ead.editor.control.actions.model.DeleteScene;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DeleteSceneTest extends ActionTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
		openEmpty();
	}

	@Test
	public void testDeleteScene() {
		Map<String, ModelEntity> scenes = model
				.getEntities(ModelEntityCategory.SCENE);

		scenes.clear();
		scenes.put("initial", new ModelEntity());

		// Not delete: only one scene in the game
		controller.action(DeleteScene.class, "initial", false);
		assertEquals(scenes.size(), 1);

		scenes.put("second", new ModelEntity());
		controller.action(DeleteScene.class, "second", false);
		assertEquals(scenes.size(), 1);

		// Assure the initial scene changes to another scene when it is removed
		scenes.put("newInitial", new ModelEntity());
		Model.getComponent(model.getGame(), EditState.class).setEditScene(
				"initial");
		Model.getComponent(model.getGame(), GameData.class).setInitialScene(
				"initial");
		controller.action(DeleteScene.class, "initial", false);
		assertEquals("newInitial",
				Model.getComponent(model.getGame(), GameData.class)
						.getInitialScene());
		assertEquals("newInitial",
				Model.getComponent(model.getGame(), EditState.class)
						.getEditScene());
	}

	@Test
	public void testDeleteUnknownScene() {
		// Assure nothing bad happens removing an non-existing scene
		controller.action(DeleteScene.class, "ñor", false);
	}
}
