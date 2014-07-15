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
package es.eucm.ead.editor.test.sequences;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.nogui.EditorGUITest;
import es.eucm.ead.editor.ui.perspectives.PerspectiveButtons;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

import static org.junit.Assert.assertEquals;

/**
 * Test the process of opening a game, adding an scene element, add a behavior
 * and add an effect to the behavior
 */
public class ChangeViewUndoRedoEditorGUITest extends EditorGUITest {

	int background = 3;

	private ModelEntity[] backgrounds;

	@Override
	protected void runTest() {
		openEmptyGame();
		addBackgrounds();
		modifyBackgrounds();
		for (int i = 0; i < background * 2; i++) {
			click("undo");
		}
		for (int i = 0; i < background; i++) {
			assertEquals(0.0f, backgrounds[i].getX(), 0.001f);
		}
		for (int i = 0; i < background * 2; i++) {
			click("redo");
		}
		for (int i = 0; i < background; i++) {
			assertEquals(0.0f, backgrounds[i].getX(), 25.f);
		}
	}

	private void addBackgrounds() {
		backgrounds = new ModelEntity[background];
		for (int i = 0; i < background; i++) {
			click(PerspectiveButtons.SCENE_SELECTOR);
			click(PerspectiveButtons.SCENE_SELECTOR + i);
			backgrounds[i] = new ModelEntity();
			controller.action(AddSceneElement.class, backgrounds[i]);
		}
	}

	private void modifyBackgrounds() {
		for (int i = 0; i < background; i++) {
			click(PerspectiveButtons.SCENE_SELECTOR);
			click(PerspectiveButtons.SCENE_SELECTOR + i);
			controller.action(SetSelection.class, Selection.EDITED_GROUP,
					Selection.SCENE_ELEMENT, backgrounds[i]);
			controller
					.action(SetField.class, backgrounds[i], FieldName.X, 25.f);
		}
	}
}
