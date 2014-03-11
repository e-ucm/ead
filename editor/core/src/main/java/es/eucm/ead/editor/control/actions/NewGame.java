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
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * New game creates an empty game. Expects exactly three parameters: arg[0]: a
 * valid path to a folder where the game should be created (String) arg[1]: a
 * not null {@link es.eucm.ead.schema.editor.game.EditorGame} object arg[2]: a
 * not null {@link es.eucm.ead.schema.game.Game} object
 */
public class NewGame extends EditorAction {

	@Override
	public void perform(Object... args) {
		String path = (String) args[0];
		EditorGame game = (EditorGame) args[1];

		ProjectAssets projectAssets = controller.getProjectAssets();
		FileHandle projectFolder = projectAssets.absolute(path);

		if (!projectFolder.exists()) {
			projectFolder.mkdirs();
		}

		if (projectFolder.exists()) {
			game.setInitialScene("scene0");
			game.setEditScene("scene0");
			game.getSceneorder().add("scene0");

			Model model = new Model();
			model.setGame(game);

			Map<String, EditorScene> scenes = new HashMap<String, EditorScene>();
			scenes.put("scene0", new EditorScene());
			model.setScenes(scenes);

			projectAssets
					.setLoadingPath(projectFolder.file().getAbsolutePath());

			controller.getEditorIO().saveAll(model);

			controller.action(OpenGame.class, projectAssets.getLoadingPath());
		} else {
			throw new EditorActionException("Impossible to create project",
					new FileNotFoundException(path));
		}
	}
}
