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
package es.eucm.ead.editor.actions.model.scene;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.ActionsTest;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection.Type;
import es.eucm.ead.editor.control.actions.model.scene.SetEditionContext;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReorderSelectionTest extends ActionsTest {

	private ModelEntity scene;

	@Before
	public void setUp() {
		super.setUp();
		scene = new ModelEntity();
		for (int i = 0; i < 10; i++) {
			scene.getChildren().add(new ModelEntity());
		}
		controller.action(SetEditionContext.class, scene);
	}

	@Test
	public void testToBack() {
		int currentIndex = 9;
		ModelEntity child = scene.getChildren().get(currentIndex);
		setSelection(child);
		for (int i = currentIndex; i > -2; i--) {
			controller.action(ReorderSelection.class, Type.TO_BACK);
			if (currentIndex <= 0) {
				currentIndex = 1;
			}
			assertEquals(--currentIndex, scene.getChildren().indexOf(child));
		}
	}

	@Test
	public void testToFront() {
		int currentIndex = 0;
		ModelEntity child = scene.getChildren().get(currentIndex);
		setSelection(child);
		for (int i = currentIndex; i < 13; i++) {
			controller.action(ReorderSelection.class, Type.TO_FRONT);
			if (currentIndex >= 9) {
				currentIndex = 8;
			}
			assertEquals(++currentIndex, scene.getChildren().indexOf(child));
		}
	}

	@Test
	public void testSendToBack() {
		ModelEntity child = scene.getChildren().get(6);
		setSelection(child);

		controller.action(ReorderSelection.class, Type.SEND_TO_BACK);
		assertEquals(0, scene.getChildren().indexOf(child));
	}

	@Test
	public void testBringToFront() {
		ModelEntity child = scene.getChildren().get(6);
		setSelection(child);

		controller.action(ReorderSelection.class, Type.BRING_TO_FRONT);
		assertEquals(scene.getChildren().size() - 1, scene.getChildren()
				.indexOf(child));
	}

	@Test
	public void testMultipleSelectionOrdenIsIrrelevant() {
		controller.getCommands().pushContext();

		ModelEntity child9 = scene.getChildren().get(9);
		ModelEntity child8 = scene.getChildren().get(8);

		setSelection(child9, child8);

		controller.action(ReorderSelection.class, Type.TO_BACK);
		assertEquals(8, scene.getChildren().indexOf(child9));
		assertEquals(7, scene.getChildren().indexOf(child8));

		controller.action(ReorderSelection.class, Type.TO_FRONT);
		assertEquals(9, scene.getChildren().indexOf(child9));
		assertEquals(8, scene.getChildren().indexOf(child8));

		// Change selection order, perform same operations
		setSelection(child8, child9);

		controller.action(ReorderSelection.class, Type.TO_BACK);
		assertEquals(8, scene.getChildren().indexOf(child9));
		assertEquals(7, scene.getChildren().indexOf(child8));

		controller.action(ReorderSelection.class, Type.TO_FRONT);
		assertEquals(9, scene.getChildren().indexOf(child9));
		assertEquals(8, scene.getChildren().indexOf(child8));
	}

	@Test
	public void testChildrenKeepZOrder() {
		ModelEntity bottomChild = scene.getChildren().get(2);
		ModelEntity topChild = scene.getChildren().get(9);

		setSelection(bottomChild, topChild);

		controller.action(ReorderSelection.class, Type.SEND_TO_BACK);
		assertEquals(0, scene.getChildren().indexOf(bottomChild));
		assertEquals(1, scene.getChildren().indexOf(topChild));

		controller.action(ReorderSelection.class, Type.BRING_TO_FRONT);
		assertEquals(scene.getChildren().size() - 2, scene.getChildren()
				.indexOf(bottomChild));
		assertEquals(scene.getChildren().size() - 1, scene.getChildren()
				.indexOf(topChild));

		bottomChild = scene.getChildren().get(0);
		topChild = scene.getChildren().get(9);

		// Change order of selection
		setSelection(topChild, bottomChild);

		controller.action(ReorderSelection.class, Type.SEND_TO_BACK);
		assertEquals(0, scene.getChildren().indexOf(bottomChild));
		assertEquals(1, scene.getChildren().indexOf(topChild));

		controller.action(ReorderSelection.class, Type.BRING_TO_FRONT);
		assertEquals(scene.getChildren().size() - 2, scene.getChildren()
				.indexOf(bottomChild));
		assertEquals(scene.getChildren().size() - 1, scene.getChildren()
				.indexOf(topChild));

	}

	@Test
	public void testExtremeToBack() {
		int currentIndex = 9;
		ModelEntity child0 = scene.getChildren().get(0);
		ModelEntity child7 = scene.getChildren().get(currentIndex - 2);
		ModelEntity child9 = scene.getChildren().get(currentIndex);

		setSelection(child0, child9, child7);

		for (int i = 0; i < 10; i++) {
			controller.action(ReorderSelection.class, Type.TO_BACK);
			assertTrue(scene.getChildren().indexOf(child7) < scene
					.getChildren().indexOf(child9));
			assertTrue(scene.getChildren().indexOf(child0) < scene
					.getChildren().indexOf(child7));
		}
		assertEquals(0, scene.getChildren().indexOf(child0));
		assertEquals(1, scene.getChildren().indexOf(child7));
		assertEquals(2, scene.getChildren().indexOf(child9));
	}

	@Test
	public void testExtremeToFront() {
		int currentIndex = 0;
		ModelEntity child0 = scene.getChildren().get(currentIndex);
		ModelEntity child1 = scene.getChildren().get(currentIndex + 1);
		ModelEntity child9 = scene.getChildren().get(9);

		setSelection(child1, child0, child9);

		for (int i = 0; i < 10; i++) {
			controller.action(ReorderSelection.class, Type.TO_FRONT);
			assertTrue(scene.getChildren().indexOf(child0) < scene
					.getChildren().indexOf(child1));
			assertTrue(scene.getChildren().indexOf(child1) < scene
					.getChildren().indexOf(child9));
		}
		assertEquals(7, scene.getChildren().indexOf(child0));
		assertEquals(8, scene.getChildren().indexOf(child1));
		assertEquals(9, scene.getChildren().indexOf(child9));
	}

	private void setSelection(Object... args) {
		Array<Object> selection = new Array<Object>();
		selection.addAll(args);
		controller.action(SetSelection.class, selection);
	}
}
