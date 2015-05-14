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
package es.eucm.ead.editor.control.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

/**
 * Creates a sibling duplicate of a project folder, and returns the path to the
 * created folder
 */
public class CopyProjectTask extends BackgroundTask<String> {

	private Controller controller;

	private String path;

	/**
	 * @param path
	 *            an absolute path to the project folder to duplicate
	 */
	public CopyProjectTask(Controller controller, String path) {
		this.controller = controller;
		this.path = path;
	}

	@Override
	public String call() throws Exception {
		FileHandle fileHandle = Gdx.files.absolute(path);
		FileHandle duplicate = ProjectUtils.getNonExistentFile(

				Gdx.files.absolute(controller.getPlatform()
						.getDefaultProjectsFolder()), ProjectUtils
						.createProjectName(), fileHandle.extension());
		fileHandle.copyTo(duplicate);

		ModelEntity game = controller.getEditorGameAssets().fromJson(
				ModelEntity.class, duplicate.child(ModelStructure.GAME_FILE));

		String newTitle = Q.buildCopyName(Q.getTitle(game), null);
		Q.setTitle(game, newTitle);
		controller.getEditorGameAssets().toJson(game, null,
				duplicate.child(ModelStructure.GAME_FILE));
		return duplicate.path();
	}
}
