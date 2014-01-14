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
package es.eucm.ead.engine.actions;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.application.TestGame;
import es.eucm.ead.schema.actions.EndGame;
import es.eucm.ead.schema.actions.GoScene;
import es.eucm.ead.schema.actions.GoSubgame;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GoEndSubgameActionTest {

	private static TestGame testGame;

	@BeforeClass
	public static void setUp() {
		testGame = new TestGame();
	}

	@Test
	public void testGoEndSubgame() {
		// Remove "@"
		String gamePath = testGame.getGamePath();
		assertEquals(Engine.assets.getLoadingPath(), gamePath);

		// Go to scene 2
		GoScene goScene = new GoScene();
		goScene.setName("scene2");
		testGame.addActionToDummyActor(goScene);
		assertEquals(Engine.sceneManager.getCurrentScenePath(),
				"scenes/scene2.json");

		String subgame = "subgame1";
		GoSubgame goSubgame = new GoSubgame();
		goSubgame.setName(subgame);

		testGame.addActionToDummyActor(goSubgame);
		testGame.act();
		String subgamePath = gamePath + "subgames/" + subgame + "/";

		assertEquals(Engine.assets.getLoadingPath(), subgamePath);
		assertEquals(Engine.sceneManager.getCurrentScenePath(),
				"scenes/subgamescene.json");
		assertEquals(
				Engine.sceneManager.getCurrentScene().getChildren().size(), 2);

		// End subgame
		EndGame endGame = new EndGame();
		testGame.addActionToDummyActor(endGame);
		testGame.act();
		assertEquals(Engine.assets.getLoadingPath(), gamePath);
		assertEquals(Engine.sceneManager.getCurrentScenePath(),
				"scenes/scene2.json");
		assertEquals(
				Engine.sceneManager.getCurrentScene().getChildren().size(), 0);

		// Quit the game
		testGame.addActionToDummyActor(endGame);
		testGame.act();

		assertTrue(testGame.getApplication().isEnded());
	}

	@Test
	public void testPostActions() {

		String subgame = "subgame1";
		GoSubgame goSubgame = new GoSubgame();
		goSubgame.setName(subgame);

		// Quit when the subgame ends
		EndGame endGame = new EndGame();
		goSubgame.getPostactions().add(endGame);

		testGame.addActionToDummyActor(goSubgame);
		testGame.act();

		assertTrue(testGame.getApplication().isEnded());
	}

}
