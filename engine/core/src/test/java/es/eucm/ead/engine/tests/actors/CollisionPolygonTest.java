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
package es.eucm.ead.engine.tests.actors;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementEngineObject;
import es.eucm.ead.engine.actors.SceneEngineObject;
import es.eucm.ead.engine.mock.MockGame;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class CollisionPolygonTest {

	@Test
	public void testCollisionPolygon() {
		MockGame mockGame = new MockGame();
		GameLoop gameLoop = mockGame.getGameLoop();
		// Load game
		mockGame.act();
		gameLoop.loadScene("collision");
		// Load scene
		mockGame.act();

		// This test use testgame/scenes/collision.json, that contains a child
		// with a triangle as renderer and collision polygon at (0, 0)
		SceneEngineObject scene = gameLoop.getGameView().getCurrentScene();

		// Hit inside the triangle
		SceneElementEngineObject actor = (SceneElementEngineObject) scene.hit(
				45, 45, true);
		assertTrue(actor.getSchema().getTags().contains("collisioned"));

		// Hit inside the children bounds (width and height), but outside the
		// triangle
		assertNotSame(actor, scene.hit(10, 80, true));

		// Now repeat test with a duplicated child (x-offset by 100) with no
		// collision polygons
		float xOffset = 100;
		// Hit inside the triangle
		actor = (SceneElementEngineObject) scene.hit(xOffset + 45, 45, true);
		assertTrue(actor.getSchema().getTags().contains("collisioned2"));

		// Hit inside the children bounds (width and height), but outside the
		// triangle (actor has not collisionPolygons, so is expected to be hit)
		assertEquals(actor, scene.hit(xOffset + 10, 80, true));
	}
}
