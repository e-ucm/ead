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
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.physics.GravityComponent;

/**
 * Simple system that retrieves the G constant from a component, that can be
 * defined anywhere but that should ideally be placed as a scene component, and
 * communicates that G to the mass system.
 * 
 * Created by jtorrente on 22/05/2015.
 */
public class GravitySystem extends IteratingSystem {
	protected GameLoop gameLoop;
	protected MassSystem massSystem;

	public GravitySystem(GameLoop gameLoop, MassSystem massSystem) {
		super(Family.all(GravityComponent.class).get());
		this.gameLoop = gameLoop;
		this.massSystem = massSystem;
	}

	public void processEntity(Entity entity, float delta) {
		this.massSystem.setG((entity.getComponent(GravityComponent.class))
				.getG());
	}
}