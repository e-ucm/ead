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
import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.control.actions.ChangeLanguage;
import es.eucm.ead.editor.control.actions.ChangePreference;
import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.Exit;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.control.actions.Redo;
import es.eucm.ead.editor.control.actions.Save;
import es.eucm.ead.editor.control.actions.ShowDialog;
import es.eucm.ead.editor.control.actions.Undo;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.view.builders.ContextMenuBuilder;
import es.eucm.ead.editor.view.builders.MenuBuilder;
import es.eucm.ead.editor.view.builders.MenuBuilder.Builder;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.NewProjectDialog;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.widgets.PatternWidget;
import es.eucm.ead.editor.view.widgets.Performance;
import es.eucm.ead.editor.view.widgets.PlaceHolder;
import es.eucm.ead.editor.view.widgets.Table;
import es.eucm.ead.editor.view.widgets.Window;
import es.eucm.ead.editor.view.widgets.engine.EngineView;
import es.eucm.ead.editor.view.widgets.layouts.ColumnsLayout;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.Menu;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.I18N.Lang;
import es.eucm.ead.schema.actors.Scene;

import java.util.Map;

public class MainBuilder implements ViewBuilder, PreferenceListener {

	public static final String NAME = "main";
	private ContextMenuBuilder contextMenuBuilder;
	private ContextMenuBuilder.Builder recentsBuilder;
	private Controller controller;
	private I18N i18n;

	public MainBuilder(Controller controller) {
		contextMenuBuilder = new ContextMenuBuilder(controller);
	}

	@Override
	public String getName() {
		return NAME;
	}

	public Actor build(Controller c) {
		this.controller = c;

		Skin skin = controller.getEditorAssets().getSkin();
		i18n = controller.getEditorAssets().getI18N();
		Window window = new Window();

		Table root = window.root(new Table(controller, skin));

		// Create main menu
		root.row(createMenu(skin)).left();

		EngineView engineView = new EngineView(controller);

		final ColumnsLayout columnsLayout = new ColumnsLayout();
		final ScenesList scenesList = new ScenesList(controller, skin);
		scenesList.prefSize(150);

		ContextMenuBuilder.Builder sceneContextMenu = new ContextMenuBuilder(
				controller).build();

		sceneContextMenu.item(i18n.m("scene.add"), AddScene.NAME);

		controller.getViews().registerContextMenu(scenesList.getBackground(),
				sceneContextMenu.done());

		columnsLayout.column(scenesList);
		columnsLayout.column(engineView).expand();
		engineView.toBack();

		final PlaceHolder mainView = new PlaceHolder();
		PatternWidget patternWidget = new PatternWidget(skin, "escheresque_ste");
		mainView.setContent(patternWidget);

		root.row(mainView).expandY().toBack();

		controller.getModel().addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				mainView.setContent(columnsLayout);
				scenesList.clearScenes();
				for (String scene : event.getModel().getScenes().keySet()) {
					scenesList.scene(scene);
				}
				Map<String, Scene> map = controller.getModel().getScenes();
				controller.getModel().addMapListener(map,
						new ModelListener<MapEvent>() {
							@Override
							public void modelChanged(MapEvent event) {
								switch (event.getType()) {
								case ENTRY_ADDED:
									scenesList.scene(event.getKey().toString());
									break;
								}
							}
						});
			}
		});

		// Create footer
		root.row().right().add(new Performance(skin));

		return window;
	}

	private Menu createMenu(Skin skin) {
		controller.getPreferences().addPreferenceListener(
				Preferences.RECENT_GAMES, this);

		recentsBuilder = contextMenuBuilder.build();
		ContextMenu recents = recentsBuilder.done();
		updateRecents();
		Builder menuBuilder = new MenuBuilder(controller).build();

		// Dynamically create languages menu
		ContextMenuBuilder.Builder contextMenuBuilder = new ContextMenuBuilder(
				controller).build();

		for (Lang lang : i18n.getAvailable()) {
			contextMenuBuilder.item(lang.name, ChangeLanguage.NAME, lang.code);
		}

		ContextMenu languages = contextMenuBuilder.done();
		return menuBuilder
				.menu(i18n.m("general.file"))
				.context(i18n.m("general.new"), ShowDialog.NAME,
						NewProjectDialog.NAME)
				.icon(skin.getDrawable("new"))
				.shortcut("Ctrl+N")
				.context(i18n.m("general.open"), OpenGame.NAME)
				.icon(skin.getDrawable("open"))
				.shortcut("Ctrl+O")
				.context(i18n.m("general.save"), Save.NAME)
				.icon(skin.getDrawable("save"))
				.shortcut("Ctrl+S")
				.context(i18n.m("file.recents"), recents)
				.separator()
				.context(i18n.m("file.exit"), Exit.NAME)
				.menu(i18n.m("general.edit"))
				.context(i18n.m("general.undo"), Undo.NAME)
				.icon(skin.getDrawable("undo"))
				.shortcut("Ctrl+Z")
				.context(i18n.m("general.redo"), Redo.NAME)
				.icon(skin.getDrawable("redo"))
				.shortcut("Ctrl+Y")
				.menu(i18n.m("menu.view"))
				.context("Mockup", CombinedAction.NAME, ChangeSkin.NAME,
						new Object[] { "mockup" }, ChangeView.NAME,
						new Object[] { InitialScreen.NAME })
				.menu(i18n.m("menu.editor"))
				.context(i18n.m("menu.editor.language"), languages)
				.menu(i18n.m("general.help")).disable().done();
	}

	@Override
	public void initialize(Controller controller) {
	}

	@Override
	public void release(Controller controller) {
	}

	private void updateRecents() {
		recentsBuilder.clearChildren();
		String[] recentGames = null;
		String preference = controller.getPreferences().getString(
				Preferences.RECENT_GAMES);

		if (preference != null && preference.contains(";")) {
			recentGames = preference.split(";");
		}

		if (recentGames == null
				|| (recentGames.length == 1 && recentGames[0].equals(controller
						.getLoadingPath()))) {
			recentsBuilder.item(i18n.m("file.recents.empty"));
		} else {
			for (String recentGame : recentGames) {
				if (!recentGame.equals(controller.getLoadingPath())) {
					recentsBuilder.item(recentGame, OpenGame.NAME, recentGame);
				}
			}
			recentsBuilder.separator();
			recentsBuilder.item(i18n.m("file.recents.clean"),
					ChangePreference.NAME, Preferences.RECENT_GAMES, "");
		}
		recentsBuilder.done().invalidateHierarchy();
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (Preferences.RECENT_GAMES.equals(preferenceName)) {
			updateRecents();
		}
	}
}
