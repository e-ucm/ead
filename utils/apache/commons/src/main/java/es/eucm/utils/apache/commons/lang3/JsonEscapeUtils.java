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
package es.eucm.utils.apache.commons.lang3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.List;
import java.util.Map;

/**
 * Utility to fully escape Strings and objects (containing Strings) in a way
 * that they can be parsed by any Json reader
 * 
 * Created by jtorrente on 23/05/2015.
 */
public class JsonEscapeUtils {

	/**
	 * Escapes, recursively, all Strings in the given object (
	 * {@code objectToEscape}). This method will scan the object's fields and
	 * replace any Strings found with an escaped version. Also scans recursively
	 * fields with the next data structures:
	 * 
	 * <pre>
	 *     <ol>
	 *         <li>arrays (Object[])</li>
	 *         <li>Libgdx's Array class</li>
	 *         <li>java.util.List</li>
	 *         <li>java.util.Map</li>
	 *     </ol>
	 * </pre>
	 * 
	 * For details on how each String is escaped, see
	 * {@link #escapeJsonString(String)}.
	 * 
	 * If {@code objectToEscape} is {@code null}, nothing happens. If
	 * {@code objectToEscape} is a String or any primitive type, nothing
	 * happens.
	 */
	public static void escapeObject(Object objectToEscape) {
		escapeOrUnescape(objectToEscape, null, true);
	}

	/**
	 * Unescapes, recursively, all Strings in the given object (
	 * {@code objectToUnescape}). Essentially, it undoes
	 * {@link #escapeObject(Object)}.
	 * 
	 * If {@code objectToUnescape} is {@code null}, nothing happens. If
	 * {@code objectToUnescape} is a String or any primitive type, nothing
	 * happens.
	 */
	public static void unescapeObject(Object objectToUnescape) {
		escapeOrUnescape(objectToUnescape, null, false);
	}

	private static void escapeOrUnescape(Object objectToEscape, Class type,
			boolean escape) {
		if (objectToEscape == null) {
			return;
		}

		if (type == null) {
			type = objectToEscape.getClass();
		}

		// If the objectToEscape is from primitive type, do not search
		if (type.isEnum() || type == Float.class || type == Double.class
				|| type == Boolean.class || type == Integer.class
				|| type == Byte.class || type == Character.class
				|| type == Long.class || type == Short.class
				|| type == String.class) {
			return;
		}

		// Iterate through fields
		for (Field field : ClassReflection.getDeclaredFields(type)) {
			field.setAccessible(true);

			Object value = null;
			try {
				value = field.get(objectToEscape);
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
			if (value == null) {
				continue;
			}

			// String field
			if (ClassReflection.isAssignableFrom(String.class, field.getType())) {
				try {
					field.set(objectToEscape,
							escapeOrUnescapeJsonString((String) value, escape));
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
			}
			// Recursive search: array, list, map,
			else if (ClassReflection.isArray(field.getType())) {
				int length = java.lang.reflect.Array.getLength(value);
				for (int i = 0; i < length; i++) {
					Object child = java.lang.reflect.Array.get(value, i);
					if (child == null) {
						continue;
					}
					if (child.getClass() == String.class) {
						java.lang.reflect.Array.set(
								value,
								i,
								escapeOrUnescapeJsonString((String) child,
										escape));
					} else {
						escapeOrUnescape(child, child.getClass(), escape);
					}
				}
			}

			else if (ClassReflection.isAssignableFrom(Array.class,
					field.getType())) {
				Array array = (Array) value;
				for (int i = 0; i < array.size; i++) {
					Object child = array.get(i);
					if (child == null) {
						continue;
					}
					if (child.getClass() == String.class) {
						array.set(
								i,
								escapeOrUnescapeJsonString((String) child,
										escape));
					} else {
						escapeOrUnescape(child, child.getClass(), escape);
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
					}
					if (child.getClass() == String.class) {
						list.set(
								i,
								escapeOrUnescapeJsonString((String) child,
										escape));
					} else {
						escapeOrUnescape(child, child.getClass(), escape);
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
					}
					if (child.getClass() == String.class) {
						map.replace(
								key,
								escapeOrUnescapeJsonString((String) child,
										escape));
					} else {
						escapeOrUnescape(child, child.getClass(), escape);
					}
				}
			}

			// Recursive search
			else {
				escapeOrUnescape(value, value.getClass(), escape);
			}
		}

		if (type.getSuperclass() != null) {
			escapeOrUnescape(objectToEscape, type.getSuperclass(), escape);
		}
	}

	private static String escapeOrUnescapeJsonString(String str, boolean escape) {
		return escape ? escapeJsonString(str) : unEscapeJsonString(str);
	}

	/**
	 * @return An escaped version of {@code strToEscape} so it can be safely
	 *         saved in a JSON format. The escaped version returned will also
	 *         include double quotes (") at the beginning and end of the String,
	 *         as some parsers do not work well otherwise. The core escape
	 *         algorithm is described in
	 *         {@link StringEscapeUtils#escapeJson(String)}.
	 */
	public static String escapeJsonString(String strToEscape) {
		if (strToEscape == null){
			return null;
		}
		String str = StringEscapeUtils.escapeJson(strToEscape);
		str = "\"" + str + "\"";
		return str;
	}

	/**
	 * @return An unescaped version of {@code strToUnEscape}. It undoes
	 *         {@link #escapeJsonString(String)}
	 */
	public static String unEscapeJsonString(String strToUnEscape) {
		if (strToUnEscape == null){
			return null;
		}
		String str = strToUnEscape;
		if (str.startsWith("\"")) {
			str = str.substring(1, str.length());
		}
		if (str.endsWith("\"")) {
			str = str.substring(0, str.length() - 1);
		}
		str = StringEscapeUtils.unescapeJson(str);
		return str;
	}

}
