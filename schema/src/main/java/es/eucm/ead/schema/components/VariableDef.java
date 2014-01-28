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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * A variable's definition in the game. Its type is the same as the initial
 * value.
 * 
 */
@Generated("org.jsonschema2pojo")
public class VariableDef {

	/**
	 * Name of the variable
	 * 
	 */
	private String name;
	/**
	 * One of 'string' 'float' 'integer' 'boolean'
	 * 
	 */
	private VariableDef.Type type;
	/**
	 * Initial value for the variable.
	 * 
	 */
	private String initialValue;

	/**
	 * Name of the variable
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Name of the variable
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * One of 'string' 'float' 'integer' 'boolean'
	 * 
	 */
	public VariableDef.Type getType() {
		return type;
	}

	/**
	 * One of 'string' 'float' 'integer' 'boolean'
	 * 
	 */
	public void setType(VariableDef.Type type) {
		this.type = type;
	}

	/**
	 * Initial value for the variable.
	 * 
	 */
	public String getInitialValue() {
		return initialValue;
	}

	/**
	 * Initial value for the variable.
	 * 
	 */
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	@Generated("org.jsonschema2pojo")
	public static enum Type {

		STRING("string"), FLOAT("float"), INTEGER("integer"), BOOLEAN("boolean");
		private final String value;
		private static Map<String, VariableDef.Type> constants = new HashMap<String, VariableDef.Type>();

		static {
			for (VariableDef.Type c : VariableDef.Type.values()) {
				constants.put(c.value, c);
			}
		}

		private Type(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static VariableDef.Type fromValue(String value) {
			VariableDef.Type constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
