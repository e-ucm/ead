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

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.mock.MockApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * White-box and black-box unit tests for the PathFinder
 */
public class PathFinderTest {

	@Before
	public void setup() {
		MockApplication.initStatics();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSimpleLineEndpoints() throws Exception {

		float s = 10;
		Polygon p = new Polygon(new float[] { 0, 0, s, 0, s, s, 0, s });
		PathFinder pf = new PathFinder(p, null);

		Vector2 tgt = new Vector2(s - 1, s - 1);
		assertArrayEquals("direct clear path in square", new Vector2[] { tgt },
				pf.lineEndpoints(new Vector2(1, 1), tgt, true));
		assertArrayEquals(
				"direct path in square, starting from opposite corner",
				new Vector2[] { tgt },
				pf.lineEndpoints(new Vector2(0, 0), tgt, true));
		assertArrayEquals("impossible path in square", new Vector2[] {
				new Vector2(s, 0), new Vector2(s, s) },
				pf.lineEndpoints(tgt, new Vector2(s + s, s / 2), true));
	}

	/**
	 * Sanity test for libgdx polygon-to-segment collision detection
	 */
	@Test
	public void testProblemWithLibgdx() {
		Polygon p = new Polygon(new float[] { 0, 0, 10, 0, 10, 10, 0, 10 });
		Vector2 source = new Vector2(9.009702f, 9.002425f);
		Vector2 target = new Vector2(20, 5);
		assertTrue("Point inside square " + source
				+ " intersects with point outside " + target,
				PathUtils.intersectSegmentPolygon(source, target, p, null));
	}

	/**
	 * Test empty path
	 */
	@Test
	public void testTrivialPath() throws Exception {
		Polygon p = new Polygon(new float[] { 0, 0, 10, 0, 10, 10, 0, 10 });
		PathFinder pf = new PathFinder(p, null);
		Vector2 start = new Vector2(1, 1);

		PathFinder.PathIterator pi = pf.findPath(start, start, 1);
		assertTrue("there is a point in the trivial path", pi.hasNext());
		assertTrue("the only point is the start == end",
				start.equals(pi.next()));
		assertTrue("there was only one point in the trivial path",
				!pi.hasNext());
	}

	/**
	 * Main test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDoubleU() throws Exception {

		/*
		 * a 9x3 letter-w. Goals on extremes of top row. Segments 1 box wide
		 */
		float s = 10;
		Polygon p = new Polygon(new float[] {
				// north-west, low, north-east
				0, 3 * s, 0, 2 * s, 2 * s, 0, 3 * s, 0, 4.5f * s, 2 * s, 6 * s,
				0, 7 * s, 0, 9 * s, 2 * s, 9 * s, 3 * s,
				// north-east, high, north-west
				8 * s, 3 * s, 6.5f * s, 1 * s, 5 * s, 3 * s, 4 * s, 3 * s,
				2.5f * s, s, 1 * s, 3 * s });
		PathFinder pf = new PathFinder(p, null);

		Vector2 tgt = new Vector2(9 * s - .1f, 3 * s - .1f);

		// test the first bend
		assertArrayEquals("first bend", new Vector2[] {
				new Vector2(2.5f * s, 1 * s), new Vector2(1 * s, 3 * s) },
				pf.lineEndpoints(new Vector2(.5f * s, 2.5f * s), tgt, true));

		// test the whole path
		Vector2 src = new Vector2(.5f * s, 2.5f * s);
		Vector2[] expectedPath = new Vector2[] { src,
				new Vector2(2.5f * s, 1 * s), new Vector2(4.5f * s, 2 * s),
				new Vector2(6.5f * s, 1 * s), tgt };
		assertSamePath("long path in W", expectedPath, pf.findPath(src, tgt));

		// lets add up the path-length
		float totalLength = 0;
		for (int i = 1; i < expectedPath.length; i++) {
			totalLength += expectedPath[i].dst(expectedPath[i - 1]);
		}
		// and try several step variations
		float stepLength = 10;
		for (int i = 1; i < 10; i++) {
			assertEquals("long path in W",
					(int) Math.ceil(totalLength / stepLength) + 1,
					countIterations(pf.findPath(src, tgt, stepLength)));
			stepLength *= 1.5f;
		}
	}

	public int countIterations(Iterator<?> it) {
		int iterations = 0;
		while (it.hasNext()) {
			iterations++;
			it.next();
		}
		return iterations;
	}

	public void assertSamePath(String text, Vector2[] expectedPoints,
			Array<PathFinder.PathPoint> pfps) {
		Vector2[] found = new Vector2[pfps.size];
		for (int i = 0; i < found.length; i++) {
			found[i] = pfps.get(i).pos;
		}
		assertArrayEquals(text, expectedPoints, found);
	}

	@Test
	public void testSimplePaths() throws Exception {

		float s = 10;
		Polygon p = new Polygon(new float[] { 0, 0, s, 0, s, s, 0, s });
		PathFinder pf = new PathFinder(p, null);
		Vector2 tgt = new Vector2(s - 1, s - 1);

		assertSamePath("direct clear path in square", new Vector2[] {
				new Vector2(1, 1), tgt }, pf.findPath(new Vector2(1, 1), tgt));
		assertSamePath("direct path in square, starting from opposite corner",
				new Vector2[] { new Vector2(0, 0), tgt },
				pf.findPath(new Vector2(0, 0), tgt));

		assertSamePath("impossible path in square (in-to-out)", new Vector2[] {
				new Vector2(1, s / 2), new Vector2(s, s / 2) },
				pf.findPath(new Vector2(1, s / 2), new Vector2(s + s, s / 2)));

		assertSamePath("impossible path in square (out-to-in)", new Vector2[] {
				new Vector2(0, s / 2), new Vector2(2, s / 2) },
				pf.findPath(new Vector2(-1, s / 2), new Vector2(2, s / 2)));
	}
}
