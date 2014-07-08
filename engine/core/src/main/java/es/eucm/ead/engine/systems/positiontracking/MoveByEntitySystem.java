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
package es.eucm.ead.engine.systems.positiontracking;

import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.positiontracking.MoveByEntityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Javier Torrente on 3/07/14.
 */
public class MoveByEntitySystem extends IteratingSystem {

	protected VariablesManager variablesManager;

	protected GameLoop gameLoop;

	public MoveByEntitySystem(GameLoop gameLoop,
			VariablesManager variablesManager) {
		this(Family.getFamilyFor(MoveByEntityComponent.class), gameLoop,
				variablesManager);
	}

	public MoveByEntitySystem(Family family, GameLoop gameLoop,
			VariablesManager variablesManager) {
		super(family);
		this.variablesManager = variablesManager;
		this.gameLoop = gameLoop;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		MoveByEntityComponent moveByEntityComponent = entity
				.getComponent(MoveByEntityComponent.class);
		updateTarget(moveByEntityComponent);
		if (moveByEntityComponent.getTrackedEntity() == null) {
			Gdx.app.debug(
					"MoveByEntitySystem",
					"Invalid entity target defined for MoveByEntity component. Entity will not be processed.");
			return;
		}

		// Check target entity actually moved
		Vector2 targetVelocity = Pools.obtain(Vector2.class);
		getSpeedOfTrackedEntity(moveByEntityComponent, targetVelocity);
		if (!MathUtils.isEqual(targetVelocity.len(), 0, 0.1F)) {
			// Calculate how much this entity's position must be adjusted
			float speedX = targetVelocity.x * moveByEntityComponent.getSpeedX();
			float speedY = targetVelocity.y * moveByEntityComponent.getSpeedY();
			((EngineEntity) entity).getGroup().moveBy(speedX, speedY);
		}

		// Update remembered position
		moveByEntityComponent.rememberPosition();
	}

	protected void updateTarget(MoveByEntityComponent moveByEntityComponent) {
		// Reevaluate target
		Object result = variablesManager
				.evaluateExpression(moveByEntityComponent.getTarget());
		EngineEntity trackedEntity = null;

		if (result instanceof EngineEntity) {
			trackedEntity = (EngineEntity) result;
		} else if (result instanceof Array
				&& ((Array) result).first() instanceof EngineEntity) {
			trackedEntity = (EngineEntity) (((Array) result).first());
		}

		if (trackedEntity != moveByEntityComponent.getTrackedEntity()) {
			moveByEntityComponent.updateTarget(trackedEntity);
		}
	}

	protected void getSpeedOfTrackedEntity(MoveByEntityComponent moveByEntity,
			Vector2 trackedEntitySpeed) {
		trackedEntitySpeed.set(
				moveByEntity.getTrackedEntity().getGroup().getX(),
				moveByEntity.getTrackedEntity().getGroup().getY()).sub(
				moveByEntity.getLastX(), moveByEntity.getLastY());
	}

}
