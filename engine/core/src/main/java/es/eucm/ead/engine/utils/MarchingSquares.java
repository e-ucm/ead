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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Marching squares to calculate the contour line. * From
 * https://raw.github.com/JosuaKrause/Bubble-Sets/master/src/main/java/setvis
 * /bubbleset/MarchingSquares.java
 * 
 * Extended to use libgdx Arrays and Vector2, and to support multiple contours
 * per invocation by Manuel Freire
 * 
 * Original license: Copyright (c) 2011 Christopher Collins, Josua Krause
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author Christopher Collins
 */
public final class MarchingSquares {

	private MarchingSquares() {
		// no constructor
	}

	private static enum Direction {
		N, S, E, W
	}

	// the direction of movement for marching squares
	private static Direction direction;

	private static double threshold;

	/**
	 * Calculates the contour of the potential area.
	 * 
	 * @param contours
	 *            The resulting contours.
	 * @param potentialArea
	 *            The potential area, in column-major order; that is, the pixel
	 *            at (x, y) is indexed as [x][y]
	 * @param step
	 *            the resolution of the calculation in pixels
	 * @param t
	 *            the threshold
	 * @return the number of continuous contours found
	 */
	public static int calculateContour(final Array<Array<Vector2>> contours,
			final double[][] potentialArea, final int step, final double t) {

		// avoids revisiting contours; self-intersection may pose problems
		boolean[][] visited = new boolean[potentialArea.length][potentialArea[0].length];

		// set starting direction for conditional states (6 & 9)
		direction = Direction.S;

		// set the threshold
		threshold = t;

		Array<Vector2> currentContour = new Array<Vector2>();

		for (int x = 0; x < potentialArea.length; x++) {
			final double[] potLine = potentialArea[x];
			for (int y = 0; y < potLine.length; y++) {
				if (!visited[x][y]) {
					// check invalid state condition
					if (test(potLine[y]) && getState(potentialArea, x, y) != 15) {
						march(currentContour, potentialArea, x, y, step);
						for (Vector2 p : currentContour) {
							int xx = (int) p.x;
							int yy = (int) p.y;
							visited[xx + 0][yy + 0] = true;
							visited[xx + 0][yy + 1] = true;
							visited[xx + 1][yy + 0] = true;
							visited[xx + 0][yy - 1] = true;
							visited[xx - 1][yy + 0] = true;
							visited[xx + 1][yy + 1] = true;
							visited[xx - 1][yy - 1] = true;
							visited[xx + 1][yy - 1] = true;
							visited[xx - 1][yy + 1] = true;
						}
						contours.add(new Array<Vector2>(currentContour));
						currentContour.clear();
					} else {
						visited[x][y] = true;
					}
				}
			}
		}
		return contours.size;
	}

	/**
	 * 2-D Marching squares algorithm. March around a given area to find an
	 * iso-energy contour.
	 * 
	 * @param contour
	 *            the surface to fill with iso-energy points
	 * @param potentialArea
	 *            the area, filled with potential values
	 * @param xpos
	 *            the current x-position in the area
	 * @param ypos
	 *            the current y-position in the area
	 * @param step
	 *            the resolution of the calculation in pixels
	 * @return true iff a continuous contour is found
	 */
	private static boolean march(final Array<Vector2> contour,
			final double[][] potentialArea, final int xpos, final int ypos,
			final int step) {
		int x = xpos;
		int y = ypos;
		for (;;) { // iterative version of the end recursion
			final Vector2 p = new Vector2((float) x * step, (float) y * step);

			// check if we're back where we started
			if (contour.contains(p, false)) {
				if (!contour.get(0).equals(p)) {
					// encountered a loop but haven't returned to start; will
					// change
					// direction using conditionals and continue
				} else
					// back to start
					return true;
			} else {
				contour.add(p);
			}

			final int state = getState(potentialArea, x, y);
			// x, y are upper left of 2X2 marching square

			switch (state) {
			case -1:
				throw new IllegalStateException("Marched out of bounds @ " + x
						+ ", " + y);
			case 0:
			case 3:
			case 2:
			case 7:
				direction = Direction.E;
				break;
			case 12:
			case 14:
			case 4:
				direction = Direction.W;
				break;
			case 6:
				direction = (direction == Direction.N) ? Direction.W
						: Direction.E;
				break;
			case 1:
			case 13:
			case 5:
				direction = Direction.N;
				break;
			case 9:
				direction = (direction == Direction.E) ? Direction.N
						: Direction.S;
				break;
			case 10:
			case 8:
			case 11:
				direction = Direction.S;
				break;
			default:
				throw new IllegalStateException(
						"Marching squares invalid state: " + state);
			}

			switch (direction) {
			case N:
				--y; // up
				break;
			case S:
				++y; // down
				break;
			case W:
				--x; // left
				break;
			case E:
				++x; // right
				break;
			default:
				throw new IllegalStateException(
						"Marching squares invalid state: " + state);
			}
		}
	}

	/**
	 * Tests whether a given value meets the threshold specified for marching
	 * squares.
	 * 
	 * @param test
	 *            the value to test
	 * @return whether the test value passes
	 */
	private static boolean test(final double test) {
		return test > threshold;
	}

	/**
	 * 2-D Marching Squares algorithm. Given a position and an area of potential
	 * energy values, calculate the current marching squares state by testing
	 * neighbouring squares.
	 * 
	 * @param potentialArea
	 *            the area filled with potential energy values
	 * @param x
	 *            the current x-position in the area
	 * @param y
	 *            the current y-position in the area
	 * @return an int value representing a marching squares state
	 */
	private static int getState(final double[][] potentialArea, final int x,
			final int y) {
		int dir = 0;
		try {
			dir += test(potentialArea[x][y]) ? 1 << 0 : 0;
			dir += test(potentialArea[x + 1][y]) ? 1 << 1 : 0;
			dir += test(potentialArea[x][y + 1]) ? 1 << 2 : 0;
			dir += test(potentialArea[x + 1][y + 1]) ? 1 << 3 : 0;
		} catch (final ArrayIndexOutOfBoundsException e) {
			System.err.println("Marched out of bounds: " + x + " " + y
					+ " bounds: " + potentialArea.length + " "
					+ potentialArea[0].length);
			return -1;
		}
		return dir;
	}

}
