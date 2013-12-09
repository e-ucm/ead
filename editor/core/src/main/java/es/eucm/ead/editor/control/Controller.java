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
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.schema.game.Game;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Properties;

import javax.swing.Action;

/**
 * Links together the main parts of the editor. Intended to be used as a singleton,
 * provides access to 
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

	final private Preferences editorConfig;
	private Properties messages;
	public static final String messageFileName = "messages";
	public static final String messageFileExtension = ".properties";

	final private EditorModel editorModel;
	final private ProjectController projectController;
	final private ViewController viewController;
	final private CommandManager commandManager;

	/**
	 * Action map. Contains all actions, generally bound to menu items or
	 * the like.
	 */
	private final LinkedHashMap<String, Action> actionMap = new LinkedHashMap<String, Action>();

	public Controller(String prefsName) {
		this.editorConfig = Gdx.app.getPreferences(prefsName);
		this.messages = new Properties();
		setLang(null);
		this.editorModel = new EditorModel(new Game());
		this.projectController = new ProjectController();
		this.commandManager = new CommandManager();
		this.viewController = new ViewController();
	}

	/**
	 * Changes the language used for string lookup.
	 * @param lang ISO-639 language code 
	 * ("es" for Spanish, "en_US" for US English, and so on). If null is passed,
	 * then the current "lang" preference will be used instead. If *that* is
	 * also null, then the default system locale will be used.
	 */
	public final void setLang(String lang) {
		if (lang == null) {
			lang = editorConfig.getString("lang", Locale.getDefault()
					.getLanguage());
		}

		// loads properties, using nested defaults
		messages = new Properties();
		try {
			// attempts to load global defaults
			FileHandle propsFile = Gdx.files.internal(messageFileName
					+ messageFileExtension);
			if (propsFile.exists()) {
				Gdx.app.log("I18N", "Loading messages: " + propsFile.name());
				messages.load(propsFile.reader());
			}
			// if a language such as en_EN, attempts to load the 'en' as next default
			if (lang.indexOf('_') > 0) {
				String prefix = lang.substring(0, lang.indexOf('_'));
				propsFile = Gdx.files.internal(messageFileName + '-' + prefix
						+ messageFileExtension);
				if (propsFile.exists()) {
					Gdx.app
							.log("I18N", "Loading messages: "
									+ propsFile.name());
					messages = new Properties(messages);
					messages.load(propsFile.reader());
				}
			}
			// attempts to load specified language over previous defaults
			propsFile = Gdx.files.internal(messageFileName + '-' + lang
					+ messageFileExtension);
			if (propsFile.exists()) {
				Gdx.app.log("I18N", "Loading messages: " + propsFile.name());
				messages = new Properties(messages);
				messages.load(propsFile.reader());
			}
		} catch (IOException ioe) {
			Gdx.app.error("I18N", "error loading messages");
		}

		Gdx.app.log("I18N", "Loaded all messages; lang is " + lang);
		editorConfig.putString("lang", lang);
		editorConfig.flush();
	}

	/**
	 * Simple internationalized (i18n) message lookup.
	 * @param key
	 * @param args which will substitute each of the '{}' patterns found in the
	 * text, in order.
	 * @return 
	 */
	public String i18n(String key, Object... args) {
		String result = messages.getProperty(key);
		if (result == null) {
			Gdx.app.log("I18N", "No message for key " + key + ", lang "
					+ editorConfig.getString("lang"));
			result = key;
		}
		int end = result.indexOf("{}");
		if (end == -1) {
			return result;
		} else if (args.length == 0) {
			return result;
		}
		StringBuilder replaced = new StringBuilder(result.length());
		int currentArg = 0;
		int start = 0;
		do {
			if (currentArg == args.length) {
				Gdx.app.log("I18N", "Substitution requested in key " + key
						+ " " + " but not enough args " + Arrays.toString(args)
						+ " provided for message '" + result + "'");
				if (args.length == 0) {
					return result;
				} else {
					currentArg %= args.length;
				}
			}
			replaced.append(result.substring(start, end)).append(
					args[currentArg]);
			currentArg++;
			start = end + "{}".length();
			end = result.indexOf("{}", start);
		} while (end != -1);
		replaced.append(result.substring(start));
		return replaced.toString();
	}

	/**
	 * Convenience method to return an internationalized message 
	 * that varies in plurality according to the first parameter. 
	 * For example "1 message found, delete it?" 
	 * vs "2 messages found, delete them?"
	 * @param cardinality either 1 (singular) or any other number (plural)
	 * @param keyOne for singular messages
	 * @param keyMany for plural messages
	 * @param args same as those used in the cardinality-independent version
	 * @return 
	 */
	public String i18n(int cardinality, String keyOne, String keyMany,
			Object... args) {
		return i18n(cardinality == 1 ? keyOne : keyMany, args);
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
