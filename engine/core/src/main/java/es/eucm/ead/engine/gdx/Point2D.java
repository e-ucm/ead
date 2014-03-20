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

public class Point2D {

	public float x, y;

	public Point2D() {
		this.x = 0;
		this.y = 0;
	}

	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean above(Point2D p) {
		// returns true if this point is above p
		return (this.y > p.y) || ((this.y == p.y) && (this.x < p.x));
	}

	public boolean leftOf(Point2D p) {
		return (this.x < p.x) || ((this.x == p.x) && (this.y > p.y));
	}

	public boolean rightOf(Point2D p) {
		return (this.x > p.x) || ((this.x == p.x) && (this.y < p.y));
	}

	public boolean leftOf(Point2D p, Point2D q) {
		// returns true if this point is left of line segment q-p
		return (q.x - p.x) * (this.y - p.y) - (q.y - p.y) * (this.x - p.x) >= 0;
	}

	public boolean contains(float X, float Y) {
		return (X - this.x) * (X - this.x) + (Y - this.y) * (Y - this.y) <= 16;
	}

	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

	@Override
	public boolean equals(Object obj) {
		final Point2D other = (Point2D) obj;
		return this.x == other.x && this.y == other.y;
	}

}
