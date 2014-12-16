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
package es.eucm.ead.editor.indexes;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.indexes.FuzzyIndex.Term;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by angel on 11/07/14.
 */
public class SceneNamesIndexTest extends EditorTest {

	private static final int SCENES = 4;

	private Array<Term> found = new Array<Term>();

	@Before
	public void setUp() {
		super.setUp();
		model.putResource(ModelStructure.GAME_FILE, new ModelEntity());
		controller.getCommands().pushStack();
		for (int i = 0; i < SCENES; i++) {
			addScene("" + i);
		}
	}

	private void addScene(String id) {
		ModelEntity scene = new ModelEntity();
		Q.getComponent(scene, Documentation.class).setName("Name:" + id);
		model.putResource(id, ResourceCategory.SCENE, scene);
	}

	@Test
	public void testIndex() {
		FuzzyIndex fuzzyIndex = controller.getIndex(SceneNamesIndex.class);
		assertEquals(SCENES, fuzzyIndex.getTerms().size);

		for (int i = 0; i < SCENES; i++) {
			fuzzyIndex.search(i + "", found);
			assertEquals(1, found.size);
			Term term = found.first();
			assertEquals("Name:" + i, term.getTermString());
			assertEquals("" + i, term.getData());
		}
	}

	@Test
	public void testAddRemoveScene() {
		controller.action(NewScene.class, "A name");
		FuzzyIndex fuzzyIndex = controller.getIndex(SceneNamesIndex.class);
		assertEquals(SCENES + 1, fuzzyIndex.getTerms().size);
		controller.action(Undo.class);
		assertEquals(SCENES, fuzzyIndex.getTerms().size);
	}
}
