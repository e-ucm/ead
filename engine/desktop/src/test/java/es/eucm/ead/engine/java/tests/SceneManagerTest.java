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
package es.eucm.ead.engine.java.tests;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SceneManagerTest extends LwjglTest {

	@Before
	public void setUp() {
		super.setUp();
		Engine.assets.setGamePath("@schema");
	}

	@Test
	public void testLoadGame() {
		Engine.sceneManager.loadGame();
		Game game = Engine.sceneManager.getGame();
		assertEquals(game.getTitle(), "Test");
		testSceneLoaded();
	}

	@Test
	public void testLoadScene() {
		Engine.sceneManager.loadScene("initial");
		testSceneLoaded();
	}

	@Test
	public void testReloadScene() {
		Engine.sceneManager.loadScene("initial");
		testSceneLoaded();
		Scene currentScene = Engine.sceneManager.getCurrentScene();
		Engine.sceneManager.reloadCurrentScene();
		Engine.assets.finishLoading();
		Engine.sceneManager.act();
		Scene newScene = Engine.sceneManager.getCurrentScene();
		// if pointers are different, the scene has been reloaded in a new
		// object
		assertTrue(currentScene != newScene);
	}

	private void testSceneLoaded() {
		assertEquals(Engine.sceneManager.getCurrentScenePath(),
				"scenes/initial.json");
		assertTrue(Engine.sceneManager.isLoading());
		assertNull(Engine.sceneManager.getCurrentScene());
		Engine.assets.finishLoading();
		Engine.sceneManager.act();
		Scene currentScene = Engine.sceneManager.getCurrentScene();
		assertNotNull(currentScene);
		assertEquals(currentScene.getChildren().size(), 1);
	}
}
