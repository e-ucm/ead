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

import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.ChangeInitialScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResourcesModifiedTest extends EditorTest {

	@Test
	public void testModified() {
		controller.getEditorGameAssets().setLoadingPath(
				platform.createTempFile(true).getAbsolutePath(), false);

		model.putResource(ModelStructure.GAME_FILE, ResourceCategory.GAME,
				new ModelEntity());
		model.putResource("scene1", ResourceCategory.SCENE, new ModelEntity());
		model.putResource("scene2", ResourceCategory.SCENE, new ModelEntity());

		controller.action(ChangeInitialScene.class, "scene1");

		assertTrue(model.getResource(ModelStructure.GAME_FILE).isModified());
		assertFalse(model.getResource("scene1").isModified());
		assertFalse(model.getResource("scene2").isModified());

		controller.action(Save.class);

		controller.action(SetSelection.class, null, Selection.PROJECT,
				model.getGame());
		controller.action(SetSelection.class, Selection.PROJECT,
				Selection.RESOURCE, "scene1");
		controller.action(EditScene.class);

		assertFalse(model.getResource(ModelStructure.GAME_FILE).isModified());
		assertFalse(model.getResource("scene1").isModified());
		assertFalse(model.getResource("scene2").isModified());

		controller.action(AddSceneElement.class, new ModelEntity());

		assertFalse(model.getResource(ModelStructure.GAME_FILE).isModified());
		assertTrue(model.getResource("scene1").isModified());
		assertFalse(model.getResource("scene2").isModified());
	}
}
