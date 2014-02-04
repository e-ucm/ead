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
package es.eucm.ead.engine.assets.serializers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.Assets;

/**
 * Default serializer that recreates a default io process. This class can be
 * extended for those serializers that only want to override a specific io
 * operation (read or write) and want to let the other with the default behavior
 * 
 * @param <T>
 *            a schema class
 */
public class DefaultSerializer<T> implements Serializer<T> {

	protected Assets assets;

	public DefaultSerializer(Assets assets) {
		this.assets = assets;
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
			Gdx.app.error("DefaultSerializer", "Error creating instance for "
					+ type, e);
		}
		json.readFields(o, jsonData);
		return o;
	}
}
