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

import ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperatorFactory;
import es.eucm.ead.engine.systems.variables.VarsContext.Variable;
import es.eucm.ead.schema.components.VariableDef;

import java.util.List;
import java.util.Map.Entry;

/**
 * Deals with variables. Can set values and handles {@link VariableListener}
 */
public class VariablesSystem extends EntitySystem {

	private Array<VariableListener> listeners;

	private VarsContext varsContext;

	private ObjectMap<String, Expression> expressionMap;

	private Array<String> pendingToEvaluate;

	private OperatorFactory operatorFactory;

	public VariablesSystem() {
		this.operatorFactory = new OperatorFactory();
		this.varsContext = new VarsContext();
		this.expressionMap = new ObjectMap<String, Expression>();
		this.pendingToEvaluate = new Array<String>();
		this.listeners = new Array<VariableListener>();
		this.sleeping = true;
	}

	/**
	 * Adds a variable listener. Will be notified of variables changes when
	 * method {@link VariableListener#listensTo(String)} returns true
	 * 
	 * @param variableListener
	 *            the listener
	 */
	public void addListener(VariableListener variableListener) {
		listeners.add(variableListener);
	}

	/**
	 * @return the variable value.If variable does not exist, returns
	 *         {@code null}
	 */
	public Object getValue(String variable) {
		return varsContext.getValue(variable);
	}

	/**
	 * Sets the variable to the value obtained of parsing the given expression.
	 * The value is set the next time {@link #update(float)} is called
	 * 
	 * @param variable
	 *            the variable name
	 * @param expression
	 *            a valid expression for the value
	 */
	public void setValue(String variable, String expression) {
		if (variable != null && expression != null) {
			pendingToEvaluate.add(variable);
			pendingToEvaluate.add(expression);
			this.sleeping = false;
		} else {
			Gdx.app.error(
					"VariablesSystem",
					"Error setting value for variable: Neither variable nor expression should be null");
		}
	}

	@Override
	public void update(float deltaTime) {
		this.sleeping = true;
		while (pendingToEvaluate.size > 0) {
			String variable = pendingToEvaluate.removeIndex(0);
			String expression = pendingToEvaluate.removeIndex(0);

			Expression e = expressionMap.get(expression);
			if (e == null) {
				e = Parser.parse(expression, operatorFactory);
				expressionMap.put(expression, e);
			}

			try {
				Object value = e.evaluate(varsContext);
				varsContext.setValue(variable, value);
				notify(variable, value);
			} catch (ExpressionEvaluationException e1) {
				Gdx.app.error("VariablesSystem", "Error evaluating "
						+ expression, e1);
			}
		}
	}

	/**
	 * Notifies listeners a change in a variable
	 */
	private void notify(String variable, Object value) {
		for (VariableListener listener : listeners) {
			if (listener.listensTo(variable)) {
				listener.variableChanged(variable, value);
			}
		}
	}

	/**
	 * Removes all variables from the system
	 */
	public void clear() {
		varsContext.clear();
	}

	/**
	 * Register a list of variables in the system
	 * 
	 * @param variablesDefinitions
	 *            a list with the variables definitions
	 */
	public void registerVariables(List<VariableDef> variablesDefinitions) {
		varsContext.registerVariables(variablesDefinitions);
		for (Entry<String, Variable> e : varsContext.getVariables().entrySet()) {
			notify(e.getKey(), e.getValue().getValue());
		}
	}

	/**
	 * Listener for variables changes
	 */
	public interface VariableListener {

		/**
		 * @return whether this listener is interested in the given variable
		 */
		boolean listensTo(String variableName);

		/**
		 * Notifies a variable change
		 * 
		 * @param variableName
		 *            the variable name
		 * @param value
		 *            the new value for the variable
		 */
		void variableChanged(String variableName, Object value);

	}
}
