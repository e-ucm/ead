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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.engine.mock.MockApplication;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Simple tests that can be run non-interactively.
 * 
 * @author mfreire
 */
public class GeometryUtilsTest {

	@BeforeClass
	public static void setupClass() {
		MockApplication.initStatics();
	}

	private static final float delta = 0.1f;

	/**
	 * Test of createPoly method, of class GeometryUtils.
	 */
	@Test
	public void testCreatePoly() {
		Polygon result = GeometryUtils.createPoly(4, 30, Vector2.Zero);
		assertArrayEquals("nice square", new float[] { 30, 0, 0, 30, -30, 0, 0,
				-30 }, result.getVertices(), delta);
	}

	/**
	 * Test of createStar method, of class GeometryUtils.
	 */
	@Test
	public void testCreateStar() {
		Polygon result = GeometryUtils.createStar(4, 22, 30, Vector2.Zero);
		assertArrayEquals("nice 4-star", new float[] { 22, 0, 21.21f, 21.21f,
				0, 22, -21.21f, 21.21f, -22, 0, -21.21f, -21.21f, 0, -22,
				21.21f, -21.21f }, result.getVertices(), delta);
	}

	/**
	 * Test of findBorders method, of class GeometryUtils.
	 */
	@Test
	public void testFindBorders() {
		Pixmap p = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
		p.setColor(Color.BLUE);
		p.fillRectangle(5, 0, 90, 50);
		Array<Geometry> result = GeometryUtils.findBorders(p, .5, 2);
		float[] coords = GeometryUtils.jtsCoordsToGdx(
				result.get(0).getCoordinates()).getVertices();
		assertArrayEquals("rectangle found", new float[] { 6, 50, 95, 50, 95,
				0, 5, 0 }, coords, delta);
	}

	/**
	 * Test of findPolygons method, of class GeometryUtils.
	 */
	@Test
	public void testFindPolygons() {
		Pixmap p = new Pixmap(100, 50, Pixmap.Format.RGBA8888);
		p.setColor(Color.BLUE);
		p.fillRectangle(5, 0, 90, 50);
		List<es.eucm.ead.schema.data.shape.Polygon> result = GeometryUtils
				.findPolygons(p);
		float[] coords = new float[result.get(0).getPoints().size];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = result.get(0).getPoints().get(i);
		}
		assertArrayEquals("rectangle found", new float[] { 6, 50, 95, 50, 95,
				0, 5, 0 }, coords, delta);
	}

	/**
	 * Test of gdxToJts method, of class GeometryUtils.
	 */
	@Test
	public void testGdxToJts() {
		Polygon square = GeometryUtils.createPoly(4, (float) Math.sqrt(2) / 2,
				Vector2.Zero);
		Polygon another = GeometryUtils.jtsCoordsToGdx(GeometryUtils.gdxToJts(
				square).getCoordinates());
		assertArrayEquals("similar squares", square.getVertices(),
				another.getVertices(), delta);
	}

	/**
	 * Test of subtract method, of class GeometryUtils.
	 */
	@Test
	public void testSubtract() {
		Polygon a = GeometryUtils.createPoly(4, 2, Vector2.Zero);
		Array<Geometry> ga = new Array<Geometry>();
		ga.add(GeometryUtils.gdxToJts(a));
		Polygon b = GeometryUtils.createPoly(4, 1, Vector2.Zero);

		GeometryUtils.subtract(ga, b);
		assertEquals("squared donut has 1 JTS polygons (with 2 rings)", 1,
				ga.size);
		assertEquals("squared donut has 4+4 vertices", 8 + 2, ga.get(0)
				.getCoordinates().length);
	}

	/**
	 * Test of merge method, of class GeometryUtils.
	 */
	@Test
	public void testMerge() {
		Polygon a = GeometryUtils.createPoly(4, (float) Math.sqrt(2) / 2,
				Vector2.Zero);
		Array<Geometry> ga = new Array<Geometry>();
		ga.add(GeometryUtils.gdxToJts(a));
		Polygon b = GeometryUtils.createPoly(4, (float) Math.sqrt(2),
				new Vector2(0.5f, 0));

		GeometryUtils.merge(ga, b);
		assertEquals("x-displaced sum of squares is a rectangle", 1, ga.size);
		assertEquals("rectangle has 4 outer vertices", 4 + 1, ga.get(0)
				.getCoordinates().length);
	}

	/**
	 * Test of simplify method, of class GeometryUtils.
	 */
	@Test
	public void testSimplify() {
		Polygon star = GeometryUtils.createStar(4, (float) Math.sqrt(2) / 4,
				(float) Math.sqrt(2) / 2, Vector2.Zero);
		Array<Geometry> ga = new Array<Geometry>();
		ga.add(GeometryUtils.gdxToJts(star));

		GeometryUtils.simplify(ga, 2);
		assertEquals("simplified 4-star has only 4 vertices", 4 + 1, ga.get(0)
				.getCoordinates().length);
	}

	/**
	 * Test of collapse method, of class GeometryUtils.
	 */
	@Test
	public void testCollapse() {
		Polygon a = GeometryUtils.createPoly(4, (float) Math.sqrt(2) / 2,
				Vector2.Zero);
		Polygon b = GeometryUtils.createPoly(4, (float) Math.sqrt(2),
				new Vector2(2, 0));
		Array<Geometry> ga = new Array<Geometry>();
		ga.add(GeometryUtils.gdxToJts(a));
		ga.add(GeometryUtils.gdxToJts(b));

		Geometry r = GeometryUtils.collapse(ga);
		assertEquals("two separate squares collapse into a single one", 8 + 2,
				r.getCoordinates().length);
	}

	/**
	 * Test of triangulate method, of class GeometryUtils.
	 */
	@Test
	public void testTriangulate() {
		System.out.println("triangulate");
		Geometry g = GeometryUtils.gdxToJts(GeometryUtils.createPoly(4, 1,
				Vector2.Zero));
		short[] expResult = new short[] { 2, 3, 1, 1, 3, 0 };
		short[] result = GeometryUtils.triangulate(g);
		assertArrayEquals(expResult, result);
	}
}
