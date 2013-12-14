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
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.BindingsLoader.BindingListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory relates schema objects with {@link EngineObject}, listening to the
 * bindings emitted by a {@link BindingsLoader}. It also provides
 * {@link Factory#newInstance(Class)}, that should be used wherever possible to
 * create schema and engine objects
 */
public class Factory implements BindingListener {

	private Map<Class<?>, Class<?>> relations;

	public Factory() {
		relations = new HashMap<Class<?>, Class<?>>();
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

	@Override
	public void bind(String alias, Class schemaClass, Class engineClass) {
		if (schemaClass != null && engineClass != null) {
			bind(schemaClass, engineClass);
		}
	}

}
