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

package es.eucm.ead.schema.components.tweens;

import javax.annotation.Generated;

/**
 * Tweens the position of an entity
 * 
 */
@Generated("org.jsonschema2pojo")
public class FieldTween extends Tween {

	/**
	 * Fully qualified name of the component class
	 * 
	 */
	private String component;
	/**
	 * Name of the filed to tween
	 * 
	 */
	private String field;
	/**
	 * target for the field
	 * 
	 */
	private float target;

	/**
	 * Fully qualified name of the component class
	 * 
	 */
	public String getComponent() {
		return component;
	}

	/**
	 * Fully qualified name of the component class
	 * 
	 */
	public void setComponent(String component) {
		this.component = component;
	}

	/**
	 * Name of the filed to tween
	 * 
	 */
	public String getField() {
		return field;
	}

	/**
	 * Name of the filed to tween
	 * 
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * target for the field
	 * 
	 */
	public float getTarget() {
		return target;
	}

	/**
	 * target for the field
	 * 
	 */
	public void setTarget(float target) {
		this.target = target;
	}

}
