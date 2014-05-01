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
package es.eucm.ead.engine.systems.variables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.schema.data.VariableDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all game variables
 */
public class VarsContext {

	/**
	 * Prefix for variables are created and managed by the engine. This is a
	 * naming convention, intended to avoid name clashes with user-defined
	 * variables.
	 */
	public static final String RESERVED_VAR_PREFIX = "_";

	/**
	 * Prefix for variables that are always copied to (and from) subroutines.
	 * This makes them "global" in the traditional programming sense. Note that
	 * global variables are all reserved.
	 */
	public static final String GLOBAL_VAR_PREFIX = RESERVED_VAR_PREFIX + "g_";

	/**
	 * Current game language. See @see es.eucm.ead.engine.I18N for details on
	 * possible values
	 */
	public static final String LANGUAGE_VAR = GLOBAL_VAR_PREFIX + "lang";

	private Map<String, Variable> variables;

	public VarsContext() {
		variables = new HashMap<String, Variable>();
	}

	}

	/**
	 * Registers the given list variables
	 * 
	 * @param vars
	 *            a list with variables
	 */
	public void registerVariables(List<VariableDef> vars) {
		for (VariableDef v : vars) {
			registerVariable(v);
		}
	}

	/**
	 * Register the given variable and sets its value to an initial value. The
	 * initial value of the variable determines the variable type. Types and
	 * names cannot change during execution.
	 * 
	 * @param v
	 *            the variable
	 */
	public void registerVariable(VariableDef v) {
		variables.put(v.getName(),
				new Variable(v.getType(), v.getInitialValue()));
	}

	/**
	 * Register the given variable and sets its value to its initial value. The
	 * initial value of the variable determines the variable type. Types and
	 * names cannot change during execution.
	 * 
	 * @param name
	 *            the name of the variable
	 * @param value
	 *            to initialize it to; also used to infer type
	 */
	public void registerVariable(String name, Object value) {
		variables.put(name, new Variable(value, value.getClass()));
	}

	/**
	 * Register the given variable and sets its value to its initial value. As
	 * opposed to {@link #registerVariable(String, Object)}, the type is
	 * explicitly given as a parameter. This is useful for registering variables
	 * that extend abstract classes or implement interfaces, since their actual
	 * instantiation type may vary during execution, and types and names cannot
	 * change during execution for variables.
	 * 
	 * @param name
	 *            the name of the variable
	 * @param value
	 *            to initialize it to; Its type must be compatible with
	 *            {@code clazz}, otherwise an exception is thrown. Cannot be
	 *            {@code null}
	 * @param value
	 *            the type of the variable.
	 * @throws java.lang.IllegalArgumentException
	 *             If the types of {@code value} and {@code class} are not
	 *             compatible
	 * @throws java.lang.NullPointerException
	 *             If {@code value} is null
	 */
	public void registerVariable(String name, Object value, Class clazz) {
		if (!ClassReflection.isAssignableFrom(clazz, value.getClass())) {
			throw new IllegalArgumentException(
					"Types of value and class provided are not compatible");
		}
		variables.put(name, new Variable(value, clazz));
	}

	/**
	 * Sets the value for the variable with given name
	 * 
	 * @param name
	 *            variable name
	 * @param value
	 *            variable value
	 */
	public void setValue(String name, Object value) {
		Variable var = getVariable(name);
		if (var != null) {
			if (!var.setValue(value)) {
				Gdx.app.error("VarsContext", "Cannot set value of " + name
						+ " to " + value + ": bad value");
			}
		} else {
			Gdx.app.error("VarsContext", "Cannot set value of " + name + " to "
					+ value + ": no such variable found");
		}
	}

	/**
	 * @param name
	 * @return true if the variable named 'name' exists
	 */
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}

	/**
	 * @param name
	 *            variable name
	 * @return Returns the variable with the given name
	 */
	public Variable getVariable(String name) {
		try {
			Variable value = variables.get(name);
			if (value == null) {
				Gdx.app.error("VarsContext", "No variable with name " + name
						+ ": returning 'null'.");
			}
			return value;
		} catch (ClassCastException e) {
			Gdx.app.error("VarsContext", "Invalid return type for variable "
					+ name + ": returning 'null'", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * @param name variable name
	 * @return the value for the variable
	 */
	public <T> T getValue(String name) {
		Variable variable = getVariable(name);
		return variable == null ? null : (T) variable.value;
	}

	/**
	 * Clears the vars context
	 */
	public void clear() {
		variables.clear();
	}

	/**
	 * Represents a variable during execution time
	 */
	public static class Variable {
		/**
		 * Type of the variable
		 */
		private final Class<?> type;

		/**
		 * Current value of the variable
		 */
		private Object value;

		public Variable(Object value, Class clazz) {
			this.type = clazz;
			this.value = value;
		}

		public Variable(VariableDef.Type type, String initialValue) {
			switch (type) {
			case BOOLEAN:
				this.type = Boolean.class;
				this.value = Boolean.parseBoolean(initialValue);
				break;
			case FLOAT:
				this.type = Float.class;
				this.value = Float.parseFloat(initialValue);
				break;
			case INTEGER:
				this.type = Integer.class;
				this.value = Integer.parseInt(initialValue);
				break;
			case STRING:
				this.type = String.class;
				this.value = initialValue;
				break;
			default:
				throw new IllegalArgumentException("Unknown VariableDef type: "
						+ type);
			}
		}

		/**
		 * 
		 * @return Type of the variable
		 */
		public Class<?> getType() {
			return type;
		}

		/**
		 * 
		 * @return the value of the variable
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * 
		 * @param value
		 *            the value
		 * @return if the value was setValue. (It returns false if type of the
		 *         variable is not compatible with the type of the value)
		 */
		public boolean setValue(Object value) {
			if (value == null
					|| ClassReflection.isAssignableFrom(type, value.getClass())) {
				this.value = value;
				return true;
			} else {
				Gdx.app.error("VarsContext", value + " is not assignable to "
						+ type);
				return false;
			}
		}
	}
}
