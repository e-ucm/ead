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
package es.eucm.ead.engine.tests.systems;

import es.eucm.ead.engine.DefaultEngineInitializer;
import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.components.physics.VelocityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.physics.Acceleration;
import es.eucm.ead.schema.components.physics.Gravity;
import es.eucm.ead.schema.components.physics.Mass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the next systems: {@link es.eucm.ead.engine.systems.VelocitySystem},
 * {@link es.eucm.ead.engine.systems.GravitySystem},
 * {@link es.eucm.ead.engine.systems.MassSystem} and
 * {@link es.eucm.ead.engine.systems.AccelerationSystem}
 * 
 * Created by jtorrente on 22/05/2015.
 */
public class SimplePhysicsTest extends EngineTest {

	@Override
	public void prepareEngine() {
		new DefaultEngineInitializer().init(gameAssets, gameLoop,
				entitiesLoader, gameView, variablesManager,
				persistentGameStateSystem, gleanerSystem);
	}

	@Override
	public void doBuild() {
		Gravity gravity = new Gravity();
		gravity.setG(2000);
		game(200, 200).scene().getLastScene().getComponents().add(gravity);
		entity(0, 0).emptyRectangle(10, 10, true).name("obj").getLastEntity()
				.getComponents().add(new Mass());
		Acceleration acceleration = new Acceleration();
		acceleration.setX(1000);
		acceleration.setY(1000);
		getLastEntity().getComponents().add(acceleration);
	}

	@Test
	public void test() {
		EngineEntity entity = entityAtPosition(1, 1);
		float elapsedTime = 0;
		float updateTime = 1.0F / 60F;
		while (elapsedTime <= 10) {
			step(entity, elapsedTime);
			update(updateTime);
			elapsedTime += updateTime;
		}
	}

	private void step(EngineEntity entity, float elapsedTime) {
		VelocityComponent velocityComponent = entity
				.getComponent(VelocityComponent.class);
		float vx = 0, vy = 0, x = entity.getGroup().getX(), y = entity
				.getGroup().getY();
		if (elapsedTime == 0) {
			assertNull(velocityComponent);
		} else {
			vx = velocityComponent.getX();
			vy = velocityComponent.getY();
		}

		float expX = 500F * elapsedTime * elapsedTime;
		float expY = -500F * elapsedTime * elapsedTime;
		float expVx = 1000F * elapsedTime;
		float expVy = -1000F * elapsedTime;
		assertEquals(expX, x, 90F);
		assertEquals(expY, y, 90F);
		assertEquals(expVx, vx, 10F);
		assertEquals(expVy, vy, 10F);

		System.out.println(x + "," + y + "   " + vx + "," + vy);
	}

}
