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
package es.eucm.ead.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EngineUtils {

	public static void setParameters(VariablesManager variablesManager,
			Object object, Iterable<Parameter> expressionFields) {
		for (Parameter parameter : expressionFields) {
			Class currentClass = object.getClass();
			while (currentClass != null) {
				try {
					Field field = ClassReflection.getDeclaredField(
							currentClass, parameter.getName());
					field.setAccessible(true);
					Object value = variablesManager
							.evaluateExpression(parameter.getValue());
					Object cast = cast(field.getType(), value);
					field.set(object, cast);
					break;
				} catch (ReflectionException e) {
					currentClass = currentClass.getSuperclass();
				}
			}

			if (currentClass == null) {
				Gdx.app.error("EngineUtils", "Impossible to set field "
						+ parameter.getName());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Object cast(Class castClass, Object o) {
		if (o.getClass() == castClass
				|| ClassReflection.isAssignableFrom(castClass, o.getClass())) {
			return o;
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
		return null;
	}
}
