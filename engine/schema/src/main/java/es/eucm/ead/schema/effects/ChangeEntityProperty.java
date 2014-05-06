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

package es.eucm.ead.schema.effects;

import javax.annotation.Generated;

/**
 * changes a property related to an entity using Accessor.
 * 
 */
@Generated("org.jsonschema2pojo")
public class ChangeEntityProperty extends Effect {

	/**
	 * An accessor expression identifying the property to be modified. Examples:
	 * group.x, components<visibility>. These expressions should be relative to
	 * the target entity (Required)
	 * 
	 */
	private String property;
	/**
	 * An expression used to determine the new value of the property
	 * 
	 */
	private String expression;

	/**
	 * An accessor expression identifying the property to be modified. Examples:
	 * group.x, components<visibility>. These expressions should be relative to
	 * the target entity (Required)
	 * 
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * An accessor expression identifying the property to be modified. Examples:
	 * group.x, components<visibility>. These expressions should be relative to
	 * the target entity (Required)
	 * 
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * An expression used to determine the new value of the property
	 * 
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * An expression used to determine the new value of the property
	 * 
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

}
