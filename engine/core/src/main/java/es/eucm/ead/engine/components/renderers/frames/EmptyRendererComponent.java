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
package es.eucm.ead.engine.components.renderers.frames;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.components.renderers.CollidableRendererComponent;

/**
 * Created by Javier Torrente on 4/06/14.
 */
public class EmptyRendererComponent extends CollidableRendererComponent {

	protected float width;

	protected float height;

	private boolean hitAll;

	@Override
	public void draw(Batch batch) {
		// Do nothing
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public void setCollider(Array<Polygon> collider) {
		super.setCollider(collider);
		updateWidthAndHeight();
	}

	public void setHitAll(boolean hitAll) {
		this.hitAll = hitAll;
	}

	private void updateWidthAndHeight() {
		if (collider != null) {
			float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE, minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
			for (Polygon polygon : collider) {
				for (int i = 0; i < polygon.getVertices().length; i++) {
					if (i % 2 == 0) {
						minX = Math.min(minX, polygon.getVertices()[i]);
						maxX = Math.max(maxX, polygon.getVertices()[i]);
					} else {
						minY = Math.min(minY, polygon.getVertices()[i]);
						maxY = Math.max(maxY, polygon.getVertices()[i]);
					}
				}
			}
			width = maxX - minX;
			height = maxY - minY;
		}
	}

	@Override
	public boolean hit(float x, float y) {
		return hitAll || super.hit(x, y);
	}
}
