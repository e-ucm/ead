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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.physics.BoundingAreaComponent;
import es.eucm.ead.engine.components.positiontracking.ChaseEntityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Javier Torrente on 30/06/14.
 */
public class ChaseEntitySystem extends MoveByEntitySystem {

	private static final float CONSTANT = 1.05f;

	public ChaseEntitySystem(GameLoop gameLoop,
			VariablesManager variablesManager) {
		super(Family.getFamilyFor(ChaseEntityComponent.class), gameLoop,
				variablesManager);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		ChaseEntityComponent chaseEntityComponent = entity
				.getComponent(ChaseEntityComponent.class);
		updateTarget(chaseEntityComponent);
		if (chaseEntityComponent.getTrackedEntity() == null) {
			Gdx.app.error(
					"ChaseEntitySystem",
					"Invalid entity target defined for chase component. Entity will not be processed.");
			return;
		}

		/*
		 * Check both entities have a valid bounding area calculated. This
		 * prevents using the component with entities that cannot have bounding
		 * area (e.g. the camera)
		 */
		BoundingAreaComponent chasing = getBoundingArea((EngineEntity) entity);
		BoundingAreaComponent target = getBoundingArea(chaseEntityComponent
				.getTrackedEntity());
		if (!chasing.isInit() || !target.isInit()) {
			Gdx.app.error(
					"ChaseEntitySystem",
					"Either chasing or target entities are not valid (they may be the camera or the hud, for example)");
			return;
		}

		// Calculate distance and move in the appropriate direction, if
		// necessary
		float d = getDistance(chaseEntityComponent, (EngineEntity) entity);
		float dif = d - chaseEntityComponent.getMaxDistance();
		if (dif > 0) {
			moveTowards(chaseEntityComponent, (EngineEntity) entity);
			d = getDistance(chaseEntityComponent, (EngineEntity) entity);
		}
		if (d < chaseEntityComponent.getMinDistance()) {
			moveAwayFrom(chaseEntityComponent, (EngineEntity) entity);
		}

		chaseEntityComponent.rememberPosition();
	}

	private void moveTowards(ChaseEntityComponent chaseEntityComponent,
			EngineEntity entity) {
		/*
		 * Calculate target's velocity. Needed just in case the chase effect has
		 * relative speed configured.
		 */
		Vector2 targetVelocity = Pools.obtain(Vector2.class);
		getSpeedOfTrackedEntity(chaseEntityComponent, targetVelocity);

		// Calculate scalar speed
		float relSpeedX = targetVelocity.x * chaseEntityComponent.getSpeedX();
		float relSpeedY = targetVelocity.y * chaseEntityComponent.getSpeedY();
		Pools.free(targetVelocity);
		boolean isRelative = chaseEntityComponent.isRelativeSpeed()
				&& (!MathUtils.isEqual(relSpeedX, 0, 0.05f) || !MathUtils
						.isEqual(relSpeedY, 0, 0.05f));
		float speedX = Math.abs(isRelative ? relSpeedX : chaseEntityComponent
				.getSpeedX());
		float speedY = Math.abs(isRelative ? relSpeedY : chaseEntityComponent
				.getSpeedY());

		// Move
		move(entity, chaseEntityComponent.getTrackedEntity(), speedX, speedY,
				!chaseEntityComponent.isCenterDistance());
	}

	private void moveAwayFrom(ChaseEntityComponent chaseEntityComponent,
			EngineEntity entity) {
		float d = getDistance(chaseEntityComponent, entity);
		float speedX = (d - chaseEntityComponent.getMinDistance()) * CONSTANT;
		float speedY = speedX;

		move(entity, chaseEntityComponent.getTrackedEntity(), speedX, speedY,
				!chaseEntityComponent.isCenterDistance());
	}

	private float getDistance(ChaseEntityComponent chaseEntityComponent,
			EngineEntity entity) {
		BoundingAreaComponent targetBoundingArea = getBoundingArea(chaseEntityComponent
				.getTrackedEntity());
		BoundingAreaComponent chasingBoundingArea = getBoundingArea(entity);
		return targetBoundingArea.distanceTo(chasingBoundingArea,
				!chaseEntityComponent.isCenterDistance());
	}

	private BoundingAreaComponent getBoundingArea(EngineEntity entity) {
		if (!entity.hasComponent(BoundingAreaComponent.class)) {
			BoundingAreaComponent boundingArea = gameLoop
					.createComponent(BoundingAreaComponent.class);
			boundingArea.set(true);
			entity.add(boundingArea);
			boundingArea.update(entity);

		}
		return entity.getComponent(BoundingAreaComponent.class);
	}

	private void move(EngineEntity chasing, EngineEntity target, float speedX,
			float speedY, boolean fromBorder) {
		/*
		 * Calculate distance vector. It is a vector that has the direction of
		 * distance vector between target and chasing entities, and module
		 * equals to the speed calculated
		 */
		Vector2 distanceVector = Pools.obtain(Vector2.class);
		BoundingAreaComponent targetBoundingArea = getBoundingArea(target);
		BoundingAreaComponent chasingBoundingArea = getBoundingArea(chasing);
		chasingBoundingArea.distanceTo(targetBoundingArea, distanceVector,
				fromBorder);
		float l = distanceVector.len();
		distanceVector.scl(speedX / l, speedY / l);

		// Move
		chasing.getGroup().moveBy(distanceVector.x, distanceVector.y);
		Pools.free(distanceVector);
	}
}
