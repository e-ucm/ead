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
package es.eucm.ead.editor.test.actions;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.views.SceneView;
import es.eucm.ead.editor.nogui.EditorGUITest;
import es.eucm.ead.editor.nogui.actions.OpenMockGame;
import es.eucm.ead.editor.nogui.actions.OpenMockGame.Game;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

import static org.junit.Assert.assertEquals;

public class CutEditorGUITest extends EditorGUITest {

	@Override
	protected void runTest() {
		Game game = new Game();
		game.setGame(new ModelEntity());
		ModelEntity scene = new ModelEntity();
		ModelEntity sceneElement = new ModelEntity();
		scene.getChildren().add(sceneElement);
		Parent parent = new Parent();
		parent.setParent(scene);
		sceneElement.getComponents().add(parent);

		game.addScene("scenes/scene1.json", scene);

		controller.action(OpenMockGame.class, game);
		controller.action(ChangeView.class, SceneView.class,
				"scenes/scene1.json");

		scene = (ModelEntity) model.getResourceObject("scenes/scene1.json",
				ResourceCategory.SCENE);
		sceneElement = scene.getChildren().first();

		setSelection(Selection.EDITED_GROUP, Selection.SCENE_ELEMENT,
				sceneElement);
		click("cut");

		assertEquals(
				0,
				controller.getModel().getSelection()
						.get(Selection.SCENE_ELEMENT).length);
		assertEquals(0, scene.getChildren().size);
		click("undo");
		assertEquals(1, scene.getChildren().size);
		assertEquals(
				1,
				controller.getModel().getSelection()
						.get(Selection.SCENE_ELEMENT).length);

		for (int i = 0; i < 10; i++) {
			assertEquals(i + 1, scene.getChildren().size);
			click("paste");
		}

	}
}
