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
package es.eucm.ead.editor.ui.maintoolbar;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.control.actions.editor.SetPreference;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.engine.I18N;

/**
 * Context menu for recent games
 */
public class RecentsMenu extends ContextMenu implements PreferenceListener {

	private Controller controller;

	private String currentPreference;

	private I18N i18N;

	public RecentsMenu(Skin skin, Controller controller, I18N i18N) {
		super(skin);
		this.controller = controller;
		this.i18N = i18N;
		controller.getPreferences().addPreferenceListener(
				Preferences.RECENT_GAMES, this);
		preferenceChanged(Preferences.RECENT_GAMES, controller.getPreferences()
				.getString(Preferences.RECENT_GAMES));
	}

	private void updateRecents(String preference) {
		if (preference == null || preference.equals(currentPreference)) {
			return;
		}

		this.currentPreference = preference;
		clearChildren();
		String[] recentGames = null;

		if (preference != null && preference.contains(";")) {
			recentGames = preference.split(";");
		}

		if (recentGames == null
				|| (recentGames.length == 1 && recentGames[0].equals(controller
						.getLoadingPath()))) {
			item(i18N.m("file.recents.empty"));
		} else {
			for (String recentGame : recentGames) {
				if (!recentGame.equals(controller.getLoadingPath())) {
					item(recentGame).addListener(
							new ActionOnDownListener(controller,
									OpenGame.class, recentGame));
				}
			}
			separator();
			item(i18N.m("file.recents.clean")).addListener(
					new ActionOnDownListener(controller, SetPreference.class,
							Preferences.RECENT_GAMES, ""));
		}
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		updateRecents(newValue.toString());
	}
}
