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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ChangeVar extends Effect {

	/**
	 * The name of the variable to change. If the variable does not exist, it is
	 * created before being set. (Required)
	 * 
	 */
	private String variable;
	/**
	 * Expression that defines the new value of the variable
	 * 
	 */
	private String expression;
	/**
	 * Allows defining and accessing variables that are either local or global.
	 * 
	 */
	private ChangeVar.Context context = ChangeVar.Context.fromValue("local");

	/**
	 * The name of the variable to change. If the variable does not exist, it is
	 * created before being set. (Required)
	 * 
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * The name of the variable to change. If the variable does not exist, it is
	 * created before being set. (Required)
	 * 
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * Expression that defines the new value of the variable
	 * 
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Expression that defines the new value of the variable
	 * 
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Allows defining and accessing variables that are either local or global.
	 * 
	 */
	public ChangeVar.Context getContext() {
		return context;
	}

	/**
	 * Allows defining and accessing variables that are either local or global.
	 * 
	 */
	public void setContext(ChangeVar.Context context) {
		this.context = context;
	}

	@Generated("org.jsonschema2pojo")
	public static enum Context {

		LOCAL("local"), GLOBAL("global");
		private final String value;
		private static Map<String, ChangeVar.Context> constants = new HashMap<String, ChangeVar.Context>();

		static {
			for (ChangeVar.Context c : ChangeVar.Context.values()) {
				constants.put(c.value, c);
			}
		}

		private Context(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static ChangeVar.Context fromValue(String value) {
			ChangeVar.Context constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
