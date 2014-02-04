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
import com.badlogic.gdx.utils.ObjectMap.Entry;

import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

public class EditorIO implements LoadedCallback {

	private ProjectAssets projectAssets;

	private Model model;

	public EditorIO(Model model, ProjectAssets projectAssets) {
		this.model = model;
		this.projectAssets = projectAssets;
	}

	public void load(String loadingPath, boolean internal) {
		model.clear();
		projectAssets.setLoadingPath(loadingPath, internal);
		projectAssets.loadGame(this);
		FileHandle scenesPath = projectAssets
				.resolve(ProjectAssets.SCENES_PATH);
		for (FileHandle sceneFile : scenesPath.list()) {
			projectAssets.loadScene(sceneFile.nameWithoutExtension(), this);
		}
		projectAssets.loadProject(this);
	}

	public void saveAll() {
		projectAssets.toJsonPath(model.getGame(), ProjectAssets.GAME_FILE);
		projectAssets
				.toJsonPath(model.getProject(), ProjectAssets.PROJECT_FILE);
		for (Entry<String, Scene> e : model.getScenes().entries()) {
			projectAssets.toJsonPath(e.value,
					projectAssets.convertSceneNameToPath(e.key));
		}
	}

	@Override
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == Game.class) {
			model.setGame((Game) assetManager.get(fileName));
		} else if (type == Scene.class) {
			model.addScene(projectAssets.resolve(fileName)
					.nameWithoutExtension(), (Scene) assetManager.get(fileName));
		} else if (type == Project.class) {
			model.setProject((Project) assetManager.get(fileName));
		}
	}
}
