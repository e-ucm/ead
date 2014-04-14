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
import es.eucm.ead.engine.entities.ActorEntity;

/**
 * Tween accessor for {@link ActorEntity} transformation (position, rotation and
 * scale)
 */
public class ActorEntityAccessor implements TweenAccessor<ActorEntity> {

	/**
	 * Tweens both x and y coordinates
	 */
	public static final int POSITION = 0,
	/**
	 * Tweens rotation
	 */
	ROTATION = 1,
	/**
	 * Tweens both x and y scale
	 */
	SCALE = 2,
	/**
	 * Tweens only x coordinate
	 */
	X = 3,
	/**
	 * Tweens only y coordinate
	 */
	Y = 4,
	/**
	 * Tweens only x scale
	 */
	SCALE_X = 5,
	/**
	 * Tweens only y scale
	 */
	SCALE_Y = 6;

	@Override
	public int getValues(ActorEntity target, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case POSITION:
			returnValues[0] = target.getGroup().getX();
			returnValues[1] = target.getGroup().getY();
			return 2;
		case ROTATION:
			returnValues[0] = target.getGroup().getRotation();
			return 1;
		case SCALE:
			returnValues[0] = target.getGroup().getScaleX();
			returnValues[1] = target.getGroup().getScaleY();
			return 2;
		case X:
			returnValues[0] = target.getGroup().getX();
			return 1;
		case Y:
			returnValues[0] = target.getGroup().getY();
			return 1;
		case SCALE_X:
			returnValues[0] = target.getGroup().getScaleX();
			return 1;
		case SCALE_Y:
			returnValues[0] = target.getGroup().getScaleY();
			return 1;
		}
		return 0;
	}

	@Override
	public void setValues(ActorEntity target, int tweenType, float[] newValues) {
		switch (tweenType) {
		case POSITION:
			target.getGroup().setPosition(newValues[0], newValues[1]);
			break;
		case ROTATION:
			target.getGroup().setRotation(newValues[0]);
			break;
		case SCALE:
			target.getGroup().setScale(newValues[0], newValues[1]);
			break;
		case X:
			target.getGroup().setX(newValues[0]);
			break;
		case Y:
			target.getGroup().setY(newValues[0]);
			break;
		case SCALE_X:
			target.getGroup().setScaleX(newValues[0]);
			break;
		case SCALE_Y:
			target.getGroup().setScaleY(newValues[0]);
			break;
		}
	}
}
