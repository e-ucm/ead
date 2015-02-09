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
package es.eucm.ead.engine.systems.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.engine.components.PathFinderComponent;
import es.eucm.ead.engine.components.WalkComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.paths.PathFinder;
import es.eucm.ead.schema.effects.GoTo;

public class GoToExecutor extends EffectExecutor<GoTo> {

	public GoToExecutor() {
	}

	@Override
	public void execute(Entity owner, GoTo effect) {
		EngineEntity actor = (EngineEntity) owner;
		PathFinderComponent pathFinderComponent = actor
				.getComponent(PathFinderComponent.class);
		PathFinder pathFinder = pathFinderComponent.getPathFinder();
		Vector2 source = new Vector2(actor.getGroup().getX(), actor.getGroup()
				.getY());
		Vector2 target = new Vector2(effect.getX(), effect.getY());
		WalkComponent walk = gameLoop.createComponent(WalkComponent.class);
		walk.initialize(pathFinder, source, target,
				pathFinderComponent.getSpeed(), pathFinder.scaleAt(source));
		actor.add(walk);
	}
}
