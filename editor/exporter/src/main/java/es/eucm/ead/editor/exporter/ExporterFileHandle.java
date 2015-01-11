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

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;

/**
 * A copy of libgdx's LwjglFileHandle. Thanks to its authors:
 * 
 * @author mzechner
 * @author Nathan Sweet
 */
public final class ExporterFileHandle extends FileHandle {
	public ExporterFileHandle(String fileName, Files.FileType type) {
		super(fileName, type);
	}

	public ExporterFileHandle(File file, Files.FileType type) {
		super(file, type);
	}

	public FileHandle child(String name) {
		if (file.getPath().length() == 0)
			return new ExporterFileHandle(new File(name), type);
		return new ExporterFileHandle(new File(file, name), type);
	}

	public FileHandle sibling(String name) {
		if (file.getPath().length() == 0)
			throw new GdxRuntimeException("Cannot get the sibling of the root.");
		return new ExporterFileHandle(new File(file.getParent(), name), type);
	}

	public FileHandle parent() {
		File parent = file.getParentFile();
		if (parent == null) {
			if (type == Files.FileType.Absolute)
				parent = new File("/");
			else
				parent = new File("");
		}
		return new ExporterFileHandle(parent, type);
	}

	public File file() {
		if (type == Files.FileType.External)
			return new File(ExporterFiles.externalPath, file.getPath());
		if (type == Files.FileType.Local)
			return new File(ExporterFiles.localPath, file.getPath());
		return file;
	}
}
