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
package es.eucm.ead.editor.editorui;

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OpenMockGame extends EditorAction {

	public OpenMockGame() {
		super(true, false, Game.class);
	}

	@Override
	public void perform(Object... args) {

		Game game = (Game) args[0];

		Model model = controller.getModel();
		model.reset();
		model.putResource(GameStructure.GAME_FILE, ResourceCategory.GAME,
				game.getGame());

		for (Entry<String, ModelEntity> scene : game.getScenes().entrySet()) {
			model.putResource(scene.getKey(), ResourceCategory.SCENE,
					scene.getValue());
		}

		if (game.getPath() == null) {
			File file = ((MockPlatform) controller.getPlatform())
					.createTempFile(true);
			game.setPath(file.getAbsolutePath(), false);
		}
		controller.getEditorGameAssets().setLoadingPath(game.getPath(),
				game.isInternal());

		controller.action(ForceSave.class);
		controller.action(OpenGame.class, game.getPath());
	}

	public static class Game {

		private String path;

		private boolean internal;

		private ModelEntity game;

		private Map<String, ModelEntity> scenes;

		public Game() {
			game = new ModelEntity();
			scenes = new HashMap<String, ModelEntity>();
		}

		public boolean isInternal() {
			return internal;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path, boolean internal) {
			this.path = path;
			this.internal = internal;
		}

		public void setGame(ModelEntity game) {
			this.game = game;
		}

		public ModelEntity getGame() {
			return game;
		}

		public Map<String, ModelEntity> getScenes() {
			return scenes;
		}

		public void addScene(String id, ModelEntity modelEntity) {
			scenes.put(id, modelEntity);
		}
	}

}
