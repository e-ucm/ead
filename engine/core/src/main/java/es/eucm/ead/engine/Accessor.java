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

import ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.components.game.GameData;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is meant to provide a convenient utility for accessing the model
 * through a namespace. This way, {@link Accessor} provides two convenient
 * methods {@link #resolve(String)} and {@link #resolve(Object, String)} that,
 * given the String that represents the fully qualified id of an object in the
 * model tree, returns the value of that object.
 * 
 * {@link Accessor} contains a map with the "root" objects ({@link #rootObjects}
 * ) in the hierarchy to resolve properties from. This way,
 * {@link #resolve(String)} assumes the fully qualified id provided refers to a
 * property in one of the root objects.
 * 
 * In contrast, {@link #resolve(Object, String)} does not make such assumption,
 * as the "root object" to search from is provided as an argument.
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
	 * {@link #Accessor(java.util.Map, EntitiesLoader)} for more details
	 */
	private Map<String, Object> rootObjects;

	// Needed to convert modelComponent classes to component classes
	private EntitiesLoader entitiesLoader;

	public static final String OBJECT_SEPARATOR = ".";
	public static final String[] MAP_SEPARATOR = { "<", ">" };
	public static final String[] LIST_SEPARATOR = { "[", "]" };

	/**
	 * Constructor. Initializes the Accessor with a map of objects that
	 * represent the top-level entities in the model hierarchy. This could be
	 * the "game" object and the "scenes" map, for example.
	 * 
	 * For example, if the Accessor is initialized with a map that contains the
	 * entry <"game", ModelEntity.class>, then this accessor will be able to
	 * resolve ids like "game" or "game.x".
	 * 
	 * @param rootObjects
	 *            A map with the root objects in the hierarchy. Example:
	 *            <"game", ModelEntity.class> => The main game object <"scenes",
	 *            Map<String, ModelEntity>> => The map with the scenes.
	 * @param entitiesLoader
	 *            Needed to convert modelComponent classes to component classes
	 */
	public Accessor(Map<String, Object> rootObjects,
			EntitiesLoader entitiesLoader) {
		this.rootObjects = rootObjects;
		this.entitiesLoader = entitiesLoader;
	}

	/**
	 * Gets the root objects, so they can be cleared out and replaced. This
	 * allows reusing the accessor without needing to create new ones.
	 */
	public Map<String, Object> getRootObjects() {
		return rootObjects;
	}

	/**
	 * Finds and returns the object represented by the given
	 * {@code fullyQualifiedId} in the namespace.
	 * 
	 * The syntax used to represent objects in the namespace is simple. It must
	 * always start with the key for any of the root elements, like "game" or
	 * "scenes". From there, any object property can be accessed by adding a "."
	 * followed by the name of the property. If the object is assignable to a
	 * supported array type ({@link java.util.List} and
	 * {@link com.badlogic.gdx.utils.Array} currently), [int] can be used to
	 * access any of the element objects in the array. If in contrast the object
	 * is assignable to a supported map type ({@link java.util.Map},
	 * {@link com.badlogic.gdx.utils.IntMap} and
	 * {@link com.badlogic.gdx.utils.ObjectMap} currently) any of the entries in
	 * the map can be accessed by adding <key> after the property name. For more
	 * information, visit <a href=
	 * "https://github.com/e-ucm/ead/wiki/Accessing-%22schema-pieces%22-through-a-namespace"
	 * target
	 * ="_blank">https://github.com/e-ucm/ead/wiki/Accessing-%22schema-pieces
	 * %22-through-a-namespace</a>.
	 * 
	 * Examples: <b>resolve("game.components<gamedata>.width")</b> returns the
	 * current width property of the main Game object, assuming "game" is a
	 * valid key in {@link #rootObjects} associated to a {@link ModelEntity}
	 * that has a {@link GameData} component. <br/>
	 * <b>resolve("scenes<scene1>")</b> returns the ModelEntity object for
	 * scene1 (assuming a "scenes" object is present in {@link #rootObjects}
	 * with type Map<String, ModelEntity>). Assuming also the scene has at least
	 * one {@code ModelEntity} children,
	 * <b>resolve("scenes<scene1>.children[0].x")</b> would return the x
	 * property for the first child entity in scene1.
	 * 
	 * @return The object that is identified by the given
	 *         {@code fullyQualifiedId}. May return {@code null}, but only if
	 *         the object exists and is {@code null}.
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
	 * Resolves the property identified by {@code fullId}, which should identify
	 * a property declared in the {@code parent} object tree provided (it must
	 * be represent either a property in {@code parent} or a property accessible
	 * through recursion over one of {@code parent}'s properties.
	 * 
	 * This method is very similar to {@link #resolve(String)}. The difference
	 * is that it takes {@code parent} as "root" object, instead of using
	 * {@link #rootObjects}, which are ignored.
	 * 
	 * Example:
	 * 
	 * <pre>
	 *     public static class A{
	 *         int a;
	 * 
	 *         List<B> bs;
	 *         ...
	 *     }
	 * 
	 *     public static class B{
	 *         int b;
	 *     }
	 * 
	 *     ...
	 * 
	 *     A a = new A(...); // Build A object containing several B objects
	 *     Accessor accessor = new Accessor(...);
	 *     accessor.resolve(a, "a"); // Returns the integer "a" value in A
	 *     accessor.resolve(a, "bs[0].b"); // Returns the integer value "b" in first B children in object a.
	 * </pre>
	 * 
	 * @param parent
	 *            The parent object that contains the property being resolved
	 * @param fullId
	 *            The fullId representing the property being resolved (e.g.
	 *            "components<componentClass>.property").
	 * @return The value of the property, once resolved
	 * @throws AccessorException
	 *             If {@code fullId} is {@code} null, bad formed or if the
	 *             property cannot be read using reflection.
	 */
	public Object resolve(Object parent, String fullId) {
		return resolve(parent, OBJECT_SEPARATOR + fullId, 0);
	}

	/**
	 * Internal method for recursive resolving. Resolves the remaining of the
	 * {@code fullId} provided (marked by the {@code start} argument), which
	 * should identify a property declared in the parent object provided.
	 * 
	 * Example:
	 * 
	 * <pre>
	 * ModelEntity scene;
	 * 
	 * <b>resolve("scene.children[2].scaleX")</b>
	 *   │            retrieves the root object "scene" and creates a recursive
	 *   │            call (see {@link #resolve(String)}):
	 *   │
	 *   └--> <b>resolve(scene,"scene.children[2].scaleX", 5)</b>
	 *            │        which tries to resolve property "children" from parent
	 *            │        object scene, and makes also a recursive call:
	 *            │
	 *            └--> <b>resolve(sceneElements, "scene.children[2].scaleX", 14)</b>
	 *                      │    which resolves the second child in the "children"
	 *                      │    list and makes other recursive call
	 *                      │
	 *                      ...
	 *                      │
	 *       and so on until the leaf property or object is resolved (scaleX in this
	 *       example).
	 * </pre>
	 * 
	 * @param parent
	 *            The object to retrieve from. {@code fullId} must represent a
	 *            valid property in the object tree {@code parent} contains.
	 * @param fullId
	 *            The fullId representing the object being resolved (e.g.
	 *            "scene.children[2].scaleX")
	 * @param start
	 *            The position to start to process from. The character at this
	 *            position should be a separator: . [ < ). It is assumed that
	 *            all in fullId before {@code start} has already been processed
	 *            to resolve {@code parent}
	 * @return The object resolved (leaf) after recursive calls.
	 * @throws AccessorException
	 *             If {@code fullId} is {@code null}, bad formed or if the
	 *             property cannot be read using reflection.
	 */
	private Object resolve(Object parent, String fullId, int start) {
		if (parent == null)
			throw new AccessorException(fullId, "Property near position "
					+ start + " in {} is null");

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
				throw new AccessorException(fullId, "The property with id '"
						+ propertyName + "' cannot be read using reflection", e);
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
			AbstractArrayWrapper wrapper = obtainArrayWrapper(parent, fullId,
					start);

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
				property = wrapper.get(childPos);
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
								+ start + ". The list has only "
								+ wrapper.size() + " items", e);
			}
			freeWrapper(wrapper);
		}

		// If the first character is a map separator, parent should be a map
		else if (separator.equals(MAP_SEPARATOR[0])) {
			AbstractMapWrapper mapWrapper = obtainMapWrapper(parent, fullId,
					start);

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
				// Get a valid key for the map based on childId
				Object key = getMapKey(mapWrapper, childId, fullId, start);
				property = mapWrapper.get(key);
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

			freeWrapper(mapWrapper);
		}

		// Now, check if call should be recursive
		if (nextStart < fullId.length()) {
			return resolve(property, fullId, nextStart);
		} else {
			return property;
		}

	}

	/**
	 * Parses the given {@code keyId} as a valid key object for the given
	 * {@code map}. Currently, only maps with String, Class, Float or Integer
	 * key types are supported. Otherwise an exception is thrown.
	 * 
	 * In case the map's key type is {@code Class}, this method does a bit of
	 * magic to transform Model component classes to Engine component classes if
	 * needed. This allows accessing entities' components map referring only to
	 * model component classes.
	 * 
	 * Examples:
	 * 
	 * <pre>
	 *     map: <String, Object>
	 *     keyId: "AString"
	 *     returns: "AString"
	 * </pre>
	 * 
	 * <pre>
	 *     map: <Class, Object>
	 *     keyId: "es.eucm.ead.AClass"
	 *     returns: Class<AClass>
	 * </pre>
	 * 
	 * <pre>
	 *     map: <Class, Object>
	 *     keyId: "es.eucm.ead.schema.components.AModelComponent"
	 *     returns: Class<AnEngineComponent>
	 * </pre>
	 * 
	 * <pre>
	 *     map: <Integer, Object>
	 *     keyId: "3"
	 *     returns: new Integer(3);
	 * </pre>
	 * 
	 * @param map
	 *            The input map. Supported map types: <String, XX>, <Class, XX>,
	 *            <Integer, XX>, <Float, XX>
	 * @param keyId
	 *            The given key id, to be transformed to an object.
	 * @param fullId
	 *            The fullId representing the object being resolved (e.g.
	 *            "scene.children[2].scaleX", assuming scene is a
	 *            {@code ModelEntity}). For building accurate exception messages
	 *            only.
	 * @param start
	 *            The position of the {@code fullId} being parsed. For building
	 *            accurate exception messages only.
	 * @return A valid key object for the given map
	 * @throws AccessorException
	 *             if the map type of {@code map} is unsupported, or if
	 *             {@code keyId} cannot be converted to a valid key for
	 *             {@code map}.
	 */
	private Object getMapKey(AbstractMapWrapper map, String keyId,
			String fullId, int start) {
		// Default class: String
		Class keyClass = String.class;
		// Inferring type of map's key
		keyClass = map.getKeyType();

		if (keyClass == String.class) {
			return keyId;
		} else if (keyClass == Integer.class) {
			try {
				return Integer.parseInt(keyId);
			} catch (NumberFormatException e) {
				throw new AccessorException(fullId, "The key " + keyId
						+ " is not valid for the given map " + map
						+ " in fully qualified id " + fullId
						+ " near position " + start);
			}
		} else if (keyClass == Float.class) {
			try {
				return Float.parseFloat(keyId);
			} catch (NumberFormatException e) {
				throw new AccessorException(fullId, "The key " + keyId
						+ " is not valid for the given map " + map
						+ " in fully qualified id " + fullId
						+ " near position " + start);
			}
		} else {
			// Do a little magic if the map's keys are Engine components and
			// keyId represents the name of a model component class
			Class clazz = null;
			try {
				clazz = ClassReflection.forName(keyId);
			} catch (ReflectionException e) {
				try {
					clazz = entitiesLoader.getClass(keyId);
				} catch (Exception e2) {
					throw new AccessorException(
							fullId,
							"The map "
									+ map
									+ " in fully qualified id "
									+ fullId
									+ " near position "
									+ start
									+ " is not supported by Accessor: Keys must be of type String, Class, Integer or Float only");
				}
			}

			if (ClassReflection.isAssignableFrom(Component.class, keyClass)
					&& ClassReflection.isAssignableFrom(ModelComponent.class,
							clazz)) {
				clazz = entitiesLoader.toEngineComponent(clazz);
			}
			return clazz;

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
		Class currentClass = parent.getClass();
		while (currentClass != null) {
			for (Field declaredField : ClassReflection
					.getDeclaredFields(currentClass)) {
				if (declaredField.getName().equals(propertyName)) {
					declaredField.setAccessible(true);
					return declaredField.get(parent);
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		throw new ReflectionException("Property " + propertyName
				+ " could not be accessed by reflection");
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

	// ///////////////////////////////////////////////////
	// Methods to obtain and free map and array wrappers
	// ///////////////////////////////////////////////////

	/**
	 * Gets a wrapper for the given {@code object}, which must be of type
	 * {@link Map}, {@link ObjectMap} or {@link IntMap}.
	 * 
	 * @param object
	 *            A {@link Map}, {@link ObjectMap} or {@link IntMap}
	 * @param fullId
	 *            The fullId representing the object being resolved (e.g.
	 *            "scene.children[2].scaleX"). For building accurate exception
	 *            messages only.
	 * @param start
	 *            The current position being parsed at {@code fullId}. For
	 *            building accurate exception messages only.
	 * @return The wrapper
	 */
	private AbstractMapWrapper obtainMapWrapper(Object object, String fullId,
			int start) {
		// Check parent is a Map
		if (!(object instanceof Map) && !(object instanceof ObjectMap)
				&& !(object instanceof IntMap)) {
			throw new AccessorException(
					fullId,
					"Object before position "
							+ start
							+ " should be of class java.util.Map, com.badlogic.gdx.utils.ObjectMap or com.badlogic.gdx.utils.IntMap. Otherwise the operator "
							+ MAP_SEPARATOR[0] + MAP_SEPARATOR[1]
							+ " cannot be used.");
		}

		AbstractMapWrapper wrapper = null;
		if (object instanceof Map) {
			wrapper = Pools.obtain(MapWrapper.class);
			((MapWrapper) wrapper).set((Map) object);
		} else if (object instanceof ObjectMap) {
			wrapper = Pools.obtain(ObjectMapWrapper.class);
			((ObjectMapWrapper) wrapper).set((ObjectMap) object);
		} else if (object instanceof IntMap) {
			wrapper = Pools.obtain(IntMapWrapper.class);
			((IntMapWrapper) wrapper).set((IntMap) object);
		}
		return wrapper;
	}

	/**
	 * Gets a wrapper for the given {@code parent} object, which must be of type
	 * {@link Array} or {@link List}.
	 * 
	 * @param parent
	 *            An {@link Array} or {@link List}.
	 * @param fullId
	 *            The fullId representing the object being resolved (e.g.
	 *            "scene.children[2].scaleX"). For building accurate exception
	 *            messages only.
	 * @param start
	 *            The current position being parsed at {@code fullId}. For
	 *            building accurate exception messages only.
	 * @return The wrapper
	 */
	private AbstractArrayWrapper obtainArrayWrapper(Object parent,
			String fullId, int start) {
		// Check parent is a List
		if (!(parent instanceof List) && !(parent instanceof Array)) {
			throw new AccessorException(
					fullId,
					"Object before position "
							+ start
							+ " should be of class java.util.List or com.badlogic.gdx.utils.Array. Otherwise the operator "
							+ LIST_SEPARATOR[0] + LIST_SEPARATOR[1]
							+ " cannot be used.");
		}

		AbstractArrayWrapper wrapper = null;
		if (parent instanceof List) {
			List list = (List) parent;
			wrapper = Pools.obtain(ListWrapper.class);
			((ListWrapper) wrapper).set(list);
		} else if (parent instanceof Array) {
			Array array = (Array) parent;
			wrapper = Pools.obtain(ArrayWrapper.class);
			((ArrayWrapper) wrapper).set(array);
		}
		return wrapper;
	}

	/**
	 * Just frees the wrapper once it's not needed anymore
	 * 
	 * @param abstractWrapper
	 *            The {@link AbstractArrayWrapper} or {@link AbstractMapWrapper}
	 *            wrapper to be freed.
	 */
	private void freeWrapper(Object abstractWrapper) {
		Pools.free(abstractWrapper);
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

	/**
	 * Abstract wrapper for different implementations of a "map" object, even if
	 * they are not subclasses of {@link Map}.
	 */
	public static interface AbstractMapWrapper {

		/**
		 * Determines the type for underlying map's keys.
		 * 
		 * @return The class for this maps' key.
		 */
		public Class getKeyType();

		/**
		 * Returns the value for the given key
		 */
		public Object get(Object key);
	}

	/**
	 * Abstract wrapper for different implementations of an "array" object, even
	 * if they are not subclasses of {@link List}
	 */
	public static interface AbstractArrayWrapper {

		/**
		 * Returns the element at position {@code index}
		 */
		public Object get(int index);

		/**
		 * @return The size of the underlying array
		 */
		public int size();
	}

	/**
	 * Wrapper for {@link Map}.
	 */
	public static class MapWrapper implements AbstractMapWrapper {

		private Map map;

		public void set(Map map) {
			this.map = map;
		}

		@Override
		public Class getKeyType() {
			Iterator iterator = map.keySet().iterator();
			if (iterator.hasNext()) {
				Object key = iterator.next();
				if (key != null) {
					return key.getClass();
				}
			}
			return null;
		}

		@Override
		public Object get(Object key) {
			return map.get(key);
		}
	}

	/**
	 * Wrapper for {@link ObjectMap}.
	 */
	public static class ObjectMapWrapper implements AbstractMapWrapper {

		private ObjectMap objectMap;

		public void set(ObjectMap objectMap) {
			this.objectMap = objectMap;
		}

		@Override
		public Class getKeyType() {
			Iterator iterator = objectMap.keys().iterator();
			if (iterator.hasNext()) {
				Object key = iterator.next();
				if (key != null) {
					if (key instanceof Class)
						return (Class) key;
					else
						return key.getClass();
				}
			}
			return null;
		}

		@Override
		public Object get(Object key) {
			return objectMap.get(key);
		}
	}

	/**
	 * Wrapper for {@link IntMap}.
	 */
	public static class IntMapWrapper implements AbstractMapWrapper {

		private IntMap intMap;

		public void set(IntMap intMap) {
			this.intMap = intMap;
		}

		@Override
		public Class getKeyType() {
			return Integer.class;
		}

		@Override
		public Object get(Object key) {
			return intMap.get((Integer) key);
		}
	}

	/**
	 * Wrapper for {@link Array}
	 */
	public static class ArrayWrapper implements AbstractArrayWrapper {

		private Array array;

		public void set(Array array) {
			this.array = array;
		}

		@Override
		public Object get(int index) {
			return array.get(index);
		}

		@Override
		public int size() {
			return array.size;
		}
	}

	/**
	 * Wrapper for {@link List}.
	 */
	public static class ListWrapper implements AbstractArrayWrapper {

		private List list;

		public void set(List list) {
			this.list = list;
		}

		@Override
		public Object get(int index) {
			return list.get(index);
		}

		@Override
		public int size() {
			return list.size();
		}
	}
}
