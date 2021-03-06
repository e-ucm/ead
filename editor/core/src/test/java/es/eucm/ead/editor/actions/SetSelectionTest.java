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

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SetSelectionTest extends ActionTest {

	private static ModelEntity scene;
	private static ModelEntity child;
	private static MockModelComponent component;
	private static MockModelComponent component1;

	@BeforeClass
	public static void prepareModel() {
		scene = new ModelEntity();
		child = new ModelEntity();
		scene.getChildren().add(child);

		component = new MockModelComponent();
		child.getComponents().add(component);

		component1 = new MockModelComponent();
		child.getComponents().add(component1);
	}

	@Test
	public void testSetSelection() {
		Selection selection = controller.getModel().getSelection();

		controller.action(SetField.class, scene, FieldName.X, 0);

		controller.action(SetSelection.class, null, "scene", scene);
		assertSame(selection.getCurrent()[0], scene);
		controller.action(SetSelection.class, "scene", "editedGroup", scene);
		assertSame(selection.getCurrent()[0], scene);
		controller.action(SetSelection.class, "scene", "editedGroup", child);
		assertSame(selection.getCurrent()[0], child);
		controller.action(Undo.class);
		assertEquals(0, selection.getContexts().size);
		controller.action(Redo.class);
		assertEquals(2, selection.getContexts().size);
		assertSame(selection.getCurrent()[0], child);
	}

	@Test
	public void testSelectionUndoRedo() {
		Selection selection = controller.getModel().getSelection();
		controller.action(SetSelection.class, null, "scene", scene);
		controller.action(SetSelection.class, "scene", "editedGroup", scene);
		controller.action(SetSelection.class, "editedGroup", "sceneElement",
				child);
		controller.action(SetSelection.class, "sceneElement", "component",
				component);

		controller.action(SetField.class, component, "intAttribute", 2);
		controller.action(SetSelection.class, "sceneElement", "component",
				component1);

		assertEquals(2, component.getIntAttribute());
		controller.action(Undo.class);

		assertEquals(0, component.getIntAttribute());
		assertSame(component, selection.getSingle("component"));

		controller.action(Redo.class);
		assertEquals(2, component.getIntAttribute());
		assertSame(component1, selection.getSingle("component"));

		controller.action(SetSelection.class, "editedGroup", "sceneElement");
		controller.action(Undo.class);

		assertEquals(0, component.getIntAttribute());
		assertSame(component, selection.getSingle("component"));
		assertSame(child, selection.getSingle("sceneElement"));
	}
}
