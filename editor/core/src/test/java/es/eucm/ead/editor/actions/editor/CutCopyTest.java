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
package es.eucm.ead.editor.actions.editor;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Copy;
import es.eucm.ead.editor.control.actions.editor.Cut;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CutCopyTest extends EditorTest {

	@Test
	public void testCopy() {
		ModelEntity copy = new ModelEntity();
		copy.setX(15.f);
		model.getSelection().set(null, Selection.SCENE_ENTITY, copy);
		controller.action(Copy.class);

		Array clipboard = controller.getEditorGameAssets().fromJson(
				Array.class, controller.getClipboard().getContents());

		ModelEntity pasted = (ModelEntity) clipboard.first();

		assertEquals(copy.getX(), pasted.getX(), 0.001f);
	}

	@Test
	public void testCut() {
		ModelEntity cut = new ModelEntity();
		cut.setX(15.f);
		model.getSelection().set(null, Selection.SCENE_ENTITY, cut);
		controller.action(Cut.class);

		Array clipboard = controller.getEditorGameAssets().fromJson(
				Array.class, controller.getClipboard().getContents());

		ModelEntity pasted = (ModelEntity) clipboard.first();

		assertEquals(cut.getX(), pasted.getX(), 0.001f);

	}
}
