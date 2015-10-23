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

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is meant to provide a convenient utility for accessing a model
 * through a namespace. It provides four public methods for <b>reading</b> or
 * <b>writing</b> a property in the model object tree, identified by a String:
 * <p/>
 * 
 * <pre>
 * <ul>
 *     <li>{@link #get(String)}</li>
 *     <li>{@link #get(Object, String)}</li>
 *     <li>{@link #set(String, Object)}</li>
 *     <li>{@link #set(Object, String, Object)}</li>
 * </ul>
 * </pre>
 * <p/>
 * <b>Usage example</b>:
 * <p/>
 * 
 * <pre>
 *     Suppose the next {@code AnObject} class is declared:
 * 
 *     public class AnObject {
 *         int a;
 *         List <AnObject> children;
 *     }
 * 
 *     With accessor, it is possible to do things like these:
 *     accessor.get("anObject.children[0]"); // Returns the first child
 *     accessor.get("anObject.children[1].a"); // Returns the int value of the second child
 *     accessor.set("anObject.children[1].a", 2); // Sets the int value of the second child to two
 * </pre>
 * <p/>
 * The core of {@link Accessor} is placed in two methods
 * {@link #resolve(String)} and {@link #resolve(Object, String)} that, given the
 * String that represents the fully qualified id of an object in the model tree,
 * returns the value of that object. All {@code get} and {@code set} methods are
 * based on the {@code resolve} methods.
 * <p/>
 * {@link Accessor} contains a map with the "root" objects ({@link #rootObjects}
 * ) in the hierarchy to resolve properties from. This way,
 * {@link #resolve(String)} assumes the fully qualified id provided refers to a
 * property in one of the root objects.
 * <p/>
 * In contrast, {@link #resolve(Object, String)} does not make such assumption,
 * as the "root object" to search from is provided as an argument.
 * <p/>
 * All the inner logic uses introspection so this class does not need to
 * "understand" the underlying model. It just applies a simple syntax defined in
 * <a href=
 * "https://github.com/e-ucm/ead/wiki/Accessing-%22schema-pieces%22-thorugh-a-namespace"
 * target="_blank">this wiki page.</a>
 * <p/>
 * Created by Javier Torrente on 9/04/14.
 */
public class Accessor {

	public static final String OBJECT_SEPARATOR = ".";
	public static final String[] MAP_SEPARATOR = { "<", ">" };
	public static final String[] LIST_SEPARATOR = { "[", "]" };

	/**
	 * Root objects in the hierarchy. See comment on
	 * {@link #Accessor(java.util.Map)} for more details
	 */
	private Map<String, Object> rootObjects;

	// Needed to convert modelComponent classes to component classes
	private ComponentLoader componentLoader;

	/**
	 * Constructor. Initializes the Accessor with a map of objects that
	 * represent the top-level entities in the model hierarchy. This could be
	 * the "game" object and the "scenes" map, for example.
	 * <p/>
	 * For example, if the Accessor is initialized with a map that contains the
	 * entry <"game", ModelEntity.class>, then this accessor will be able to
	 * resolve ids like "game" or "game.x".
	 * 
	 * @param rootObjects
	 *            A map with the root objects in the hierarchy. Example:
	 *            <"game", ModelEntity.class> => The main game object <"scenes",
	 *            Map<String, ModelEntity>> => The map with the scenes. Can be
	 *            {@code null} if {@link #get(String)} and
	 *            {@link #set(String, Object)} are not meant to be used.
	 */
	public Accessor(Map<String, Object> rootObjects) {
		this.rootObjects = rootObjects;
	}

	/**
	 * @param componentLoader
	 *            Needed to convert modelComponent classes to component classes.
	 *            May be {@code null} if no model component aliases are meant to
	 *            be used.
	 */
	public void setComponentLoader(ComponentLoader componentLoader) {
		this.componentLoader = componentLoader;
	}

	public void setRootObjects(Map<String, Object> rootObjects) {
		this.rootObjects = rootObjects;
	}

	/**
	 * Convenient constructor that initializes Accessor with
	 * {@link #rootObjects} and {@link #componentLoader} to {@code null}. When
	 * this constructor is used (meant for testing mainly)
	 * {@link #get(Object, String)} should be used instead of
	 * {@link #get(String)}, which will trigger an exception. Also
	 * {@link #set(Object, String, Object)} must be used instead of
	 * {@link #set(String, Object)}. Also no model component aliases can be
	 * used.
	 */
	public Accessor() {
		this(new HashMap<String, Object>());
	}

	/**
	 * Gets the root objects, so they can be cleared out and replaced. This
	 * allows reusing the accessor without needing to create new ones.
	 */
	public Map<String, Object> getRootObjects() {
		return rootObjects;
	}

	/**
	 * Returns the value of the property identified by {@code fullyQualifiedId}
	 * in {@link #rootObjects}. See {@link #resolve(String)} for more details.
	 */
	public Object get(String fullyQualifiedId) {
		Property property = resolve(fullyQualifiedId);
		Object value = property.get();
		Pools.free(property);
		return value;
	}

	/**
	 * Returns the value of the property identified by {@code fullyQualifiedId}
	 * in the given {@code parent} object. {@link #rootObjects} is not used. See
	 * {@link #resolve(Object, String)} for more details.
	 */
	public Object get(Object parent, String fullId) {
		Property property = resolve(parent, fullId);
		Object value = property.get();
		Pools.free(property);
		return value;
	}

	/**
	 * Sets the value of the property identified by {@code fullyQualifiedId} in
	 * {@link #rootObjects} to the given {@code value}. See
	 * {@link #resolve(String)} for more details.
	 */
	public void set(String fullyQualifiedId, Object value) {
		Property property = resolve(fullyQualifiedId);
		property.set(value);
		Pools.free(property);
	}

	/**
	 * Sets the value of the property identified by {@code fullyId} in the given
	 * {@code parent} object to the given {@code value}. {@link #rootObjects} is
	 * not used. See {@link #resolve(Object, String)} for more details.
	 */
	public void set(Object parent, String fullId, Object value) {
		Property property = resolve(parent, fullId);
		property.set(value);
		Pools.free(property);
	}

	/**
	 * Finds and returns the object represented by the given
	 * {@code fullyQualifiedId} in the namespace.
	 * <p/>
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
	 * <p/>
	 * Examples: <b>resolve("game.components<gamedata>.width")</b> returns the
	 * current width property of the main Game object, assuming "game" is a
	 * valid key in {@link #rootObjects} associated to a {@link ModelEntity}
	 * that has a GameData component. <br/>
	 * <b>resolve("scenes<scene1>")</b> returns the ModelEntity object for
	 * scene1 (assuming a "scenes" object is present in {@link #rootObjects}
	 * with type Map<String, ModelEntity>). Assuming also the scene has at least
	 * one {@code ModelEntity} children,
	 * <b>resolve("scenes<scene1>.children[0].x")</b> would return the x
	 * property for the first child entity in scene1.
	 * 
	 * @return A wrapper for the object that is identified by the given
	 *         {@code fullyQualifiedId}, allowing read-write operations.
	 * @throws {@link Accessor.AccessorException} if the object cannot be
	 *         resolved, or if a syntax error is detected while parsing the
	 *         {@code fullyQualifiedId} (for example, if there are unclosed
	 *         brackets).
	 */
	private Property resolve(String fullyQualifiedId) {
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
			// Return a wrapper to the rootObject obtained
			return obtainProperty(
					obtainMapWrapper(rootObjects, fullyQualifiedId, 0),
					rootObjectKey);
		}
	}

	/**
	 * Resolves the property identified by {@code fullId}, which should identify
	 * a property declared in the {@code parent} object tree provided (it must
	 * be represent either a property in {@code parent} or a property accessible
	 * through recursion over one of {@code parent}'s properties.
	 * <p/>
	 * This method is very similar to {@link #resolve(String)}. The difference
	 * is that it takes {@code parent} as "root" object, instead of using
	 * {@link #rootObjects}, which are ignored.
	 * <p/>
	 * Example:
	 * <p/>
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
	 * @return A read-write wrapper of the property, once resolved
	 * @throws AccessorException
	 *             If {@code fullId} is {@code} null, bad formed or if the
	 *             property cannot be read using reflection.
	 */
	private Property resolve(Object parent, String fullId) {
		return resolve(parent, OBJECT_SEPARATOR + fullId, 0);
	}

	/**
	 * Internal method for recursive resolving. Resolves the remaining of the
	 * {@code fullId} provided (marked by the {@code start} argument), which
	 * should identify a property declared in the parent object provided.
	 * <p/>
	 * Example:
	 * <p/>
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
	private Property resolve(Object parent, String fullId, int start) {
		if (parent == null)
			throw new AccessorException(fullId, "Property near position "
					+ start + " in {} is null");

		Property result = null;
		String propertyName = null;
		int nextStart = fullId.length();
		// First character should be a separator
		String separator = fullId.substring(start, start + 1);
		// If it is an object separator (.), retrieve the property from parent
		if (separator.equals(OBJECT_SEPARATOR)) {
			int nextSeparator = getNextSeparatorAt(fullId, start + 1);
			propertyName = fullId.substring(start + 1, nextSeparator);
			try {
				result = getProperty(fullId, parent, propertyName);
			} catch (ReflectionException e) {
				throw new AccessorException(fullId, "The property with id '"
						+ propertyName + "' cannot be read using reflection", e);
			}
			if (result == null) {
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
				result = obtainProperty(wrapper, childPos);
				result.get(); // To detect errors as soon as possible
				// (exceptions may be thrown)
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
				result = obtainProperty(mapWrapper, key);
				if (result == null || result.get() == null)
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
			return resolve(result.get(), fullId, nextStart);
		} else {
			return result;
		}
	}

	/**
	 * Parses the given {@code keyId} as a valid key object for the given
	 * {@code map}. Currently, only maps with String, Class, Float or Integer
	 * key types are supported. Otherwise an exception is thrown.
	 * <p/>
	 * In case the map's key type is {@code Class}, this method does a bit of
	 * magic to transform Model component classes to Engine component classes if
	 * needed. This allows accessing entities' components map referring only to
	 * model component classes.
	 * <p/>
	 * Examples:
	 * <p/>
	 * 
	 * <pre>
	 *     map: <String, Object>
	 *     keyId: "AString"
	 *     returns: "AString"
	 * </pre>
	 * <p/>
	 * 
	 * <pre>
	 *     map: <Class, Object>
	 *     keyId: "es.eucm.ead.AClass"
	 *     returns: Class<AClass>
	 * </pre>
	 * <p/>
	 * 
	 * <pre>
	 *     map: <Class, Object>
	 *     keyId: "es.eucm.ead.schema.components.AModelComponent"
	 *     returns: Class<AnEngineComponent>
	 * </pre>
	 * <p/>
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
					clazz = componentLoader.getClass(keyId);
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
				clazz = componentLoader.toEngineComponent(clazz);
			}
			return clazz;

		}
	}

	@SuppressWarnings("unchecked")
	public static Object cast(Class castClass, Object o) {
		if (o == null || o.getClass() == castClass
				|| ClassReflection.isAssignableFrom(castClass, o.getClass())) {
			return o;
		}

		if (castClass.isEnum()) {
			for (Object enumConstant : castClass.getEnumConstants()) {
				if (o.getClass() == String.class) { // String
					try {
						java.lang.reflect.Field field = enumConstant.getClass()
								.getSuperclass().getDeclaredField("name");
						field.setAccessible(true);
						String constantName = (String) (field.get(enumConstant));
						if (constantName.equalsIgnoreCase(o.toString())) {
							return enumConstant;
						}
					} catch (IllegalAccessException e) {
					} catch (NoSuchFieldException e) {
					}
				} else if (Number.class.isAssignableFrom(o.getClass())) { // Number
					try {
						java.lang.reflect.Field field = enumConstant.getClass()
								.getSuperclass().getDeclaredField("ordinal");
						field.setAccessible(true);
						Integer constantOrdinal = (Integer) (field
								.get(enumConstant));
						if (constantOrdinal.equals(o)) {
							return enumConstant;
						}
					} catch (IllegalAccessException e) {
					} catch (NoSuchFieldException e) {
					}
				}
			}
		}

		if (castClass == Array.class || castClass == Iterable.class) {
			Array array = new Array();
			if (o instanceof Iterable) {
				for (Object object : (Iterable) o) {
					array.add(object);
				}
			} else {
				array.add(o);
			}
			return array;
		} else if (castClass == ArrayList.class || castClass == List.class
				|| castClass == Collection.class) {
			ArrayList arrayList = new ArrayList();
			if (o instanceof Iterable) {
				for (Object object : (Iterable) o) {
					arrayList.add(object);
				}
			} else {
				arrayList.add(o);
			}
			return arrayList;
		} else if (castClass == boolean.class && o instanceof Boolean) {
			return o;
		} else if (castClass.isPrimitive() && o instanceof Number) {
			return o;
		}

		Gdx.app.error("EngineUtils", "Impossible to cast " + o + " to "
				+ castClass);

		String message = "Error in cast. Impossible to cast " + o + " to "
				+ castClass;
		throw new AccessorException("", message);
	}

	/**
	 * Retrieves the property with the given name from the given object via
	 * reflection ({@link ClassReflection}).
	 * 
	 * @param parent
	 *            The object to retrieve from
	 * @param propertyName
	 *            The name of the property to be retrieved.
	 * @return The {@link Property} wrapper. Allows reading and writing the
	 *         value of the property
	 * @throws ReflectionException
	 *             if the property cannot be accessed
	 */
	private Property getProperty(String fullId, Object parent,
			String propertyName) throws ReflectionException {
		Class currentClass = parent.getClass();
		while (currentClass != null) {
			for (Field declaredField : ClassReflection
					.getDeclaredFields(currentClass)) {
				if (declaredField.getName().equals(propertyName)) {
					declaredField.setAccessible(true);
					return obtainProperty(declaredField, fullId, parent);
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
		return minIndex(fullId, start, OBJECT_SEPARATOR, LIST_SEPARATOR[0],
				MAP_SEPARATOR[0]);
	}

	private int minIndex(String fullId, int start, String... separators) {
		int nextSeparator = fullId.length();
		for (String c : separators) {
			int index = fullId.indexOf(c, start);
			nextSeparator = index == -1 ? nextSeparator : Math.min(
					nextSeparator, index);
		}
		return nextSeparator;
	}

	// ////////////////////////////////////////////////////
	// Property wrappers
	// ////////////////////////////////////////////////////

	/**
	 * Creates a wrapper for the given element
	 * 
	 * @param elementToWrap
	 *            Element to be wrapped. Supported classes: {@link Field},
	 *            {@link AbstractArrayWrapper}, {@link AbstractMapWrapper}.
	 * @param arguments
	 *            Arguments used to initialize the object
	 * @return the {@link Property} wrapper or {@code null} if
	 *         {@code elementToWrap} has an incompatible type.
	 */
	private Property obtainProperty(Object elementToWrap, Object... arguments) {
		if (elementToWrap instanceof Field) {
			FieldProperty property = Pools.obtain(FieldProperty.class);
			property.init((String) arguments[0], (Field) elementToWrap,
					arguments[1]);
			return property;
		} else if (elementToWrap instanceof AbstractArrayWrapper) {
			ArrayProperty property = Pools.obtain(ArrayProperty.class);
			property.init((AbstractArrayWrapper) elementToWrap,
					(Integer) arguments[0]);
			return property;
		} else if (elementToWrap instanceof AbstractMapWrapper) {
			MapProperty property = Pools.obtain(MapProperty.class);
			property.init((AbstractMapWrapper) elementToWrap, arguments[0]);
			return property;
		}
		return null;
	}

	/**
	 * Simple wrapper for getting or setting a property
	 */
	public interface Property extends Pool.Poolable {
		public abstract Object get();

		public abstract void set(Object value);
	}

	/**
	 * Wrapper for an element of a map {@code get} is equivalent to
	 * {@code map.get(key)} {@code set} is equivalent to
	 * {@code map.put(key, value)}
	 */
	public static class MapProperty implements Property {

		private AbstractMapWrapper map;

		private Object key;

		public MapProperty() {
		}

		public void init(AbstractMapWrapper map, Object key) {
			this.map = map;
			this.key = key;
		}

		@Override
		public Object get() {
			return map.get(key);
		}

		@Override
		public void set(Object value) {
			map.put(key, value);
		}

		@Override
		public void reset() {
			Pools.free(map);
			key = null;
		}
	}

	/**
	 * Wrapper for an element in an array. {@code get} is equivalent to
	 * {@code array[index]} {@code set} is equivalent to
	 * {@code array[index]=value}
	 */
	public static class ArrayProperty implements Property {

		private AbstractArrayWrapper array;

		private int index;

		public ArrayProperty() {
		}

		public void init(AbstractArrayWrapper array, int index) {
			this.array = array;
			this.index = index;
		}

		@Override
		public Object get() {
			return array.get(index);
		}

		@Override
		public void set(Object value) {
			array.set(value, index);
		}

		@Override
		public void reset() {
			Pools.free(array);
			index = 0;
		}
	}

	/**
	 * Wrapper for a simple property {@code get} is equivalent to
	 * {@code object.prop;} {@code set} is equivalent to
	 * {@code object.prop = otherProp;}
	 */
	public static class FieldProperty implements Property {

		private Field field;

		private Object object;

		private String fullyQualifiedId;

		public FieldProperty() {
		}

		public void init(String fullyQualifiedId, Field field, Object object) {
			this.field = field;
			this.object = object;
			this.fullyQualifiedId = fullyQualifiedId;
		}

		@Override
		public Object get() {
			try {
				return field.get(object);
			} catch (ReflectionException e) {
				String message = "Error executing get() on property:"
						+ field.getName() + " on object " + object;
				throw new AccessorException(fullyQualifiedId, message, e);
			}
		}

		@Override
		public void set(Object value) {
			try {
				value = cast(field.getType(), value);
			} catch (AccessorException e) {
				e.setFullyQualifiedId(fullyQualifiedId);
				throw e;
			}
			field.setAccessible(true);

			try {
				field.set(object, value);
			} catch (ReflectionException e) {
				String message = "Error executing set() on property:"
						+ field.getName() + " on object " + object
						+ " with value="
						+ (value instanceof String ? "\"" : "") + value
						+ (value instanceof String ? "\"" : "");
				throw new AccessorException(fullyQualifiedId, message, e);
			}
		}

		@Override
		public void reset() {
			field = null;
			object = null;
			fullyQualifiedId = null;
		}
	}

	// ///////////////////////////////////////////////////
	// Map and array wrappers
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
				&& !(object instanceof IntMap) && !(object instanceof Bag)) {
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
		} else if (object instanceof Bag) {
			wrapper = Pools.obtain(BagWrapper.class);
			((BagWrapper) wrapper).setBag((Bag) object);
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

		/**
		 * Puts the given {@code value} into the map associated to the given
		 * {@code key}
		 */
		public void put(Object key, Object value);
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
		 * Sets the element at position {@code index}
		 */
		public void set(Object object, int index);

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

		@Override
		public void put(Object key, Object value) {
			map.put(key, value);
		}
	}

	public static class BagWrapper implements AbstractMapWrapper {

		private HashMap<Class, Object> map = new HashMap<Class, Object>();

		public void setBag(Bag bag) {
			map.clear();
			for (int i = 0; i < bag.size(); i++) {
				if (bag.get(i) != null) {
					map.put(bag.get(i).getClass(), bag.get(i));
				}
			}
		}

		@Override
		public Class getKeyType() {
			return Component.class;
		}

		@Override
		public Object get(Object key) {
			return map.get(key);
		}

		@Override
		public void put(Object key, Object value) {
			throw new RuntimeException("Can't set a value in Bag");
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

		@Override
		public void put(Object key, Object value) {
			objectMap.put(key, value);
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

		@Override
		public void put(Object key, Object value) {
			if (!(key instanceof Integer))
				throw new IllegalArgumentException(
						"The key must be an integer!");
			intMap.put((Integer) key, value);
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
		public void set(Object object, int index) {
			array.set(index, object);
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
		public void set(Object object, int index) {
			list.set(index, object);
		}

		@Override
		public int size() {
			return list.size();
		}
	}

	// ////////////////////////////////
	// Exception
	// /////////////////////////////

	/**
	 * Simple class for wrapping exceptions generated while parsing an id
	 */
	public static class AccessorException extends RuntimeException {
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
