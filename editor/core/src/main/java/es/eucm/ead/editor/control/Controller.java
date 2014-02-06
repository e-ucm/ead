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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorAssets;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.platform.Platform;

public class Controller {

	private Model model;

	private Platform platform;

	private EditorAssets editorAssets;

	private ProjectAssets projectAssets;

	private Views views;

	private Actions actions;

	private Preferences preferences;

	private Commands commands;

	private EditorIO editorIO;

	public Controller(Platform platform, Files files, Group rootView) {
		this.platform = platform;
		this.editorAssets = new EditorAssets(files);
		editorAssets.finishLoading();
		this.projectAssets = new ProjectAssets(files, editorAssets);
		this.model = new Model();
		this.views = new Views(this, rootView);
		this.editorIO = new EditorIO(this);
		this.actions = new Actions(this);
		this.preferences = new Preferences(
				editorAssets.resolve("preferences.json"));
		this.commands = new Commands(this);
		loadPreferences();
	}

	/**
	 * Process preferences concerning the controller
	 */
	private void loadPreferences() {
		getEditorAssets().getI18N().setLang(
				preferences.getString(Preferences.EDITOR_LANGUAGE));
	}

	public Model getModel() {
		return model;
	}

	public ProjectAssets getProjectAssets() {
		return projectAssets;
	}

	public EditorAssets getEditorAssets() {
		return editorAssets;
	}

	public Platform getPlatform() {
		return platform;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public Commands getCommands() {
		return commands;
	}

	public void view(String viewName) {
		views.setView(viewName);
	}

	public void action(String actionName, Object... args) {
		try {
			Gdx.app.debug("Controller", "Executing action " + actionName
					+ " with " + args);
			actions.perform(actionName, args);
		} catch (ClassCastException e) {
			throw new EditorActionException("Invalid arguments for "
					+ actionName + " width arguments " + args, e);
		} catch (NullPointerException e) {
			throw new EditorActionException("Invalid arguments for "
					+ actionName + " width arguments " + args, e);
		}
	}

	public String getLoadingPath() {
		return projectAssets.getLoadingPath();
	}

	public void loadGame(String gamePath, boolean internal) {
		editorIO.load(gamePath, internal);
		updateRecentGamesPreference(getLoadingPath());
	}

	public void saveAll() {
		editorIO.saveAll(model);
	}

	public EditorIO getEditorIO() {
		return editorIO;
	}

	private void updateRecentGamesPreference(String gamePath) {
		// XXX should this method be in the controller?
		int maxRecents = 15;
		String[] currentRecents = preferences.getString(
				Preferences.RECENT_GAMES).split(";");
		Array<String> recents = new Array<String>();
		recents.add(gamePath);
		for (String path : currentRecents) {
			if (!recents.contains(path, false)) {
				recents.add(path);
			}
			maxRecents--;
			if (maxRecents <= 0) {
				break;
			}
		}

		String recentsPreferences = "";
		for (String path : recents) {
			recentsPreferences += path + ";";
		}

		preferences.putString(Preferences.RECENT_GAMES, recentsPreferences);
	}

	public void setLanguage(String language) {
		getEditorAssets().getI18N().setLang(language);
		views.clearCache();
		views.reloadCurrentView();
		preferences.putString(Preferences.EDITOR_LANGUAGE, language);
	}

	public void notify(ModelEvent event) {
		model.notify(event);
	}

	public void command(Command command) {
		commands.command(command);
	}

}
