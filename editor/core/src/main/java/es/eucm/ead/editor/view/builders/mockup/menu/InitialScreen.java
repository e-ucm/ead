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
package es.eucm.ead.editor.view.builders.mockup.menu;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.assets.EditorAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Controller.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.gallery.ProjectGallery;
import es.eucm.ead.editor.view.widgets.mockup.Options;
import es.eucm.ead.editor.view.widgets.mockup.RecentProjects;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.game.Game;

public class InitialScreen implements ViewBuilder, PreferenceListener,
		BackListener {

	public static final String NAME = "mockup_initial";
	private static final String IC_NEWPROJECT = "ic_newproject",
			IC_GALLERY = "ic_gallery";

	public static final FileHandle MOCKUP_PROJECT_FILE = Gdx.files
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
		final Vector2 viewport = controller.getPlatform().getSize();

		EditorGame project = new EditorGame();
		project.setNotes(new Note());
		project.getNotes().setTitle("");
		project.getNotes().setDescription("");
		Button newProjectButton = new MenuButton(viewport,
				i18n.m("general.mockup.new-project"), skin, IC_NEWPROJECT,
				Position.BOTTOM, this.controller, CombinedAction.class,
				NewGame.class, new Object[] {
						MOCKUP_PROJECT_FILE.file().getAbsolutePath()
								+ File.separator + i18n.m("project.untitled"),
						project, new Game() }, ChangeView.class,
				new Object[] { ProjectScreen.NAME });
		Button projectGallery = new MenuButton(viewport,
				i18n.m("general.mockup.project-gallery"), skin, IC_GALLERY,
				Position.BOTTOM, this.controller, ChangeView.class,
				ProjectGallery.NAME);

		Options opt = new Options(viewport, controller, skin);

		this.recents = new RecentProjects(viewport);
		updateRecents(this.controller.getPreferences().getString(
				Preferences.RECENT_GAMES));

		Table window = new Table();
		window.defaults().expand();
		window.setFillParent(true);
		window.add(newProjectButton);
		window.add(projectGallery);
		window.row();
		window.add(this.recents).colspan(2).bottom();
		window.addActor(opt);

		return window;
	}

	@Override
	public void initialize(Controller controller) {
	}

	@Override
	public void release(Controller controller) {
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (Preferences.RECENT_GAMES.equals(preferenceName)) {
			if (newValue == null)
				return;
			updateRecents(newValue.toString());
		}
	}

	private void updateRecents(String newValue) {
		this.recents.clearRecents();
		String[] recentGames = null;
		final String preference = newValue;

		if (preference.contains(";")) {
			recentGames = preference.split(";");
		} else {
			recentGames = new String[1];
			recentGames[0] = preference;
		}

		if (recentGames == null || "".equals(recentGames)) {
			return;
		} else {
			final EditorAssets editorAssets = this.controller.getEditorAssets();
			final Vector2 viewport = this.controller.getPlatform().getSize();
			final String ending = "game.json";
			final I18N i18n = this.controller.getEditorAssets().getI18N();
			final String mockupProjectsPath = MOCKUP_PROJECT_FILE.file()
					.getAbsolutePath();
			for (String recentGame : recentGames) {
				if (!recentGame.startsWith(mockupProjectsPath)
						|| recentGame.isEmpty()) {
					continue;
				}
				final String loadingPath = recentGame + ending;
				FileHandle projectFile = this.controller.getProjectAssets()
						.absolute(loadingPath);
				if (!projectFile.exists()) {
					Gdx.app.log("Mockup InitialScreen", "Recent project "
							+ loadingPath
							+ " skipped 'cause the file does not exist");
					continue;
				}
				try {
					EditorGame gameMetadata = editorAssets.fromJson(
							EditorGame.class, projectFile);

					this.recents.addRecent(new ProjectButton(viewport, i18n,
							gameMetadata, this.skin, this.controller,
							CombinedAction.class, OpenGame.class,
							new Object[] { recentGame }, ChangeView.class,
							new Object[] { ProjectScreen.NAME }));

				}
				// A SerializationException may occur if the recent project
				// cannot be loaded. As this is not a crucial problem, just skip
				// it silently
				catch (SerializationException e) {
					Gdx.app.log(
							"Mockup InitialScreen",
							"Recent project "
									+ loadingPath
									+ " skipped 'cause the project could not be loaded",
							e);
					continue;
				}
			}
		}
	}

	@Override
	public void onBackPressed() {

	}
}
