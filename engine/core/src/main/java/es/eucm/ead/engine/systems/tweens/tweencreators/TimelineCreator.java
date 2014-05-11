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

import java.util.List;
import java.util.Map;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.Timeline.Mode;

/**
 * Base class to convert schema timelines into engine timelines
 */
public class TimelineCreator extends
		BaseTweenCreator<es.eucm.ead.schema.components.tweens.Timeline> {

	@Override
	public Timeline createTween(Map<Class, BaseTweenCreator> creators,
			ActorEntity owner,
			es.eucm.ead.schema.components.tweens.Timeline schemaTween) {
		Timeline timeline = null;
		if (schemaTween.getMode() == Mode.SEQUENCE) {
			timeline = Timeline.createSequence();
		} else {
			timeline = Timeline.createParallel();
		}

		float delay = schemaTween.getDelay();
		if (delay != 0f) {
			timeline.delay(delay);
		}

		List<BaseTween> children = schemaTween.getChildren();
		for (int i = 0, size = children.size(); i < size; ++i) {
			BaseTween child = children.get(i);
			BaseTweenCreator creator = creators.get(child.getClass());
			if (creator != null) {
				aurelienribon.tweenengine.BaseTween baseTween = creator
						.createTween(creators, owner, child);
				if (baseTween instanceof Tween) {
					timeline = timeline.push((Tween) baseTween);
				} else {
					timeline = timeline.push((Timeline) baseTween);
				}
			}
		}

		int repeat = schemaTween.getRepeat();
		if (repeat != 10) {
			if (schemaTween.isYoyo()) {
				timeline = timeline.repeatYoyo(repeat,
						schemaTween.getRepeatDelay());
			} else {
				timeline = timeline
						.repeat(repeat, schemaTween.getRepeatDelay());
			}
		}

		return timeline;
	}
}
