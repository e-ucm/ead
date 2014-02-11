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
package es.eucm.ead.editor.view.builders.mockup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;
import es.eucm.ead.editor.view.widgets.LinearLayout;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.editor.view.widgets.mockup.RecentProjects;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;
import es.eucm.ead.engine.I18N;

public class InitialScreen implements ViewBuilder, PreferenceListener {

	public static final String NAME = "mockup_initial";

	private final FileHandle MOCKUP_PROJECT_FILE = Gdx.files
			.external("/eAdventureMockup/");
	private RecentProjects recents;
	private Controller controller;

	private Skin skin;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		this.controller = controller;
		this.controller.getPreferences().addPreferenceListener(
				Preferences.RECENT_GAMES, this);
		this.skin = this.controller.getEditorAssets().getSkin();
		I18N i18n = this.controller.getEditorAssets().getI18N();
		LinearLayout horizontalButtons = new LinearLayout(true).center();
		final String IC_NEWPROJECT = "ic_newproject", IC_GALLERY = "ic_gallery";
		horizontalButtons.addActor(new MenuButton(i18n
				.m("general.mockup.new-project"), skin, IC_NEWPROJECT,
				this.controller, CombinedAction.NAME, NewGame.NAME,
				new Object[] { MOCKUP_PROJECT_FILE.path() }, ChangeView.NAME,
				new Object[] { ProjectScreen.NAME }));
		horizontalButtons.addActor(new MenuButton(i18n
				.m("general.mockup.project-gallery"), skin, IC_GALLERY,
				this.controller, CombinedAction.NAME, ChangeSkin.NAME,
				new Object[] { "default" }, ChangeView.NAME,
				new Object[] { MainBuilder.NAME }));

		recents = new RecentProjects();
		updateRecents();

		LinearLayout vertical = new LinearLayout(false);
		vertical.addActor(horizontalButtons);
		vertical.addActor(recents);

		Window window = new Window();
		window.root(vertical);
		return window;
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (Preferences.RECENT_GAMES.equals(preferenceName)) {
			updateRecents();
		}
	}

	private void updateRecents() {
		this.recents.clearRecents();
		String[] recentGames = null;
		String preference = this.controller.getPreferences().getString(
				Preferences.RECENT_GAMES);

		if (preference != null && preference.contains(";")) {
			recentGames = preference.split(";");
		}

		if (recentGames == null || "".equals(recentGames)) {
			return;
		} else {
			for (String recentGame : recentGames) {
				if (recentGame.isEmpty()) {
					continue;
				}
				// TODO create a new project with title and description from
				// path
				Project project = new Project();
				project.setTitle("Project's title");
				project.setDescription(recentGame);
				this.recents.addRecent(new ProjectButton(project, this.skin));
			}
		}
	}
}
