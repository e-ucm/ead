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
package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.LoadEvent.Type;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneMetadata;
import es.eucm.ead.schema.game.Game;
import es.eucm.ead.schema.game.GameMetadata;

import java.util.Map;

public class ModelCommand extends Command {

	private Model model;

	private Game game;

	private GameMetadata gameMetadata;

	private Map<String, Scene> scenes;

	private Map<String, SceneMetadata> scenesMetadata;

	public ModelCommand(Model model, Game game, GameMetadata gameMetadata,
			Map<String, Scene> scenes, Map<String, SceneMetadata> scenesMetadata) {
		this.model = model;
		this.game = game;
		this.gameMetadata = gameMetadata;
		this.scenes = scenes;
		this.scenesMetadata = scenesMetadata;
	}

	@Override
	public ModelEvent doCommand() {
		model.clearListeners();
		model.setGame(game);
		model.setScenes(scenes);
		model.setScenesMetadata(scenesMetadata);
		model.setGameMetadata(gameMetadata);
		return new LoadEvent(Type.LOADED, model);
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public ModelEvent undoCommand() {
		return null;
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}
}
