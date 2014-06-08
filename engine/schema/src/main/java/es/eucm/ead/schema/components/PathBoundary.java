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

package es.eucm.ead.schema.components;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.data.shape.Polygon;

@Generated("org.jsonschema2pojo")
public class PathBoundary extends ModelComponent {

	/**
	 * Polygons within which objects can find paths. The first polygon is the
	 * outside boundary, the rest are disjoint holes.
	 * 
	 */
	private List<Polygon> boundaryPolygons = new ArrayList<Polygon>();
	/**
	 * A simple polygon representation. Points are stored as consecutive x and y
	 * coordinates, i.e., [x0, y0, x1, y1, x2, y2, ...].
	 * 
	 */
	private Polygon viewSquare;
	/**
	 * Speed of objects when 'walking' paths
	 * 
	 */
	private float speed = 5.0F;

	/**
	 * Polygons within which objects can find paths. The first polygon is the
	 * outside boundary, the rest are disjoint holes.
	 * 
	 */
	public List<Polygon> getBoundaryPolygons() {
		return boundaryPolygons;
	}

	/**
	 * Polygons within which objects can find paths. The first polygon is the
	 * outside boundary, the rest are disjoint holes.
	 * 
	 */
	public void setBoundaryPolygons(List<Polygon> boundaryPolygons) {
		this.boundaryPolygons = boundaryPolygons;
	}

	/**
	 * A simple polygon representation. Points are stored as consecutive x and y
	 * coordinates, i.e., [x0, y0, x1, y1, x2, y2, ...].
	 * 
	 */
	public Polygon getViewSquare() {
		return viewSquare;
	}

	/**
	 * A simple polygon representation. Points are stored as consecutive x and y
	 * coordinates, i.e., [x0, y0, x1, y1, x2, y2, ...].
	 * 
	 */
	public void setViewSquare(Polygon viewSquare) {
		this.viewSquare = viewSquare;
	}

	/**
	 * Speed of objects when 'walking' paths
	 * 
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Speed of objects when 'walking' paths
	 * 
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
