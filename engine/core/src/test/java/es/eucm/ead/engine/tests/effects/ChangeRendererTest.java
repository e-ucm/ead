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
package es.eucm.ead.engine.tests.effects;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.effects.ChangeRenderer;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.renderers.Circle;
import es.eucm.ead.schema.renderers.Rectangle;
import es.eucm.ead.schema.renderers.Renderer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 2/02/14 to test
 * {@link es.eucm.ead.schema.effects.ChangeRenderer}.
 * */

public class ChangeRendererTest {
	private MockGame mockGame;

	private GameLoop gameLoop;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		gameLoop = mockGame.getGameLoop();
	}

	/**
	 * This test is double: first, creates a ChangeRendererEngineObject that
	 * changes renderer Rectangle -> Circle, and asserts if the change actually
	 * happened.
	 * 
	 * Then, restores the initial renderer with another changeRenderer effect.
	 * Checks the original one has been restored.
	 */
	@Test
	public void testChangeAndRestoreInitialRenderer() {
		// Create initialRenderer1
		Rectangle initialRenderer1 = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setBottom(0);
		bounds.setTop(20);
		bounds.setLeft(0);
		bounds.setRight(20);
		initialRenderer1.setBounds(bounds);
		initialRenderer1.setPaint("FFFFFF;000000");

		// Create the renderer to be set (circle)
		Circle rendererToBeSet1 = new Circle();
		rendererToBeSet1.setRadius(30);
		rendererToBeSet1.setPaint("FFFFFF;000000");

		test(initialRenderer1, rendererToBeSet1, true);
	}

	/**
	 * Tests this effect when the initialRenderer of the sceneELement is equals
	 * to the one to be set. It should just work normally.
	 */
	@Test
	public void testEqualsRenderers() {
		// Create initialRenderer1
		Rectangle initialRenderer1 = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setBottom(0);
		bounds.setTop(100);
		bounds.setLeft(0);
		bounds.setRight(100);
		initialRenderer1.setBounds(bounds);
		initialRenderer1.setPaint("FFFFFF;000000");

		// Create the renderer to be set (rectangle)
		Rectangle rendererToBeSet = new Rectangle();
		Bounds bounds2 = new Bounds();
		bounds2.setBottom(0);
		bounds2.setTop(100);
		bounds2.setLeft(0);
		bounds2.setRight(100);
		rendererToBeSet.setBounds(bounds2);
		rendererToBeSet.setPaint("FFFFFF;000000");
		test(initialRenderer1, rendererToBeSet, false);
	}

	/**
	 * Tests this effect when the initialRenderer of the sceneElement is null,
	 * or the new renderer to be set is null, or both.
	 */
	@Test
	public void testNulls() {
		// Renderer1
		Rectangle renderer1 = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setBottom(0);
		bounds.setTop(100);
		bounds.setLeft(0);
		bounds.setRight(100);
		renderer1.setBounds(bounds);
		renderer1.setPaint("FFFFFF;000000");

		// Renderer2
		Circle renderer2 = new Circle();
		renderer2.setRadius(50);
		renderer2.setPaint("FFFFFF;000000");

		test(null, renderer1, true);
		test(renderer2, null, true);
		test(null, null, true);
	}

	/**
	 * Creates an element with the given initialRenderer. Then, changes the
	 * renderer to rendererToBeSet. If restore is true, it also tries to restore
	 * to the original renderer.
	 * 
	 * @param initialRenderer
	 *            The initial renderer for the element. This one is not attached
	 *            to any effect, it is part of the scene element
	 * @param rendererToBeSet
	 *            The renderer to be set. This one is part of the first
	 *            changeRenderer effect
	 * @param restore
	 *            True if a second changeRenderer effect must be created and
	 *            executed to restore the initial renderer, false otherwise.
	 */
	private void test(Renderer initialRenderer, Renderer rendererToBeSet,
			boolean restore) {
		// Force loading first scene
		mockGame.act();

		// Creates a scene element.
		SceneElement sceneElement = new SceneElement();
		sceneElement.setRenderer(initialRenderer);

		// Create the effect that changes the renderer of sceneElement to the
		// given one
		ChangeRenderer changeRenderer = new ChangeRenderer();
		changeRenderer.setSetInitialRenderer(false);
		changeRenderer.setNewRenderer(rendererToBeSet);

		// Adds sceneElement to the game and retrieves the reference to
		// SceneElementActor
		gameLoop.getSceneView().getCurrentScene().addActor(sceneElement);
		mockGame.act();
		SceneElementEngineObject sceneElementActor = ((SceneElementEngineObject) (gameLoop
				.getSceneElement(sceneElement)));

		// Get the oldRenderer (before the effect is executed)
		Renderer oldRenderer = getCurrentRenderer(sceneElementActor);

		// Add the effect to the actor and force update
		sceneElementActor.addEffect(changeRenderer);
		mockGame.act();

		// Get the newRenderer (after the effect is executed)
		Renderer newRenderer = getCurrentRenderer(sceneElementActor);

		// Check the renderer was properly updated
		check(initialRenderer, oldRenderer, newRenderer, changeRenderer);

		if (restore) {
			// Create a new changeRenderer effect that restores the initial
			// renderer. newRenderer is null (not needed).
			ChangeRenderer restoreRenderer = new ChangeRenderer();
			restoreRenderer.setSetInitialRenderer(true);
			restoreRenderer.setNewRenderer(null);

			// Run the second effect
			sceneElementActor.addEffect(restoreRenderer);
			mockGame.act();

			// Get the newRenderer (after the effect is executed)
			Renderer restoredRenderer = getCurrentRenderer(sceneElementActor);

			// Check the renderer was properly restored (circle -> rectangle)
			check(initialRenderer, newRenderer, restoredRenderer,
					restoreRenderer);
		}
	}

	/**
	 * Checks the element's renderer has changed as it should. This is the
	 * method that makes the assertions in this test.
	 * 
	 * @param initialRenderer
	 *            The renderer the sceneElement was defined with
	 * @param oldRenderer
	 *            The sceneElement's oldRenderer (should be collected before
	 *            executing the ChangeRenderer cr effect to be tested)
	 * @param newRenderer
	 *            The sceneElement's newRenderer (should bhe collected after
	 *            executing the ChangeRenderer cr effect to be tested)
	 * @param cr
	 *            The ChangeRenderer effect to be tested.
	 */
	private void check(Renderer initialRenderer, Renderer oldRenderer,
			Renderer newRenderer, ChangeRenderer cr) {
		// Check what should have happened, depending on whether
		// this change renderer should have restored the initial
		// renderer or applied a new one
		if (!cr.isSetInitialRenderer()) {
			assertTrue(
					"Old and new renderers should be different instances or simultaneously null, although they can represent the same renderer",
					(oldRenderer == null && newRenderer == null)
							|| oldRenderer != newRenderer);
			assertTrue(
					"New renderer should be the one specified by the effect",
					EqualsBuilder.reflectionEquals(cr.getNewRenderer(),
							newRenderer));
		} else {
			assertTrue("The new renderer must be exactly the original one",
					initialRenderer == newRenderer);

			assertTrue("The new renderer must be equals to the original one",
					EqualsBuilder
							.reflectionEquals(initialRenderer, newRenderer));
		}

	}

	/**
	 * Gets the current renderer for the given sceneElementActor
	 * 
	 * @param sceneElementActor
	 *            The sceneElementActor we want to find out its renderer
	 *            (schema)
	 * @return The underlying Renderer object this sceneElementActor is using
	 */
	private Renderer getCurrentRenderer(
			SceneElementEngineObject sceneElementActor) {
		return (sceneElementActor.getRenderer() != null) ? (Renderer) sceneElementActor
				.getRenderer().getSchema() : null;
	}

}
