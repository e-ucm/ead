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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Preferences;

/**
 * Action to update the preference with the recent projects list. This action
 * should be called every time a game is open or created. Receives as parameter
 * 1 string, containing the path of game opened
 */
public class UpdateRecents extends EditorAction {

	public static final int MAX_RECENTS = 15;

	@Override
	public void perform(Object... args) {
		// FIXME control the args.... number and well formed
		String projectPath = controller.getEditorAssets().toCanonicalPath(
				(String) args[0]);
		Preferences preferences = controller.getPreferences();
		int maxRecents = MAX_RECENTS;
		String[] currentRecents = preferences.getString(
				Preferences.RECENT_GAMES).split(";");
		Array<String> recents = new Array<String>();
		recents.add(projectPath);
		for (String path : currentRecents) {
			if (controller.getApplicationAssets().absolute(path).exists()) {
				if (!recents.contains(path, false)) {
					recents.add(path);
					maxRecents--;
				}
			}
			if (recents.size >= MAX_RECENTS) {
				break;
			}
		}

		String recentsPreferences = null;
		for (String path : recents) {
			if (recentsPreferences == null) {
				recentsPreferences = path;
			} else {
				recentsPreferences += ";" + path;
			}
		}

		preferences.putString(Preferences.RECENT_GAMES, recentsPreferences);
	}
}
