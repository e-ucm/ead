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
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.InitializationComponent;
import es.eucm.ead.engine.systems.behaviors.EffectLauncherSystem;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Launches the effects associated to the intialization (load) of an entity.
 * 
 * Created by Javier Torrente on 13/06/14.
 */
public class InitializationSystem extends EffectLauncherSystem {
	public InitializationSystem(GameLoop engine,
			VariablesManager variablesManager) {
		super(engine, variablesManager, Family
				.getFamilyFor(InitializationComponent.class));
	}

	@Override
	public void doProcessEntity(Entity entity, float deltaTime) {
		InitializationComponent component = entity
				.getComponent(InitializationComponent.class);
		addEffects(entity, (component.getEffects()));
		entity.remove(InitializationComponent.class);
	}
}
