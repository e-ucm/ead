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
package es.eucm.ead.editor.control.engine.converters;

import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.components.tweens.Tween.EaseEquation;
import es.eucm.ead.schema.components.tweens.Tween.EaseType;
import es.eucm.ead.schema.editor.components.animations.TransformAnimation;

public abstract class TransformAnimationConverter<S extends TransformAnimation>
		implements ComponentConverter<S, BaseTween> {

	protected void setEase(S transform, Tween tween) {
		switch (transform.getEase()) {
		case LINEAR:
			tween.setEaseEquation(EaseEquation.LINEAR);
			break;
		case CUBIC_IN:
			tween.setEaseEquation(EaseEquation.CUBIC);
			tween.setEaseType(EaseType.IN);
			break;
		case CUBIC_OUT:
			tween.setEaseEquation(EaseEquation.CUBIC);
			tween.setEaseType(EaseType.OUT);
			break;
		case CUBIC_IN_OUT:
			tween.setEaseEquation(EaseEquation.CUBIC);
			tween.setEaseType(EaseType.INOUT);
			break;
		case BOUNCE_IN:
			tween.setEaseEquation(EaseEquation.BOUNCE);
			tween.setEaseType(EaseType.IN);
			break;
		case BOUNCE_OUT:
			tween.setEaseEquation(EaseEquation.BOUNCE);
			tween.setEaseType(EaseType.OUT);
			break;
		}
		tween.setYoyo(transform.isYoyo());
		tween.setRepeat(transform.getRepeat());

	}
}
