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
import com.badlogic.gdx.utils.SnapshotArray;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.behaviors.MovesComponent;
import es.eucm.ead.engine.components.behaviors.PointerPositionComponent;
import es.eucm.ead.engine.components.behaviors.events.RuntimeMove;
import es.eucm.ead.engine.systems.GameEntitySystem;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.behaviors.events.Move;

public class MoveBehaviorSystem extends BehaviorSystem {

	private PointerPositionComponent pointerPositionComponent;

	public MoveBehaviorSystem(GameLoop gameLoop,
			VariablesManager variablesManager) {
		super(gameLoop, variablesManager, Family.all(MovesComponent.class)
				.get());
	}

	private void init() {
		if (!isInit()) {
			pointerPositionComponent = GameEntitySystem.getGameComponent(
					PointerPositionComponent.class, gameLoop);
		}
	}

	private boolean isInit() {
		return pointerPositionComponent != null;
	}

	@Override
	public void update(float deltaTime) {
		init();
		if (isInit() && pointerPositionComponent.hasMoved()) {
			super.update(deltaTime);
		}
	}

	@Override
	public void doProcessEntity(Entity entity, float delta) {
		MovesComponent movesComponent = entity
				.getComponent(MovesComponent.class);
		SnapshotArray<RuntimeMove> movesSnapshotArray = movesComponent
				.getBehaviors();
		for (RuntimeMove move : movesSnapshotArray) {
			Move.Type type = move.getType();
			if (type == Move.Type.ANY
					|| ((type == Move.Type.DRAG) == pointerPositionComponent
							.isDrag())) {
				// Make x and y available as variables
				variablesManager.registerVar(
						ReservedVariableNames.INPUT_EVENT_X,
						pointerPositionComponent.getX(), true);
				variablesManager.registerVar(
						ReservedVariableNames.INPUT_EVENT_Y,
						pointerPositionComponent.getY(), true);
				variablesManager.registerVar(
						ReservedVariableNames.INPUT_EVENT_MOVE_DELTA_X,
						pointerPositionComponent.getDeltaX(), true);
				variablesManager.registerVar(
						ReservedVariableNames.INPUT_EVENT_MOVE_DELTA_Y,
						pointerPositionComponent.getDeltaY(), true);
				variablesManager.registerVar(
						ReservedVariableNames.INPUT_EVENT_INITIAL_X,
						pointerPositionComponent.getInitialX(), true);
				variablesManager.registerVar(
						ReservedVariableNames.INPUT_EVENT_INITIAL_Y,
						pointerPositionComponent.getInitialY(), true);
				addEffects(entity, move.getEffects());
			}
		}
	}

}
