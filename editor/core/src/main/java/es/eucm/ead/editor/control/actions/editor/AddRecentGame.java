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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.EditorAction;

/**
 * <p>
 * Updates the preference with the recent projects list.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> The path to add to recent games
 * list</dd>
 * </dl>
 */
public class AddRecentGame extends EditorAction {

	public static final int MAX_RECENT_GAMES = 15;

	@Override
	public boolean validate(Object... args) {
		return args.length == 1 && args[0] instanceof String;
	}

	@Override
	public void perform(Object... args) {
		String projectPath = controller.getEditorGameAssets().toCanonicalPath(
				(String) args[0]);

		Preferences preferences = controller.getPreferences();

		String[] currentRecentGames = preferences.getString(
				Preferences.RECENT_GAMES).split(";");

		Array<String> recentGames = new Array<String>();
		recentGames.add(projectPath);
		for (String path : currentRecentGames) {
			if (controller.getApplicationAssets().absolute(path).exists()) {
				if (!recentGames.contains(path, false)) {
					recentGames.add(path);
				}
			}
			if (recentGames.size >= MAX_RECENT_GAMES) {
				break;
			}
		}

		String preferenceValue = null;
		for (String path : recentGames) {
			if (preferenceValue == null) {
				preferenceValue = path;
			} else {
				preferenceValue += ";" + path;
			}
		}

		preferences.putString(Preferences.RECENT_GAMES, preferenceValue);
	}

}
