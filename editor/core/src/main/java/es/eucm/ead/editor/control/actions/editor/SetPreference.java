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
package es.eucm.ead.editor.control.actions.editor;

import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.EditorAction;

/**
 * <p>
 * Sets the value for a preference.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> The preference key</dd>
 * <dd><strong>args[1]</strong> <em>Object</em> The preference value. Must be a
 * a simple type {@link Integer}, {@link Float}, {@link String}, {@link Boolean}
 * </dd>
 * </dl>
 */
public class SetPreference extends EditorAction {

	public SetPreference() {
		super(true, false, String.class, Object.class);
	}

	@Override
	public boolean validate(Object... args) {
		if (super.validate(args)) {
			Object value = args[1];
			return value instanceof Integer || value instanceof Float
					|| value instanceof Boolean || value instanceof String;
		}
		return false;
	}

	@Override
	public void perform(Object... args) {
		Preferences preferences = controller.getPreferences();
		String key = (String) args[0];
		Object value = args[1];

		if (value instanceof Integer) {
			preferences.putInteger(key, (Integer) value);
		} else if (value instanceof Float) {
			preferences.putFloat(key, (Float) value);
		} else if (value instanceof Boolean) {
			preferences.putBoolean(key, (Boolean) value);
		} else if (value instanceof String) {
			preferences.putString(key, (String) value);
		}
	}
}
