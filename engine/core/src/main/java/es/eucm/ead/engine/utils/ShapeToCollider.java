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
package es.eucm.ead.engine.utils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.data.shape.Shape;

/**
 * Provides a polygon that contains the given shape. Useful for creating
 * colliders.
 * 
 * Created by Javier Torrente on 8/06/14.
 */
public class ShapeToCollider {

	/**
	 * Returns a bounding polygon for the given shape that can be used as a
	 * collider.
	 * 
	 * @param shape
	 *            The shape the polygon must approximate to.
	 * @param nSides
	 *            The number of sides the polygon must have. Only used for
	 *            {@link Circle}s, ignored for {@link Rectangle}s and
	 *            {@link es.eucm.ead.schema.data.shape.Polygon}s.
	 * @return A runtime polygon (libgdx)
	 */
	public static Polygon buildShapeCollider(Shape shape, int nSides) {
		if (shape instanceof Rectangle) {
			return buildRectangleCollider((Rectangle) shape);
		} else if (shape instanceof es.eucm.ead.schema.data.shape.Polygon) {
			return buildPolygonCollider((es.eucm.ead.schema.data.shape.Polygon) shape);
		} else if (shape instanceof Circle) {
			return buildCircleCollider((Circle) shape, nSides);
		} else
			return null;
	}

	/**
	 * @return A simple runtime polygon (libgdx) for the given {@code rectangle}
	 *         that can be used as a collider.
	 */
	public static Polygon buildRectangleCollider(Rectangle rectangle) {
		Polygon polygon = new Polygon();
		float[] vertices = new float[8]; // four vertex
		vertices[0] = 0;
		vertices[1] = 0;
		vertices[2] = rectangle.getWidth();
		vertices[3] = 0;
		vertices[4] = rectangle.getWidth();
		vertices[5] = rectangle.getHeight();
		vertices[6] = 0;
		vertices[7] = rectangle.getHeight();
		polygon.setVertices(vertices);
		return polygon;
	}

	/**
	 * @return A runtime polygon (libgdx) equivalent to the schema polygon
	 *         provided as argument. The returning polygon can be used as a
	 *         collider
	 */
	public static Polygon buildPolygonCollider(
			es.eucm.ead.schema.data.shape.Polygon schemaPolygon) {
		Polygon polygon = new Polygon();
		float[] vertices = new float[schemaPolygon.getPoints().size()];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = schemaPolygon.getPoints().get(i);
		}
		polygon.setVertices(vertices);
		return polygon;
	}

	/**
	 * Builds a circumscribed polygon for a {@code circle} that can be used as a
	 * collider.
	 * 
	 * The number of sides of the polygon is specified through param
	 * {@code nSides}.
	 * 
	 * The algorithm to calculate the minimum nSides polygon that covers a
	 * circle (circumscribed) is as follows:
	 * 
	 * (To see what a circumscribed polygon is, visit: <a href=
	 * "http://www.vitutor.com/geometry/plane/circumscribed_polygons.html"
	 * >http://www.vitutor.com/geometry/plane/circumscribed_polygons.html</a>)
	 * 
	 * 1) Distance from circle's center to each vertex (equidistant) is
	 * calculated (d).
	 * 
	 * 2) A vector with length d is calculated.
	 * 
	 * 3) Vector is added to the circle's center to get one vertex.
	 * 
	 * 4) The vector is rotated "a" degrees. "a" is calculated by dividing 360º
	 * by the number of sides of the polygon.
	 * 
	 * (3) and (4) are repeated until all the vertices are calculated.
	 * 
	 * Note: d=r/cos(a/2), where: r: circle's radius a: angle
	 * 
	 * See <a href="https://github.com/e-ucm/ead/wiki/Shape-renderer">This wiki
	 * page</a> for more details
	 */
	public static Polygon buildCircleCollider(Circle circle, int nSides) {
		Polygon polygon = new Polygon();
		float vertices[] = new float[nSides * 2];
		// The radius
		float r = circle.getRadius();
		// The center of the circle. Since we work on a coordinate system where
		// the origin is in the bottom-left corner of the element, the center is
		// located in (radius, radius).
		float cx = r, cy = r;

		// How much we must rotate the radius vector (normal to the circle)
		float angleInc = 360.0F / nSides;
		double halfAngleIncRad = Math.PI / nSides;

		// Distance from center to each of the vertices
		float d = (float) (r / Math.cos(halfAngleIncRad));

		// Initialization of the vector used to calculate each vertex.
		Vector2 centerToVertex = new Vector2();
		centerToVertex.set(0, d);
		centerToVertex.rotateRad((float) halfAngleIncRad);

		for (int i = 0; i < nSides; i++) {
			// Calculate vertex
			vertices[2 * i] = cx + centerToVertex.x;
			vertices[2 * i + 1] = cy + centerToVertex.y;
			// Rotate for the next vertex
			centerToVertex.rotate(angleInc);
		}

		polygon.setVertices(vertices);
		return polygon;
	}
}
