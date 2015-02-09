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
package es.eucm.ead.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.engine.paths.PathFinder;

/**
 * Adds a walking behavior from one position to another.
 */
public class WalkComponent extends Component {

	private PathFinder.PathIterator pathIterator;

	private float baseStepSize;

	private float oldScale;

	public WalkComponent() {
	}

	/**
	 * initializes a walk following the optimal path from start to finish
	 * 
	 * @param pathFinder
	 *            polygon + projection where paths are to be found
	 * @param start
	 *            point in polygon. If start is not in the polygon, the closest
	 *            inside point will be used.
	 * @param finish
	 *            point in polygon. If finish is not in the polygon, the closest
	 *            inside point will be used.
	 * @param stepSize
	 *            step size used to traverse polygon.
	 * @param oldScale
	 *            initial scale of object at its start location in the
	 *            pathFinder.
	 */
	public void initialize(PathFinder pathFinder, Vector2 start,
			Vector2 finish, float stepSize, float oldScale) {
		this.baseStepSize = stepSize;
		this.pathIterator = pathFinder.findPath(start, finish, baseStepSize);
		this.oldScale = oldScale;
	}

	/**
	 * @return true if there is a start, a finish, and we are not yet at the
	 *         finish. False if there is no walking going on, either because
	 *         there is no start&finish, or because the finish has been reached
	 *         already.
	 */
	public boolean isWalking() {
		return pathIterator != null && pathIterator.hasNext();
	}

	/**
	 * @param delta
	 *            the time between last frame and this frame
	 * @return the next point in the path
	 */
	public Vector2 getNext(float delta) {
		pathIterator.setStepSize(baseStepSize * delta);
		return pathIterator.next();
	}

	public float getOldScale() {
		return oldScale;
	}
}
