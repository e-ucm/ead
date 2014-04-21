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

import ashley.core.Entity;
import ashley.core.Family;
import ashley.core.PooledEngine;
import es.eucm.ead.engine.components.VisibilityComponent;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.systems.variables.VariablesSystem;

/**
 * Deals with entities that have conditioned visibility. For each of these
 * entities, it evaluates its condition and updates its visibility accordingly.
 * 
 * Created by Javier Torrente on 17/04/14.
 */
public class VisibilitySystem extends ConditionalSystem {

	public VisibilitySystem(PooledEngine engine, VariablesSystem variablesSystem) {
		super(engine, variablesSystem, Family
				.getFamilyFor(VisibilityComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		VisibilityComponent visibilityComponent = entity
				.getComponent(VisibilityComponent.class);

		if (entity instanceof ActorEntity) {
			boolean condition = evaluateCondition(visibilityComponent
					.getCondition());
			// Change the visibility
			ActorEntity actorEntity = (ActorEntity) entity;
			actorEntity.getGroup().setVisible(condition);
		}
	}
}
