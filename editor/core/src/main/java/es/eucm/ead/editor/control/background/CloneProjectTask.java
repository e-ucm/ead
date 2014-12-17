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
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a sibling duplicate of a file/folder, and returns an array with: the
 * path to the created file, the title of duplicate and the path to the
 * thumbnail
 */
public class CloneProjectTask extends BackgroundTask<Object[]> {

	private static Pattern pattern;

	private Controller controller;

	private String path;

	/**
	 * @param path
	 *            an absolute path to the file/folder to duplicate
	 */
	public CloneProjectTask(Controller controller, String path) {
		this.controller = controller;
		this.path = path;
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

		String copySuffix = controller.getApplicationAssets().getI18N()
				.m("copy_suffix");

		if (pattern == null) {
			String regex = ".*\\(" + copySuffix + "\\s?(\\d*)\\)$";
			pattern = Pattern.compile(regex);
		}

		String newTitle = Q.getTitle(game);

		Matcher matcher = pattern.matcher(newTitle);
		if (matcher.find()) {
			int start = matcher.start(1);
			String number = matcher.group(1);
			String count = number.isEmpty() ? " " + 1 : ""
					+ (Integer.parseInt(number) + 1);
			newTitle = newTitle.substring(0, start) + count + ")";
		} else {
			newTitle += " (" + copySuffix + ")";
		}

		Q.setTitle(game, newTitle);

		String initialScene = Q.getComponent(game, GameData.class)
				.getInitialScene();
		controller.getEditorGameAssets().toJson(game, null,
				duplicate.child(ModelStructure.GAME_FILE));
		return new Object[] { duplicate.path(), newTitle,
				duplicate.child(Q.getThumbnailPath(initialScene)).path() };
	}
}
