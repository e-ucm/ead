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
package es.eucm.ead.buildtools;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;

public class GenerateBindings {

	/**
	 * Location for processed skins
	 */
	public static final String BINDINGS_LOCATION = "assets/bindings.json";

	public static final String PACKAGE = "es/eucm/ead/schema/";

	public static final String SCHEMA_FOLDER = "engine/schema/src/main/java/";

	public static void main(String[] args) {
		System.out.println("Generating bindings...");
		Files files = new LwjglFiles();
		String bindings = "[";
		bindings += addBindings(files.internal(SCHEMA_FOLDER + PACKAGE));
		bindings = bindings.substring(0, bindings.length() - 1) + "]";
		new FileHandle(files.internal(BINDINGS_LOCATION).file()).writeString(
				bindings, false);
	}

	public static String addBindings(FileHandle folder) {
		String childrenBindings = "";
		String folderPackage = folder.path().substring(SCHEMA_FOLDER.length());

		String packageBindings = "[" + folderPackage.replace("/", ".") + "],";

		for (FileHandle child : folder.list()) {
			if (child.isDirectory()) {
				childrenBindings += addBindings(child);
			} else {
				packageBindings += "[" + child.nameWithoutExtension() + "],";
			}
		}
		return packageBindings + childrenBindings;
	}
}
