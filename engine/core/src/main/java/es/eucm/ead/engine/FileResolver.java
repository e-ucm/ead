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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

/**
 * Resolves files location. File resolver follows the next conventions:
 * <ul>
 * <li>If the path begins with "/" or "X:" (being X any letter to define a
 * Windows volume), the path is interpreted as an absolute path, and it reminds
 * untouched.</li>
 * <li>If the path begins with "@", it refers an internal file which root is in
 * the project assets folder. E.g., "@binds.json" refers to the file binds.json
 * in the assets folder. However, the file resolver always try first to load the
 * file from the game folder. This mechanism allows override some default files,
 * to customize some things (e.g. default images given by the engine)</li>
 * <li>Else, the path is interpreted as a project file, and the final path is
 * the result of concatenating the path set in the file resolver and the given
 * path. If the file doesn't exist, tries to lad the same file for the internal
 * folder</li>
 * </ul>
 * <a href="https://github.com/e-ucm/ead/wiki/File-paths">More info about
 * paths</a>
 */
public class FileResolver implements FileHandleResolver {

	private String gamePath;

	/**
	 * Sets the path for the game files. If the path is null, the game path is
	 * set to "@", meaning that all files will be internal
	 * 
	 * @param gamePath
	 *            the game files path. A slash is automatically added at the end
	 *            if it's not already there
	 */
	public void setGamePath(String gamePath) {
		if (gamePath == null) {
			gamePath = "@";
		}

		gamePath = gamePath.replaceAll("\\\\", "/");
		if (!gamePath.endsWith("/")) {
			gamePath += "/";
		}
		this.gamePath = gamePath;
	}

	/**
	 * Resolves the path following following the file resolver conventions
	 * 
	 * @param path
	 *            the path
	 * @return a file handle pointing the given path. The file could not exist
	 * 
	 */
	public FileHandle resolve(String path) {
		path = path.replaceAll("\\\\", "/");
		// Absolute file
		if (path.startsWith("/") || (path.indexOf(':') == 1)) {
			return Gdx.files.absolute(path);
			// Internal file
		} else if (path.startsWith("@")) {
			// Remove "@"
			path = path.substring(1);
			FileHandle fh = Gdx.files.internal(path);
			if (fh.exists()) {
				return fh;
			} else {
				// Fallback: look into game files
				return Gdx.files.absolute(gamePath + path);
			}
			// Game file
		} else {
			FileHandle fh = Gdx.files.absolute(gamePath + path);
			if (fh.exists()) {
				return fh;
			} else {
				// Fallback: look into internal files
				return Gdx.files.internal(path);
			}
		}
	}
}
