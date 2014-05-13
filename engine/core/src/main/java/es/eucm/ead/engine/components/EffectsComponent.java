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
package es.eucm.ead.engine.components;

import ashley.core.Component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.ead.schema.effects.Effect;

public class EffectsComponent extends Component implements Poolable {

	private Array<String> argumentNames;

	private Array<Object> argumentValues;

	private Array<Effect> effectList;

	public EffectsComponent() {
		effectList = new Array<Effect>();
		argumentNames = new Array<String>();
		argumentValues = new Array<Object>();
	}

	public Array<Effect> getEffectList() {
		return effectList;
	}

	@Override
	public void reset() {
		effectList.clear();
		argumentNames.clear();
		argumentValues.clear();
	}

	public void addArgument(String name, String initialValue) {
		argumentNames.add(name);
		argumentValues.add(parseInitialValue(initialValue));
	}

	public void setArgumentValues(Object... values) {
		if (values.length != argumentValues.size) {
			Gdx.app.debug("InitEntityComponent",
					"The number of arguments passed (" + values.length
							+ ") does not match the expected ("
							+ argumentValues.size + ") for this entity ");
		}

		for (int i = 0; i < Math.min(values.length, argumentValues.size); i++) {
			argumentValues.set(i, values[i]);
		}
	}

	public Array<String> getArgumentNames() {
		return argumentNames;
	}

	public Array<Object> getArgumentValues() {
		return argumentValues;
	}

	/**
	 * Parses a string initialValue to its real type (Integer, Float, Boolean or
	 * String)
	 */
	private static Object parseInitialValue(String initialValue) {
		if (initialValue == null || initialValue.length() == 0)
			return null;

		if ("true".equals(initialValue.toLowerCase())
				|| "false".equals(initialValue.toLowerCase())) {
			return Boolean.parseBoolean(initialValue);
		}

		try {
			return Integer.parseInt(initialValue);
		} catch (NumberFormatException e) {
		}

		try {
			return Float.parseFloat(initialValue);
		} catch (NumberFormatException e) {
		}

		return initialValue;
	}

}
