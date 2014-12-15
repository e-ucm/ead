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

import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.editor.components.animations.MoveAnimation;

public class MoveAnimationConverter extends
		TransformAnimationConverter<MoveAnimation> {

	public static final float MAX_SPEED_TIME_TO_CROSS = 0.25f;
	public static final float MIN_SPEED_TIME_TO_CROSS = 10.0f;

	private Vector2 aux = new Vector2();

	@Override
	public BaseTween convert(MoveAnimation component) {
		float height = Q.getGameHeight();
		float width = Q.getGameWidth();
		float screenSize = Math.max(height, width);
		MoveTween moveTween = new MoveTween();
		super.setEase(component, moveTween);

		moveTween.setRelative(true);

		aux.set(1, 0);
		aux.rotate(component.getDirection());
		aux.scl(screenSize * component.getAmplitude());

		moveTween.setX(aux.x);
		moveTween.setY(aux.y);

		float maxSpeed = screenSize / MAX_SPEED_TIME_TO_CROSS;
		float minSpeed = screenSize / MIN_SPEED_TIME_TO_CROSS;
		float speed = (maxSpeed - minSpeed) * component.getSpeed() + minSpeed;

		moveTween.setDuration(aux.len() / speed);

		return moveTween;
	}
}
