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

import java.util.Map;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenEquations;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.schema.components.tweens.Tween.EaseEquation;
import es.eucm.ead.schema.components.tweens.Tween.EaseType;

/**
 * Base class to convert schema tweens into engine tweens
 */
public abstract class TweenCreator<T extends es.eucm.ead.schema.components.tweens.Tween>
		extends BaseTweenCreator<T> {

	/**
	 * @return the type for the tween
	 */
	public abstract int getTweenType(T tween);

	/**
	 * @return the target values for the tween
	 */
	public abstract float[] getTargets(int tweenType, T tween);

	/**
	 * @return the object target of the tween
	 */
	public Object getTarget(ActorEntity entity, T t) {
		return entity.getGroup();
	}

	/**
	 * Creates a tween engine object from a tween schema object
	 * 
	 * @param owner
	 *            owner of the tween
	 * @param schemaTween
	 *            the tween schema object
	 * @return the create tween engine object
	 */
	@Override
	public Tween createTween(Map<Class, BaseTweenCreator> tweenCreators,
			ActorEntity owner, T schemaTween) {

		int tweenType = getTweenType(schemaTween);

		Tween tween = Tween.to(getTarget(owner, schemaTween), tweenType,
				schemaTween.getDuration());

		float delay = schemaTween.getDelay();
		if (delay != 0f) {
			tween.delay(delay);
		}

		tween.ease(getTweenEquation(schemaTween.getEaseEquation(),
				schemaTween.getEaseType()));

		if (schemaTween.isRelative()) {
			tween.targetRelative(getTargets(tweenType, schemaTween));
		} else {
			tween.target(getTargets(tweenType, schemaTween));
		}

		int repeat = schemaTween.getRepeat();
		if (repeat != 0) {
			if (schemaTween.isYoyo()) {
				tween.repeatYoyo(repeat, schemaTween.getRepeatDelay());
			} else {
				tween.repeat(repeat, schemaTween.getRepeatDelay());
			}
		}

		return tween;
	}

	/**
	 * @return tween equation for the given parameters
	 */
	private TweenEquation getTweenEquation(EaseEquation easeEquation,
			EaseType easeType) {
		TweenEquation equation;
		int type = easeType == EaseType.IN ? 0 : easeType == EaseType.OUT ? 1
				: 2;
		switch (easeEquation) {
		case LINEAR:
			equation = TweenEquations.easeNone;
			break;
		case QUAD:
			equation = type == 0 ? TweenEquations.easeInQuad
					: type == 1 ? TweenEquations.easeOutQuad
							: TweenEquations.easeInOutQuad;
			break;

		case CUBIC:
			equation = type == 0 ? TweenEquations.easeInCubic
					: type == 1 ? TweenEquations.easeOutCubic
							: TweenEquations.easeInOutCubic;
			break;
		case QUART:
			equation = type == 0 ? TweenEquations.easeInQuart
					: type == 1 ? TweenEquations.easeOutQuart
							: TweenEquations.easeInOutQuart;
			break;
		case QUINT:
			equation = type == 0 ? TweenEquations.easeInQuint
					: type == 1 ? TweenEquations.easeOutQuint
							: TweenEquations.easeInOutQuint;
			break;
		case CIRC:
			equation = type == 0 ? TweenEquations.easeInCirc
					: type == 1 ? TweenEquations.easeOutCirc
							: TweenEquations.easeInOutCirc;
			break;
		case SINE:
			equation = type == 0 ? TweenEquations.easeInSine
					: type == 1 ? TweenEquations.easeOutSine
							: TweenEquations.easeInOutSine;
			break;
		case EXPO:
			equation = type == 0 ? TweenEquations.easeInExpo
					: type == 1 ? TweenEquations.easeOutExpo
							: TweenEquations.easeInOutExpo;
			break;
		case BACK:
			equation = type == 0 ? TweenEquations.easeInBack
					: type == 1 ? TweenEquations.easeOutBack
							: TweenEquations.easeInOutBack;
			break;
		case BOUNCE:
			equation = type == 0 ? TweenEquations.easeInBounce
					: type == 1 ? TweenEquations.easeOutBounce
							: TweenEquations.easeInOutBounce;
			break;
		case ELASTIC:
			equation = type == 0 ? TweenEquations.easeInElastic
					: type == 1 ? TweenEquations.easeOutElastic
							: TweenEquations.easeInOutElastic;
			break;
		default:
			equation = TweenEquations.easeNone;
		}
		return equation;
	}
}
