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

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * Abstract representation of a bounding area for collision purposes. Currently
 * just provides functionality to calculate distances between areas.
 * 
 * Created by Javier Torrente on 5/07/14.
 */
public abstract class AreaWrapper<T extends Shape2D> implements Pool.Poolable {

	/**
	 * Calculates the geometric center of the area and stores it into
	 * {@code center}.
	 */
	protected abstract void getCenter(Vector2 center);

	/**
	 * Intersects this area with the segment {@code start}->{@code end}. The
	 * result is a point in the contour of the area that intersects the segment.
	 * 
	 * @param start
	 *            Initial point of the segment
	 * @param end
	 *            Final point of the segment
	 * @param intersection
	 *            Vector used to store the result of the intersection. If are
	 *            and segment do not intersect, {@code intersection} is not
	 *            updated.
	 * @return True if the area intersects the segment, false otherwise.
	 */
	protected abstract boolean intersectToSegment(Vector2 start, Vector2 end,
			Vector2 intersection);

	/**
	 * @return The inner libgdx's shape used to make calculations (e.g.
	 *         {@link com.badlogic.gdx.math.Rectangle} or
	 *         {@link com.badlogic.gdx.math.Circle}).
	 */
	public abstract T getInnerShape();

	/**
	 * Calculates distance from {@code this area} to {@code end}, stores the
	 * resulting vector in {@code intersection}, and returns the float value of
	 * the distance calculated.
	 * 
	 * @param end
	 *            Area to calculate distance to
	 * @param intersection
	 *            Vector to store distance. If distance cannot be calculated,
	 *            this vector is not updated. Resulting vector points from this
	 *            area to {@code end}.
	 * @param fromBorder
	 *            True if distance is to be calculated between areas' borders,
	 *            false if distance must be calculated between centers. If
	 *            {@code fromBorder} is true, and one of the areas contains the
	 *            other, this method may return {@code -1} if each area contains
	 *            the other's center and {@code intersection} is not updated. If
	 *            {@code fromBorder} is true but each area does not contain the
	 *            other's center (but both areas overlap), distance is
	 *            calculated although it is not representative, and
	 *            {@code intersection} points the other way (from end to start).
	 * @return The float value of the distance calculated, or -1 (see comment
	 *         above).
	 */
	public float getDistance(AreaWrapper end, Vector2 intersection,
			boolean fromBorder) {
		Vector2 c1 = Pools.obtain(Vector2.class);
		Vector2 c2 = Pools.obtain(Vector2.class);
		end.getCenter(c1);
		getCenter(c2);

		if (fromBorder) {
			Vector2 i1 = Pools.obtain(Vector2.class);
			Vector2 i2 = Pools.obtain(Vector2.class);

			boolean intersected = end.intersectToSegment(c1, c2, i1)
					&& intersectToSegment(c1, c2, i2);
			if (!intersected) {
				return -1;
			}

			i1.sub(i2);
			intersection.set(i1);

			Pools.free(i1);
			Pools.free(i2);
		} else {
			intersection.set(c1.sub(c2));
		}
		Pools.free(c1);
		Pools.free(c2);

		return intersection.len();
	}
}
