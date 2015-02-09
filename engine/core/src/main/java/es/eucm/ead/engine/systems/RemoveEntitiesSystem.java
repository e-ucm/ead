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
package es.eucm.ead.engine.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.RemoveEntityComponent;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;

/**
 * Removes entities that have been marked for removal.
 * 
 * Entities cannot be removed directly since that may interfere with any
 * processing loops.
 * 
 * Created by Javier Torrente on 31/05/14.
 */
public class RemoveEntitiesSystem extends IteratingSystem {

	private GameLoop gameLoop;

	private VariablesManager variablesManager;

	public RemoveEntitiesSystem(GameLoop gameLoop,
			VariablesManager variablesManager) {
		super(Family.all(RemoveEntityComponent.class).get());
		this.gameLoop = gameLoop;
		this.variablesManager = variablesManager;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		// Just remove the entity and check if "newest" reserved var has to be
		// set to null
		gameLoop.removeEntity(entity);
		if (variablesManager.getValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR) == entity) {
			variablesManager.globalNewestEntityVar(null);
		}
	}
}
