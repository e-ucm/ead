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
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.physics.AngularVelocityComponent;
import es.eucm.ead.engine.entities.EngineEntity;

public class AngularVelocitySystem extends IteratingSystem {

	private GameLoop gameLoop;

	public AngularVelocitySystem(GameLoop gameLoop) {
		super(Family.all(AngularVelocityComponent.class).get());
		this.gameLoop = gameLoop;
	}

	@Override
	public void processEntity(Entity entity, float delta) {
		AngularVelocityComponent velocity = entity
				.getComponent(AngularVelocityComponent.class);
		Group actor = ((EngineEntity) entity).getGroup();
		actor.setRotation(actor.getRotation() + velocity.getOmega() * delta);
	}
}
