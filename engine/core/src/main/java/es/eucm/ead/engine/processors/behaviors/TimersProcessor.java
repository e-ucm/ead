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
package es.eucm.ead.engine.processors.behaviors;

import ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.components.behaviors.TimersComponent;
import es.eucm.ead.engine.components.behaviors.TimersComponent.RuntimeTimer;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.behaviors.timers.Timer;
import es.eucm.ead.schema.components.behaviors.timers.Timers;

/**
 * Converts {@link Timers} model component into a {@link TimersComponent} engine
 * component
 */
public class TimersProcessor extends ComponentProcessor<Timers> {

	public TimersProcessor(PooledEngine engine) {
		super(engine);
	}

	@Override
	public TimersComponent getComponent(Timers component) {
		TimersComponent runtimeTimers = engine
				.createComponent(TimersComponent.class);

		for (Timer timer : component.getTimers()) {
			RuntimeTimer runtimeTimer = Pools.obtain(RuntimeTimer.class);
			runtimeTimer.setCondition(timer.getCondition());
			runtimeTimer.setEffect(timer.getEffects());
			runtimeTimer.setRepeat(timer.getRepeat());
			runtimeTimer.setTime(timer.getTime());

			runtimeTimers.getTimers().add(runtimeTimer);
		}
		return runtimeTimers;
	}
}
