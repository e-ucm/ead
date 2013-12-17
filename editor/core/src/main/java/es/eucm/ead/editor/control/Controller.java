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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import es.eucm.ead.editor.Prefs;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.engine.I18N;

import javax.swing.Action;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Links together the main parts of the editor. Intended to be used as a
 * singleton, provides access to
 * <ul>
 * <li>persistent editor preferences</li>
 * <li>internationalized messages (i18n)</li>
 * <li>currently-edited game</li>
 * <li>project controller (in charge of creating and managing games)</li>
 * <li>view controller (in charge of creating and managing dialogs and windows)</li>
 * <li>command-manager (for undo/redo)</li>
 * <li>actions (reusable editor calls)</li>
 * </ul>
 */
public class Controller {

	private Preferences editorConfig;

	final private EditorModel editorModel;
	final private ProjectController projectController;
	final private ViewController viewController;
	final private CommandManager commandManager;

	/**
	 * Action map. Contains all actions, generally bound to menu items or the
	 * like.
	 */
	private final LinkedHashMap<String, Action> actionMap = new LinkedHashMap<String, Action>();

	public Controller(String prefsName) {
		loadPreferences(prefsName);
		loadLanguage();
		this.editorModel = new EditorModel();
		this.projectController = new ProjectController();
		this.commandManager = new CommandManager();
		this.viewController = new ViewController();
	}

	/**
	 * Loads the preferences with the given name
	 * 
	 * @param prefsName
	 *            preferences name
	 */
	private void loadPreferences(String prefsName) {
		this.editorConfig = Gdx.app.getPreferences(prefsName);
		if (editorConfig.get().isEmpty()) {
			Gdx.app.error("Controller", "No preferences loaded; fileName is "
					+ prefsName + ": please remove file (it is corrupt)");
		} else {
			Gdx.app.error("Controller", "Loaded " + editorConfig.get().size()
					+ " preferences");
			editorConfig.flush();
		}
		editorConfig.flush();
	}

	/** Load the configured language **/
	private void loadLanguage() {
		I18N.setLang(editorConfig.getString(Prefs.lang, Prefs.defaultLang));
		if (!editorConfig.contains(Prefs.lang)) {
			editorConfig.putString(Prefs.lang, Prefs.defaultLang);
		}
	}

	public Preferences getPrefs() {
		return editorConfig;
	}

	public EditorModel getModel() {
		return editorModel;
	}

	public ProjectController getProjectController() {
		return projectController;
	}

	public ViewController getViewController() {
		return viewController;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public Collection<Action> getActions() {
		return actionMap.values();
	}

	public Action getAction(String name) {
		return actionMap.get(name);
	}

	public void putAction(String name, Action action) {
		actionMap.put(name, action);
	}
}
