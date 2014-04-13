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
package es.eucm.ead.engine.components;

import ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.ead.schema.effects.Effect;

import java.util.List;

/**
 * Component holding all timers associated to an entity
 */
public class TimersComponent extends Component implements Poolable {

	private Array<RuntimeTimer> timers = new Array<RuntimeTimer>();

	/**
	 * Adds a timer the component
	 * 
	 * @param time
	 *            time for timer (in seconds)
	 * @param repeat
	 *            number of repeats. {@code -1} is interpreted as infinite
	 *            repeats
	 * @param effect
	 *            effects for the timer
	 */
	public void addTimer(float time, int repeat, List<Effect> effect) {
		timers.add(new RuntimeTimer(time, repeat, effect));
	}

	/**
	 * @return the list of the active timers of this component
	 */
	public Array<RuntimeTimer> getTimers() {
		return timers;
	}

	@Override
	public void reset() {
		timers.clear();
	}

	/**
	 * Runtime timer with the necessary logic to update given a delta time
	 */
	public static class RuntimeTimer {

		private float time;

		private int repeat;

		private List<Effect> effect;

		private float remainingTime;

		public RuntimeTimer(float time, int repeat, List<Effect> effect) {
			this.time = time;
			this.repeat = repeat;
			this.effect = effect;
			this.remainingTime = time;
		}

		/**
		 * @return a list with the effects associated to the timer
		 */
		public List<Effect> getEffect() {
			return effect;
		}

		/**
		 * Updates the timer
		 * 
		 * @param delta
		 *            time since last update
		 * @return timer's repetition after processing the given delta
		 */
		public int update(float delta) {
			remainingTime -= delta;

			int count = 0;
			if (remainingTime <= 0) {
				count = (int) (Math.floor(Math.abs(remainingTime) / time)) + 1;

				if (repeat > 0) {
					count = Math.min(repeat, count);
					repeat = Math.max(0, repeat - count);
				}

				for (int i = 0; i < count; i++) {
					remainingTime += time;
				}
			}
			return count;
		}

		/**
		 * @return whether timer has finished (no more repetitions awaiting)
		 */
		public boolean isDone() {
			return repeat == 0;
		}
	}
}
