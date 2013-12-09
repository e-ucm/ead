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

public class FileResolver implements FileHandleResolver {

	private String path;

	private boolean internal;

	public FileResolver() {

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (path == null) {
			path = "@";
		} else if (!path.endsWith("/")) {
			path += "/";
		}

		if (path.startsWith("@")) {
			path = path.substring(1);
			internal = true;
		} else {
			internal = false;
		}
		this.path = path;
	}

	public FileHandle resolve(String name) {
		if (name.startsWith("@")) {
			return Gdx.files.internal(name.substring(1));
		} else if (name.startsWith("/") || (name.indexOf(':') == 1)) {
			return Gdx.files.absolute(name);
		}
		String filePath = name.startsWith(path) ? name : path + name;
		return internal ? Gdx.files.internal(filePath) : Gdx.files
				.absolute(filePath);
	}
}
