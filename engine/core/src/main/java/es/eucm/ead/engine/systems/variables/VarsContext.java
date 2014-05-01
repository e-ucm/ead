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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.schema.data.VariableDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all game variables
 */
public class VarsContext implements Pool.Poolable {

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

	/**
	 * Reserved keyword for the owner entity. It is a local variable (changes
	 * over time depending on what entity is being processed).
	 */
	public static final String THIS_VAR = RESERVED_VAR_PREFIX + "this";

	/**
	 * Reserved keyword for other entity involved in any expression or condition
	 * evaluation. It is a local variable.
	 */
	public static final String RESERVED_ENTITY_VAR = RESERVED_VAR_PREFIX
			+ "entity";

	private Map<String, Variable> variables;

	private VarsContext child;

	public VarsContext() {
		variables = new HashMap<String, Variable>();
		child = null;
	}

	/**
	 * Sets the child for this context. If child is not null, it is used to
	 * resolve variables that are not present in this varsContext.
	 * 
	 * @param localContext
	 *            The localContext to be added as a child to the current
	 *            context.
	 */
	public void setChild(VarsContext localContext) {
		this.child = localContext;
	}

	/**
	 * Sets child to null and returns the old child value
	 * 
	 * @return The VarsContext that was stored in {@link #child}
	 */
	public VarsContext removeChild() {
		VarsContext oldChild = child;
		child = null;
		return oldChild;
	}

	/**
	 * Clears and frees all the variables in this context.
	 * 
	 * Also sets child to {@code null} although it is not freed, just in case
	 * {@code VariablesSystem} needs to use it.
	 */
	@Override
	public void reset() {
		child = null;
		for (Variable variable : variables.values()) {
			Pools.free(variable);
		}
		variables.clear();
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
		Variable newVariable = Pools.obtain(Variable.class);
		newVariable.set(v.getType(), v.getInitialValue());
		variables.put(v.getName(), newVariable);
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
		Variable newVariable = Pools.obtain(Variable.class);
		newVariable.set(value, value.getClass());
		variables.put(name, newVariable);
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
		Variable newVariable = Pools.obtain(Variable.class);
		newVariable.set(value, clazz);
		variables.put(name, newVariable);
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
	 * @return true if the variable named 'name' exists. First, it checks its
	 *         own variables. If not found, propagates the call down the tree.
	 */
	public boolean hasVariable(String name) {
		if (variables.containsKey(name)) {
			return true;
		} else if (child != null) {
			return child.hasVariable(name);
		}
		return false;
	}

	/**
	 * @param name
	 *            variable name
	 * @return Returns the variable with the given name. If the variable is not
	 *         present in this context, the call is propagated down the tree.
	 */
	public Variable getVariable(String name) {
		try {
			Variable value = variables.get(name);
			if (value == null) {
				if (child != null) {
					return child.getVariable(name);
				} else {
					Gdx.app.error("VarsContext", "No variable with name "
							+ name + ": returning 'null'.");
				}
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
	 * Represents a variable during execution time
	 */
	public static class Variable implements Pool.Poolable {
		/**
		 * Type of the variable
		 */
		private Class<?> type;

		/**
		 * Current value of the variable
		 */
		private Object value;

		public Variable() {

		}

		public void set(Object value, Class clazz) {
			this.type = clazz;
			this.value = value;
		}

		public void set(VariableDef.Type type, String initialValue) {
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
		 * @return if the value was set. (It returns false if type of the
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

		@Override
		public void reset() {
			value = null;
			type = null;
		}
	}
}
