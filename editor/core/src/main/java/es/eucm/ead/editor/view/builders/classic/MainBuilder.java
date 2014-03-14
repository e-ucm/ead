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
import es.eucm.ead.editor.control.actions.Copy;
import es.eucm.ead.editor.control.actions.Cut;
import es.eucm.ead.editor.control.actions.Exit;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.control.actions.Paste;
import es.eucm.ead.editor.control.actions.Redo;
import es.eucm.ead.editor.control.actions.Save;
import es.eucm.ead.editor.control.actions.ShowDialog;
import es.eucm.ead.editor.control.actions.Undo;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
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
import es.eucm.ead.schema.editor.actors.EditorScene;

import java.util.List;

public class MainBuilder implements ViewBuilder, PreferenceListener {

	public static final String NAME = "main";
	private ContextMenuBuilder contextMenuBuilder;
	private ContextMenuBuilder.Builder recentsBuilder;
	private Controller controller;
	private I18N i18n;

	/**
	 * Container for the scenes list. This container is added to the root table
	 * of this component. It is kept as a parameter because it is needed for
	 * adding the scenesList to this container just when scenes have already
	 * been loaded.
	 */
	private PlaceHolder mainView;

	/**
	 * This is the container of the scenesList. It is kept to be added to the
	 * mainView just in time
	 */
	private ColumnsLayout columnsLayout;

	/**
	 * The list of scene widgets. Kept as a field to deal with scene updates
	 * (addition, removal, reordering, etc.).
	 */
	private ScenesList scenesList;

	public MainBuilder(Controller controller) {
		contextMenuBuilder = new ContextMenuBuilder(controller);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller c) {
		this.controller = c;

		Skin skin = controller.getApplicationAssets().getSkin();
		i18n = controller.getApplicationAssets().getI18N();
		Window window = new Window();

		Table root = window.root(new Table(controller, skin));

		// Create main menu
		root.row(createMenu(skin)).left();

		EngineView engineView = new EngineView(controller);

		columnsLayout = new ColumnsLayout();
		scenesList = new ScenesList(controller, skin);
		scenesList.prefSize(150);

		columnsLayout.column(scenesList);
		columnsLayout.column(engineView).expand();
		engineView.toBack();

		mainView = new PlaceHolder();
		PatternWidget patternWidget = new PatternWidget(skin, "escheresque_ste");
		mainView.setContent(patternWidget);

		root.row(mainView).expandY().toBack();

		// Add the listener to the model that gets notified when it is loaded.
		// This initializes the scenes list
		controller.getModel().addLoadListener(new ScenesListInitializer());

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
			contextMenuBuilder.item(lang.name, ChangeLanguage.class, lang.code);
		}

		ContextMenu languages = contextMenuBuilder.done();
		return menuBuilder
				.addMenuItem(i18n.m("general.file"))
				.addContextItem(i18n.m("general.new"), ShowDialog.class,
						NewProjectDialog.NAME)
				.setIcon(skin.getDrawable("new"))
				.setShortcut("Ctrl+N")
				.addContextItem(i18n.m("general.open"), OpenGame.class)
				.setIcon(skin.getDrawable("open"))
				.setShortcut("Ctrl+O")
				.addContextItem(i18n.m("general.save"), Save.class)
				.setIcon(skin.getDrawable("save"))
				.setShortcut("Ctrl+S")
				.addContextItem(i18n.m("file.recents"), recents)
				.addSeparator()
				.addContextItem(i18n.m("file.exit"), Exit.class)
				.addMenuItem(i18n.m("general.edit"))
				.addContextItem(i18n.m("general.undo"), Undo.class)
				.setIcon(skin.getDrawable("undo"))
				.setShortcut("Ctrl+Z")
				.addContextItem(i18n.m("general.redo"), Redo.class)
				.setIcon(skin.getDrawable("redo"))
				.setShortcut("Ctrl+Y")
				.addContextItem(i18n.m("general.cut"), Cut.class)
				.setShortcut("Ctrl+X")
				.addContextItem(i18n.m("general.copy"), Copy.class)
				.setShortcut("Ctrl+C")
				.setIcon(skin.getDrawable("copy"))
				.addContextItem(i18n.m("general.paste"), Paste.class)
				.setIcon(skin.getDrawable("clipboard"))
				.setShortcut("Ctrl+V")
				.addMenuItem(i18n.m("menu.view"))
				.addContextItem("Mockup", CombinedAction.class,
						ChangeSkin.class, new Object[] { "mockup" },
						ChangeView.class, new Object[] { InitialScreen.NAME })
				.addMenuItem(i18n.m("menu.editor"))
				.addContextItem(i18n.m("menu.editor.language"), languages)
				.addMenuItem(i18n.m("general.help")).disable().getMenu();
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
					recentsBuilder.item(recentGame, OpenGame.class, recentGame);
				}
			}
			recentsBuilder.separator();
			recentsBuilder.item(i18n.m("file.recents.clean"),
					ChangePreference.class, Preferences.RECENT_GAMES, "");
		}
		recentsBuilder.done().invalidateHierarchy();
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (Preferences.RECENT_GAMES.equals(preferenceName)) {
			updateRecents();
		}
	}

	/**
	 * This class handles initialization of the scenes list. It is designed to
	 * react to {@link es.eucm.ead.editor.model.events.LoadEvent}s, as this is
	 * the way the {@link es.eucm.ead.editor.model.Model} notifies editor's
	 * components that the a new model (i.e. game) has been loaded. This sort of
	 * event is launched in all the situations the scene list has to be
	 * recreated: both when an existing game is reloaded and when a new one is
	 * created.
	 * 
	 * It implements the {@link es.eucm.ead.editor.model.Model.ModelListener
	 * <es.eucm.ead.editor.model.events.LoadEvent>} interface to be able to
	 * react to {@link es.eucm.ead.editor.model.events.LoadEvent}s
	 */
	private class ScenesListInitializer implements ModelListener<LoadEvent> {

		@Override
		public void modelChanged(LoadEvent event) {
			// Add the scenes' list container to the table root. With this
			// operation, the background pattern widget is removed
			mainView.setContent(columnsLayout);

			// Get all the new model's scenes added to the sceneslist view.
			initializeScenesList(event.getModel().getGame().getSceneorder());

			// When a new model is loaded, add a listener that is notified
			// when scenes are re-ordered, added and removed.
			// Reordering a list involves two events. REMOVE, and then ADD
			event.getModel().addListListener(
					event.getModel().getGame().getSceneorder(),
					new ScenesUpdater());
		}

		/**
		 * This method re-initializes the scenes lists: all existing scene
		 * widgets are cleared out, and new widgets are created and added for
		 * each of the new scenes in the game
		 * 
		 * @param sceneOrder
		 *            The new list of scenes in the game. this argument is just
		 *            a List containing the scene ids as Strings.
		 */
		private void initializeScenesList(List<String> sceneOrder) {
			scenesList.clearScenes();
			for (String sceneId : sceneOrder) {
				EditorScene sceneMetadata = controller.getModel().getScenes()
						.get(sceneId);
				String sceneName = sceneMetadata.getName();
				scenesList.addScene(sceneId, sceneName);
			}
		}
	}

	/**
	 * This class handles all updates related to scenes: - Scene addition -
	 * Scene removal - Scene reordering (involves a removal and an addition)
	 */
	private class ScenesUpdater implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			if (event.getList() == controller.getModel().getGame()
					.getSceneorder()) {
				// Scene removals
				if (event.getType() == ListEvent.Type.REMOVED) {
					scenesList.removeScene(event.getElement().toString());
				}
				// Scene additions
				else if (event.getType() == ListEvent.Type.ADDED) {
					String sceneId = event.getElement().toString();
					String sceneName = controller.getModel().getScenes()
							.get(sceneId).getName();
					scenesList.addScene(sceneId, sceneName, event.getIndex());
				}
			}
		}
	};
}
