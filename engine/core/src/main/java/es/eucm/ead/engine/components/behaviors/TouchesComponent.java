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
package es.eucm.ead.engine.components.behaviors;

import ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.schema.data.Condition;
import es.eucm.ead.schema.effects.Effect;

import java.util.List;

/**
 * Component holding all effects that are executed when the owner entity
 * receives a touch
 */
public class TouchesComponent extends Component implements Poolable {

	private Array<RuntimeTouch> touches;

	public TouchesComponent() {
		touches = new Array<RuntimeTouch>();
	}

	public Array<RuntimeTouch> getTouches() {
		return touches;
	}

	@Override
	public void reset() {
		for (RuntimeTouch touch : touches) {
			Pools.free(touch);
		}
		touches.clear();
	}

	/**
	 * Runtime touch. Just a runtime container for
	 * {@link es.eucm.ead.schema.components.behaviors.touches.Touch}
	 */
	public static class RuntimeTouch extends Condition implements Poolable {

		private Array<Effect> effects = new Array<Effect>();

		/**
		 * @return a list with the effects associated to the touch
		 */
		public Array<Effect> getEffects() {
			return effects;
		}

		public void setEffects(List<Effect> effects) {
			this.effects.clear();
			for (Effect effect : effects) {
				this.effects.add(effect);
			}
		}

		@Override
		public void reset() {
			effects.clear();
		}
	}

}
