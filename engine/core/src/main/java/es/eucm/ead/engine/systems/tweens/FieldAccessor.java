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
package es.eucm.ead.engine.systems.tweens;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.systems.tweens.FieldAccessor.FieldWrapper;

/**
 * Tween accessor for {@link FieldWrapper}s
 */
public class FieldAccessor implements TweenAccessor<FieldWrapper> {

	@Override
	public int getValues(FieldWrapper target, int tweenType,
			float[] returnValues) {
		try {
			returnValues[0] = target.get();
			return 1;
		} catch (ReflectionException e) {
			Gdx.app.error("FieldAccessor", "Error accessing field.", e);
			return 0;
		}
	}

	@Override
	public void setValues(FieldWrapper target, int tweenType, float[] newValues) {
		try {
			target.set(newValues[0]);
		} catch (ReflectionException e) {
			Gdx.app.error("FieldAccessor", "Error accessing field.", e);
		}
	}

	/**
	 * Wrapper to group an object and a field
	 */
	public static class FieldWrapper {

		private Field field;

		private Object object;

		public FieldWrapper(Field field, Object object) {
			this.field = field;
			this.object = object;
		}

		public float get() throws ReflectionException {
			return (Float) field.get(object);
		}

		public void set(float value) throws ReflectionException {
			field.set(object, value);
		}
	}
}
