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
package es.eucm.ead.editor.views;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.editor.view.widgets.groupeditor.Modifier;
import es.eucm.ead.engine.mock.MockApplication;

public class ModifierTest {

	private static final int WIDTH = 800, HEIGHT = 600;

	private Modifier modifier;

	private Actor actor;

	@Before
	public void setUp() {
		MockApplication.initStatics();
		GroupEditorConfiguration config = new GroupEditorConfiguration();
		modifier = new Modifier(null, new GroupEditor(new ShapeRenderer(),
				config), config);
		actor = new Actor();
		actor.setSize(WIDTH, HEIGHT);
		Group parent = new Group();
		parent.addActor(actor);
	}

	@Test
	public void testTranslation() {
		float[] values = new float[] { 100, 200, -200, 600.2f, -1, 2, 4,
				-15.5f, 90, 47, 1, 3, -5, -7, -9, -11, -13, 17, 47 };
		for (int i = 0; i < values.length - 6; i++) {
			actor.setOrigin(values[i], values[i + 1]);
			actor.setPosition(values[i + 2], values[i + 3]);
			actor.setScale(values[i + 4], values[i + 5]);
			modifier.setSelection(actor);
			modifier.getHandles().readActorTransformation();
			resetActor();
			modifier.getHandles().applyHandleTransformation();
			assertTrue("Failed: " + i,
					MathUtils.isEqual(actor.getX(), values[i + 2], 0.1f));
			assertTrue("Failed: " + i,
					MathUtils.isEqual(actor.getY(), values[i + 3], 0.1f));
			assertTrue(MathUtils
					.isEqual(actor.getScaleX(), values[i + 4], 0.1f));
			assertTrue(MathUtils
					.isEqual(actor.getScaleY(), values[i + 5], 0.1f));
		}
	}

	private void resetActor() {
		actor.setPosition(0, 0);
		actor.setScale(1, 1);
	}
}
