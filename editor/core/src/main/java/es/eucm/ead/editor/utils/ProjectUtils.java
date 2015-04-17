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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schemax.ModelStructure;

import java.util.List;
import java.util.Map;

/**
 * Some useful methods to deal with file system and projects
 */
public class ProjectUtils {

	public static final String ZIP_EXTENSION = "zip";

	private static final Array<String> imageExtensions = new Array<String>(
			new String[] { "jpg", "jpeg", "png", "gif", "bmp" });

	// To detect sound and video extensions
	private static final Array<String> binaryExtensions = new Array<String>(
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
	 * 
	 * @return a valid project name.
	 */
	public static String createProjectName() {
		return "mokap" + System.currentTimeMillis() + MathUtils.random(100);
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
	 *            {@link #imageExtensions} and {@link #binaryExtensions}),
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

	/**
	 * Utility that searches all string fields in the given object recursively,
	 * and replaces any occurrences of the oldRef param by the newRef Param
	 */
	public static void replaceBinaryRef(Object object, String oldRef,
			String newRef) {
		replaceBinaryRef(object, null, oldRef, newRef);
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
			for (String imageExtension : imageExtensions) {
				if (strValue.endsWith("." + imageExtension.toLowerCase())) {
					hasBinaryExtension = true;
					break;
				}
			}
			if (!hasBinaryExtension) {
				for (String binaryExtension : binaryExtensions) {
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

	private static void replaceBinaryRef(Object object, Class clazz,
			String oldRef, String newRef) {
		if (clazz == null) {
			clazz = object.getClass();
		}

		// If the object is from primitive type or null, do not search
		if (object == null || clazz.isEnum() || clazz == Float.class
				|| clazz == Double.class || clazz == Boolean.class
				|| clazz == Integer.class || clazz == Byte.class
				|| clazz == Character.class || clazz == Long.class
				|| clazz == Short.class) {
			return;
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
				for (int i = 0; i < array.size; i++) {
					Object child = array.get(i);
					if (child == null) {
						continue;
					} else if (child instanceof String) {
						String strValue = (String) child;
						if (strValue.toLowerCase().equals(oldRef.toLowerCase())) {
							array.set(i, newRef);
						}
					} else {
						replaceBinaryRef(child, child.getClass(), oldRef,
								newRef);
					}
				}
			}

			else if (ClassReflection.isAssignableFrom(List.class,
					field.getType())) {
				List list = (List) value;
				for (int i = 0; i < list.size(); i++) {
					Object child = list.get(i);
					if (child == null) {
						continue;
					} else if (child instanceof String) {
						String strValue = (String) child;
						if (strValue.toLowerCase().equals(oldRef.toLowerCase())) {
							list.set(i, newRef);
						}
					} else {
						replaceBinaryRef(child, child.getClass(), oldRef,
								newRef);
					}
				}
			}

			else if (ClassReflection.isAssignableFrom(Map.class,
					field.getType())) {
				Map map = (Map) value;
				for (Object key : map.keySet()) {
					Object child = map.get(key);
					if (child == null) {
						continue;
					} else if (child instanceof String) {
						String strValue = (String) child;
						if (strValue.toLowerCase().equals(oldRef.toLowerCase())) {
							map.put(key, newRef);
						}
					} else {
						replaceBinaryRef(child, child.getClass(), oldRef,
								newRef);
					}
				}
			} else if (ClassReflection.isAssignableFrom(String.class,
					field.getType())) {
				String strValue = (String) value;
				// Check if value matches oldRef
				if (strValue.toLowerCase().equals(oldRef.toLowerCase())) {
					try {
						field.set(object, newRef);
					} catch (ReflectionException e) {
						Gdx.app.error("Error setting binary ref in field "
								+ field.getName(), "", e);
					}
				}
			}
			// Recursive search
			else {
				replaceBinaryRef(value, value.getClass(), oldRef, newRef);
			}
		}

		if (clazz.getSuperclass() != null) {
			replaceBinaryRef(object, clazz.getSuperclass(), oldRef, newRef);
		}
	}

	public static String newSceneId(Model model) {
		int count = 0;
		String prefix = ModelStructure.SCENES_PATH + "scene";
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
		return !file.isDirectory()
				&& imageExtensions.contains(file.extension().toLowerCase(),
						false);
	}
}
