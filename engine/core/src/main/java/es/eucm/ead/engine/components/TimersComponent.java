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

public class TimersComponent extends Component implements Poolable {

	private Array<Timer> timers = new Array<Timer>();

	public void addTimer(Timer timer) {
		timers.add(timer);
	}

	public Array<Timer> getTimers() {
		return timers;
	}

	@Override
	public void reset() {
		timers.clear();
	}

	public static class Timer {

		private List<Effect> effect;

		private float time;

		private float remainingTime;

		private int repeat;

		public void set(float time, int repeat, List<Effect> effect) {
			this.time = time;
			this.repeat = repeat;
			this.effect = effect;
			this.remainingTime = time;
		}

		public List<Effect> getEffect() {
			return effect;
		}

		public float getTime() {
			return time;
		}

		public void setTime(float time) {
			this.time = time;
		}

		public boolean update(float delta) {
			remainingTime -= delta;
			return remainingTime < 0;
		}

		public boolean isDone() {
			return repeat == 0;
		}

		public void repeat() {
			while (remainingTime < 0) {
				remainingTime += time;
				if (repeat > 0) {
					repeat--;
				}
			}
		}
	}
}
