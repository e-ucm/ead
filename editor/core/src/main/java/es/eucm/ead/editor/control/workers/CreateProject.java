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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

import java.util.concurrent.atomic.AtomicBoolean;

public class CreateProject extends Worker {

	private EditorGameAssets assets;

	private AtomicBoolean ready = new AtomicBoolean(false);

	private int width;

	private int height;

	public CreateProject() {
		super(true);
	}

	@Override
	public void setController(Controller controller) {
		super.setController(controller);
		assets = controller.getEditorGameAssets();
	}

	@Override
	protected void prepare() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				width = Gdx.graphics.getWidth();
				height = Gdx.graphics.getHeight();
				ready.set(true);
			}
		});
	}

	@Override
	protected boolean step() {
		if (ready.get()) {
			FileHandle projectsFolder = assets.absolute(controller
					.getPlatform().getDefaultProjectsFolder());

			FileHandle projectFolder = projectsFolder.child(ProjectUtils
					.createProjectName());

			projectFolder.mkdirs();

			ModelEntity game = controller.getTemplates().createGame("", "",
					width, height);

			ModelEntity scene = controller.getTemplates().createScene("");

			FileHandle scenesFolder = projectFolder
					.child(GameStructure.SCENES_PATH);
			scenesFolder.mkdirs();
			FileHandle sceneFile = scenesFolder.child("scene0.json");
			Q.getComponent(game, GameData.class).setInitialScene(
					GameStructure.SCENES_PATH + "scene0.json");

			assets.toJson(game, null, null,
					projectFolder.child(GameStructure.GAME_FILE));
			assets.toJson(scene, null, null, sceneFile);

			result(projectFolder.path());
			return true;
		} else {
			return false;
		}
	}
}
