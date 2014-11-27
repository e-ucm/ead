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
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.nogui.EditorGUITest;
import es.eucm.ead.editor.ui.perspectives.PerspectiveButtons;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Test the process of opening a game, adding an scene element, add a behavior
 * and add an effect to the behavior
 */
public class RedoWithSelectionEditorGUITest extends EditorGUITest {

	@Override
	protected void runTest() {
		// TODO Commented test
		/*
		 * openEmptyGame(); click(PerspectiveButtons.SCENE_SELECTOR);
		 * click(PerspectiveButtons.SCENE_SELECTOR + "0"); ModelEntity
		 * sceneElement = new ModelEntity();
		 * controller.action(AddSceneElement.class, sceneElement);
		 * assertSame(selection.getSingle(Selection.SCENE_ELEMENT),
		 * sceneElement);
		 * 
		 * controller.action(SetField.class, sceneElement, FieldName.X, 50f);
		 * click("undo"); controller.action(SetSelection.class,
		 * Selection.EDITED_GROUP, Selection.SCENE_ELEMENT);
		 * 
		 * assertTrue(controller.getActions().getAction(Redo.class).isEnabled());
		 * click("redo"); assertEquals(50.0f, sceneElement.getX(), 0.0001f);
		 */
	}
}
