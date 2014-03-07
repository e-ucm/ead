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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.schema.game.GameMetadata;

import java.io.File;

public class ChangeProjectTitle extends EditorAction {

	private static final String PROJECT_TITLE_FIELD = "Project title";

	public ChangeProjectTitle() {
        // FIXME I think this class needs renaming (ProjectTitle)
	}

	@Override
	public void perform(Object... args) {
		final GameMetadata currProj = controller.getModel().getGameMetadata();
		final String oldTitle = currProj.getNotes().getTitle();
		final String newTitle = args[0].toString();
		if (newTitle.equals(oldTitle)) {
			Gdx.app.log(PROJECT_TITLE_FIELD, "Old title equals new title!");
			return;
		}

		final Command changeTitleCom = new FieldCommand(currProj.getNotes(),
				FieldNames.PROJECT_TITLE, newTitle, false);
		controller.command(changeTitleCom);
		controller.getEditorIO().save(controller.getModel().getGameMetadata());

		final ProjectAssets projectAssets = controller.getProjectAssets();
		final String oldProjPath = projectAssets.getLoadingPath();
		final FileHandle projectDir = projectAssets.absolute(oldProjPath);
		if (!projectDir.exists()) {
			Gdx.app.error(PROJECT_TITLE_FIELD, "Project path doesn't exist!");
			return;
		}
		if (!projectDir.isDirectory()) {
			Gdx.app.error(PROJECT_TITLE_FIELD,
					"Project path isn't a directory!");
			return;
		}

		final String projectDirName = projectDir.name();
		if (projectDirName.equals(newTitle)) {
			Gdx.app.error(PROJECT_TITLE_FIELD,
					"Project's folder has the same title!");
			return;
		}
		final FileHandle parentDir = projectDir.parent();
		for (FileHandle child : parentDir.list()) {
			if (child != projectDir && child.isDirectory()
					&& child.name().equals(newTitle)) {
				Gdx.app.error(PROJECT_TITLE_FIELD,
						"There is another project with the same title!");
				return;
			}
		}
		final String newPath = parentDir.file().getAbsolutePath()
				+ File.separator + newTitle + File.separator;
		projectDir.moveTo(projectAssets.absolute(newPath));
		projectAssets.setLoadingPath(newPath, false);
		// XXX Shouldn't there be a method like
		// Preferences#addRecent ?
		final Preferences prefs = controller.getPreferences();
		prefs.putString(Preferences.RECENT_GAMES,
				newPath + ";" + prefs.getString(Preferences.RECENT_GAMES, ""));
		Gdx.app.log(PROJECT_TITLE_FIELD,
				"Project renamed and preferences updated!");
	}

	interface TitleChangedListener {

		/**
		 * Invoked when the title was changed.
		 */
		void onTitleChanged();
	}
}
