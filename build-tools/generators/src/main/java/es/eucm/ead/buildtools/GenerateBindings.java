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

import java.util.Arrays;
import java.util.Comparator;

public class GenerateBindings {

	/**
	 * Location for processed skins
	 */
	public static final String SCHEMA_BINDINGS_LOCATION = "assets/bindings.json";

	public static final String SCHEMA_PACKAGE = "es/eucm/ead/schema/";

	public static final String SCHEMA_FOLDER = "engine/schema/src/main/java/";

	public static final String EDITOR_SCHEMA_BINDINGS_LOCATION = "assets/editor-bindings.json";

	public static final String EDITOR_SCHEMA_PACKAGE = "es/eucm/ead/schema/editor/";

	public static final String EDITOR_SCHEMA_FOLDER = "editor/schema/src/main/java/";

	public static void main(String[] args) {
		System.out.println("Generating bindings for engine schema...");
		generateBindingsFile(SCHEMA_FOLDER, SCHEMA_PACKAGE,
				SCHEMA_BINDINGS_LOCATION);
		System.out.println("Generating bindings for editor schema...");
		generateBindingsFile(EDITOR_SCHEMA_FOLDER, EDITOR_SCHEMA_PACKAGE,
				EDITOR_SCHEMA_BINDINGS_LOCATION);
	}

	public static void generateBindingsFile(String schemaFolder,
			String schemaPackage, String schemaBindingsLocation) {
		Files files = new LwjglFiles();
		String bindings = "[\n  ";
		bindings += addBindings(files.internal(schemaFolder + schemaPackage),
				schemaFolder);
		int lastComma = bindings.lastIndexOf(',');
		if (lastComma > 0) {
			bindings = bindings.substring(0, lastComma);
		}
		bindings += "\n]";
		new FileHandle(files.internal(schemaBindingsLocation).file())
				.writeString(bindings, false);
	}

	public static String addBindings(FileHandle folder, String schemaFolder) {
		String childrenBindings = "";
		String folderPackage = folder.path().substring(schemaFolder.length());

		String packageBindings = folderPackage.replace("/", ".") + ",\n  ";

		FileHandle[] children = folder.list();
		Arrays.sort(children, new Comparator<FileHandle>() {
			@Override
			public int compare(FileHandle o1, FileHandle o2) {
				return o1.name().compareTo(o2.name());
			}
		});

		for (FileHandle child : children) {
			if (child.isDirectory()) {
				childrenBindings += addBindings(child, schemaFolder);
			} else {
				packageBindings += "" + child.nameWithoutExtension() + ",\n  ";
			}
		}
		return packageBindings + childrenBindings;
	}
}
