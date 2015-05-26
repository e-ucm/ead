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
import es.eucm.ead.schema.renderers.SpineAnimation;
import es.eucm.ead.schemax.ModelStructure;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Some useful methods to deal with file system and projects
 */
public class ProjectUtils {

	public static final String ZIP_EXTENSION = "zip";

	private static final Array<String> imageExtensions = new Array<String>(
			new String[] { "jpg", "jpeg", "png", "gif", "bmp" });

	private static final Array<String> audioExtensions = new Array<String>(
			new String[] { "mp3", "ogg", "wav" });

	// To detect sound and video extensions
	private static final Array<String> videoExtensions = new Array<String>(
			new String[] { "mpg", "mpeg", "avi" });

	/**
	 * Checks if the given String has a binary extension. That is, if the String
	 * ends with "." followed by a spine animation file (".json" or ".atlas"),
	 * an image file extension ({@link #imageExtensions}), an audio file
	 * extension ({@link #audioExtensions}), or a video file extension (
	 * {@link #videoExtensions}). The comparison is case insensitive
	 * 
	 * @param strValue
	 *            The String value to be checked
	 * @return True if the value ends with binary format extension, false
	 *         otherwise (or if null)
	 */
	public static boolean hasBinaryExtension(String strValue) {
		if (strValue == null)
			return false;
		String strValueLowerCase = strValue.toLowerCase();
		if (strValueLowerCase.endsWith(".json")
				|| strValueLowerCase.endsWith(".atlas")) {
			return true;
		}
		for (String imageExtension : imageExtensions) {
			if (strValueLowerCase.endsWith("." + imageExtension.toLowerCase())) {
				return true;
			}
		}
		for (String binaryExtension : videoExtensions) {
			if (strValueLowerCase.endsWith("." + binaryExtension.toLowerCase())) {
				return true;
			}
		}
		for (String binaryExtension : audioExtensions) {
			if (strValueLowerCase.endsWith("." + binaryExtension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

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
	 *            {@link #imageExtensions}, {@link #audioExtensions}) and
	 *            {@link #videoExtensions}), either lowercase or uppercase.
	 * 
	 * @param object
	 *            The object to search binary references in.
	 */
	public static Array<String> listRefBinaries(Object object) {
		BinaryReferences binaryPaths = new BinaryReferences();
		listRefBinaries(object, null, binaryPaths);
		return binaryPaths.getReferences();
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
			BinaryReferences binaryPaths) {
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
			String strValue = (String) object;
			binaryPaths.checkAndAdd(strValue);
		}
		// Special case: SpineAnimation does not store extension, and therefore
		// it needs to be treated differently
		else if (ClassReflection.isAssignableFrom(SpineAnimation.class, clazz)) {
			SpineAnimation spineAnimation = (SpineAnimation) object;
			String baseUri = spineAnimation.getUri();
			if (baseUri != null) {
				if (baseUri.toLowerCase().endsWith(".json")) {
					baseUri = baseUri.substring(0, baseUri.length() - 5);
				}
				String pngUri = baseUri + ".png";
				String jsonUri = baseUri + ".json";
				String atlasUri = baseUri + ".atlas";
				// Avoid adding the same reference twice
				binaryPaths.checkAndAdd(pngUri);
				binaryPaths.checkAndAdd(jsonUri);
				binaryPaths.checkAndAdd(atlasUri);
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

		// Special case: SpineAnimation does not store extension, and therefore
		// it needs to be treated differently
		else if (ClassReflection.isAssignableFrom(SpineAnimation.class, clazz)) {
			SpineAnimation spineAnimation = (SpineAnimation) object;
			String baseUri = spineAnimation.getUri();
			if (baseUri != null) {
				baseUri = baseUri.toLowerCase();
				oldRef = oldRef.toLowerCase();
				if (baseUri.endsWith(".json")) {
					baseUri = baseUri.substring(0, baseUri.length() - 5);
				}
				if (oldRef.endsWith(".json")) {
					oldRef = oldRef.substring(0, oldRef.length() - 5);
				}
				if (baseUri.equals(oldRef)) {
					spineAnimation.setUri(newRef);
				}
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
		name = name.replaceAll("[^a-zA-Z0-9.-]", "_");

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
		return !file.isDirectory() && isSupportedImage(file.path());
	}

	public static boolean isSupportedImage(String path) {
		path = path.toLowerCase();
		for (String extension : imageExtensions) {
			if (path.endsWith("." + extension)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSupportedText(FileHandle file) {
		return !file.isDirectory()
				&& "txt".equals(file.extension().toLowerCase());
	}

	/**
	 * 
	 * @return true if the file is a supported audio that can be loaded.
	 */
	public static boolean isSupportedAudio(FileHandle file) {
		return !file.isDirectory()
				&& audioExtensions.contains(file.extension().toLowerCase(),
						false);
	}

	/**
	 * 
	 * @param path
	 *            used to extract the file's name
	 * @return the file name or the path itself
	 */
	public static String getFileName(String path) {
		int separatorIndex = path.lastIndexOf(File.separator);
		return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1,
				path.length());
	}

	/**
	 * List of unique String references (case-insensitive). Only Strings that
	 * are not in the list are actually added (case-insensitive comparison)
	 */
	private static class BinaryReferences {
		private Array<String> lowerCaseReferences;
		private Array<String> originalReferences;

		public BinaryReferences() {
			lowerCaseReferences = new Array<String>();
			originalReferences = new Array<String>();
		}

		/**
		 * Adds the given String reference to the list if it is not still
		 * present and it is a binary file
		 * 
		 * @param binaryReference
		 *            The reference to be added (e.g. "image.png" or
		 *            "sound.wav")
		 * @return True if added, false otherwise
		 */
		public boolean checkAndAdd(String binaryReference) {
			if (binaryReference == null
					|| !hasBinaryExtension(binaryReference)
					|| lowerCaseReferences.contains(
							binaryReference.toLowerCase(), false)) {
				return false;
			}
			lowerCaseReferences.add(binaryReference.toLowerCase());
			originalReferences.add(binaryReference);
			return true;
		}

		public Array<String> getReferences() {
			return originalReferences;
		}
	}
}
