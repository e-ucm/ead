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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.I18N;

import java.util.HashMap;
import java.util.Map;

public class DefaultOptionsPanel extends OptionsPanel {

	private I18N i18N;

	private Class<?> clazz;

	public DefaultOptionsPanel(Controller controller, Class<?> clazz) {
		super(controller, null);
		i18N = controller.getEditorAssets().getI18N();
		this.clazz = clazz;
		addOptions(clazz);
	}

	private void addOptions(Class<?> clazz) {
		String name = ClassReflection.getSimpleName(clazz);
		for (Field f : ClassReflection.getDeclaredFields(clazz)) {
			String label = i18N.m(name + "." + f.getName()) + ": ";
			String fieldName = f.getName();
			if (f.getType() == Float.class || f.getType() == float.class
					|| f.getType() == int.class || f.getType() == Integer.class) {
				number(label, fieldName);
			} else if (f.getType().isEnum()) {
				Object[] values = f.getType().getEnumConstants();
				Map<String, Object> map = new HashMap<String, Object>();
				for (Object v : values) {
					map.put(v.toString(), v);
				}
				values(label, fieldName, map);
			} else if (f.getType() == String.class) {
				string(label, fieldName);
			} else if (f.getType() == boolean.class
					|| f.getType() == Boolean.class) {
				bool(label, fieldName);
			} else {
				options(label, fieldName);
			}
		}
	}

	@Override
	public SubOptionsPanel options(String label, String field) {
		try {
			Field f = ClassReflection.getDeclaredField(clazz, field);
			SubOptionsPanel options = new SubOptionsPanel(controller, label,
					null, field, f.getType());
			addOption(options);
			return options;
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
