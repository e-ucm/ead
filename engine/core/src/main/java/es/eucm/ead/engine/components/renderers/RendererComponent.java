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
package es.eucm.ead.engine.components.renderers;

import ashley.core.Component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Base class for renderer components
 */
public abstract class RendererComponent extends Component {

	protected Actor parent;

	public void setParent(Actor parent) {
		this.parent = parent;
	}

	/**
	 * Updates the renderer
	 * 
	 * @param delta
	 *            time since last update
	 */
	public void act(float delta) {

	}

	/**
	 * Draws the renderer
	 */
	public abstract void draw(Batch batch);

	/**
	 * @return current width of the renderer
	 */
	public abstract float getWidth();

	/**
	 * @return current height of the renderer
	 */
	public abstract float getHeight();

	/**
	 * @return the polygons representing this renderer contour, used for hits
	 *         checking
	 */
	public abstract Array<Polygon> getCollider();

	/**
	 * @param x
	 *            coordinate in the renderer system
	 * @param y
	 *            coordinate in the renderer system
	 * @return if the given point hits the renderer
	 */
	public boolean hit(float x, float y) {
		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
			Array<Polygon> collider = getCollider();
			if (collider != null && collider.size > 0) {
				int polygonsHit = 0;
				for (Polygon p : collider) {
					if (p.contains(x, y)) {
						polygonsHit++;
					}
				}
				return polygonsHit % 2 == 1;
			} else {
				return true;
			}
		}
		return false;
	}
}
