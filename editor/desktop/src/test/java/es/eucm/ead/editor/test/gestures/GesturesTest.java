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
package es.eucm.ead.editor.test.gestures;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.nogui.ViewGUITest;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import org.junit.Test;

public class GesturesTest extends ViewGUITest {

	private Group target;

	private GroupEditor groupEditor;

	@Override
	protected Group buildView() {
		groupEditor = new GroupEditor(new ShapeRenderer(),
				new GroupEditorConfiguration());

		Group root = new Group();
		target = new Group();

		root.addActor(target);

		groupEditor.setRootGroup(root);
		groupEditor.setSize(800, 600);
		groupEditor.setFillParent(true);
		return groupEditor;
	}

	@Test
	public void testRotation() {
		Array<Actor> selection = new Array<Actor>();
		selection.add(target);
		groupEditor.getGroupEditorDragListener().setSelection(selection);

		press(400, 300, 0);
		press(500, 300, 1);

		drag(500, 200, 0);
		drag(500, 0, 1);

		// assertEquals(180.f, target.getRotation(), 0.1f);

	}
}
