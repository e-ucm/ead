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

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

/**
 * Creates an Empty New Project.
 * Action used in the Mockup.
 * Does not open the created project nor changes the actual view.
 */
public class NewMockupProject extends EditorAction {
	public static final String NAME = "newMockupProject";
	
	private final String EMPTY_PROJECT_ERROR = "Impossible to create empty project";
	private final FileHandle MOCKUP_PROJECTS_FILE = Gdx.files.absolute("/eAdventureMockup/");

	public NewMockupProject() {
		super(NAME);
		if(!MOCKUP_PROJECTS_FILE.exists()){
			MOCKUP_PROJECTS_FILE.mkdirs();
		}
	}

	@Override
	public void perform(Object... args) {
		System.out.println("path: " + MOCKUP_PROJECTS_FILE.path());
		createGame(MOCKUP_PROJECTS_FILE.path());
	}

	private void createGame(String result) {
		ProjectAssets projectAssets = controller.getProjectAssets();
		I18N i18N = projectAssets.getI18N();
		FileHandle projectFolder = projectAssets.absolute(result);

		if (!projectFolder.exists()) {
			throw new EditorActionException(EMPTY_PROJECT_ERROR,
					new FileNotFoundException(projectFolder.path()));
		}

		projectFolder = projectFolder.child(i18N.m("project.untitled"));
		projectFolder.mkdirs();

		if (projectFolder.exists()) {
			final String initialScene = "scene0";
			Project project = new Project();
			Game game = new Game();
			game.setInitialScene(initialScene);
			project.setEditScene(initialScene);
			// 16:9
			game.setWidth(1024);
			game.setHeight(576);
			game.setTitle(i18N.m("project.untitled"));

			Model model = new Model();
			model.setProject(project);
			model.setGame(game);

			Map<String, Scene> scenes = new HashMap<String, Scene>();
			scenes.put(initialScene, new Scene());
			model.setScenes(scenes);

			projectAssets
					.setLoadingPath(projectFolder.file().getAbsolutePath());

			controller.getEditorIO().saveAll(model);
			
		} else {
			throw new EditorActionException(EMPTY_PROJECT_ERROR,
					new FileNotFoundException(projectFolder.path()));
		}
	}
}
