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
package es.eucm.ead.engine.tests.systems.effects;

import ashley.core.Entity;
import ashley.core.EntityListener;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.systems.effects.RemoveEntityExecutor;
import es.eucm.ead.schema.effects.RemoveEntity;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by angel on 30/04/14.
 */
public class RemoveEntityTest {

	private boolean removed;

	@Test
	public void testRemoveEntity() {
		removed = false;
		GameLoop gameLoop = new GameLoop();
		RemoveEntityExecutor executor = new RemoveEntityExecutor();
		executor.initialize(gameLoop);

		final ActorEntity actorEntity = gameLoop.createEntity();
		gameLoop.addEntityListener(new EntityListener() {
			@Override
			public void entityAdded(Entity entity) {
			}

			@Override
			public void entityRemoved(Entity entity) {
				removed = entity == actorEntity;
			}
		});
		executor.execute(actorEntity, new RemoveEntity());
		assertTrue(removed);
	}
}
