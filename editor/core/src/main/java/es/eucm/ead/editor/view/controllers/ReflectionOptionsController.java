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
package es.eucm.ead.editor.view.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An options panel to represent a class, field by field
 * 
 * 
 * Created by angel on 20/03/14.
 */
public class ReflectionOptionsController<T> extends OptionsController {

	private Class<T> clazz;

	public ReflectionOptionsController(Controller controller, Skin skin,
			Class<T> reflectedClass) {
		super(controller, skin);
		this.clazz = reflectedClass;

		Class clazz = reflectedClass;
		i18nPrefix(ClassReflection.getSimpleName(clazz).toLowerCase());
		// Generate an option for each field
		while (clazz != null) {
			for (Field field : ClassReflection.getDeclaredFields(clazz)) {
				String fieldName = field.getName();
				if (field.getType() == Integer.class
						|| field.getType() == int.class) {
					this.intNumber(fieldName).change(0);
				} else if (field.getType() == Float.class
						|| field.getType() == float.class) {
					floatNumber(fieldName).change(0f);
				} else if (field.getType() == String.class) {
					string(fieldName).change("");
				} else if (field.getType() == Boolean.class
						|| field.getType() == boolean.class) {
					bool(fieldName).change(false);
				} else if (field.getType().isEnum()) {
					Map<String, Object> values = new LinkedHashMap<String, Object>();
					for (Object o : field.getType().getEnumConstants()) {
						values.put(o.toString(), o);
					}
					select(fieldName, values).change(
							values.keySet().iterator().next());
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	public void read(T object) {
		try {
			Class clazz = this.clazz;
			while (clazz != null) {
				for (Field field : ClassReflection.getDeclaredFields(clazz)) {
					field.setAccessible(true);
					String fieldName = field.getName();
					Object value = field.get(object);
					setValue(fieldName, value);
				}
				clazz = clazz.getSuperclass();
			}
		} catch (ReflectionException e) {
			Gdx.app.error("ReflectionOptionsController", "Error", e);
		}
	}

	/**
	 * @return an instance of the object with all the options contained by this
	 *         controller
	 */
	public T newInstance() {
		try {
			Object o = ClassReflection.newInstance(clazz);
			for (Entry<String, Object> e : this.getValues().entrySet()) {
				Field field = ClassReflection.getField(clazz, e.getKey());
				if (field != null) {
					field.set(o, e.getValue());
				} else {
					Gdx.app.error("ReflectionOptionsController", "No field "
							+ e.getKey() + " in class " + clazz);
				}
			}
			return (T) o;
		} catch (ReflectionException e) {
			Gdx.app.error("ReflectionOptionsController",
					"Error creating instance", e);
		}
		return null;
	}
}
