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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

import java.util.HashMap;
import java.util.Map;

public class EditorIO implements LoadedCallback {

	private Controller controller;

	private ProjectAssets projectAssets;

	private Project project;

	private Game game;

	private Map<String, Scene> scenes;

	public EditorIO(Controller controller) {
		this.controller = controller;
		this.projectAssets = controller.getProjectAssets();
		scenes = new HashMap<String, Scene>();
	}

	public void load(String loadingPath, boolean internal) {
		project = null;
		game = null;
		scenes.clear();

		projectAssets.setLoadingPath(loadingPath, internal);
		projectAssets.loadGame(this);
		FileHandle scenesPath = projectAssets
				.resolve(ProjectAssets.SCENES_PATH);
		for (FileHandle sceneFile : scenesPath.list()) {
			projectAssets.loadScene(sceneFile.nameWithoutExtension(), this);
		}
		projectAssets.loadProject(this);
	}

	public void saveAll(Model model) {
		projectAssets.toJsonPath(model.getGame(), ProjectAssets.GAME_FILE);
		projectAssets
				.toJsonPath(model.getProject(), ProjectAssets.PROJECT_FILE);
		for (Map.Entry<String, Scene> e : model.getScenes().entrySet()) {
			projectAssets.toJsonPath(e.getValue(),
					projectAssets.convertSceneNameToPath(e.getKey()));
		}
	}

	@Override
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == Game.class) {
			game = assetManager.get(fileName);
		} else if (type == Scene.class) {
			String sceneName = projectAssets.resolve(fileName)
					.nameWithoutExtension();
			Scene scene = assetManager.get(fileName);
			scenes.put(sceneName, scene);
		} else if (type == Project.class) {
			project = assetManager.get(fileName);
			// Project is the last thing loaded, generate command
			controller.command(new ModelCommand(controller.getModel(), game,
					project, scenes));

		}
	}
}
