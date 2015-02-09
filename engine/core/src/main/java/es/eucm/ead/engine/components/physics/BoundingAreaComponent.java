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
package es.eucm.ead.engine.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.collision.AreaWrapper;
import es.eucm.ead.engine.collision.BoundingAreaBuilder;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.physics.BoundingArea;

/**
 * See {@link BoundingArea} for more details.
 * 
 * Created by Javier Torrente on 3/07/14.
 */
public class BoundingAreaComponent extends Component {

	// If true, boundingRect is used. Otherwise boundingCircle is used.
	// boundingRect and boundingCircle are always expressed in scene_content
	// coordinates.
	private boolean rect = true;
	private AreaWrapper area;

	/**
	 * Calculates the {@code float} distance to another bounding area.
	 * 
	 * @param anotherArea
	 *            Another bounding area
	 * @param fromBorder
	 *            If true, distance is calculated between the borders, otherwise
	 *            they are calculated between their centers.
	 * @return A positive or zero float representing the distance between this
	 *         bounding area and the given one, or a negative float if
	 *         {@code fromBorder} is true and one area contains the other.
	 */
	public float distanceTo(BoundingAreaComponent anotherArea,
			boolean fromBorder) {
		Vector2 intersection = Pools.obtain(Vector2.class);
		float d = distanceTo(anotherArea, intersection, fromBorder);
		Pools.free(intersection);
		return d;
	}

	/**
	 * Calculates the {@code float} distance to another bounding area. This
	 * method not only provides the float value for the distance, but also the
	 * resulting vector in {@code distanceVector}.
	 * 
	 * @param anotherArea
	 *            Another bounding area
	 * @param distanceVector
	 *            Used to store the direction vector calculated. The resulting
	 *            vector points from this area to {@code anotherArea}. Under
	 *            some circumstances {@distanceVector} may not get updated (see
	 *            {@link es.eucm.ead.engine.collision.BoundingAreaBuilder} for
	 *            more details).
	 * @param fromBorder
	 *            If true, distance is calculated between the borders, otherwise
	 *            they are calculated between their centers.
	 * @return A positive or zero float representing the distance between this
	 *         bounding area and the given one, or a negative float if
	 *         {@code fromBorder} is true and one area contains the other.
	 */
	public float distanceTo(BoundingAreaComponent anotherArea,
			Vector2 distanceVector, boolean fromBorder) {
		return area.getDistance(anotherArea.area, distanceVector, fromBorder);
	}

	/**
	 * Initializes the component to calculate rectangles if {@code rect} is true
	 * or circles if {@code rect} is false
	 */
	public void set(boolean rect) {
		this.rect = rect;
	}

	/**
	 * @return true if an area has already been calculated
	 */
	public boolean isInit() {
		return area != null;
	}

	/**
	 * Recalculates the bounding area for the given {@code entity}, which most
	 * frequently will turn out this component's parent. Should be invoked each
	 * time entity's position, size or shape changes.
	 */
	public void update(EngineEntity entity) {
		if (area != null) {
			Pools.free(area);
		}
		area = rect ? BoundingAreaBuilder.getBoundingRectangle(entity)
				: BoundingAreaBuilder.getBoundingCircle(entity);
	}
}
