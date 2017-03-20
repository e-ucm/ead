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
import es.eucm.ead.engine.components.KeyPressedComponent;
import es.eucm.ead.engine.components.behaviors.KeysComponent;
import es.eucm.ead.engine.components.behaviors.events.RuntimeKey;
import es.eucm.ead.engine.systems.GameEntitySystem;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Subhaschandra on 04/06/2014.
 */

public class KeyBehaviorSystem extends BehaviorSystem {

	private KeyPressedComponent gameComponent;

	public KeyBehaviorSystem(GameLoop engine, VariablesManager variablesSystem) {
		super(engine, variablesSystem, Family.all(KeysComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		init();
		if (isInit()) {
			super.update(deltaTime);
		}
	}

	protected boolean isInit() {
		return gameComponent != null;
	}

	protected void init() {
		if (isInit()) {
			return;
		}
		gameComponent = GameEntitySystem.getGameComponent(
				KeyPressedComponent.class, gameLoop);
	}

	@Override
	public void doProcessEntity(Entity entity, float delta) {
		/*
		 * KeyPressedComponent pressed = entity
		 * .getComponent(KeyPressedComponent.class);
		 */

		KeysComponent keysComponent = entity
				.getComponent((KeysComponent.class));
		for (RuntimeKey keyEvent : gameComponent.getKeyEvents()) {
			for (RuntimeKey runtimeKeys : keysComponent.getBehaviors()) {
				if (keyEvent.compareEvents(runtimeKeys)) {
					addEffects(entity, runtimeKeys.getEffects());
				}
			}
		}
	}

}
