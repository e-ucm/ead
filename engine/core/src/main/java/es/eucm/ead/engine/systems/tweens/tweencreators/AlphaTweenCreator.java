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
package es.eucm.ead.engine.systems.tweens.tweencreators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.systems.tweens.FieldAccessor.FieldWrapper;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.FieldTween;

/**
 * Creates tweens for {@link FieldTween}
 */
public class AlphaTweenCreator extends TweenCreator<AlphaTween> {

	@Override
	public Object getTarget(EngineEntity entity, AlphaTween fieldTween) {
		try {
			Class clazz = Color.class;
			Field field = ClassReflection.getDeclaredField(clazz, "a");
			field.setAccessible(true);
			return new FieldWrapper(field, entity.getGroup().getColor());
		} catch (ReflectionException e) {
			Gdx.app.error("FieldTweenCreator", "Error creating field wrapper",
					e);
		}
		return null;
	}

	@Override
	public int getTweenType(AlphaTween tween) {
		return 0;
	}

	@Override
	public float[] getTargets(int tweenType, AlphaTween tween) {
		return new float[] { tween.getAlpha() };
	}
}
