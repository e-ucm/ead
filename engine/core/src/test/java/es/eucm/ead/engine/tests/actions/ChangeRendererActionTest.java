/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.tests.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.actors.SceneActor;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.engine.renderers.AbstractRenderer;
import es.eucm.ead.schema.actions.ChangeRenderer;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.renderers.Renderer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 2/02/14 to test
 * {@link es.eucm.ead.schema.actions.ChangeRenderer}. This class uses file
 * /engine/core/test/resources/testgame/scenes/changerenderertest.json, please
 * do not modify or remove. The test loads a scene that has 5 scene elements,
 * each one with several behaviors for animation after mouse enter/exit, and
 * also a behavior for mouse clicks that triggers a change renderer action. The
 * test will iterate over these behaviors and launch the change renderer actions
 * automatically.
 * */

public class ChangeRendererActionTest {

	@Test
	public void testVariousRenderers() {
		MockGame game = new MockGame();
		// Load game
		game.act();
		game.getGameLoop().loadScene("changerenderertest");
		// Load scene
		game.act();

		// Get the scene
		SceneActor sceneActor = game.getGameLoop().getSceneView()
				.getCurrentScene();
		Scene scene = sceneActor.getSchema();

		// Iterate over scene elements in this scene. This code searches for
		// scenelements with Change Renderer actions in their behaviors.
		for (SceneElement element : scene.getChildren()) {
			for (Behavior behavior : element.getBehaviors()) {
				if (behavior.getAction() instanceof ChangeRenderer) {

					// A ChangeRenderer behavior found. Get the corresponding
					// actor so this action can be scheduled.
					Actor elementActor = sceneActor.getSceneElement(element);
					if (elementActor instanceof SceneElementActor) {
						SceneElementActor sceneElementActor = (SceneElementActor) elementActor;
						// Get the old renderer for comparison
						AbstractRenderer oldAbstractRenderer = sceneElementActor
								.getRenderer();
						Renderer oldRenderer = (Renderer) oldAbstractRenderer
								.getSchema();

						// Execute the action
						ChangeRenderer cr = (ChangeRenderer) (behavior
								.getAction());
						sceneElementActor.addAction(cr);
						game.act();

						// Get the new renderer for comparison
						AbstractRenderer newAbstractRenderer = sceneElementActor
								.getRenderer();
						Renderer newRenderer = (Renderer) newAbstractRenderer
								.getSchema();

						// Check what should have happened, depending on whether
						// this change renderer should have restored the initial
						// renderer or applied a new one
						if (!cr.isSetInitialRenderer()) {
							assertFalse(
									"Old and new renderers should be different instances, although they can represent the same renderer",
									oldRenderer == newRenderer);
							assertTrue(
									"New renderer should be the one specified by the action",
									equalsObjects(cr.getNewRenderer(),
											newRenderer));
						} else {
							assertTrue(
									"If setInitialRenderer is set to true, then renderers should be the same",
									equalsObjects(oldRenderer, newRenderer));
							assertTrue(
									"The new renderer must be the original one",
									equalsObjects(element.getRenderer(),
											newRenderer));
						}

					}

				}
			}
		}
	}

	/**
	 * This method provides a "reflection-based" implementation of the equals
	 * method for any two given objects. First, it will check if they are
	 * pointers to the same reference (o1==o2). If so, it will return true.
	 * Then, it checks if they are instances of the same class. If not, it
	 * returns false. Finally, it iterates through all methods starting with
	 * "is" or "get". It executes each of these methods in objects o1 and o2 and
	 * compares the results. If any of these tests fail, returns false. The
	 * comparison of "is" and "get" methods is recursive: if o1 and o2 extend
	 * from a superclass, this method will also iterate through the
	 * superclasses' methods.
	 * 
	 * @param o1
	 *            The first object to compare
	 * @param o2
	 *            The second object to compare
	 * @return True if they are the same type and contain the same information,
	 *         false otherwise.
	 * 
	 */
	private static boolean equalsObjects(Object o1, Object o2) {
		if (o1 == o2)
			return true;

		if ((o1 == null) != (o2 == null))
			return false;

		if (o1 == null && o2 == null)
			return true;

		if (!o1.getClass().getCanonicalName()
				.equals(o2.getClass().getCanonicalName()))
			return false;

		return checkObjects(o1, o1.getClass(), o2, o2.getClass());

	}

	private static boolean checkObjects(Object o1, Class c1, Object o2, Class c2) {
		if (c1.getDeclaredFields().length != c2.getDeclaredFields().length)
			return false;

		if (c1.getDeclaredMethods().length != c2.getDeclaredMethods().length)
			return false;

		Method[] methods = c1.getDeclaredMethods();

		for (int i = 0; i < c1.getDeclaredMethods().length; i++)
			try {
				Method m1 = c1.getDeclaredMethods()[i];
				Method m2 = c2.getDeclaredMethods()[i];
				if (!m1.equals(m2))
					return false;

				if (m1.getName().startsWith("get")
						|| m1.getName().startsWith("is")) {
					Object r1 = m1.invoke(o1);
					Object r2 = m2.invoke(o2);
					if ((r1 == null) != (r2 == null))
						return false;
					if (!r1.equals(r2))
						return false;
				}

			} catch (Exception e) {
				return false;
			}

		if (c1.getSuperclass() != null) {
			return checkObjects(o1, c1.getSuperclass(), o2, c2.getSuperclass());
		}

		return true;

	}
}
