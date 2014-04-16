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
package es.eucm.ead.schemax;

import com.badlogic.gdx.files.FileHandle;

/**
 * Convenient utility for handling Json extensions in {@code String}s and
 * {@code FileHandle}s
 */
public class JsonExtension {

	public static final String DOT_JSON = ".json";
	private static final String JSON = "json";

	/**
	 * @return True if the given {@code string} ends with {@value #DOT_JSON},
	 *         false otherwise or if it is null. The comparison is non-case
	 *         sensitive.
	 */
	public static boolean hasJsonEnd(String string) {
		return string != null
				&& string.toLowerCase().endsWith(DOT_JSON.toLowerCase());
	}

	/**
	 * Removes the {@value #DOT_JSON} extension from the given {@code string}.
	 * 
	 * @param string
	 *            The string from which to remove the extension.
	 * @return The {@code string} without {@value #DOT_JSON} extension, or the
	 *         same {@code string} if it has no {@value #DOT_JSON} extension.
	 */
	public static String removeJsonEnd(String string) {
		if (!hasJsonEnd(string))
			return string;
		return string.substring(0, string.length() - DOT_JSON.length());
	}

	/**
	 * Adds a {@value #DOT_JSON} extension to the end of the given
	 * {@code string}, if it does not have it yet.
	 * 
	 * @param string
	 *            The string to append the {@value #DOT_JSON} extension to.
	 * @return A string that ends with {@value #DOT_JSON}.
	 */
	public static String addJsonEnd(String string) {
		if (hasJsonEnd(string))
			return string;
		return string + DOT_JSON;
	}

	/**
	 * Checks if the given {@code file} has {@value #JSON} extension. The
	 * comparison is non-case sensitive.
	 * 
	 * @param file
	 *            The file which extension is to be checked.
	 * @return True if {@code file} has {@value #JSON} extension, false
	 *         otherwise.
	 */
	public static boolean hasJsonExtension(FileHandle file) {
		return JSON.toLowerCase().equals(file.extension().toLowerCase());
	}
}
