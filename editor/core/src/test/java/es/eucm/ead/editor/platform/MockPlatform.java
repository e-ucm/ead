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
package es.eucm.ead.editor.platform;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.IOException;

public class MockPlatform implements Platform {

	private Vector2 size;

	private Array<File> tempFiles;

	private Array<String> pathsStack;

	public MockPlatform() {
		size = new Vector2();
		tempFiles = new Array<File>();
		pathsStack = new Array<String>();
	}

	@Override
	public void askForFile(StringListener listener) {
		if (pathsStack.size > 0) {
			listener.string(pathsStack.pop());
			return;
		}
		File file = createTempFile(false);
		listener.string(file.getAbsolutePath());
	}

	@Override
	public void askForFolder(StringListener listener) {
		if (pathsStack.size > 0) {
			listener.string(pathsStack.pop());
			return;
		}
		File file = createTempFile(true);
		listener.string(file.getAbsolutePath());
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void setSize(int width, int height) {
		size.set(width, height);
	}

	@Override
	public Vector2 getSize() {
		return size;
	}

	public void removeTempFiles() {
		for (File file : tempFiles) {
			file.delete();
		}
	}

	public File lastTempFile() {
		if (tempFiles.size == 0) {
			return null;
		}
		return tempFiles.peek();
	}

	public void pushPath(String path) {
		pathsStack.add(path);
	}

	public File createTempFile(boolean folder) {
		try {
			File file = File.createTempFile("eadeditortest", folder ? "folder"
					: "file");
			if (folder) {
				file.delete();
				file.mkdir();
			}
			tempFiles.add(file);
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
