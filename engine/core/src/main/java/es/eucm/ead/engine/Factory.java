/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory relates schema objects with {@link EngineObject}. It also provides
 * {@link Factory#newInstance(Class)}, that should be used wherever possible to
 * create schema and engine objects
 */
public class Factory {

	private Json json;

	private Map<Class<?>, Class<?>> relations;

	public Factory() {
		relations = new HashMap<Class<?>, Class<?>>();
		json = new Json();
	}

	/**
	 * Loads bindings stored in the file
	 * 
	 * @param fileHandle
	 *            file storing the bindings
	 * @return if the bindings loading was completely correct. It might fail if
	 *         the the file is not a valid or a non existing or invalid class is
	 *         found
	 */
	@SuppressWarnings("all")
	public void loadBindings(FileHandle fileHandle) {
		try {
			Array<Array<String>> bindings = json.fromJson(Array.class,
					fileHandle);
			read(bindings);
		} catch (SerializationException e) {
			Gdx.app.error("Factory", fileHandle.path()
					+ " doesn't contain a valid bindings file");
		}
	}

	private boolean read(Array<Array<String>> bindings) {
		String schemaPackage = "";
		String corePackage = "";
		for (Array<String> entry : bindings) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
				corePackage = entry.size == 1 ? null : entry.get(1);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					Class coreClass = null;
					if (entry.size == 2) {
						coreClass = corePackage == null ? null
								: ClassReflection.forName(corePackage + "."
										+ entry.get(1));
					}
					bind(schemaClass, coreClass);
				} catch (ReflectionException e) {
					Gdx.app.error("Factory", "Error loading bindings", e);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Binds a schema class with an engine class
	 * 
	 * @param schemaClazz
	 *            the schema class
	 * @param engineClazz
	 *            the engine class wrapping the schema class
	 */
	public void bind(Class<?> schemaClazz,
			Class<? extends EngineObject> engineClazz) {
		relations.put(schemaClazz, engineClazz);
	}

	/**
	 * @param clazz
	 *            a schema class
	 * @return Returns true if the given schema class has a correspondent engine
	 *         class
	 */
	public boolean containsRelation(Class<?> clazz) {
		return relations.containsKey(clazz);
	}

	/**
	 * Builds an engine object from an schema object
	 * 
	 * @param element
	 *            the schema object
	 * @return an engine object representing the schema object
	 */
	@SuppressWarnings("unchecked")
	public <S, T extends EngineObject> T getEngineObject(S element) {
		Class<?> clazz = relations.get(element.getClass());
		if (clazz == null) {
			Gdx.app.error("Factory", "No actor for class" + element.getClass()
					+ ". Null is returned");
			return null;
		} else {
			T a = (T) newInstance(clazz);
			a.setSchema(element);
			return a;
		}
	}

	/**
	 * Returns the element to the objects pool. Be careful to ensure that
	 * nothing refers to this object, because it will be eventually returned by
	 * {@link Factory#newInstance(Class)}
	 * 
	 * @param o
	 *            the object that is not longer used
	 */
	public void free(Object o) {
		Pools.free(o);
	}

	/**
	 * Creates a new instance of the given class
	 * 
	 * @param clazz
	 *            the clazz
	 * @param <T>
	 *            the type of the element returned
	 * @return an instance of the given class
	 */
	public <T> T newInstance(Class<T> clazz) {
		return Pools.obtain(clazz);
	}

}
