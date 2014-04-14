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

import es.eucm.ead.engine.systems.tweens.ActorEntityAccessor;
import es.eucm.ead.schema.components.tweens.ScaleTween;

/**
 * Creates tweens for {@link ScaleTween}
 */
public class ScaleTweenCreator extends TweenCreator<ScaleTween> {

	@Override
	public int getTweenType(ScaleTween scaleTween) {
		if (scaleTween.isRelative()) {
			if (scaleTween.getScaleX() == 0.0f) {
				return ActorEntityAccessor.SCALE_Y;
			} else if (scaleTween.getScaleY() == 0.0f) {
				return ActorEntityAccessor.SCALE_X;
			}
		}
		return ActorEntityAccessor.SCALE;
	}

	@Override
	public float[] getTargets(int type, ScaleTween tween) {
		switch (type) {
		case ActorEntityAccessor.SCALE_X:
			return new float[] { tween.getScaleX() };
		case ActorEntityAccessor.SCALE_Y:
			return new float[] { tween.getScaleY() };
		case ActorEntityAccessor.SCALE:
			return new float[] { tween.getScaleX(), tween.getScaleY() };
		}
		return null;
	}
}
