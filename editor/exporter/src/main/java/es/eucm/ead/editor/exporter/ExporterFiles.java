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
package es.eucm.ead.editor.exporter;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;

/**
 * Literally a copy of libgdx's LwjglFiles. Thanks to its authors:
 * 
 * @author mzechner
 * @author Nathan Sweet
 * 
 *         Created by Javier Torrente on 4/04/14.
 */
public final class ExporterFiles implements Files {
	static public final String externalPath = System.getProperty("user.home")
			+ File.separator;
	static public final String localPath = new File("").getAbsolutePath()
			+ File.separator;

	@Override
	public FileHandle getFileHandle(String fileName, FileType type) {
		return new ExporterFileHandle(fileName, type);
	}

	@Override
	public FileHandle classpath(String path) {
		return new ExporterFileHandle(path, FileType.Classpath);
	}

	@Override
	public FileHandle internal(String path) {
		return new ExporterFileHandle(path, FileType.Internal);
	}

	@Override
	public FileHandle external(String path) {
		return new ExporterFileHandle(path, FileType.External);
	}

	@Override
	public FileHandle absolute(String path) {
		return new ExporterFileHandle(path, FileType.Absolute);
	}

	@Override
	public FileHandle local(String path) {
		return new ExporterFileHandle(path, FileType.Local);
	}

	@Override
	public String getExternalStoragePath() {
		return externalPath;
	}

	@Override
	public boolean isExternalStorageAvailable() {
		return true;
	}

	@Override
	public String getLocalStoragePath() {
		return localPath;
	}

	@Override
	public boolean isLocalStorageAvailable() {
		return true;
	}
}
