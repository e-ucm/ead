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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.engine.mock.schema.MockModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SelectionTest {

	private static ModelEntity scene;
	private static ModelEntity child;
	private static MockModelComponent component;
	private static MockModelComponent component1;

	private Selection selection;

	@BeforeClass
	public static void setUpSelection() {
		scene = new ModelEntity();
		child = new ModelEntity();
		scene.getChildren().add(child);

		component = new MockModelComponent();
		child.getComponents().add(component);

		component1 = new MockModelComponent();
		child.getComponents().add(component1);
	}

	@Before
	public void setUp() {
		selection = new Selection();
	}

	@Test
	public void testSimple() {

		selection.setRoot("scene", scene);
		assertEquals(1, selection.getContexts().size);
		selection.set("scene", "editedGroup", scene);
		assertEquals(2, selection.getContexts().size);
		selection.set("editedGroup", "sceneElement", child);
		assertEquals(3, selection.getContexts().size);
		selection.set("sceneElement", "component", component);
		assertEquals(4, selection.getContexts().size);

		assertSame(selection.get("scene").first(), scene);
		assertSame(selection.get("component").first(), component);

		selection.set("sceneElement", "component", component1);
		assertEquals(4, selection.getContexts().size);
		assertSame(selection.get("component").first(), component1);

		selection.set("scene", "editedGroup", scene);
		assertEquals(4, selection.getContexts().size);
		assertSame(selection.getCurrent().first(), scene);
		selection.set("scene", "editedGroup", new ModelEntity());
		assertEquals(2, selection.getContexts().size);
	}

	@Test
	public void testRemove() {

		selection.setRoot("scene", scene);
		selection.set("scene", "editedGroup", scene);
		selection.set("editedGroup", "sceneElement", child);
		selection.set("sceneElement", "component", child);

		assertEquals(4, selection.getContexts().size);

		Array<Context> contextRemoved = selection.set("scene", "editedGroup",
				new ModelEntity());
		assertEquals(2, selection.getContexts().size);
		assertEquals(2, contextRemoved.size);

		selection.set("scene", "editedGroup", scene);
		selection.set("editedGroup", "sceneElement", child);
		selection.set("sceneElement", "component", child);

		assertEquals(4, selection.getContexts().size);

		contextRemoved = selection.set("editedGroup", "whatever", component);
		assertEquals(3, selection.getContexts().size);
		assertEquals(2, contextRemoved.size);

	}
}
