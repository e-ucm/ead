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
package es.eucm.ead.engine.tests.systems.behaviors;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.TouchedComponent;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffect.MockEffectListener;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.processors.behaviors.TouchesProcessor;
import es.eucm.ead.engine.systems.behaviors.TouchSystem;
import es.eucm.ead.schema.components.behaviors.touches.Touch;
import es.eucm.ead.schema.components.behaviors.touches.Touches;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TouchesTest extends BehaviorTest implements MockEffectListener {

	private int executed;

	@Override
	protected void registerComponentProcessors(GameLoop gameLoop,
			Map<Class, ComponentProcessor> componentProcessors) {
		componentProcessors.put(Touches.class, new TouchesProcessor(gameLoop));
	}

	public void addSystems(GameLoop gameLoop) {
		gameLoop.addSystem(new TouchSystem(gameLoop));
	}

	@Test
	public void test() {
		executed = 0;

		ModelEntity modelEntity = new ModelEntity();

		Touch touch = new Touch();
		touch.getEffects().add(new MockEffect(this));

		Touches touches = new Touches();
		touches.getTouches().add(touch);

		modelEntity.getComponents().add(touches);

		ActorEntity entity = addEntity(modelEntity);

		TouchedComponent touched = new TouchedComponent();
		touched.touch();

		entity.add(touched);

		gameLoop.update(0);
		assertTrue("Effect wasn't executed", executed == 1);
		gameLoop.update(0);
		assertTrue("Effect executed again. It shouldn't be executed",
				executed == 1);
	}

	@Override
	public void executed() {
		executed++;
	}
}
