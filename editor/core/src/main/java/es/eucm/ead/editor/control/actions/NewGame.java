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
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.platform.Platform.StringListener;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * New game creates an empty game. If a path is passed as parameter, the game is
 * created in a subfolder of the given path. If no path is passed along, the
 * action invokes the action {@link ChooseFolder} to ask the user for a folder
 */
public class NewGame extends EditorAction implements StringListener {

	public static final String NAME = "newGame";

	public NewGame() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			controller.action(ChooseFolder.NAME, this);
		} else {
			createGame(args[0].toString());
		}
	}

	@Override
	public void string(String result) {
		createGame(result);
	}

	private void createGame(String result) {
		ProjectAssets projectAssets = controller.getProjectAssets();
		I18N i18N = projectAssets.getI18N();
		FileHandle projectFolder = projectAssets.absolute(result);

		if (!projectFolder.exists()) {
			throw new EditorActionException(
					"Impossible to create empty project",
					new FileNotFoundException(projectFolder.path()));
		}

		projectFolder = projectFolder.child(i18N.m("project.untitled"));
		projectFolder.mkdirs();

		if (projectFolder.exists()) {
			Project project = new Project();
			Game game = new Game();
			game.setInitialScene("scene0");
			project.setEditScene("scene0");
			// 16:9
			game.setWidth(1024);
			game.setHeight(576);
			game.setTitle(i18N.m("project.untitled"));

			Model model = new Model();
			model.setProject(project);
			model.setGame(game);

			Map<String, Scene> scenes = new HashMap<String, Scene>();
			scenes.put("scene0", new Scene());
			model.setScenes(scenes);

			projectAssets
					.setLoadingPath(projectFolder.file().getAbsolutePath());

			controller.getEditorIO().saveAll(model);

			controller.action(OpenGame.NAME, projectAssets.getLoadingPath());
		} else {
			throw new EditorActionException(
					"Impossible to create empty project",
					new FileNotFoundException(projectFolder.path()));
		}
	}
}
