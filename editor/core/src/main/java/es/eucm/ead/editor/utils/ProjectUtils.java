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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schemax.GameStructure;

import java.util.List;
import java.util.Map;

/**
 * Some useful methods to deal with file system and projects
 */
public class ProjectUtils {

	private static final Array<String> IMAGE_EXTENSIONS = new Array<String>(
			new String[] { "jpg", "jpeg", "png", "gif", "bmp" });

	// To detect sound and video extensions
	private static final Array<String> BINARY_EXTENSIONS = new Array<String>(
			new String[] { "midi", "mp3", "wav", "ogg", "mpg", "mpeg", "avi" });

	/**
	 * @return an array with paths of all the projects inside the given folder
	 */
	public static Array<String> findProjects(FileHandle folder) {
		Array<String> projects = new Array<String>();
		findProjects(folder, projects);
		projects.sort();
		return projects;
	}

	private static void findProjects(FileHandle folder, Array<String> projects) {
		for (FileHandle child : folder.list()) {
			if (child.isDirectory()) {
				if (child.child("game.json").exists()) {
					projects.add(child.path());
				} else {
					findProjects(child, projects);
				}
			}
		}
	}

	/**
	 * Searches for any reference to binary files (images, sounds and videos) in
	 * the given
	 * 
	 * @param object
	 *            . Search is performed recursively using reflection, so it can
	 *            be used to search for references in any piece of the model,
	 *            entity or component. The method takes into account for
	 *            recursive search maps, libgdx's arrays, and lists. Also fields
	 *            in any superclasses of the object are searched. It is
	 *            considered a reference to a binary file any String field which
	 *            value ends with any of the supported binary formats (see
	 *            {@link #IMAGE_EXTENSIONS} and {@link #BINARY_EXTENSIONS}),
	 *            either lowercase or uppercase.
	 * 
	 * @param object
	 *            The object to search binary references in.
	 */
	public static Array<String> listRefBinaries(Object object) {
		Array<String> binaryPaths = new Array<String>();
		listRefBinaries(object, null, binaryPaths);
		return binaryPaths;
	}

	private static void listRefBinaries(Object object, Class clazz,
			Array<String> binaryPaths) {
		if (clazz == null) {
			clazz = object.getClass();
		}

		// If the object is from primitive type, do not search
		if (clazz.isEnum() || clazz == Float.class || clazz == Double.class
				|| clazz == Boolean.class || clazz == Integer.class
				|| clazz == Byte.class || clazz == Character.class
				|| clazz == Long.class || clazz == Short.class) {
			return;
		}

		// If the object is a String (leaf)
		// Leaf: String
		if (ClassReflection.isAssignableFrom(String.class, clazz)) {
			String strValue = ((String) object).toLowerCase();
			boolean hasBinaryExtension = false;
			for (String imageExtension : IMAGE_EXTENSIONS) {
				if (strValue.endsWith("." + imageExtension.toLowerCase())) {
					hasBinaryExtension = true;
					break;
				}
			}
			if (!hasBinaryExtension) {
				for (String binaryExtension : BINARY_EXTENSIONS) {
					if (strValue.endsWith("." + binaryExtension.toLowerCase())) {
						hasBinaryExtension = true;
						break;
					}
				}
			}
			// Avoid adding the same reference twice
			if (hasBinaryExtension && !binaryPaths.contains(strValue, false)) {
				binaryPaths.add(strValue);
			}
		}

		// Iterate through fields
		for (Field field : ClassReflection.getDeclaredFields(clazz)) {
			field.setAccessible(true);

			Object value = null;
			try {
				value = field.get(object);
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
			if (value == null) {
				continue;
			}

			// Recursive search: array, list, map,
			if (ClassReflection.isAssignableFrom(Array.class, field.getType())) {
				Array array = (Array) value;
				for (Object child : array) {
					if (child == null) {
						continue;
					}
					listRefBinaries(child, child.getClass(), binaryPaths);
				}
			}

			else if (ClassReflection.isAssignableFrom(List.class,
					field.getType())) {
				List list = (List) value;
				for (Object child : list) {
					if (child == null) {
						continue;
					}
					listRefBinaries(child, child.getClass(), binaryPaths);
				}
			}

			else if (ClassReflection.isAssignableFrom(Map.class,
					field.getType())) {
				Map map = (Map) value;
				for (Object child : map.values()) {
					if (child == null) {
						continue;
					}
					listRefBinaries(child, child.getClass(), binaryPaths);
				}
			}

			// Recursive search
			else {
				listRefBinaries(value, value.getClass(), binaryPaths);
			}
		}

		if (clazz.getSuperclass() != null) {
			listRefBinaries(object, clazz.getSuperclass(), binaryPaths);
		}
	}

	public static String newSceneId(Model model) {
		int count = 0;
		String prefix = GameStructure.SCENES_PATH + "scene";
		String id;
		do {
			id = prefix + count++ + ".json";
		} while (model.getResource(id) != null);
		return id;
	}

	/**
	 * @param directory
	 *            the directory where we want to search the new file.
	 * @return the first {@link FileHandle} that doesn't exist. Note that the
	 *         result will probably end with an index. E.g. {@code file} name:
	 *         "image", extension: ".jpg" => result: "image4.jpg" if that is the
	 *         first found file that doesn't exist.
	 */
	public static FileHandle getNonExistentFile(FileHandle directory,
			String name, String extension) {

		if (!extension.isEmpty() && !extension.startsWith(".")) {
			extension = "." + extension;
		}

		FileHandle result;
		result = directory.child(name + extension);
		if (result.exists()) {
			int count = 2;
			do {
				result = directory.child(name + count++ + extension);
			} while (result.exists());
		}

		return result;
	}

	/**
	 * 
	 * @return true if the file is a supported image that can be loaded.
	 */
	public static boolean isSupportedImage(FileHandle file) {
		if (file.isDirectory()) {
			return false;
		}
		String fileName = file.name();
		for (String imageExtension : IMAGE_EXTENSIONS) {
			if (fileName.endsWith(imageExtension)) {
				return true;
			}
		}
		return false;
	}
}
