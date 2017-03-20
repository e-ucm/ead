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
package es.eucm.ead.engine.systems.behaviors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.collision.BoundingAreaBuilder;
import es.eucm.ead.engine.components.behaviors.CollisionComponent;
import es.eucm.ead.engine.components.behaviors.events.RuntimeCollision;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.engine.variables.VariablesManager;
import com.badlogic.gdx.math.Polygon;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;

/**
 * Created by jtorrente on 19/11/2015.
 */
public class CollisionSystem extends BehaviorSystem {
	public CollisionSystem(GameLoop engine, VariablesManager variablesManager) {
		super(engine, variablesManager, Family.all(CollisionComponent.class)
				.get());
	}

	@Override
	public void doProcessEntity(Entity entity, float deltaTime) {
		Polygon polygon1 = BoundingAreaBuilder
				.getBoundingPolygon((EngineEntity) entity);
		CollisionComponent collisionComponent = entity
				.getComponent(CollisionComponent.class);
		SnapshotArray<RuntimeCollision> runtimeCollisions = collisionComponent
				.getBehaviors();
		Object[] runtimes = runtimeCollisions.begin();
		for (int i = 0; i < runtimeCollisions.size; i++) {
			RuntimeCollision runtime = (RuntimeCollision) runtimes[i];
			runtime.updateTargets(variablesManager);
			runtime.updateBoundingAreas(gameLoop);
			for (EngineEntity potentialTarget : runtime.getPotentialTargets()) {
				Polygon polygon2 = BoundingAreaBuilder
						.getBoundingPolygon(potentialTarget);
				if (Intersector.overlapConvexPolygons(polygon1, polygon2)) {
					ChangeVar targetEntity = new ChangeVar();
					targetEntity.setContext(ChangeVar.Context.LOCAL);
					targetEntity.setExpression("(fromid s"
							+ potentialTarget.getId() + ")");
					targetEntity
							.setVariable(ReservedVariableNames.COLLISION_TARGET);
					Array<Effect> additionalEffect = new Array<Effect>();
					additionalEffect.add(targetEntity);
					addEffects(entity, additionalEffect);
					addEffects(entity, runtime.getEffects());
					break;
				}
			}
		}
		runtimeCollisions.end();
	}
}
