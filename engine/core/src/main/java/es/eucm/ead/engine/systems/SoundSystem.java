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
import es.eucm.ead.engine.components.assets.SoundComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.assets.Sound;

/**
 * A system that plays back sounds and music.
 */
public class SoundSystem extends IteratingSystem {

	private boolean initialized = false;

	public VariablesManager variablesManager;

	public SoundSystem(VariablesManager variablesManager) {
		super(Family.all(SoundComponent.class).get());
		this.variablesManager = variablesManager;
	}

	private void checkInitialization() {
		if (!initialized) {
			variablesManager.registerVar(ReservedVariableNames.EFFECTS_VOLUME,
					1F, true);
			initialized = true;
		}
	}

	private float calculateVolume(Sound config) {
		String volumeVar = ReservedVariableNames.EFFECTS_VOLUME;
		return (Float) variablesManager.getValue(volumeVar)
				* config.getVolume();
	}

	@Override
	public void processEntity(Entity entity, float delta) {
		checkInitialization();

		final EngineEntity actor = (EngineEntity) entity;
		SoundComponent soundComponent = actor
				.getComponent(SoundComponent.class);
		if (soundComponent.isLoaded()) {
			Sound config = soundComponent.getConfig();
			if (!soundComponent.isStarted()) {
				soundComponent.play(calculateVolume(config));
			} else if (!soundComponent.isFinished()) {
				soundComponent.changeVolume(calculateVolume(config));
			} else {
				actor.remove(SoundComponent.class);
			}
		}
	}
}
