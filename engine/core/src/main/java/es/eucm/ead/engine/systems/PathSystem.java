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
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.engine.WalkComponent;
import es.eucm.ead.engine.components.PathFinderComponent;
import es.eucm.ead.engine.entities.ActorEntity;

/**
 * A system for entities that are walking a path.
 */
public class PathSystem extends IteratingSystem {

	public PathSystem() {
		super(Family.getFamilyFor(WalkComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float delta) {
		ActorEntity actor = (ActorEntity) entity;
		WalkComponent walk = actor.getComponent(WalkComponent.class);
		PathFinderComponent pathFinder = actor
				.getComponent(PathFinderComponent.class);

		if (walk.isWalking()) {
			Vector2 next = walk.getNext(delta);
			float scale = walk.getOldScale()
					/ pathFinder.getPathFinder().scaleAt(next);
			actor.getGroup().setPosition(next.x, next.y);
			actor.getGroup().setScale(scale);
		} else {
			actor.remove(WalkComponent.class);
		}
	}
}
