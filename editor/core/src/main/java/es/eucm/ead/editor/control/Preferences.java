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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import java.util.HashMap;
import java.util.Map;

public class Preferences {

	public static final String PREFERENCES_NAME = "eadeditor";

	public static final String WINDOW_WIDTH = "windowWidth";
	public static final String WINDOW_HEIGHT = "windowHeight";
	public static final String WINDOW_MAXIMIZED = "windowMaximized";
	public static final String RECENT_GAMES = "recentGames";
	public static final String FILE_CHOOSER_LAST_FOLDER = "lastFolder";
	public static final String EDITOR_LANGUAGE = "editorLanguage";

	private com.badlogic.gdx.Preferences preferences;

	private Map<String, Array<PreferenceListener>> preferenceListeners;

	@SuppressWarnings("all")
	public Preferences(FileHandle preferencesFiles) {
		preferenceListeners = new HashMap<String, Array<PreferenceListener>>();
		// Load defaults
		ObjectMap<String, Object> defaultPreferences = new Json().fromJson(
				ObjectMap.class, preferencesFiles);

		// Load user preferences
		preferences = Gdx.app.getPreferences(PREFERENCES_NAME);

		for (Entry<String, Object> e : defaultPreferences.entries()) {
			if (!preferences.contains(e.key)) {
				if (e.value.getClass() == Boolean.class) {
					preferences.putBoolean(e.key, (Boolean) e.value);
				} else if (e.value.getClass() == Integer.class) {
					preferences.putInteger(e.key, (Integer) e.value);
				} else if (e.value.getClass() == Float.class) {
					preferences.putFloat(e.key, (Float) e.value);
				} else {
					preferences.putString(e.key, e.value.toString());
				}
			}
		}
	}

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
		Array<PreferenceListener> listeners = preferenceListeners.get(key);
		if (listeners != null) {
			for (PreferenceListener listener : listeners) {
				listener.preferenceChanged(key, value);
			}
		}
		preferences.flush();
	}

	public void putBoolean(String key, boolean val) {
		preferences.putBoolean(key, val);
		notify(key, val);
	}

	public void putInteger(String key, int val) {
		preferences.putInteger(key, val);
		notify(key, val);
	}

	public void putFloat(String key, float val) {
		preferences.putFloat(key, val);
		notify(key, val);
	}

	public void putString(String key, String val) {
		preferences.putString(key, val);
		notify(key, val);
	}

	public boolean getBoolean(String key) {
		return preferences.getBoolean(key);
	}

	public int getInteger(String key) {
		return preferences.getInteger(key);
	}

	public float getFloat(String key) {
		return preferences.getFloat(key);
	}

	public String getString(String key) {
		return preferences.getString(key);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	public int getInteger(String key, int defValue) {
		return preferences.getInteger(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return preferences.getFloat(key, defValue);
	}

	public String getString(String key, String defValue) {
		return preferences.getString(key, defValue);
	}

	public boolean contains(String key) {
		return preferences.contains(key);
	}

	public interface PreferenceListener {
		void preferenceChanged(String preferenceName, Object newValue);
	}
}
