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

	public static final int DEFAULT_CIRCLE_COLLIDER_NSIDES = 50;

	/**
	 * Returns a bounding polygon for the given shape.
	 * 
	 * @param shape
	 *            The shape the polygon must approximate to.
	 * @return A runtime polygon (libgdx).
	 */
	public static Polygon buildShapeCollider(Shape shape) {
		return buildShapeCollider(shape, DEFAULT_CIRCLE_COLLIDER_NSIDES);
	}

	/**
	 * Returns a bounding polygon for the given shape.
	 * 
	 * @param shape
	 *            The shape the polygon must approximate to.
	 * @param nSides
	 *            The number of sides the polygon must have. Only used for
	 *            {@link Circle}s.
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

	private static Polygon buildRectangleCollider(Rectangle rectangle) {
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

	private static Polygon buildPolygonCollider(
			es.eucm.ead.schema.data.shape.Polygon schemaPolygon) {
		Polygon polygon = new Polygon();
		float[] vertices = new float[schemaPolygon.getPoints().size()];
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = schemaPolygon.getPoints().get(i);
		}
		polygon.setVertices(vertices);
		return polygon;
	}

	/*
	 * The algorithm to calculate the minimum nSides polygon that covers a
	 * circle is as follows:
	 * 
	 * All the sides of a circle's bounding polygon are tangent to the circle.
	 * This means each polygon side intersects the circle in just one point.
	 * Therefore, the idea is to calculate as many tangent lines to the circle
	 * as sides the polygon must have, to latter get them intersected. These
	 * intersections are the vertices for the polygon.
	 * 
	 * Each tangent is calculated by rotating the same radiusVector a fixed
	 * angle
	 */
	private static Polygon buildCircleCollider(Circle circle, int nSides) {
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

		// Initialization of the normal vector used to calculate tangents.
		Vector2 radiusVector = new Vector2();
		radiusVector.set(0, r);

		// Initialization of tangent vector and points used to define the first
		// line for the intersection
		Vector2 tangentVector = new Vector2();
		tangentVector.set(-1, 0);
		Vector2 tangentPoint = new Vector2();
		Vector2 tangentPoint2 = new Vector2();
		tangentPoint.set(radiusVector.x + cx, radiusVector.y + cy);
		tangentPoint2.set(tangentPoint);
		tangentPoint2.add(tangentVector);

		// vector and points for the second line. These are always calculated in
		// the loop
		Vector2 otherTangentVector = new Vector2();
		Vector2 otherTangentPoint = new Vector2();
		Vector2 otherTangentPoint2 = new Vector2();

		// To store the result of the intersection
		Vector2 intersection = new Vector2();

		for (int i = 0; i < nSides; i++) {
			// Calculate the second tangent line:
			// 1) rotate the normal vector
			radiusVector.rotate(angleInc);
			// 2) rotate also the tangent vector
			otherTangentVector.set(tangentVector);
			otherTangentVector.rotate(angleInc);
			// 3) Calculate two points for the line using normal and tangent
			// vectors
			otherTangentPoint.set(radiusVector.x + cx, radiusVector.y + cy);
			otherTangentPoint2.set(otherTangentPoint);
			otherTangentPoint2.add(otherTangentVector);

			// Intersect lines
			boolean intersected = Intersector.intersectLines(tangentPoint,
					tangentPoint2, otherTangentPoint, otherTangentPoint2,
					intersection);
			if (intersected) {
				// Set vertex calculated
				vertices[2 * i] = intersection.x;
				vertices[2 * i + 1] = intersection.y;

				// Update first line for the next intersection
				tangentVector.set(otherTangentVector);
				tangentPoint.set(otherTangentPoint);
				tangentPoint2.set(otherTangentPoint2);
			} else {
				throw new RuntimeException(
						"something went wrong while creating collider for the circle");
			}
		}

		polygon.setVertices(vertices);
		return polygon;
	}
}
