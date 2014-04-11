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
package es.eucm.ead.engine;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is meant to provide a convenient utility for accessing the model
 * through a namespace. This way, {@link Accessor} provides a convenient method
 * {@link #resolve(String)} that, given the String that represents the fully
 * qualified id of an object in the model tree, returns the object.
 * 
 * All the inner logic uses introspection so this class does not need to
 * "understand" the underlying model. It just applies a simple syntax defined in
 * <a href=
 * "https://github.com/e-ucm/ead/wiki/Accessing-%22schema-pieces%22-thorugh-a-namespace"
 * target="_blank">this wiki page.</a>
 * 
 * Created by Javier Torrente on 9/04/14.
 */
public class Accessor {

	/**
	 * Root objects in the hierarchy. See comment on
	 * {@link #Accessor(java.util.Map)} for more details
	 */
	private Map<String, Object> rootObjects;

	public static final String OBJECT_SEPARATOR = ".";
	public static final String[] MAP_SEPARATOR = { "<", ">" };
	public static final String[] LIST_SEPARATOR = { "[", "]" };

	/**
	 * Constructor. Initializes the Accessor with a map of objects that
	 * represent the top-level entities in the model hierarchy. This will
	 * typically be the "game" object and the "scenes" map.
	 * 
	 * For example, if the Accessor is initialized with a map that contains the
	 * entry <"game", Game.class>, then this accessor will be able to resolve
	 * ids like "game" or "game.width".
	 * 
	 * @param rootObjects
	 *            A map with the root objects in the hierarchy. This will
	 *            typically contain two objects: <"game", Game.class> => The
	 *            main game object (it could also be EditorGame) <"scenes",
	 *            Map<String, Scene>> => The map with the scenes (could also
	 *            contain EditorScene).
	 */
	public Accessor(Map<String, Object> rootObjects) {
		this.rootObjects = rootObjects;
	}

	/**
	 * Finds and returns the object represented by the given
	 * {@code fullyQualifiedId} in the namespace.
	 * 
	 * The syntax used to represent objects in the namespace is simple. It must
	 * always start with the key for any of the root elements, like "game" or
	 * "scenes". Any object properties can be accessed by adding a "." followed
	 * by the name of the property. If the object is an instance of
	 * {@link java.util.List}, [int] can be used to access any of the children
	 * objects. If the object is an instance of {@link java.util.Map} any of its
	 * children can be accessed by adding <key> after the property name. For
	 * more information, visit <a href=
	 * "https://github.com/e-ucm/ead/wiki/Accessing-%22schema-pieces%22-through-a-namespace"
	 * target
	 * ="_blank">https://github.com/e-ucm/ead/wiki/Accessing-%22schema-pieces
	 * %22-through-a-namespace</a>.
	 * 
	 * Examples: <b>resolve("game.width")</b> returns the current width property
	 * of the main Game object. <b>resolve("scenes<scene1>")</b> returns the
	 * Scene object for scene1.
	 * <b>resolve("scenes<scene1>.children[0].transformation.x")</b> returns the
	 * x property for the transformation of the first sceneElement in scene1.
	 * 
	 * @return The object that is identified by the given
	 *         {@code fullyQualifiedId}. May return null, but only if the object
	 *         exists and is null.
	 * @throws {@link Accessor.AccessorException} if the object cannot be
	 *         resolved, or if a syntax error is detected while parsing the
	 *         {@code fullyQualifiedId} (for example, if there are unclosed
	 *         brackets).
	 */
	public Object resolve(String fullyQualifiedId) {
		if (fullyQualifiedId == null)
			throw new NullPointerException("The id cannot be null");
		int firstSeparatorIndex = getNextSeparatorAt(fullyQualifiedId, 0);
		String rootObjectKey = fullyQualifiedId.substring(0,
				firstSeparatorIndex);
		Object rootObject = rootObjects.get(rootObjectKey);
		if (rootObject == null) {
			throw new AccessorException(fullyQualifiedId,
					"There's no object with id '" + rootObjectKey + "'");
		}
		if (firstSeparatorIndex < fullyQualifiedId.length()) {
			return resolve(rootObject, fullyQualifiedId, firstSeparatorIndex);
		} else {
			return rootObject;
		}
	}

	/**
	 * Internal method for recursive resolving. Resolves the remaining of the
	 * fullId provided (marked by the start argument), which should identify a
	 * property declared in the parent object provided.
	 * 
	 * Example: Scene scene;
	 * <b>resolve("scene.children[2].transformation.scaleX")</b> retrieves the
	 * root object "scene" and creates a recursive call (see
	 * {@link #resolve(String)}): ---> <b>resolve(scene,
	 * "scene.children[2].transformation.scaleX", 5)</b>, which tries to resolve
	 * property "children" from parent object scene, and makes also a recursive
	 * call: --> <b>resolve(sceneElements,
	 * "scene.children[2].transformation.scaleX", 14)</b>, which resolves the
	 * second child in the list sceneElements and makes other recursive call...
	 * ... and so on until the leaf property or object is resolved.
	 * 
	 * @param parent
	 *            The object to retrieve from
	 * @param fullId
	 *            The fullId representing the object being resolved (e.g.
	 *            "scene.children[2].transformation.scaleX")
	 * @param start
	 *            The position to start to process from. The character at this
	 *            position should be a separator: . [ <). It is assumed that all
	 *            in fullId before {@code start} has already been processed to
	 *            resolve {@code parent}
	 * @return The object resolved (leaf) after recursive calls.
	 */
	private Object resolve(Object parent, String fullId, int start) {
		Object property = null;
		String propertyName = null;
		int nextStart = fullId.length();
		// First character should be a separator
		String separator = fullId.substring(start, start + 1);
		// If it is an object separator (.), retrieve the property from parent
		if (separator.equals(OBJECT_SEPARATOR)) {
			int nextSeparator = getNextSeparatorAt(fullId, start + 1);
			propertyName = fullId.substring(start + 1, nextSeparator);
			try {
				property = getProperty(parent, propertyName);
			} catch (ReflectionException e) {
				throw new AccessorException(fullId,
						"The property with id '" + propertyName
								+ "' cannot be read using introspection", e);
			}
			if (property == null) {
				throw new AccessorException(fullId,
						"There's no property with id '" + propertyName
								+ "' in object near position " + start);
			}
			nextStart = nextSeparator;
		}

		// If the first character is a list separator, parent should be a list
		else if (separator.equals(LIST_SEPARATOR[0])) {
			// Check parent is a List
			if (!(parent instanceof List)) {
				throw new AccessorException(
						fullId,
						"Object before position "
								+ start
								+ " should be of class java.util.List. Otherwise the operator "
								+ LIST_SEPARATOR[0] + LIST_SEPARATOR[1]
								+ " cannot be used.");
			}

			List list = (List) parent;
			// Get the index
			int secondSeparator = fullId.indexOf(LIST_SEPARATOR[1], start);
			if (secondSeparator < 0) {
				throw new AccessorException(fullId, LIST_SEPARATOR[1]
						+ " is missing for " + LIST_SEPARATOR[0]
						+ " at position " + start);
			}
			String childId = fullId.substring(start + 1, secondSeparator);
			nextStart = secondSeparator + 1;
			try {
				int childPos = Integer.parseInt(childId);
				property = list.get(childPos);
			} catch (NumberFormatException e) {
				throw new AccessorException(
						fullId,
						"The position of the list to be retrieved cannot be determined between positions "
								+ start
								+ " and "
								+ secondSeparator
								+ " ("
								+ fullId.substring(start, secondSeparator + 1)
								+ "). Only integers can be placed between "
								+ LIST_SEPARATOR[0] + LIST_SEPARATOR[1], e);
			} catch (IndexOutOfBoundsException e) {
				throw new AccessorException(
						fullId,
						"The position of the list to be retrieved ("
								+ childId
								+ ") is less than zero or exceeds the size of the list at position "
								+ start + ". The list has only " + list.size()
								+ " items", e);
			}
		}

		// If the first character is a map separator, parent should be a map
		else if (separator.equals(MAP_SEPARATOR[0])) {
			// Check parent is a Map
			if (!(parent instanceof Map)) {
				throw new AccessorException(
						fullId,
						"Object before position "
								+ start
								+ " should be of class java.util.Map. Otherwise the operator "
								+ MAP_SEPARATOR[0] + MAP_SEPARATOR[1]
								+ " cannot be used.");
			}

			Map map = (Map) parent;
			// Get the index
			int secondSeparator = fullId.indexOf(MAP_SEPARATOR[1], start);
			if (secondSeparator < 0) {
				throw new AccessorException(fullId, LIST_SEPARATOR[1]
						+ " is missing for " + LIST_SEPARATOR[0]
						+ " at position " + start);
			}
			String childId = fullId.substring(start + 1, secondSeparator);
			nextStart = secondSeparator + 1;
			try {
				property = map.get(childId);
				if (property == null)
					throw new AccessorException(fullId,
							"The map before position " + start
									+ " does not contain the key specified: '"
									+ childId + "'");
			} catch (ClassCastException e) {
				throw new AccessorException(fullId,
						"The key provided has no appropriate type for map before position "
								+ start + " ("
								+ fullId.substring(start, secondSeparator + 1)
								+ ").", e);
			} catch (NullPointerException e) {
				throw new AccessorException(fullId, "The map before position "
						+ start + " does not accept nulls.", e);
			}
		}

		// Now, check if call should be recursive
		if (nextStart < fullId.length()) {
			return resolve(property, fullId, nextStart);
		} else {
			return property;
		}

	}

	/**
	 * Retrieves the property with the given name from the given object via
	 * reflection ({@link ClassReflection}).
	 * 
	 * @param parent
	 *            The object to retrieve from
	 * @param propertyName
	 *            The name of the property to be retrieved.
	 * @return The object wrapping the property.
	 */
	private Object getProperty(Object parent, String propertyName)
			throws ReflectionException {
		Field field = ClassReflection.getDeclaredField(parent.getClass(),
				propertyName);
		field.setAccessible(true);
		Object property = field.get(parent);
		return property;
	}

	/**
	 * Finds the next separator . < or [ starting from {@code start}
	 * 
	 * @param fullId
	 *            The string to search the separator from
	 * @param start
	 *            The initial position in the string to start searching
	 * @return The position of the next separator in the string, or
	 *         {@code fullId.length()} if not found
	 */
	private int getNextSeparatorAt(String fullId, int start) {

		String propertyId = fullId.substring(start);
		int nextSeparator = fullId.length();

		Pattern pattern = Pattern.compile(Pattern.quote(OBJECT_SEPARATOR) + "|"
				+ Pattern.quote(LIST_SEPARATOR[0]) + "|"
				+ Pattern.quote(MAP_SEPARATOR[0]));
		Matcher matcher = pattern.matcher(propertyId);
		if (matcher.find()) {
			nextSeparator = matcher.start() + start;
		}
		return nextSeparator;
	}

	/**
	 * Simple class for wrapping exceptions generated while parsing an id
	 */
	public class AccessorException extends RuntimeException {
		public static final String MESSAGE = "An error occurred trying to fetch the schema piece defined by id '{}': ";

		private String fullyQualifiedId;

		public AccessorException(String fullyQualifiedId, String reason) {
			super(MESSAGE.replace("{}", fullyQualifiedId) + reason);
			this.fullyQualifiedId = fullyQualifiedId;
		}

		public AccessorException(String fullyQualifiedId, String reason,
				Throwable cause) {
			super(MESSAGE.replace("{}", fullyQualifiedId) + reason, cause);
			this.fullyQualifiedId = fullyQualifiedId;
		}

		public String getFullyQualifiedId() {
			return fullyQualifiedId;
		}

		public void setFullyQualifiedId(String fullyQualifiedId) {
			this.fullyQualifiedId = fullyQualifiedId;
		}
	}
}
