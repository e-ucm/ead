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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Timeline.Mode;
import es.eucm.ead.schema.editor.components.animations.BlinkAnimation;

public class BlinkAnimationConverter extends
		TransformAnimationConverter<BlinkAnimation> {

	public static final float MAX_SPEED_TIME = 0.25f;
	public static final float MIN_SPEED_TIME = 5f;

	@Override
	public BaseTween convert(BlinkAnimation component) {
		Timeline blinkTween = new Timeline();
		blinkTween.setMode(Mode.SEQUENCE);
		Array<BaseTween> blink = new Array<BaseTween>();

		AlphaTween alphaTween = new AlphaTween();
		alphaTween.setAlpha(component.getStartAlpha());
		alphaTween.setDuration(0f);

		AlphaTween alphaTween2 = new AlphaTween();
		alphaTween2.setAlpha(component.getEndAlpha());
		super.setEase(component, alphaTween2);
		super.setRepeatsAndYoyo(component, blinkTween);

		float speed = (MAX_SPEED_TIME - MIN_SPEED_TIME) * component.getSpeed()
				+ MIN_SPEED_TIME;

		alphaTween2.setDuration((component.getEndAlpha() - component
				.getStartAlpha()) * speed);

		blink.addAll(alphaTween, alphaTween2);
		blinkTween.setChildren(blink);

		return blinkTween;
	}

}
