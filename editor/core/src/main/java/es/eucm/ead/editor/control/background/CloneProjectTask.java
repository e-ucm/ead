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
 * Creates a sibling duplicate of a file/folder, and returns an array with: the
 * path to the created file, the title of duplicate and the path to the
 * thumbnail
 */
public class CloneProjectTask extends BackgroundTask<Object[]> {

	private Controller controller;

	private String path;

	private Array<String> projectNames;

	/**
	 * @param path
	 *            an absolute path to the file/folder to duplicate
	 */
	public CloneProjectTask(Controller controller, String path,
			Array<String> projectNames) {
		this.controller = controller;
		this.path = path;
		this.projectNames = projectNames;
	}

	@Override
	public Object[] call() throws Exception {
		FileHandle fileHandle = Gdx.files.absolute(path);
		FileHandle duplicate = ProjectUtils.getNonExistentFile(
				fileHandle.parent(), fileHandle.nameWithoutExtension(),
				fileHandle.extension());
		fileHandle.copyTo(duplicate);

		ModelEntity game = controller.getEditorGameAssets().fromJson(
				ModelEntity.class, duplicate.child(ModelStructure.GAME_FILE));

		String newTitle = Q.buildCopyName(Q.getTitle(game), projectNames);
		Q.setTitle(game, newTitle);
		String initialScene = Q.getComponent(game, GameData.class)
				.getInitialScene();
		controller.getEditorGameAssets().toJson(game, null,
				duplicate.child(ModelStructure.GAME_FILE));
		return new Object[] { duplicate.path(), newTitle,
				duplicate.child(Q.getThumbnailPath(initialScene)).path() };
	}
}
