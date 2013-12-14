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
package es.eucm.ead.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets extends AssetManager {

	private static FileResolver fileResolver;

	private Skin skin;

	private BitmapFont defaultFont = new BitmapFont();

	public Assets() {
		// FIXME AssetManager and Assets needs to share the same file resolver,
		// but AssetManager only receives fileResolver in the constructor, and
		// thus the static
		super(fileResolver = new FileResolver());
		loadSkin("default");
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
	public void loadSkin(String skinName) {
		String skinFile = "skins/" + skinName + "/skin.json";
		load(skinFile, Skin.class);
		finishLoading();
		this.skin = get(skinFile);
	}

	/**
	 * @return Returns a default font to draw text
	 */
	public BitmapFont defaultFont() {
		return defaultFont;
	}

	/**
	 * Resolves the file handle with the given path
	 * 
	 * @param path
	 *            the path
	 * @return the file handle
	 */
	public FileHandle resolve(String path) {
		return fileResolver.resolve(path);
	}

	/**
	 * Sets the game path
	 * 
	 * @param gamePath
	 *            the game path, if null, it is set to "@"
	 */
	public void setGamePath(String gamePath) {
		fileResolver.setGamePath(gamePath);
	}
}
