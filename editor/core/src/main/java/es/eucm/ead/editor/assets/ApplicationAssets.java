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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.assets.loaders.ExtendedSkin;

/**
 * This asset manager is meant to deal with the editor's own assets. That is,
 * for example, the preferences, the skin and the i18n files for the
 * application.
 * 
 * For managing the game's assets, use {@link EditorGameAssets} instead.
 */
public class ApplicationAssets extends Assets {

	/**
	 * Default name for the editor's default preferences. These preferences are
	 * loaded and added to the {@link es.eucm.ead.editor.control.Preferences}
	 * object in case they were not stored persistently
	 */
	private static final String DEFAULT_PREFERENCES_FILE = "preferences.json";

	/**
	 * Id to let libgdx identify the user-generated prefs to be loaded. In
	 * desktop, this usually matches the name of the preferences file which is
	 * stored in user_folder/.prefs
	 */
	public static final String PREFERENCES_NAME = "eadeditor";

	/**
	 * Location of the file with the
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} object used to
	 * identify the version of the editor
	 */
	public static final String RELEASE_FILE = "appdata/release.json";

	public static final String DEFAULT_SKIN = "skins/light/skin";

	/**
	 * This field serves a similar purpose to static field {@link #RELEASE_FILE}
	 * . Both fields are kept for two reasons: 1) [Futurible] the actual path of
	 * this file may change depending on the platform and therefore it may be
	 * needed to assign the path of the release file dynamically from a set of
	 * static options (e.g. MAC OSX, WINDOWS, LINUX)
	 * 
	 * 2) [Testing] This allows modifying the field by reflection in tests.
	 */
	private String releaseFile;

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public ApplicationAssets(Files files) {
		this(files, DEFAULT_SKIN);
	}

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 * @param skin
	 *            the Skin name you want to be loaded initially
	 */
	public ApplicationAssets(Files files, String skin) {
		super(files);
		releaseFile = RELEASE_FILE;
		loadSkin(skin);
	}

	/**
	 * The skin is not loaded via {@link com.badlogic.gdx.assets.AssetManager}
	 * because we need a more flexible way to clear disposable assets (e.g.
	 * thumbnail textures).
	 */
	@Override
	public void loadSkin(String pathWithoutExtension) {
		if (skin == null) {
			String skinJson = pathWithoutExtension + ".json";
			FileHandle skinFile = files.internal(skinJson);
			FileHandle atlasFile = skinFile.sibling(skinFile
					.nameWithoutExtension() + ".atlas");
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			skin = new ExtendedSkin(this, atlas);
			skin.load(skinFile);
		}
	}

	@Override
	public FileHandle resolve(String path) {
		FileHandle absolute = files.absolute(path);
		if (absolute.exists()) {
			return absolute;
		}
		return files.internal(path);
	}

	/**
	 * Loads and returns the Preferences object for the application. This method
	 * loads user-defined preferences by using libgdx's support. These prefs
	 * work across platforms. In desktop, these preferences are typically stored
	 * in a ".prefs" folder under the user's home dir (e.g.
	 * "C:/Users/Javier/.prefs/{@value #PREFERENCES_NAME}").
	 * 
	 * Before returning these preferences, a set of default properties stored in
	 * json format (in file {@link #DEFAULT_PREFERENCES_FILE}) are added, in
	 * case they are not present.
	 * 
	 * @return The {@link es.eucm.ead.editor.control.Preferences} object for the
	 *         controller.
	 */
	public Preferences loadPreferences() {
		// Load default preferences. The default preferences are stored in a
		// json file under the
		// path DEFAULT_PREFERENCES_FILE (e.g. "preferences.json"). This file
		// looks similar to this:
		/*
		 * { "windowMaximized": true, "windowWidth": 800, "windowHeight": 600 }
		 */
		// Where the part before : means the key for the preference and the part
		// after : is the value
		FileHandle preferencesFile = resolve(DEFAULT_PREFERENCES_FILE);
		ObjectMap<String, Object> defaultPreferences = new Json().fromJson(
				ObjectMap.class, preferencesFile);

		/*
		 * Load user preferences. For this, libGDX's support is used. LibGDX
		 * stores the preferences persistently ina file called PREFERENCES_NAME
		 * under a folder ".prefs" that is usually located on the user's main
		 * folder
		 */
		com.badlogic.gdx.Preferences libGDXPreferences = Gdx.app
				.getPreferences(PREFERENCES_NAME);

		// Combine default and user-defined preferences. All default preferences
		// not present in
		// user-defined prefs are added to the libgdx's object
		for (ObjectMap.Entry<String, Object> e : defaultPreferences.entries()) {
			if (!libGDXPreferences.contains(e.key)) {
				if (e.value.getClass() == Boolean.class) {
					libGDXPreferences.putBoolean(e.key, (Boolean) e.value);
				} else if (e.value.getClass() == Integer.class) {
					libGDXPreferences.putInteger(e.key, (Integer) e.value);
				} else if (e.value.getClass() == Float.class) {
					libGDXPreferences.putFloat(e.key, (Float) e.value);
				} else {
					libGDXPreferences.putString(e.key, e.value.toString());
				}
			}
		}

		// Return the preferences object, a wrapper for libGDX's preferences
		// object
		return new Preferences(libGDXPreferences);

	}

	/**
	 * This method retrieves the release file from disk. If anything unexpected
	 * happens and the file cannot be loaded, it just initializes
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} with a default
	 * one.
	 * 
	 * This method should be used only once, when
	 * {@link es.eucm.ead.editor.control.Controller} is initialized
	 * 
	 * @return The {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} object
	 *         indicating the version of this application.
	 */
	public ReleaseInfo loadReleaseInfo() {
		ReleaseInfo releaseInfo = null;
		FileHandle releaseFH = this.resolve(releaseFile);
		if (checkFileExistence(releaseFH)) {
			releaseInfo = this.fromJson(ReleaseInfo.class, releaseFH);
		} else {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"release.json file not found. Default release object will be used.");
		}

		// Check if default releaseInfo must be used
		if (releaseInfo == null) {
			releaseInfo = new DefaultReleaseInfo();
		}

		// Check field validity. If appVersion is not found, use default "0.0.0"
		if (releaseInfo.getAppVersion() == null) {
			releaseInfo.setAppVersion("0.0.0");
		}

		// Backend properties
		if (releaseInfo.getBackendURL() == null) {
			releaseInfo.setBackendURL("api.mokap.es");
		}

		if (releaseInfo.getBackendApiKey() == null) {
			releaseInfo.setBackendApiKey("API_KEY_NOT_PRESENT");
		}

		if (releaseInfo.getBackendSearchServlet() == null) {
			releaseInfo.setBackendSearchServlet("search");
		}

		return releaseInfo;
	}

	private static class DefaultReleaseInfo extends ReleaseInfo {
		public DefaultReleaseInfo() {
			setAppVersion("0.0.0");
			setReleaseType(ReleaseType.NIGHTLY);
			setDev(false);
		}
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
		skin.dispose();
	}
}
