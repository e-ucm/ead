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
package es.eucm.ead.engine.tests.effects;

import es.eucm.ead.engine.EngineAssets;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.effects.EndGame;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.GoSubgame;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GoEndSubgameTest {

	private MockGame mockGame;

	private EngineAssets engineAssets;

	private GameLoop gameLoop;

	private GameView gameView;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		// Load first scene
		mockGame.act();
		gameLoop = mockGame.getGameLoop();
		engineAssets = gameLoop.getEngineAssets();
		gameView = gameLoop.getGameView();
	}

	@Test
	public void testGoEndSubgame() {
		String gamePath = engineAssets.getLoadingPath();

		// Go to scene 2
		GoScene goScene = new GoScene();
		goScene.setName("scene2");
		mockGame.addEffect(goScene);
		assertEquals(gameLoop.getCurrentScene(), "scene2");

		String subgame = "subgame1";
		GoSubgame goSubgame = new GoSubgame();
		goSubgame.setName(subgame);

		mockGame.addEffect(goSubgame);
		mockGame.act(10);
		String subgamePath = gamePath + "subgames/" + subgame + "/";

		assertEquals(engineAssets.getLoadingPath(), subgamePath);
		assertEquals(gameLoop.getCurrentScene(), "subgamescene");
		assertEquals(gameView.getCurrentScene().getSchema().getChildren()
				.size(), 2);

		// End subgame
		EndGame endGame = new EndGame();
		mockGame.addEffect(endGame);
		mockGame.getGameLoop().getEngineAssets().finishLoading();
		mockGame.act();
		assertEquals(engineAssets.getLoadingPath(), gamePath);
		assertEquals(gameLoop.getCurrentScene(), "scene2");
		assertEquals(gameView.getCurrentScene().getSchema().getChildren()
				.size(), 0);

		// Quit the game
		mockGame.addEffect(endGame);
		mockGame.act(10);

		assertTrue(mockGame.getApplication().isEnded());
	}

	@Test
	public void testPostEffects() {

		String subgame = "subgame1";
		GoSubgame goSubgame = new GoSubgame();
		goSubgame.setName(subgame);

		// Quit when the subgame ends
		EndGame endGame = new EndGame();
		goSubgame.getPostEffects().add(endGame);

		mockGame.addEffect(goSubgame);
		mockGame.act();
		mockGame.addEffect(endGame);
		mockGame.act();

		assertTrue(mockGame.getApplication().isEnded());
	}

}
