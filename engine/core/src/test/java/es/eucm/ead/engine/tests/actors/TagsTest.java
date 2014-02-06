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
package es.eucm.ead.engine.tests.actors;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneEngineObject;
import es.eucm.ead.engine.mock.MockGame;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TagsTest {

	@Test
	public void testTags() {
		MockGame mockGame = new MockGame();
		GameLoop gameLoop = mockGame.getGameLoop();
		// Load game
		mockGame.act();
		gameLoop.loadScene("tags");
		// Load scene
		mockGame.act();

		SceneEngineObject sceneActor = gameLoop.getSceneView()
				.getCurrentScene();
		assertEquals(sceneActor.findByTag("ñor").size, 0);
		assertEquals(sceneActor.findByTag("tag1").size, 3);
		assertEquals(sceneActor.findByTag("tag2").size, 2);
		assertEquals(sceneActor.findByTag("tag3").size, 5);
	}
}
