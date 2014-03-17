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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Editor preferences, saved across all game projects
 */
public class Preferences {

	public static final String WINDOW_X = "windowX";
	public static final String WINDOW_Y = "windowY";
	public static final String WINDOW_WIDTH = "windowWidth";
	public static final String WINDOW_HEIGHT = "windowHeight";
	public static final String WINDOW_MAXIMIZED = "windowMaximized";
	public static final String RECENT_GAMES = "recentGames";
	public static final String FILE_CHOOSER_LAST_FOLDER = "lastFolder";
	public static final String EDITOR_LANGUAGE = "editorLanguage";

	private com.badlogic.gdx.Preferences innerPreferences;

	private Map<String, Array<PreferenceListener>> preferenceListeners;

	@SuppressWarnings("all")
	public Preferences(com.badlogic.gdx.Preferences innerPreferences) {
		this.innerPreferences = innerPreferences;
		preferenceListeners = new HashMap<String, Array<PreferenceListener>>();

	}

	/**
	 * Adds a preferences listener, that is notified any time the preference
	 * change
	 * 
	 * @param preferenceKey
	 *            the preference key
	 * @param listener
	 *            the listener
	 */
	public void addPreferenceListener(String preferenceKey,
			PreferenceListener listener) {
		Array<PreferenceListener> listeners = preferenceListeners
				.get(preferenceKey);
		if (listeners == null) {
			listeners = new Array<PreferenceListener>();
			preferenceListeners.put(preferenceKey, listeners);
		}
		listeners.add(listener);
	}

	private void notify(String key, Object value) {
		innerPreferences.flush();
		Array<PreferenceListener> listeners = preferenceListeners.get(key);
		if (listeners != null) {
			for (PreferenceListener listener : listeners) {
				listener.preferenceChanged(key, value);
			}
		}
	}

	/**
	 * Sets a boolean preference
	 * 
	 * @param key
	 *            the preference key
	 * @param val
	 *            the preference value
	 */
	public void putBoolean(String key, boolean val) {
		innerPreferences.putBoolean(key, val);
		notify(key, val);
	}

	/**
	 * Sets a boolean preference
	 * 
	 * @param key
	 *            the preference key
	 * @param val
	 *            the preference value
	 */
	public void putInteger(String key, int val) {
		innerPreferences.putInteger(key, val);
		notify(key, val);
	}

	/**
	 * Sets a float preference
	 * 
	 * @param key
	 *            the preference key
	 * @param val
	 *            the preference value
	 */
	public void putFloat(String key, float val) {
		innerPreferences.putFloat(key, val);
		notify(key, val);
	}

	/**
	 * Sets a string preference
	 * 
	 * @param key
	 *            the preference key
	 * @param val
	 *            the preference value
	 */
	public void putString(String key, String val) {
		innerPreferences.putString(key, val);
		notify(key, val);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @return Returns a boolean preference
	 */
	public boolean getBoolean(String key) {
		return innerPreferences.getBoolean(key);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @return Returns an integer preference
	 */
	public int getInteger(String key) {
		return innerPreferences.getInteger(key);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @return Returns a float preference
	 */
	public float getFloat(String key) {
		return innerPreferences.getFloat(key);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @return Returns a string preference
	 */
	public String getString(String key) {
		return innerPreferences.getString(key);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @param defValue
	 *            default value if the preference does not exist
	 * @return Returns a boolean preference
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return innerPreferences.getBoolean(key, defValue);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @param defValue
	 *            default value if the preference does not exist
	 * @return Returns an integer preference
	 */
	public int getInteger(String key, int defValue) {
		return innerPreferences.getInteger(key, defValue);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @param defValue
	 *            default value if the preference does not exist
	 * @return Returns a float preference
	 */
	public float getFloat(String key, float defValue) {
		return innerPreferences.getFloat(key, defValue);
	}

	/**
	 * 
	 * @param key
	 *            the preference key
	 * @param defValue
	 *            default value if the preference does not exist
	 * @return Returns a string preference
	 */
	public String getString(String key, String defValue) {
		return innerPreferences.getString(key, defValue);
	}

	/**
	 * Basic interface for preferences listeners
	 */
	public interface PreferenceListener {
		/**
		 * Called when the preference changes
		 * 
		 * @param preferenceName
		 *            the preference name
		 * @param newValue
		 *            the prefence new value
		 */
		void preferenceChanged(String preferenceName, Object newValue);
	}
}
