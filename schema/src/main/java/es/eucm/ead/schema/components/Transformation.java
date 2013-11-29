/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import javax.annotation.Generated;

/**
 * Contains a 2D transformation (position, scale, rotation and color)
 * 
 */
@Generated("org.jsonschema2pojo")
public class Transformation {

	private Color color;
	/**
	 * Rotation of the transformation (in degrees)
	 * 
	 */
	private float rotation = 0.0F;
	/**
	 * Scale in the x axis
	 * 
	 */
	private float scaleX = 1.0F;
	/**
	 * Scale in the y axis
	 * 
	 */
	private float scaleY = 1.0F;
	/**
	 * x coordinate of the transformation
	 * 
	 */
	private float x = 0.0F;
	/**
	 * y coordinate of the transformation
	 * 
	 */
	private float y = 0.0F;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Rotation of the transformation (in degrees)
	 * 
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * Rotation of the transformation (in degrees)
	 * 
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	/**
	 * Scale in the x axis
	 * 
	 */
	public float getScaleX() {
		return scaleX;
	}

	/**
	 * Scale in the x axis
	 * 
	 */
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * Scale in the y axis
	 * 
	 */
	public float getScaleY() {
		return scaleY;
	}

	/**
	 * Scale in the y axis
	 * 
	 */
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * x coordinate of the transformation
	 * 
	 */
	public float getX() {
		return x;
	}

	/**
	 * x coordinate of the transformation
	 * 
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * y coordinate of the transformation
	 * 
	 */
	public float getY() {
		return y;
	}

	/**
	 * y coordinate of the transformation
	 * 
	 */
	public void setY(float y) {
		this.y = y;
	}

}
