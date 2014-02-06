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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.engine.Assets;

public class EditorAssets extends Assets {

	public static final String SKINS_PATH = "skins/";

	public static final String SKIN_FILE = "/skin.json";

	public static final String SKIN_ATLAS = "/skin.atlas";

	/**
	 * Current UI for the editor
	 */
	private Skin skin;

	private LoadedCallback callback = new LoadedCallback() {
		@Override
		public void finishedLoading(AssetManager assetManager, String fileName,
				Class type) {
			if (type == Skin.class) {
				skin = assetManager.get(fileName);
			}
		}
	};

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public EditorAssets(Files files) {
		super(files);
		setSkin("default");
	}

	/**
	 * 
	 * @return returns the current skin for the UI
	 */
	public Skin getSkin() {
		return skin;
	}

	/**
	 * Loads the skin with the given name. It will be necessary to rebuild the
	 * UI to see changes reflected
	 * 
	 * @param skinName
	 *            the skin name
	 */
	public void setSkin(String skinName) {
		SkinParameter skinParameter = new SkinParameter(convertNameToPath(
				skinName + SKIN_ATLAS, SKINS_PATH, false, false));
		skinParameter.loadedCallback = callback;
		load(convertNameToPath(skinName + SKIN_FILE, SKINS_PATH, false, false),
				Skin.class, skinParameter);
	}

	@Override
	public FileHandle resolve(String path) {
		return files.internal(path);
	}
}
