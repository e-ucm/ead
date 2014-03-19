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
package es.eucm.ead.engine.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Geometry utilities. Uses JTS for the heavy lifting.
 * 
 * @author mfreire
 */
public class GeometryUtils {

	private static final GeometryFactory gf = new GeometryFactory();

	/**
	 * Creates an n-sided regular polygon with the requested radius and location
	 * 
	 * @param sides
	 *            count
	 * @param radius
	 *            distance from center to each vertex
	 * @param location
	 *            of center
	 * @return resulting polygon
	 */
	public static Polygon createPoly(int sides, float radius, Vector2 location) {
		if (sides < 3 || radius <= 0) {
			throw new IllegalArgumentException(
					"negative radius or <3 sides not supported");
		}

		float[] vs = new float[sides * 2];
		double angle = 0;
		double angleDelta = Math.PI * 2 / (double) sides;
		for (int i = 0, j = 0; i < sides; i++, j += 2) {
			vs[j] = (float) (Math.cos(angle) * radius + location.x);
			vs[j + 1] = (float) (Math.sin(angle) * radius + location.y);
			angle += angleDelta;
		}

		return new Polygon(vs);
	}

	/**
	 * Creates an n-sided star with the selected inner and outer radius and
	 * location.
	 * 
	 * @param sides
	 *            count
	 * @param innerRadius
	 *            distance from center to each inner vertex
	 * @param outerRadius
	 *            distance from center to each exterior vertex
	 * @param location
	 *            of center
	 * @return resulting polygon
	 */
	public static Polygon createStar(int sides, float innerRadius,
			float outerRadius, Vector2 location) {
		if (sides < 3 || innerRadius <= 0 || outerRadius <= 0) {
			throw new IllegalArgumentException(
					"negative radius or <3 sides not supported");
		} else if (innerRadius > outerRadius) {
			throw new IllegalArgumentException(
					"inner radius should be <= outer radius");
		}
		float[] vs = new float[sides * 4];
		double angle = 0;
		double angleDelta = Math.PI / (double) sides;
		for (int i = 0, j = 0; i < sides; i++, j += 4) {
			vs[j] = (float) (Math.cos(angle) * innerRadius + location.x);
			vs[j + 1] = (float) (Math.sin(angle) * innerRadius + location.y);
			angle += angleDelta;
			vs[j + 2] = (float) (Math.cos(angle) * outerRadius + location.x);
			vs[j + 3] = (float) (Math.sin(angle) * outerRadius + location.y);
			angle += angleDelta;
		}

		return new Polygon(vs);
	}

	/**
	 * Finds the borders of a pixmap. Uses tarnsparency to decide what to
	 * include as "in", and builds a separate, non-overlapping polygon that
	 * delimits each region.
	 * 
	 * @param pm
	 *            pixmap to create borders for
	 * @param threshold
	 *            a number between 0 (transparent) and 1 (opaque) used to
	 *            determine the sensitivity of the borders. Recommended value is
	 *            0.5
	 * @param distanceTolerance
	 *            used during polygon-simplification. Points in the polygon will
	 *            be separated by at least distanceTolerance pixels.
	 * @return a list of non-overlapping JTS polygons
	 */
	public static ArrayList<Geometry> findBorders(Pixmap pm, double threshold,
			double distanceTolerance) {
		final int w = pm.getWidth();
		final int h = pm.getHeight();
		// uses a 2-pixel padding to avoid marching off-bounds
		double[][] area = new double[w+4][h+4];
		for (int x = 0; x < w; x++) {
			double[] column = area[x+2];
			for (int y = 0; y < h; y ++) {
				int p = pm.getPixel(x, h-y-1);
				// take the alpha-value of each pixel as its potential
				column[y+2] = (p & 0xff) * 1.0 / 256;
			}
		}
		ArrayList<ArrayList<Point2D>> contours = new ArrayList<ArrayList<Point2D>>();
		MarchingSquares.calculateContour(contours, area, 1, threshold);

		ArrayList<Geometry> geo = new ArrayList<Geometry>();
		for (ArrayList<Point2D> contour : contours) {
			float[] vs = new float[contour.size() * 2];
			int i = 0;
			for (Point2D p : contour) {
				// undoes the padding
				vs[i++] = (float) p.getX()-1;
				vs[i++] = (float) p.getY()-1;
			}
			geo.add(gdxToJts(new Polygon(vs)));
		}
		simplify(geo, distanceTolerance);

		return geo;
	}

	/**
	 * Converts a JTS polygonal geometry to an EAD polygon
	 * 
	 * @param g
	 *            geometry to convert (must be a closed polygon)
	 * @return a schema polygon
	 */
	public static es.eucm.ead.schema.components.Polygon jtsToSchemaPolygon(
			Geometry g) {
		if (!(g instanceof Polygonal)) {
			throw new IllegalArgumentException(
					"Can only convert JTS Polygons, but supplied a " + g);
		}

		es.eucm.ead.schema.components.Polygon p = new es.eucm.ead.schema.components.Polygon();
		Coordinate[] cs = g.getCoordinates();
		Float vs[] = new Float[(cs.length - 1) * 2];
		for (int i = 0, j = 0; j < vs.length; i++) {
			vs[j++] = (float) cs[i].x;
			vs[j++] = (float) cs[i].y;
		}
		p.setPoints(Arrays.asList(vs));
		return p;
	}

	/**
	 * High-level border-detetion for pixmaps. Uses defaults thresholds and
	 * returns a list of schema polygons.
	 */
	public static List<es.eucm.ead.schema.components.Polygon> findPolygons(
			Pixmap pm) {
		ArrayList<Geometry> jtsBorders = findBorders(pm, 0.5, 2);
		ArrayList<es.eucm.ead.schema.components.Polygon> schemaPolygons = new ArrayList<es.eucm.ead.schema.components.Polygon>();
		for (Geometry g : jtsBorders) {
			schemaPolygons.add(jtsToSchemaPolygon(g));
		}
		return schemaPolygons;
	}

	/**
	 * Converts a gdx polygon to JTS.
	 * 
	 * @param p
	 *            a gdx polygon
	 * @return a jts polygon (a subclass of JTS Geometry)
	 */
	public static com.vividsolutions.jts.geom.Polygon gdxToJts(Polygon p) {
		float vs[] = p.getVertices();

		// note that JTS line-strings must end with the same vertex they start
		Coordinate[] cs = new Coordinate[vs.length / 2 + 1];
		for (int i = 0, j = 0; i < vs.length; i += 2) {
			cs[j++] = new Coordinate(vs[i], vs[i + 1]);
		}
		cs[vs.length / 2] = new Coordinate(vs[0], vs[1]);

		LinearRing shell = new LinearRing(new CoordinateArraySequence(cs), gf);
		return new com.vividsolutions.jts.geom.Polygon(shell, null, gf);
	}

	/**
	 * Converts a JTS coordinate-set to a libgdx "polygon". Both will have the
	 * same vertices, but the libgdx polygon cannot be assumed to be equivalent,
	 * since libgdx makes no provision for holes or disconnected bits.
	 * 
	 * @param cs
	 *            a series of coordinates forming a possibly-closed ring
	 * @return a gdx polygon with these coordinates
	 */
	public static Polygon jtsCoordsToGdx(Coordinate[] cs) {
		boolean closed = cs[0].equals(cs[cs.length - 1]);
		float vs[] = new float[(cs.length - (closed ? 1 : 0)) * 2];
		for (int i = 0, j = 0; j < vs.length; i++) {
			vs[j++] = (float) cs[i].x;
			vs[j++] = (float) cs[i].y;
		}
		return new Polygon(vs);
	}

	/**
	 * Polygonal subtraction. Updates the 'current' geometry by substracting a
	 * polygon.
	 * 
	 * @param current
	 *            geometry, composed of several non-overlapping JTS polygons.
	 *            Will be updated with 'next'. Can be empty.
	 * @param next
	 *            polygon to merge
	 */
	public static void subtract(ArrayList<Geometry> current, Polygon next) {
		Geometry m = gdxToJts(next);
		ArrayList<Geometry> fragments = new ArrayList<Geometry>();
		for (int i = 0; i < current.size(); i++) {
			Geometry g = current.get(i);
			if (m.intersects(g)) {
				Geometry r = g.difference(m);
				// remove 'g' from 'current'
				if (i < current.size() - 1) {
					current.set(i, current.get(current.size() - 1));
				}
				current.remove(current.size() - 1);

				// queue all non-empty bits for later re-addition
				for (int j = 0; j < r.getNumGeometries(); j++) {
					if (!r.getGeometryN(j).isEmpty()) {
						fragments.add(r.getGeometryN(j));
					}
				}
			}
		}
		current.addAll(fragments);
	}

	/**
	 * Incremental polygonal merge. Updates the 'current' geometry with a new
	 * polygon.
	 * 
	 * @param current
	 *            geometry, composed of several non-overlapping JTS polygons.
	 *            Will be updated with 'next'. Can be empty.
	 * @param next
	 *            polygon to merge
	 */
	public static void merge(ArrayList<Geometry> current, Polygon next) {
		Geometry m = gdxToJts(next);
		boolean merged = true;
		while (merged) {
			merged = false;
			for (int i = 0; i < current.size() && !merged; i++) {
				Geometry g = current.get(i);
				if (m.intersects(g)) {
					// remove 'g' from 'current'
					if (i < current.size() - 1) {
						current.set(i, current.get(current.size() - 1));
					}
					current.remove(current.size() - 1);
					// merge 'g' & 'm'
					m = m.union(g);
					merged = true;
				}
			}
		}
		current.add(m);
	}

	/**
	 * Polygonal simplification.
	 * 
	 * @param polys
	 *            (non-overlapping) to simplify
	 * @param distanceTolerance
	 *            consecutive vertices closer than this will be merged
	 */
	public static void simplify(ArrayList<Geometry> polys,
			double distanceTolerance) {
		for (int i = 0; i < polys.size(); i++) {
			Geometry g = TopologyPreservingSimplifier.simplify(polys.get(i),
					distanceTolerance);
			polys.set(i, g);
		}
	}

	/**
	 * Collapses a collection of non-overlapping JTS geometries into a single
	 * geometry.
	 * 
	 * @param gs
	 *            input (non-overlapping polygons)
	 * @return a JTS multipolygon, good for triangulation
	 */
	public static Geometry collapse(ArrayList<Geometry> gs) {
		com.vividsolutions.jts.geom.Polygon ga[] = gs
				.toArray(new com.vividsolutions.jts.geom.Polygon[gs.size()]);
		Geometry g = gf.createMultiPolygon(ga);
		g.union();
		return g;
	}

	/**
	 * Triangulates (delaunay standard) a geometry.
	 * 
	 * @param g
	 *            a JTS geometry, on which contains() operations are valid (ie:
	 *            one composed of non-intersecting polygons)
	 * @return a triangulation, consisting of triples of indices (in the order
	 *         defined by g.getCoordinates())
	 */
	public static short[] triangulate(Geometry g) {
		DelaunayTriangulationBuilder triangulator = new DelaunayTriangulationBuilder();
		Coordinate cs[] = g.getCoordinates();
		if (cs.length > Short.MAX_VALUE) {
			throw new IllegalArgumentException(
					" cannot triangulate polygons exceeding " + Short.MAX_VALUE
							+ " vertices; this one has " + cs.length
							+ " vertices");
		}

		HashMap<Coordinate, Short> cMap = new HashMap<Coordinate, Short>();
		// do not close (because the last == the first)
		for (short i = 0; i < cs.length - 1; i++) {
			cMap.put(cs[i], i);
		}

		Gdx.app.debug("GeometryUtils", "triangulating " + (cs.length - 1));

		triangulator.setSites(g);
		Geometry all = triangulator.getTriangles(gf);
		int allTriangles = all.getNumGeometries();
		ArrayList<Coordinate[]> ts = new ArrayList<Coordinate[]>(allTriangles);
		for (int i = 0; i < allTriangles; i++) {
			Geometry tg = all.getGeometryN(i);
			if (g.contains(tg.getInteriorPoint())) {
				ts.add(tg.getGeometryN(i).getCoordinates());
			}
		}

		Gdx.app.debug("GeometryUtils", "... got " + ts.size() + " triangles");

		short indices[] = new short[ts.size() * 3];
		int j = 0;
		for (Coordinate[] triangle : ts) {
			indices[j++] = cMap.get(triangle[0]);
			indices[j++] = cMap.get(triangle[1]);
			indices[j++] = cMap.get(triangle[2]);
			Gdx.app.debug("GeometryUtils", "Added triangle: " + indices[j - 1]
					+ " " + indices[j - 2] + " " + indices[j - 3]);
		}
		return indices;
	}
}
