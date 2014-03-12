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

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.assets.EditorAssets;
import es.eucm.ead.editor.control.Preferences;

/**
 * Deletes a project given the path (args[0]). The project file is deleted from
 * disk and the preferences updated. You can additionally add a second argument
 * (args[1]), a {@link DeleteProjectListener} that will be notified if the
 * project was deleted or not.
 */
public class DeleteProject extends EditorAction {

	private static final String DELETE_PROJECT = "Delete project";

	@Override
	public void perform(Object... args) {
		final EditorAssets editorAssets = controller.getEditorAssets();
		String projectPath = args[0].toString();
		final Object listener = args[1];
		final DeleteProjectListener deleteListener = (listener != null && listener instanceof DeleteProjectListener) ? ((DeleteProjectListener) listener)
				: null;

		if (!projectPath.endsWith(File.separator)) {
			projectPath += File.separator;
		}
		// Try to delete the project, if possible
		final FileHandle projectHandle = editorAssets.absolute(projectPath);
		if (!projectHandle.exists()) {
			Gdx.app.log(DELETE_PROJECT, "Project file doesn't exist: "
					+ projectPath);
			if (deleteListener != null) {
				deleteListener.projectDeleted(false);
			}
			return;
		}
		if (!projectHandle.isDirectory()) {
			Gdx.app.log(DELETE_PROJECT, "Project file is not a directory: "
					+ projectPath);
			if (deleteListener != null) {
				deleteListener.projectDeleted(false);
			}
			return;
		}
		projectHandle.deleteDirectory();
		// Update preferences
		final Preferences prefs = controller.getPreferences();
		final String currPrefs = prefs.getString(Preferences.RECENT_GAMES, "");
		if (currPrefs.isEmpty()) {
			Gdx.app.log(DELETE_PROJECT, "Empry preferences, no need to update.");
			if (deleteListener != null) {
				deleteListener.projectDeleted(false);
			}
			return;
		}
		prefs.putString(Preferences.RECENT_GAMES,
				currPrefs.replace(projectPath, ""));
		Gdx.app.log(DELETE_PROJECT, "Project deleted and preferences updated!");
		if (deleteListener != null) {
			deleteListener.projectDeleted(true);
		}
	}

	public interface DeleteProjectListener {

		/**
		 * Invoked when a project was deleted.
		 * 
		 * @param succeed
		 *            if true, the project was deleted.
		 */
		void projectDeleted(boolean succeed);
	}
}
