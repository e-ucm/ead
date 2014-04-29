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
package es.eucm.ead.engine.paths;

import com.badlogic.gdx.math.*;

/**
 * Utility functions for paths and perspective projections.
 */
public class PathUtils {

	/**
	 * A unit square. Intended to be used as a default "no-perspective" argument
	 * for the PathFinder constructor.
	 */
	public static final Polygon UNIT_SQUARE = new Polygon(new float[] { 0, 0,
			1, 0, 1, 1, 0, 1 });

	/**
	 * A square viewed under perspective projection, such that lower-y objects
	 * appear closer. Intended to be used as a default "simple-perspective"
	 * argument for the PathFinder constructor.
	 */
	public static final Polygon CENTRAL_ONE_QUARTER_SQUARE = new Polygon(
			new float[] { 0, 0, 400, 0, 300, 100, 100, 100 });

	/**
	 * Extracts all vertices from a polygon. The first point would go again
	 * after the last to close the polygon.
	 * 
	 * @param p
	 *            the polygon to extract from.
	 * @return vertices in the correct order, with no duplicates.
	 */
	public static Vector2[] polygonToPoints(Polygon p) {
		float[] vs = p.getVertices();
		Vector2[] points = new Vector2[vs.length / 2];
		for (int i = 0, j = 0; i < vs.length; i += 2) {
			points[j++] = new Vector2(vs[i], vs[i + 1]);
		}
		return points;
	}

	/**
	 * Extracts all vertices from a polygon, adding the first one after the
	 * last.
	 * 
	 * @param p
	 *            the polygon to extract from
	 * @return vertices in the correct order, with the first one also in the
	 *         last position.
	 */
	public static Vector2[] polygonToPointsCircular(Polygon p) {
		float[] vs = p.getVertices();
		Vector2[] points = new Vector2[(vs.length / 2) + 1];
		for (int i = 0, j = 0; i < vs.length + 1; i += 2) {
			points[j++] = new Vector2(vs[i % vs.length],
					vs[(i + 1) % vs.length]);
		}
		return points;
	}

	/**
	 * Transforms all supplied polygons using a 3x3 projection matrix.
	 * 
	 * @param t
	 *            the 3x3 transform matrix
	 * @param ps
	 *            the polygons to transform
	 */
	public static void transformPolygons(Matrix3 t, Polygon... ps) {
		Vector3 v = new Vector3();
		for (Polygon p : ps) {
			float[] vs = p.getVertices();
			for (int i = 0; i < vs.length; i += 2) {
				v.set(vs[i], vs[i + 1], 1);
				v.mul(t);
				vs[i] = v.x / v.z;
				vs[i + 1] = v.y / v.z;
			}
			p.dirty();
		}
	}

	/**
	 * Returns the point on a polygon edge that is closest to the given point.
	 */
	public static Vector2 closestInternalPoint(Vector2 point, Polygon... polys) {
		Vector2 best = new Vector2();
		float bestDistance2 = Float.POSITIVE_INFINITY;

		for (Polygon p : polys) {
			float[] vs = p.getVertices();
			float x0 = vs[vs.length - 2];
			float y0 = vs[vs.length - 1];
			Vector2 current = new Vector2();
			for (int i = 0; i < vs.length; i += 2) {
				float x1 = vs[i];
				float y1 = vs[i + 1];
				Intersector.nearestSegmentPoint(x0, y0, x1, y1, point.x,
						point.y, current);
				float d2 = current.dst2(point);
				if (d2 < bestDistance2) {
					bestDistance2 = d2;
					best.set(current);
				}
				x0 = x1;
				y0 = y1;
			}
		}
		return best;
	}

	/**
	 * Transforms all points in a polygon using a 3x3 projection matrix.
	 * 
	 * @param t
	 *            the 3x3 transform matrix
	 * @param points
	 *            the points to transform. Coordinates will be overwritten
	 */
	public static void transformPoints(Matrix3 t, Vector2... points) {
		Vector3 v3 = new Vector3();
		for (Vector2 v : points) {
			v3.set(v.x, v.y, 1);
			v3.mul(t);
			v.set(v3.x / v3.z, v3.y / v3.z);
		}
	}

	/**
	 * Check whether the given segment and {@link Polygon} intersect. Adapted
	 * from LibGDX code to avoid problems with
	 * 
	 * @param p1
	 *            The first point of the segment
	 * @param p2
	 *            The second point of the segment
	 * @param pos
	 *            Intersection position (ignored if null or no intersection)
	 * @param polygon
	 *            The polygon
	 * @return Whether polygon and segment intersects
	 */
	public static boolean intersectSegmentPolygon(Vector2 p1, Vector2 p2,
			Polygon polygon, Vector2 pos) {
		float[] vertices = polygon.getTransformedVertices();
		float x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;
		int n = vertices.length;
		float x3 = vertices[n - 2], y3 = vertices[n - 1];
		for (int i = 0; i < n; i += 2) {
			float x4 = vertices[i], y4 = vertices[i + 1];
			float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
			if (d != 0) {
				float yd = y1 - y3;
				float xd = x1 - x3;
				float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
				if (ua >= 0 && ua <= 1) {
					float ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
					if (ub >= 0 && ub <= 1) {
						if (pos != null) {
							pos.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
						}
						return true;
					}
				}
			}
			x3 = x4;
			y3 = y4;
		}
		return false;
	}

	private static Matrix3 mapBasisToImage(Vector2[] v) {
		// solve (v1 v2 v3) * <a b c> = v4 using <a b c> = (v1 v2 v3)^-1 * v4
		Matrix3 v123 = new Matrix3(new float[] { v[0].x, v[0].y, 1, v[1].x,
				v[1].y, 1, v[2].x, v[2].y, 1 });
		Vector3 v4 = new Vector3(new float[] { v[3].x, v[3].y, 1 });
		float[] M = v123.val.clone();
		v4.mul(v123.inv());
		// scale by solutions
		for (int i = 0; i < 3; i++) {
			M[i + 0] *= v4.x;
			M[i + 3] *= v4.y;
			M[i + 6] *= v4.z;
		}
		Matrix3 bti = new Matrix3(M);
		return bti;
	}

	/**
	 * Calculates a projection matrix from two corresponding sets of four
	 * non-collinear 2d vertices. Notice that "view" and "world" are actually
	 * interchangeable. Use sparingly: several intermediate matrices are
	 * required, and performance may suffer if called in a tight loop.
	 * 
	 * @param view
	 *            four points in the "view" (a camera pointed at the world,
	 *            probably with some kind of perspective distortion)
	 * @param world
	 *            four corresponding points in the "world" (where distances and
	 *            angles are correct)
	 * @return a 3d projection that can be used to transform any view-based 2d
	 *         vector into 'real space', and inverted to undo the process.
	 * @see <a href="http://jsfiddle.net/dFrHS/1/">this example</a>, referenced
	 *      and described an excellent @see <a
	 *      href="http://math.stackexchange.com/questions/296794/">
	 *      math.stackexchange.com answer</a>.
	 */
	public static Matrix3 getProjectionMatrix(Vector2[] view, Vector2[] world) {

		Matrix3 A = mapBasisToImage(view);
		Matrix3 B = mapBasisToImage(world);

		// combined - translates any vector from v-space to w-space
		return B.mul(A.inv());
	}

	/**
	 * Helper method that builds a projection matrix from a single square. The
	 * square must be specified as a positive-winding polygon that starts at the
	 * lower-left corner. The first edge is used for scaling.
	 * 
	 * @param projectedSquare
	 *            a (real-distance world) square in the (perspective-projected)
	 *            view, with vertices enumerated counter-clockwise.
	 * @return matrix that, when applied to view coordinates, transforms them to
	 *         world coordinates. The inverse of this matrix transforms world
	 *         coordinates into view coordinates.
	 */
	public static Matrix3 getProjectionMatrix(Polygon projectedSquare) {
		Vector2[] viewSquarePoints = PathUtils
				.polygonToPoints(projectedSquare != null ? projectedSquare
						: UNIT_SQUARE);
		float scale = viewSquarePoints[0].dst(viewSquarePoints[1]);
		Vector2[] unitSquare = new Vector2[] { new Vector2(0, 0),
				new Vector2(scale, 0), new Vector2(scale, scale),
				new Vector2(0, scale) };
		return PathUtils.getProjectionMatrix(viewSquarePoints, unitSquare);
	}
}
