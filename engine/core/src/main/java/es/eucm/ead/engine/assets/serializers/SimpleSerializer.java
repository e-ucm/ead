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
package es.eucm.ead.engine.assets.serializers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.Assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Default serializer that recreates a default io process.
 * 
 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer} can be set up
 * to schedule load for any other file that {@link T} objects may refer to. For
 * example, if {@link T} contains an attribute "ref" that points to another json
 * file representing an element type of the schema (e.g. a scene),
 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer} can be
 * configured to automatically register dependency between "ref" and the Java
 * Class this additional file represents.
 * 
 * Examples:
 * 
 * ("uri", Texture.class) ("uri", TextureAtlas.class) ("styleref",
 * TextStyle.class)
 * 
 * This class can be extended for those serializers that only want to override a
 * specific io operation (read or write) and want to let the other with the
 * default behavior. In those cases that dependencies cannot be dealt with the
 * automatic behaviour implemented, please override
 * {@link #doExtraDependenciesProcessing(Object)}
 * 
 * @param <T>
 *            a schema class
 */
public class SimpleSerializer<T> implements Serializer<T> {

	protected Assets assets;

	/**
	 * List of "extra" dependencies to be dealt with. In this context, a
	 * dependency is a String attribute of the {@link T} class that represents a
	 * path to another file (e.g. another JSON file, an image, a video, etc.)
	 * whose load must be scheduled. Each
	 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer.Dependency}
	 * links the name of the attribute as it must be accessed by reflection in
	 * {@link T}, to the Class that represents the type of the referenced file.
	 * 
	 * Example: {@link es.eucm.ead.schema.renderers.Image} represents any image
	 * for the game. It only contains a "pointer" to the path of the image. This
	 * pointer is the attribute {@link es.eucm.ead.schema.renderers.Image#uri}.
	 * Once the image is loaded, it will be "stored" into a
	 * {@link com.badlogic.gdx.graphics.Texture} object. For
	 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer}, this
	 * dependency is specified as follows:
	 * 
	 * ("uri", Texture.class)
	 * 
	 */
	protected List<Dependency> dependencies;

	/**
	 * Creates a simple serializer with no additional dependencies
	 * 
	 * @param assets
	 *            The assets object that is to be used for resolving
	 *            dependencies
	 */
	public SimpleSerializer(Assets assets) {
		this.assets = assets;
		dependencies = new ArrayList<Dependency>();
	}

	/**
	 * Creates a serializer with an additional dependency: (field, clazz)
	 * 
	 * @param assets
	 *            The assets object that is to be used for resolving
	 *            dependencies
	 * @param field
	 *            The name of the field that in type T holds a pointer to an
	 *            external path. E.g.: "uri", "styleref"
	 * @param clazz
	 *            The Class that represents the Java type of the external file
	 *            (e.g. Texture.class, TextStyle.class)
	 */
	public SimpleSerializer(Assets assets, String field, Class clazz) {
		this(assets);
		dependencies.add(new Dependency(field, clazz));
	}

	/**
	 * Creates a serializer with more than one additional dependency (fields[0],
	 * classes[0]), (fields[1], classes[1]), ... (fields[N-1], classes[N-1])
	 * 
	 * @param assets
	 *            The assets object that is to be used for resolving
	 *            dependencies
	 * @param fields
	 *            An array with the name of the fields that in type T hold a
	 *            pointer to an external path. E.g.: "uri", "styleref"
	 * @param classes
	 *            An array with the Class objects that represent the Java types
	 *            of the external files this object points to (e.g.
	 *            Texture.class, TextStyle.class)
	 */
	public SimpleSerializer(Assets assets, String[] fields, Class[] classes) {
		this(assets);
		for (int i = 0; i < Math.min(fields.length, classes.length); i++) {
			dependencies.add(new Dependency(fields[i], classes[i]));
		}
	}

	@Override
	public void write(Json json, T object, Class knownType) {
		json.writeObjectStart(object.getClass(), knownType);
		json.writeFields(object);
		json.writeObjectEnd();
	}

	@Override
	@SuppressWarnings("all")
	public T read(Json json, JsonValue jsonData, Class type) {
		T o = null;
		try {
			o = (T) ClassReflection.newInstance(type);
		} catch (ReflectionException e) {
			Gdx.app.error("SimpleSerializer", "Error creating instance for "
					+ type, e);
		}
		json.readFields(o, jsonData);
		doExtraDependenciesProcessing(o);
		return o;
	}

	/**
	 * This method processes dependencies that T objects may have to any
	 * external files (JSON, images, videos, etc.). These dependencies are
	 * defined in {@link #dependencies} following the structure detailed in
	 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer.Dependency}
	 * 
	 * This method can be extended to support additional dependency processing.
	 * 
	 * @param o
	 *            The object that is being parsed and that will be returned.
	 */
	protected void doExtraDependenciesProcessing(T o) {
		for (Dependency dependency : dependencies) {
			try {
				Field field = ClassReflection.getDeclaredField(o.getClass(),
						dependency.getField());
				field.setAccessible(true);
				String fileValue = (String) field.get(o);
				if (fileValue != null) {
					assets.addDependency(fileValue, dependency.getClazz());
				}
			} catch (ReflectionException e) {
				Gdx.app.error(
						"SimpleSerializer",
						"Reflection exception on #doExtraDependenciesProcessing: Object class="
								+ o.getClass()
								+ " List of dependencies="
								+ Dependency
										.dependencyListToString(dependencies),
						e);
			}

		}
	}

	/**
	 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer.Dependency}
	 * holds the name of a field that represents a path (String) to a resource
	 * (file) that needs to be scheduled for loading. This field name is
	 * associated with the Class object that represents the Java type of the
	 * resource.
	 * 
	 * Example: {@link es.eucm.ead.schema.renderers.Image} represents any image
	 * for the game. It only contains a "pointer" to the path of the image. This
	 * pointer is the attribute {@link es.eucm.ead.schema.renderers.Image#uri}.
	 * Once the image is loaded, it will be "stored" into a
	 * {@link com.badlogic.gdx.graphics.Texture} object. For
	 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer}, this
	 * dependency is specified as follows:
	 * 
	 * ("uri", Texture.class)
	 * 
	 * Another example: {@link es.eucm.ead.schema.renderers.Text} can hold a
	 * pointer to a {@link es.eucm.ead.schema.renderers.TextStyle} that is
	 * defined in a different json file. This pointer is stored in
	 * {@link es.eucm.ead.schema.renderers.Text#styleref}. For
	 * {@link es.eucm.ead.engine.assets.serializers.SimpleSerializer}, this
	 * dependency is specified as follows:
	 * 
	 * ("styleref", TextStyle.class)
	 * 
	 */
	protected static class Dependency {
		private String field;
		private Class clazz;

		public Dependency(String field, Class clazz) {
			this.field = field;
			this.clazz = clazz;
		}

		public void setField(String field) {
			this.field = field;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public String getField() {
			return field;
		}

		public Class getClazz() {
			return clazz;
		}

		public String toString() {
			return "Dependency [field: " + field + " , class: " + clazz + "]";
		}

		/**
		 * Convenient method for printing out lists of dependencies. For logging
		 * purposes only
		 * 
		 * @param dependencyList
		 *            List of dependencies
		 * @return A string representing the list of dependencies
		 */
		public static String dependencyListToString(
				List<Dependency> dependencyList) {
			String dependenciesToString = "";
			for (int i = 0; i < dependencyList.size(); i++) {
				dependenciesToString += dependencyList.get(i).toString();
				if (i < dependencyList.size() - 1) {
					dependenciesToString += " , ";
				}
			}
			return dependenciesToString;
		}
	}
}
