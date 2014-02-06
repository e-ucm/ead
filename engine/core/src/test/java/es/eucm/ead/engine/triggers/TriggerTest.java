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
package es.eucm.ead.engine.triggers;

import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.engine.mock.engineobjects.EmptyMock;
import es.eucm.ead.engine.mock.engineobjects.SceneElementMock;
import es.eucm.ead.engine.mock.schema.Empty;
import es.eucm.ead.schema.actors.SceneElement;
import org.junit.Before;

public class TriggerTest {

	protected MockGame mockGame;

	protected SceneElement sceneElement;

	protected GameLoop gameLoop;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		gameLoop = mockGame.getGameLoop();
		Assets assets = gameLoop.getAssets();
		assets.bind("mockempty", Empty.class, EmptyMock.class);
		assets.bind("sceneelement", SceneElement.class, SceneElementMock.class);
		// Load first scene
		sceneElement = assets.fromJsonPath(SceneElement.class,
				"square100x100.json");
		mockGame.act();
		gameLoop.getAssets().finishLoading();
	}

}
