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

import aurelienribon.tweenengine.BaseTween;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.tweens.*;

/**
 * Created by jtorrente on 02/12/2015.
 */
public class ColorTweenCreator extends BaseTweenCreator<ColorTween> {

	private TimelineCreator timelineCreator;

	public ColorTweenCreator(TimelineCreator creator) {
		this.timelineCreator = creator;
	}

	@Override
	public BaseTween createTween(EngineEntity owner, ColorTween schemaTween) {
		Timeline timeline = new Timeline();
		timeline.setMode(Timeline.Mode.PARALLEL);
		timeline.getChildren().add(createTweenAttribute(schemaTween, "r"));
		timeline.getChildren().add(createTweenAttribute(schemaTween, "g"));
		timeline.getChildren().add(createTweenAttribute(schemaTween, "b"));
		return timelineCreator.createTween(owner, timeline);
	}

	private Tween createTweenAttribute(ColorTween schema, String attribute) {
		Tween tween = null;
		if (attribute.equals("r")) {
			tween = new RedTween();
			((RedTween) tween).setRed(schema.getRed());
		} else if (attribute.equals("b")) {
			tween = new BlueTween();
			((BlueTween) tween).setBlue(schema.getBlue());
		} else if (attribute.equals("g")) {
			tween = new GreenTween();
			((GreenTween) tween).setGreen(schema.getGreen());
		}
		tween.setDelay(schema.getDelay());
		tween.setDuration(schema.getDuration());
		tween.setYoyo(schema.isYoyo());
		tween.setRelative(schema.isRelative());
		tween.setRepeat(schema.getRepeat());
		tween.setRepeatDelay(schema.getRepeatDelay());
		tween.setEaseType(schema.getEaseType());
		tween.setEaseEquation(schema.getEaseEquation());
		tween.setParameters(schema.getParameters());
		return tween;
	}
}
