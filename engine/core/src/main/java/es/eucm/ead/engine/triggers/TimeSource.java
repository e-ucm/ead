/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.triggers;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.behaviors.Time;
import es.eucm.ead.schema.behaviors.Trigger;

/**
 * Source for time triggers
 */
public class TimeSource implements TriggerSource {

	/**
	 * Current time events
	 */
	private Array<TimeController> timeControllers = new Array<TimeController>();

	@Override
	public void act(float delta) {
		for (TimeController controller : timeControllers) {
			controller.remaningTime -= delta;
			if (controller.remaningTime <= 0) {
				controller.actor.process(controller.time);
				if (controller.repeats == 0) {
					timeControllers.removeValue(controller, true);
				} else {
					controller.repeat();
				}
			}
		}
	}

	@Override
	public void registerForTrigger(SceneElementActor actor, Trigger trigger) {
		timeControllers.add(new TimeController(actor, (Time) trigger));
	}

	@Override
	public void unregisterForAllTriggers(SceneElementActor actor) {
		for (TimeController timeController : timeControllers) {
			if (timeController.actor == actor) {
				timeControllers.removeValue(timeController, true);
			}
		}
	}

	/**
	 * Aux class to keep track of time events
	 */
	private class TimeController {
		public Time time;
		public SceneElementActor actor;
		public float remaningTime;
		public int repeats;

		public TimeController(SceneElementActor actor, Time time) {
			this.actor = actor;
			this.time = time;
			remaningTime = time.getTime();
			repeats = time.getRepeat();
		}

		public void repeat() {
			if (repeats > 0) {
				repeats--;
			}
			// remaining time can accumulate negative time, to compensate we add
			// (and not set) the timer time
			remaningTime += time.getTime();
		}
	}
}
