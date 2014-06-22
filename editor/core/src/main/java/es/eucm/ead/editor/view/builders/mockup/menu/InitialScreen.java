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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.SerializationException;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Controller.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Preferences.PreferenceListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.Exit;
import es.eucm.ead.editor.control.actions.editor.NewGame;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.gallery.ProjectGallery;
import es.eucm.ead.editor.view.widgets.mockup.ConfirmationDialog;
import es.eucm.ead.editor.view.widgets.mockup.Options;
import es.eucm.ead.editor.view.widgets.mockup.RecentProjects;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

public class InitialScreen implements ViewBuilder, PreferenceListener,
		BackListener {
	/**
	 * The default width of the new project.
	 */
	public static final int DEFAULT_GAME_WIDTH = 1280;
	/**
	 * The default height of the new project.
	 */
	public static final int DEFAULT_GAME_HEIGHT = 800;

	private static final String IC_NEWPROJECT = "ic_newproject",
			IC_GALLERY = "ic_galleryproject", IC_GO_BACK = "ic_exit";

	public static final FileHandle MOCKUP_PROJECT_FILE = Gdx.files
			.external("/eAdventureMockup/");
	private RecentProjects recents;
	private Controller controller;

	private Skin skin;
	private Dialog exitDialog;

	private Actor view;

	@Override
	public Actor getView(Object... args) {
		return view;
	}

	@Override
	public void initialize(final Controller controller) {
		this.controller = controller;
		this.controller.getPreferences().addPreferenceListener(
				Preferences.RECENT_GAMES, this);
		this.skin = this.controller.getApplicationAssets().getSkin();
		final I18N i18n = this.controller.getApplicationAssets().getI18N();
		this.exitDialog = new ConfirmationDialog(i18n.m("exit-title"),
				i18n.m("exit-text"), i18n.m("general.accept"),
				i18n.m("general.cancel"), this.skin) {
			@Override
			protected void result(Object object) {
				if ((Boolean) object) {
					InitialScreen.this.controller.action(Exit.class);
				}
			}
		};

		final Vector2 viewport = controller.getPlatform().getSize();

		final ModelEntity defaultGame = controller.getTemplates().createGame(
				"", "", DEFAULT_GAME_WIDTH, DEFAULT_GAME_HEIGHT);
		final Button newProjectButton = new MenuButton(viewport,
				i18n.m("general.mockup.new-project"), this.skin, IC_NEWPROJECT,
				Position.BOTTOM);
		newProjectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(NewGame.class,
						MOCKUP_PROJECT_FILE.file().getAbsolutePath()
								+ File.separator + i18n.m("project.untitled"),
						defaultGame);
				controller.action(ChangeView.class, ProjectScreen.class);
			}
		});
		final Button projectGallery = new MenuButton(viewport,
				i18n.m("general.mockup.project-gallery"), this.skin,
				IC_GALLERY, Position.BOTTOM, this.controller, ChangeView.class,
				ProjectGallery.class);

		final Options opt = new Options(viewport, controller, this.skin);
		final MenuButton exit = new BottomProjectMenuButton(viewport,
				i18n.m("file.exit"), skin, IC_GO_BACK, 0.06f, 0.12f,
				Position.RIGHT);
		exit.setAligmentText(Align.center);
		exit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				InitialScreen.this.exitDialog.show(InitialScreen.this.recents
						.getStage());
			}
		});

		this.recents = new RecentProjects(viewport);
		updateRecents(this.controller.getPreferences().getString(
				Preferences.RECENT_GAMES));

		final Table window = new Table();

		window.setFillParent(true);
		window.add(exit).top().left();
		window.row();
		window.defaults().expand();
		window.add(newProjectButton);
		window.add(projectGallery);
		window.row();
		window.add(this.recents).colspan(2).bottom();
		window.addActor(opt);

		view = window;
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
		this.recents.clearCards();
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
			final ApplicationAssets applicationAssets = this.controller
					.getApplicationAssets();
			final Vector2 viewport = this.controller.getPlatform().getSize();
			final String ending = "/" + GameStructure.GAME_FILE;
			final I18N i18n = applicationAssets.getI18N();
			final String mockupProjectsPath = MOCKUP_PROJECT_FILE.file()
					.getAbsolutePath();
			for (final String recentGame : recentGames) {
				if (!recentGame.startsWith(mockupProjectsPath)
						|| recentGame.isEmpty()) {
					continue;
				}
				final String loadingPath = recentGame + ending;
				final FileHandle projectFile = this.controller
						.getEditorGameAssets().absolute(loadingPath);
				if (!projectFile.exists()) {
					Gdx.app.log("Mockup InitialScreen", "Recent project "
							+ loadingPath
							+ " skipped 'cause the file does not exist");
					continue;
				}
				try {
					final ModelEntity gameMetadata = controller
							.getEditorGameAssets().fromJson(ModelEntity.class,
									projectFile);

					ProjectButton proj = new ProjectButton(viewport, i18n,
							gameMetadata, this.skin);
					this.recents.addSelectable(proj);

					proj.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							controller.action(OpenGame.class, recentGame);
							controller.action(ChangeView.class,
									ProjectScreen.class);
						}
					});

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
		this.exitDialog.show(this.recents.getStage());
	}
}
