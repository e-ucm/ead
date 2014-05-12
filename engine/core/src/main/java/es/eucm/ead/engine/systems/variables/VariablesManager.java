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

import ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.Accessor;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperatorFactory;
import es.eucm.ead.schema.data.VariableDef;

import java.util.List;

/**
 * Deals with variables and expressions. Can set variable values (
 * {@link #setValue(String, String)}), which triggers notifications through
 * {@link VariableListener}. It also can evaluate expressions (
 * {@link #evaluateExpression(String)}) and conditions (
 * {@link #evaluateCondition(String, boolean)}), which are just boolean
 * expressions.
 * 
 * It also handles local and global {@link VarsContext}. By default, it creates
 * a global {@code VarsContext} to hold user-defined variables and global scope
 * variables like current language ({@link VarsContext#LANGUAGE_VAR}). New local
 * contexts can be created to register local variables by calling
 * {@link #push()} and {@link #registerVar(String, Object)} afterwards. This is
 * useful for setting the current owner {@code Entity} that is being processed
 * as a variable ({@link VarsContext#THIS_VAR}) so its properties can be
 * referenced in expressions to be evaluated. Once the current local context is
 * not needed anymore, {@link #pop()} must be called to get it removed.
 * 
 * This is a typical usage example: Let's suppose the expression
 * "(hastag $_this sTag1)" has to be evaluated, where "$_this" refers to the
 * entity that wherever holds the expression. First, the {@link #push()} must be
 * invoked to create a new local context on top of the current varsContext,
 * which may be the global context if no other local context has been pushed or
 * if those were already popped out. Then {@link #registerVar(String, Object)}
 * or simply {@link #localOwnerVar(ashley.core.Entity)} can be called to setup
 * the entity variable in the recently created context. Since these methods
 * return the same {@code VariablesManager}, calls can be chained.
 * 
 * Then, {@code evaluateCondition("(hastag $_this sTag1)")} can be called and
 * the system will be able to resolve $_this, since it has been registered as a
 * local variable.
 * 
 * Finally, once the result of the condition evaluation is returned, the local
 * context must be popped since it is not needed anymore using {@link #pop()}.
 * 
 * <pre>
 *     VariablesManager variablesManager = ...
 *     String expression = "(hastag $_this sTag1)";
 *     Entity owner = ... //Entity that holds the expression. For example, if the expression is in an effect, it may be the entity that contains the EffectsComponent.
 * 
 *     boolean conditionResult =
 *          variablesManager.push().localOwnerVar(owner).evaluateCondition(expression, false);
 *     variablesManager.pop();
 * 
 *     if (conditionResult){
 *         ...
 *     } else {
 *         ...
 *     }
 * </pre>
 */
public class VariablesManager {

	private static final String LOG_TAG = "VariablesManager";

	private Array<VariableListener> listeners;

	private VarsContext varsContext;

	private VarsContext globalContext;

	private ObjectMap<String, Expression> expressionMap;

	private OperatorFactory operatorFactory;

	public VariablesManager(Accessor accessor) {
		this.operatorFactory = new OperatorFactory(accessor);
		this.varsContext = Pools.obtain(VarsContext.class);
		this.globalContext = this.varsContext;
		this.expressionMap = new ObjectMap<String, Expression>();
		this.listeners = new Array<VariableListener>();
		registerReservedVars();
	}

	/**
	 * Creates a new local {@code VarsContext} on top of current
	 * {@link #varsContext}. Example: At any moment, the {@code VarsContext}
	 * stack available may look like this:
	 * 
	 * <pre>
	 *     varsContext-->localContext1
	 *                        |
	 *                   localContext2
	 *                        |
	 *                       ...
	 *                        |
	 *                   globalContext
	 * </pre>
	 * 
	 * When {@link #push()} is called, then a new context is created and it is
	 * linked to the context on top:
	 * 
	 * <pre>
	 *     varsContext-->newLocalContext
	 *                        |
	 *                   localContext1
	 *                        |
	 *                   localContext2
	 *                        |
	 *                       ...
	 *                        |
	 *                   globalContext
	 * </pre>
	 * 
	 * New local contexts predominate over other existing contexts when
	 * resolving variables, since the resolving process always starts by the
	 * context on top. If the variable is not found, then the next context is
	 * checked. The global context is always the latest to be checked.
	 * 
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object)} and
	 *         {@link #setValue(String, String)} calls can be chained.
	 */
	public VariablesManager push() {
		VarsContext newLocalContext = Pools.obtain(VarsContext.class);
		newLocalContext.setParent(varsContext);
		varsContext = newLocalContext;
		return this;
	}

	/**
	 * Removes the current local {@code VarsContext} on top so its variables
	 * won't be available. Example: Assuming the current {@code VarsContext}
	 * stack looks like this:
	 * 
	 * <pre>
	 *     varsContext-->localContext1
	 *                        |
	 *                   localContext2
	 *                        |
	 *                       ...
	 *                        |
	 *                   globalContext
	 * </pre>
	 * 
	 * Calling {@link #pop()} results in getting localContext1 freed and setting
	 * varsContext pointer to localContext2:
	 * 
	 * <pre>
	 *     varsContext-->localContext2
	 *                        |
	 *                       ...
	 *                        |
	 *                   globalContext
	 * </pre>
	 * 
	 * However, this operation is not supported if varsContext is pointing to
	 * globalContext. If that's the case, an exception is thrown. This prevents
	 * the global context getting removed by accident. Should this be attempted.
	 * 
	 * @throws java.lang.UnsupportedOperationException
	 *             If there is no local context to be popped out, since the
	 *             global context cannot be removed.
	 */
	public void pop() {
		if (varsContext != globalContext) {
			VarsContext parent = varsContext.removeParent();
			Pools.free(varsContext);
			varsContext = parent;
		} else {
			Gdx.app.debug(LOG_TAG, "Cannot pop the global context!");
			throw new UnsupportedOperationException(
					"Cannot pop the global context! You may need to call push() first.");
		}
	}

	/**
	 * Registers a new var to the current local vars context on top so it can be
	 * used in further expression or condition evaluation.
	 * 
	 * @param name
	 *            The name of the variable. Examples:
	 *            {@link VarsContext#THIS_VAR},
	 *            {@link VarsContext#RESERVED_ENTITY_VAR}.
	 * @param value
	 *            The object value for the variable.
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object)} and
	 *         {@link #setValue(String, String)} calls can be chained.
	 */
	public VariablesManager registerVar(String name, Object value) {
		varsContext.registerVariable(name, value);
		notify(name, value);
		return this;
	}

	/**
	 * Register a list of variables in the system
	 * 
	 * @param variablesDefinitions
	 *            a list with the variables definitions
	 * @throws IllegalArgumentException
	 *             If any of the user-defined variables starts with reserved
	 *             prefix {@value VarsContext#RESERVED_VAR_PREFIX}.
	 */
	public void registerVariables(List<VariableDef> variablesDefinitions) {
		for (VariableDef variableDef : variablesDefinitions) {
			// Check the user-defined variable does not start with the reserved
			// prefix
			if (variableDef.getName().startsWith(
					VarsContext.RESERVED_VAR_PREFIX)) {
				String message = "Illegal user-defined variable "
						+ variableDef.getName()
						+ ": it cannot start with reserved prefix "
						+ VarsContext.RESERVED_VAR_PREFIX;
				Gdx.app.debug(LOG_TAG, message);
				throw new IllegalArgumentException(message);
			}

			varsContext.registerVariable(variableDef);
			notify(variableDef.getName(),
					varsContext.getValue(variableDef.getName()));
		}
	}

	/**
	 * Registers the given {@code owner} entity to the "$_this" reserved local
	 * variable.
	 * 
	 * @param owner
	 *            The entity that owns the expression. Registering the owner
	 *            entity as a variable allows the expression to resolve entity's
	 *            properties (e.g. a given tag) by using "$_this".
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object)} and
	 *         {@link #setValue(String, String)} calls can be chained.
	 */
	public VariablesManager localOwnerVar(Entity owner) {
		registerVar(VarsContext.THIS_VAR, owner);
		return this;
	}

	/**
	 * Registers the {@code otherEntity} to the "$_entity" reserved variable.
	 * 
	 * @param otherEntity
	 *            Other entity whose properties may be needed for later
	 *            expression evaluation.
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object)} and
	 *         {@link #setValue(String, String)} calls can be chained.
	 */
	public VariablesManager localEntityVar(Entity otherEntity) {
		registerVar(VarsContext.RESERVED_ENTITY_VAR, otherEntity);
		return this;
	}

	/**
	 * Adds a variable listener. Will be notified of variables changes when
	 * method {@link VariablesManager.VariableListener#listensTo(String)}
	 * returns true
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
	 * Evaluates the given {@code expression} and assigns the resulting value to
	 * the given {@code variable}. If the variable is actually assigned,
	 * listeners are notified immediately
	 * 
	 * @param variable
	 *            the variable name. Cannot be {@code null}.
	 * @param expression
	 *            a valid expression. Cannot be {@code null}.
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object)} and
	 *         {@link #setValue(String, String)} calls can be chained.
	 */
	public VariablesManager setValue(String variable, String expression) {
		if (variable != null) {
			Object value = evaluateExpression(expression);
			if (value != null) {
				Object oldValue = varsContext.getValue(variable);
				if (!value.equals(oldValue)) {
					varsContext.setValue(variable, value);
					notify(variable, varsContext.getValue(variable));
				}
			}
		}

		else {
			Gdx.app.error(LOG_TAG,
					"Error setting value for variable: It cannot be null");
		}
		return this;
	}

	/**
	 * Evaluates an anonymous expression and returns the value obtained.
	 * 
	 * @param expression
	 *            A valid not-null expression (see the wiki for more details on
	 *            valid expressions).
	 */
	public Object evaluateExpression(String expression) {
		if (expression != null) {
			// Variable assignation
			Expression e = expressionMap.get(expression);
			if (e == null) {
				e = Parser.parse(expression, operatorFactory);
				expressionMap.put(expression, e);
			}

			try {
				Object value = e.evaluate(varsContext);
				return value;
			} catch (ExpressionEvaluationException e1) {
				Gdx.app.error(LOG_TAG, "Error evaluating " + expression, e1);
			}

		} else {
			Gdx.app.error(
					LOG_TAG,
					"Error setting value for variable: Neither variable nor expression should be null");
		}
		return null;
	}

	/**
	 * Convenient method for evaluating boolean expressions. Useful for checking
	 * conditions in {@link es.eucm.ead.engine.components.ConditionedComponent}
	 * s.
	 * 
	 * @param expression
	 *            The boolean expression
	 * @param defaultValue
	 *            The value to be returned if the expression is null (usually
	 *            because it was not defined in the model) or if the expression
	 *            cannot be evaluated to a boolean
	 * @return The result of the evaluation
	 */
	public boolean evaluateCondition(String expression, boolean defaultValue) {
		if (expression == null)
			return defaultValue;

		Object result = evaluateExpression(expression);

		if (result == null)
			return defaultValue;

		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		} else if (result instanceof Integer) {
			return ((Integer) result).intValue() > 0;
		} else {
			return defaultValue;
		}
	}

	/**
	 * Re-initializes the system
	 */
	public void reset() {
		// Pop all local contexts, if any
		while (globalContext != varsContext) {
			pop();
		}
		// Clear variables
		globalContext.reset();
		// Register default variables (e.g. lang)
		registerReservedVars();
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
	 * Just puts into the global context reserved variables so they can be
	 * accessed by others
	 */
	private void registerReservedVars() {
		globalContext.registerVariable(VarsContext.LANGUAGE_VAR,
				I18N.DEFAULT_LANGUAGE);
	}

	/**
	 * Listener for changes in variables.
	 */
	public interface VariableListener {

		/**
		 * @return whether this listener is interested in the given variable
		 */
		boolean listensTo(String variableName);

		/**
		 * Notifies a variable change. Gets updated when the value for a
		 * variable changes.
		 * 
		 * @param variableName
		 *            the variable name
		 * @param value
		 *            the new value for the variable
		 */
		void variableChanged(String variableName, Object value);

	}
}
