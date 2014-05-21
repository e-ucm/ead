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
package es.eucm.ead.engine;

import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.mock.MockEngineComponent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by angel on 30/04/14.
 */
public class GameLoopTest {

	private GameLoop gameLoop;

	@Before
	public void setUp() {
		gameLoop = new GameLoop();
	}

	@Test
	public void testEntitiesArePooled() {
		EngineEntity engineEntity = gameLoop.createEntity();
		EngineEntity child = gameLoop.createEntity();
		engineEntity.getGroup().addActor(child.getGroup());
		gameLoop.removeEntity(engineEntity);
		assertEquals(gameLoop.getEntityPool().getFree(), 2);
		EngineEntity other = gameLoop.createEntity();
		assertEquals(gameLoop.getEntityPool().getFree(), 1);
		EngineEntity other2 = gameLoop.createEntity();
		assertEquals(gameLoop.getEntityPool().getFree(), 0);
		gameLoop.removeEntity(other);
		assertEquals(gameLoop.getEntityPool().getFree(), 1);
		gameLoop.removeEntity(other2);
		assertEquals(gameLoop.getEntityPool().getFree(), 2);
	}

	@Test
	public void testComponentsArePooled() {
		MockEngineComponent.instances = 0;

		for (int i = 0; i < 50; i++) {
			EngineEntity entity = gameLoop.createEntity();
			MockEngineComponent component = gameLoop
					.createComponent(MockEngineComponent.class);
			entity.add(component);
			gameLoop.removeEntity(entity);
		}

		assertEquals(MockEngineComponent.instances, 1);
		assertEquals(gameLoop.getEntityPool().getFree(), 1);

		EngineEntity parent = gameLoop.createEntity();
		EngineEntity child = gameLoop.createEntity();
		parent.add(gameLoop.createComponent(MockEngineComponent.class));
		child.add(gameLoop.createComponent(MockEngineComponent.class));

		parent.getGroup().addActor(child.getGroup());

		gameLoop.removeEntity(parent);
		assertEquals(MockEngineComponent.instances, 2);

	}
}
