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
package es.eucm.ead.engine.collision;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Pools;

/**
 * Created by Javier Torrente on 5/07/14.
 */
public class RectangleWrapper extends AreaWrapper<Rectangle> {
	Rectangle rectangle;

	public void set(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	@Override
	protected void getCenter(Vector2 center) {
		rectangle.getCenter(center);
	}

	@Override
	public boolean intersectToSegment(Vector2 start, Vector2 end,
			Vector2 intersection) {
		boolean intersected = false;
		Vector2 v1 = Pools.obtain(Vector2.class);
		Vector2 v2 = Pools.obtain(Vector2.class);
		Polygon rectPol = Pools.obtain(Polygon.class);
		toPolygon(rectPol);
		for (int i = 0; i < 4 && !intersected; i++) {
			v1.set(rectPol.getVertices()[(2 * i) % 8],
					rectPol.getVertices()[(2 * i + 1) % 8]);
			v2.set(rectPol.getVertices()[(2 * i + 2) % 8],
					rectPol.getVertices()[(2 * i + 3) % 8]);
			intersected = Intersector.intersectSegments(v1, v2, start, end,
					intersection);
		}
		Pools.free(v1);
		Pools.free(v2);
		Pools.free(rectPol);
		return intersected;
	}

	@Override
	public Rectangle getInnerShape() {
		return rectangle;
	}

	private void toPolygon(Polygon polygon) {
		if (polygon.getVertices().length != 8) {
			polygon.setVertices(new float[8]);
		}
		polygon.getVertices()[0] = rectangle.x;
		polygon.getVertices()[1] = rectangle.y;
		polygon.getVertices()[2] = rectangle.x + rectangle.width;
		polygon.getVertices()[3] = rectangle.y;
		polygon.getVertices()[4] = rectangle.x + rectangle.width;
		polygon.getVertices()[5] = rectangle.y + rectangle.height;
		polygon.getVertices()[6] = rectangle.x;
		polygon.getVertices()[7] = rectangle.y + rectangle.height;
	}

	@Override
	public void reset() {
		Pools.free(rectangle);
		rectangle = null;
	}
}
