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
package es.eucm.ead.editor.view.builders.classic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.actions.ChangeLanguage;
import es.eucm.ead.editor.control.actions.ChangePreference;
import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.control.actions.Redo;
import es.eucm.ead.editor.control.actions.Save;
import es.eucm.ead.editor.control.actions.Undo;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.InitialScreen;
import es.eucm.ead.editor.view.widgets.LinearLayout;
import es.eucm.ead.editor.view.widgets.Performance;
import es.eucm.ead.editor.view.widgets.ScenesList;
import es.eucm.ead.editor.view.widgets.Table;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.editor.view.widgets.engine.EngineView;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.Menu;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.I18N.Lang;

public class MainBuilder implements ViewBuilder, PreferenceListener {

	public static final String NAME = "main";
	private ContextMenu recents;
	private Controller controller;
	private I18N i18n;

	@Override
	public String getName() {
		return NAME;
	}

	public Actor build(Controller controller) {
		this.controller = controller;
		controller.getPreferences().addPreferenceListener(
				Preferences.RECENT_GAMES, this);

		Skin skin = controller.getEditorAssets().getSkin();
		i18n = controller.getEditorAssets().getI18N();
		Window window = new Window();

		Table root = window.root(new Table(controller, skin));

		recents = new ContextMenu(controller, skin);
		updateRecents();

		Menu menu = new Menu(controller, skin);
		menu.item(i18n.m("general.file"))
				.subitem(i18n.m("general.new"), NewGame.NAME)
				.subitem(i18n.m("general.open"), OpenGame.NAME)
				.subitem(i18n.m("general.save"), Save.NAME)
				.subitem(i18n.m("file.recents"), recents);

		menu.item(i18n.m("general.edit"))
				.subitem(i18n.m("general.undo"), Undo.NAME)
				.subitem(i18n.m("general.redo"), Redo.NAME);

		menu.item(i18n.m("menu.view")).subitem("Mockup", CombinedAction.NAME,
				ChangeSkin.NAME, new Object[] { "mockup" }, ChangeView.NAME,
				new Object[] { InitialScreen.NAME });

		ContextMenu languages = new ContextMenu(controller, skin);

		for (Lang lang : i18n.getAvailable()) {
			languages.item(lang.name, ChangeLanguage.NAME, lang.code);
		}

		menu.item(i18n.m("menu.editor")).subitem(
				i18n.m("menu.editor.language"), languages);

		menu.item(i18n.m("general.help"));

		root.row().left().add(menu);

		LinearLayout mainView = new LinearLayout(true);

		mainView.addActor(new ScenesList(controller));
		mainView.addActor(new EngineView(controller));

		root.row().expandY().add(mainView).toBack();

		root.row().right().add(new Performance(skin));

		return window;
	}

	private void updateRecents() {
		recents.clearChildren();
		String[] recentGames = null;
		String preference = controller.getPreferences().getString(
				Preferences.RECENT_GAMES);

		if (preference != null && preference.contains(";")) {
			recentGames = preference.split(";");
		}

		if (recentGames == null
				|| "".equals(recentGames)
				|| (recentGames.length == 1 && recentGames[0].equals(controller
						.getLoadingPath()))) {
			recents.item(i18n.m("file.recents.empty"));
		} else {
			for (String recentGame : recentGames) {
				if (!recentGame.equals(controller.getLoadingPath())) {
					recents.item(recentGame, OpenGame.NAME, recentGame);
				}
			}
			recents.item(i18n.m("file.recents.clean"), ChangePreference.NAME,
					Preferences.RECENT_GAMES, "");
		}
		recents.invalidateHierarchy();
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (Preferences.RECENT_GAMES.equals(preferenceName)) {
			updateRecents();
		}
	}
}
