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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.indexes.FuzzyIndex;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.schemax.File;
import es.eucm.ead.schemax.Fixed;
import es.eucm.ead.schemax.Search;
import es.eucm.ead.schemax.Text;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An options panel to represent a class, field by field
 * 
 * Created by angel on 20/03/14.
 */
public class ClassOptionsController<T> extends OptionsController {

	private Class<T> clazz;

	protected T object;

	private OptionFieldListener fieldListener = new OptionFieldListener();

	public ClassOptionsController(Controller controller, Skin skin,
			Class<T> clazz) {
		this(controller, skin, clazz, null);
	}

	public T getObjectRepresented() {
		return object;
	}

	public ClassOptionsController(Controller controller, Skin skin,
			Class<T> reflectedClass, Array<String> ignoreFields) {
		super(controller, skin);
		this.clazz = reflectedClass;
		i18nPrefix(ClassReflection.getSimpleName(clazz).toLowerCase());

		Class clazz = reflectedClass;
		while (clazz != null) {
			for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
				String fieldName = field.getName();
				if (ignoreFields != null
						&& ignoreFields.contains(fieldName, false)) {
					continue;
				}
				if (field.isAnnotationPresent(Search.class)) {
					Search search = field.getAnnotation(Search.class);
					try {
						FuzzyIndex index = controller.getIndex(ClassReflection
								.forName(search.index()));
						search(fieldName, index);
					} catch (ReflectionException e) {
						Gdx.app.error("ClassOptionsController", "No class for "
								+ search.index());
					}
				} else if (field.isAnnotationPresent(Text.class)) {
					Text text = field.getAnnotation(Text.class);
					text(fieldName, text.lines());
				} else if (field.isAnnotationPresent(Fixed.class)) {
					fixed(fieldName);
				} else if (field.isAnnotationPresent(File.class)) {
					File file = field.getAnnotation(File.class);
					file(fieldName).folder(file.folder()).mustExist(
							file.mustExist());
				} else if (field.getType() == Integer.class
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

	@Override
	public void setValue(String key, Object value) {
		controller.action(SetField.class, object, key, value);
		super.setValue(key, value);
	}

	/**
	 * Reads the object values and updates all options accordingly
	 */
	public void read(T object) {
		if (this.object != null) {
			controller.getModel().removeListener(this.object, fieldListener);
		}

		this.object = object;
		controller.getModel().addFieldListener(this.object, fieldListener);

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
			Gdx.app.error("ClassOptionsController", "Error", e);
		}
	}

	private class OptionFieldListener implements FieldListener {

		@Override
		public boolean listenToField(String fieldName) {
			return true;
		}

		@Override
		public void modelChanged(FieldEvent event) {
			ClassOptionsController.super.setValue(event.getField(),
					event.getValue());
		}
	}
}
