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
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.TouchedComponent;
import es.eucm.ead.engine.components.behaviors.TouchesComponent;
import es.eucm.ead.engine.components.behaviors.events.RuntimeTouch;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.behaviors.events.Touch.Type;

/**
 * Detects entities that are being touched (i.e., with a
 * {@link TouchedComponent}) and launches effects associated, contained in a
 * {@link es.eucm.ead.engine.components.behaviors.TouchesComponent}.
 */
public class TouchBehaviorSystem extends BehaviorSystem {

	public TouchBehaviorSystem(GameLoop gameLoop,
			VariablesManager variablesManager) {
		super(gameLoop, variablesManager, Family.all(TouchedComponent.class,
				TouchesComponent.class).get());
	}

	@Override
	public void doProcessEntity(Entity entity, float delta) {
		TouchedComponent touched = entity.getComponent(TouchedComponent.class);

		TouchesComponent touchInteraction = entity
				.getComponent(TouchesComponent.class);

		for (Type type : touched.getEvents()) {
			for (RuntimeTouch runtimeTouch : touchInteraction.getBehaviors()) {
				if (runtimeTouch.getType() == type) {
					addEffects(entity, runtimeTouch.getEffects());
				}
			}
		}
	}

}
