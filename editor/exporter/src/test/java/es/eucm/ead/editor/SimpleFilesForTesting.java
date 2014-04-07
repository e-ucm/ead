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
package es.eucm.ead.editor;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;

/**
 * Simple implementation of LibGdx's {@link com.badlogic.gdx.Files} interface.
 * It is needed to locate the engine lib used for testing the exportation in
 * {@link ExporterTest} regardless where the test is being launched from.
 * 
 * This class is a literal copy of MockFiles, which is not accessible from this
 * project.
 * 
 * Created by Javier Torrente on 4/04/14.
 */
public class SimpleFilesForTesting implements Files {

	static public final String externalPath = System.getProperty("user.home")
			+ "/";

	@Override
	public FileHandle getFileHandle(String fileName, FileType type) {
		return new SimpleFileHandle(fileName, type);
	}

	@Override
	public FileHandle classpath(String path) {
		return new SimpleFileHandle(path, FileType.Classpath);
	}

	@Override
	public FileHandle internal(String path) {
		return new SimpleFileHandle(path, FileType.Internal);
	}

	@Override
	public FileHandle external(String path) {
		return new SimpleFileHandle(path, FileType.External);
	}

	@Override
	public FileHandle absolute(String path) {
		return new SimpleFileHandle(path, FileType.Absolute);
	}

	@Override
	public FileHandle local(String path) {
		return new SimpleFileHandle(path, FileType.Local);
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
		return "";
	}

	@Override
	public boolean isLocalStorageAvailable() {
		return true;
	}

	public class SimpleFileHandle extends FileHandle {
		public SimpleFileHandle(String fileName, Files.FileType type) {
			super(fileName, type);
		}

		public SimpleFileHandle(File file, Files.FileType type) {
			super(file, type);
		}

		public FileHandle child(String name) {
			if (file.getPath().length() == 0)
				return new SimpleFileHandle(new File(name), type);
			return new SimpleFileHandle(new File(file, name), type);
		}

		public FileHandle sibling(String name) {
			if (file.getPath().length() == 0)
				throw new GdxRuntimeException(
						"Cannot get the sibling of the root.");
			return new SimpleFileHandle(new File(file.getParent(), name), type);
		}

		public FileHandle parent() {
			File parent = file.getParentFile();
			if (parent == null) {
				if (type == Files.FileType.Absolute)
					parent = new File("/");
				else
					parent = new File("");
			}
			return new SimpleFileHandle(parent, type);
		}

		public File file() {
			if (type == Files.FileType.External)
				return new File(SimpleFilesForTesting.externalPath,
						file.getPath());
			return file;
		}
	}

}
