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
import es.eucm.ead.engine.EAdEngine.BindListener;

import java.util.HashMap;
import java.util.Map;

public class Factory implements BindListener {

	private Map<Class<?>, Class<?>> relations;

	public Factory() {
		relations = new HashMap<Class<?>, Class<?>>();
	}

	public void bind(Class<?> schemaClazz, Class<? extends Element> coreClazz) {
		relations.put(schemaClazz, coreClazz);
	}

	public boolean containsRelation(Class<?> clazz) {
		return relations.containsKey(clazz);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Builds an engine element from an schema object
	 */
	public <S, T> T getElement(S element) {
		Class<?> clazz = relations.get(element.getClass());
		if (clazz == null) {
			Gdx.app.error("Factory", "No actor for class" + element.getClass()
					+ ". Null is returned");
			return null;
		} else {
			T a = (T) Pools.get(clazz).obtain();
			((Element) a).setElement(element);
			return a;
		}
	}

	public void free(Object element) {
		Pools.free(element);
	}

	public <T> T newInstance(Class<T> transformation) {
		return Pools.obtain(transformation);
	}

	@Override
	public void bind(String alias, Class schemaClass, Class coreClass) {
		if (schemaClass != null && coreClass != null) {
			bind(schemaClass, coreClass);
		}
	}

}
