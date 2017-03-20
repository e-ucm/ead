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
package es.eucm.ead.engine.components.behaviors;

import com.badlogic.ashley.core.Component;

/**
 * Created by jtorrente on 19/11/2015.
 */
public class PointerPositionComponent extends Component {
	private float x;
	private float y;

	private float initialX;
	private float initialY;

	private float previousX;
	private float previousY;

	private boolean drag;

	public void start(float eventX, float eventY) {
		initialX = eventX;
		initialY = eventY;
		previousX = initialX;
		previousY = initialY;
	}

	public void update(float x, float y, boolean drag) {
		previousX = this.x;
		previousY = this.y;
		this.x = x;
		this.y = y;
		this.drag = drag;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getInitialX() {
		return initialX;
	}

	public float getInitialY() {
		return initialY;
	}

	public boolean isDrag() {
		return drag;
	}

	public float getDeltaX() {
		return x - previousX;
	}

	public float getDeltaY() {
		return y - previousY;
	}

	public boolean hasMoved() {
		return previousX != x || previousY != y;
	}

	public void reset() {
		x = previousX;
		y = previousY;
	}
}
