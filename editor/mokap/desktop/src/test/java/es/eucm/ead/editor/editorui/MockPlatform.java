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
package es.eucm.ead.editor.editorui;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.MokapDesktopPlatform;

public class MockPlatform extends MokapDesktopPlatform {

	private static final String DEFAULT_RESPONSE = "default";

	private Array<File> tempFiles = new Array();
	private ObjectMap<String, Object> httpResponses = new ObjectMap<String, Object>();

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

	@Override
	public <T> T sendHttpRequest(HttpRequest httpRequest, Class<T> type)
			throws IOException {

		Object ret = httpResponses.get(httpRequest.getUrl());
		if (ret == null) {
			ret = httpResponses.get(DEFAULT_RESPONSE);
		}
		return (T) ret;
	}

	public void putHttpResponse(String URL, Object object) {
		httpResponses.put(URL, object);
	}

	public void putDefaultHttpResponse(Object object) {
		httpResponses.put(DEFAULT_RESPONSE, object);
	}
}
