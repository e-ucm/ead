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
package es.eucm.ead.editor.control.workers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Load all the projects and their associated thumbnails. Thumbnails path are
 * absolute and can be null
 */
public class LoadProjects extends Worker {

	private Assets assets;

	private Array<String> projectPaths;

	public LoadProjects() {
		super(true);
	}

	@Override
	public void setController(Controller controller) {
		super.setController(controller);
		assets = controller.getEditorGameAssets();
	}

	@Override
	protected void prepare() {
		FileHandle projectsFolder = assets.absolute(controller.getPlatform()
				.getDefaultProjectsFolder());
		if (projectsFolder.exists()) {
			projectPaths = ProjectUtils.findProjects(projectsFolder);
		}
	}

	@Override
	protected boolean step() {
		if (projectPaths == null || projectPaths.size == 0) {
			return true;
		}
		String projectPath = projectPaths.removeIndex(0);
		ModelEntity game = findGame(projectPath);
		if (game != null) {
			result(projectPath, findTitle(game),
					findThumbnail(game, projectPath));

		}
		return projectPaths.size == 0;
	}

	private String findTitle(ModelEntity game) {
		return Q.getComponent(game, Documentation.class).getName();
	}

	private String findThumbnail(ModelEntity game, String path) {
		String scenePath = Q.getComponent(game, GameData.class)
				.getInitialScene();
		if (scenePath != null) {
			return assets.absolute(path).child(Q.getThumbnailPath(scenePath))
					.path();
		} else {
			return null;
		}
	}

	private ModelEntity findGame(String path) {
		FileHandle game = assets.absolute(path).child(GameStructure.GAME_FILE);
		if (game.exists()) {
			return assets.fromJson(ModelEntity.class, game);
		} else {
			return null;
		}
	}

	private ModelEntity findScene(String projectPath, String scenePath) {
		FileHandle scene = assets.absolute(projectPath).child(scenePath);
		if (scene.exists()) {
			return assets.fromJson(ModelEntity.class, scene);
		} else {
			return null;
		}
	}
}
