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
package es.eucm.ead.editor.commands;

import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ModelCommandTest extends CommandTest {

	private GameMetadata gameMetadata;

	private Game game;

	private Map<String, Scene> scenes;

	private Map<String, SceneMetadata> scenesMetadata;

	@Before
	public void setUp() {
		gameMetadata = new GameMetadata();
		game = new Game();
		scenes = new HashMap<String, Scene>();
		scenes.put("initial", new Scene());
		scenesMetadata = new HashMap<String, SceneMetadata>();
		scenesMetadata.put("initial", new SceneMetadata());
	}

	@Test
	public void test() {
		ModelCommand command = new ModelCommand(model, game, gameMetadata,
				scenes, scenesMetadata);
		command.doCommand();
		assertEquals(gameMetadata, model.getGameMetadata());
		assertEquals(game, model.getGame());
	}
}
