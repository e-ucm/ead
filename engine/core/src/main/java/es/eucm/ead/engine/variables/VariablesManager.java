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
package es.eucm.ead.engine.variables;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperationsFactory;

/**
 * Deals with variables and expressions. Can set variable values (
 * {@link #setVarToExpression(String, String)}), which triggers notifications
 * through {@link VariableListener}. It also can evaluate expressions (
 * {@link #evaluateExpression(String)}) and conditions (
 * {@link #evaluateCondition(String, boolean)}), which are just boolean
 * expressions.
 * <p/>
 * It also handles local and global {@link VarsContext}. By default, it creates
 * a global {@code VarsContext} to hold user-defined variables and global scope
 * variables like current language ({@link VarsContext#LANGUAGE_VAR}). New local
 * contexts can be created to register local variables by calling
 * {@link #push()} and {@link #registerVar(String, Object, boolean)} afterwards.
 * This is useful for setting the current owner {@code Entity} that is being
 * processed as a variable ({@link VarsContext#THIS_VAR}) so its properties can
 * be referenced in expressions to be evaluated. Once the current local context
 * is not needed anymore, {@link #pop()} must be called to get it removed.
 * <p/>
 * This is a typical usage example: Let's suppose the expression
 * "(hastag $_this sTag1)" has to be evaluated, where "$_this" refers to the
 * entity that wherever holds the expression. First, the {@link #push()} must be
 * invoked to create a new local context on top of the current varsContext,
 * which may be the global context if no other local context has been pushed or
 * if those were already popped out. Then
 * {@link #registerVar(String, Object, boolean)} or simply
 * {@link #localOwnerVar(com.badlogic.ashley.core.Entity)} can be called to
 * setup the entity variable in the recently created context. Since these
 * methods return the same {@code VariablesManager}, calls can be chained.
 * <p/>
 * Then, {@code evaluateCondition("(hastag $_this sTag1)")} can be called and
 * the system will be able to resolve $_this, since it has been registered as a
 * local variable.
 * <p/>
 * Finally, once the result of the condition evaluation is returned, the local
 * context must be popped since it is not needed anymore using {@link #pop()}.
 * <p/>
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

	public static final String EXPRESSION_DELIMITER = "#";
	public static final String VAR_PREFIX = "$";

	private Array<VariableListener> listeners;

	private VarsContext varsContext;

	private VarsContext globalContext;

	private ObjectMap<String, Expression> expressionMap;

	private OperationsFactory operationsFactory;

	private Accessor accessor;

	public VariablesManager(Accessor accessor,
			OperationsFactory operationsFactory) {
		this.accessor = accessor;
		this.operationsFactory = operationsFactory;
		this.varsContext = Pools.obtain(VarsContext.class);
		this.globalContext = this.varsContext;
		this.expressionMap = new ObjectMap<String, Expression>();
		this.listeners = new Array<VariableListener>();
		registerReservedVars();
	}

	/**
	 * Returns the {@link Accessor} tool, which can be used to get or set any
	 * property at runtime
	 */
	public Accessor getAccessor() {
		return accessor;
	}

	/**
	 * Creates a new local {@code VarsContext} on top of current
	 * {@link #varsContext}. Example: At any moment, the {@code VarsContext}
	 * stack available may look like this:
	 * <p/>
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
	 * <p/>
	 * When {@link #push()} is called, then a new context is created and it is
	 * linked to the context on top:
	 * <p/>
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
	 * <p/>
	 * New local contexts predominate over other existing contexts when
	 * resolving variables, since the resolving process always starts by the
	 * context on top. If the variable is not found, then the next context is
	 * checked. The global context is always the latest to be checked.
	 * 
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object, boolean)} and
	 *         {@link #setVarToExpression(String, String)} calls can be chained.
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
	 * <p/>
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
	 * <p/>
	 * Calling {@link #pop()} results in getting localContext1 freed and setting
	 * varsContext pointer to localContext2:
	 * <p/>
	 * 
	 * <pre>
	 *     varsContext-->localContext2
	 *                        |
	 *                       ...
	 *                        |
	 *                   globalContext
	 * </pre>
	 * <p/>
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
	 * Registers a new var so it can be used in further expression or condition
	 * evaluation.
	 * <p/>
	 * The context of the variable is determined by argument {@code global}. If
	 * true, the global context is used and the variable will be persistent. If
	 * false, the local context on top of the stack is used, and the variable
	 * will be volatile.
	 * 
	 * @param name
	 *            The name of the variable. Examples:
	 *            {@link VarsContext#THIS_VAR},
	 *            {@link VarsContext#RESERVED_ENTITY_VAR}.
	 * @param value
	 *            The object value for the variable.
	 * @param global
	 *            If true, the variable is global, if false it is local
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object, boolean)} and
	 *         {@link #setVarToExpression(String, String)} calls can be chained.
	 */
	public VariablesManager registerVar(String name, Object value,
			boolean global) {
		if (global) {
			globalContext.registerVariable(name, value);
		} else {
			varsContext.registerVariable(name, value);
		}
		notify(name, value);
		return this;
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
	 *         {@link #registerVar(String, Object, boolean)} and
	 *         {@link #setVarToExpression(String, String)} calls can be chained.
	 */
	public VariablesManager localOwnerVar(Entity owner) {
		registerVar(VarsContext.THIS_VAR, owner, false);
		return this;
	}

	/**
	 * Registers the {@code otherEntity} to the "
	 * {@value VarsContext#RESERVED_ENTITY_VAR}" reserved variable.
	 * 
	 * @param otherEntity
	 *            Other entity whose properties may be needed for later
	 *            expression evaluation.
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object, boolean)} and
	 *         {@link #setVarToExpression(String, String)} calls can be chained.
	 */
	public VariablesManager localEntityVar(Entity otherEntity) {
		registerVar(VarsContext.RESERVED_ENTITY_VAR, otherEntity, false);
		return this;
	}

	/**
	 * Registers the {@code newestEntity} to the "
	 * {@value VarsContext#RESERVED_NEWEST_ENTITY_VAR}" reserved variable.
	 * 
	 * @param newestEntity
	 *            A global variable that points to the newest entity (the entity
	 *            added in last place). This variable may point to null if the
	 *            newest entity is removed
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object, boolean)} and
	 *         {@link #setVarToExpression(String, String)} calls can be chained.
	 */
	public VariablesManager globalNewestEntityVar(Entity newestEntity) {
		varsContext.setValue(VarsContext.RESERVED_NEWEST_ENTITY_VAR,
				newestEntity);
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
		if (!listeners.contains(variableListener, true)) {
			listeners.add(variableListener);
		}
	}

	/**
	 * Removes the given listener from the listeners list
	 * 
	 * @return if the value was found and removed
	 */
	public boolean removeListener(VariableListener variableListener) {
		return listeners.removeValue(variableListener, true);
	}

	/**
	 * @return the variable value. If variable does not exist, returns
	 *         {@code null}
	 */
	public Object getValue(String variable) {
		return varsContext.getValue(variable);
	}

	/**
	 * @return true if the variable with the given name exists, false otherwise
	 */
	public boolean isVariableDefined(String variable) {
		return getValue(variable) != null;
	}

	/**
	 * Sets the given variable locally.
	 * <p/>
	 * Equivalent to setVarToExpression(variable, expression, false).
	 */
	public VariablesManager setVarToExpression(String variable,
			String expression) {
		return setVarToExpression(variable, expression, false);
	}

	/**
	 * Evaluates the given {@code expression} and assigns the resulting value to
	 * the given {@code variable}. If the variable does not exist, it is
	 * created, locally or globally, depending on the {@code global} argument.
	 * <p/>
	 * If the variable is actually assigned, listeners are notified immediately
	 * 
	 * @param variable
	 *            the variable name. Cannot be {@code null}.
	 * @param expression
	 *            a valid expression. Cannot be {@code null}.
	 * @param global
	 *            True if global context must be used, false if local context
	 *            must be used.
	 * @return This VariablesManager so {@link #push()},
	 *         {@link #registerVar(String, Object, boolean)} and
	 *         {@link #setVarToExpression(String, String)} calls can be chained.
	 */
	public VariablesManager setVarToExpression(String variable,
			String expression, boolean global) {
		Object value = evaluateExpression(expression);
		setValue(variable, value, global);
		return this;
	}

	/**
	 * Assigns the given value to the given local {@code variable}. If the
	 * variable does not exist, it is created, locally or globally, depending on
	 * the {@code global} argument.
	 * <p/>
	 * If the variable is actually assigned, listeners are notified immediately
	 * 
	 * @param variable
	 *            the variable name. Cannot be {@code null}.
	 * @param value
	 *            value for teh variable
	 */
	public void setValue(String variable, Object value) {
		setValue(variable, value, false);
	}

	/**
	 * Assigns the given value to the given {@code variable}. If the variable
	 * does not exist, it is created, locally or globally, depending on the
	 * {@code global} argument.
	 * <p/>
	 * If the variable is actually assigned, listeners are notified immediately
	 * 
	 * @param variable
	 *            the variable name. Cannot be {@code null}.
	 * @param value
	 *            value for teh variable
	 * @param global
	 *            True if global context must be used, false if local context
	 *            must be used.
	 */
	public void setValue(String variable, Object value, boolean global) {
		VarsContext contextToUse = global ? globalContext : varsContext;
		if (variable != null) {
			if (value != null) {
				// Check variable is registered
				if (!contextToUse.hasVariable(variable)) {
					contextToUse.registerVariable(variable, value);
					notify(variable, contextToUse.getValue(variable));
				} else {
					Object oldValue = contextToUse.getValue(variable);
					if (!value.equals(oldValue)) {
						contextToUse.setValue(variable, value);
						notify(variable, contextToUse.getValue(variable));
					}
				}
			}
		} else {
			Gdx.app.error(LOG_TAG,
					"Error setting value for variable: It cannot be null");
		}
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
				e = Parser.parse(expression, operationsFactory);
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
		// use a dummy value; the engine initializer will overwrite it later
		globalContext.registerVariable(VarsContext.LANGUAGE_VAR, "");
		globalContext.registerVariable(VarsContext.RESERVED_NEWEST_ENTITY_VAR,
				null, EngineEntity.class);
	}

	/**
	 * Reads the expressions and variables contained in the given text
	 */
	public void readExpressions(String text, Array<String> expressions,
			Array<String> variables) {
		variables.clear();
		expressions.clear();
		int i = -2;
		while (i < text.length() && i != -1) {
			i = text.indexOf(EXPRESSION_DELIMITER, i + 1);
			if (i != -1) {
				int end = text.indexOf(EXPRESSION_DELIMITER, i + 1);
				if (end != -1) {
					String expression = text.substring(i + 1, end);
					if (!(expressions.contains(expression, false))) {
						expressions.add(expression);
					}
					readVariables(expression, variables);
					i = end + 1;
				} else {
					i = -1;
				}
			}
		}
	}

	private void readVariables(String expression, Array<String> variables) {
		int i = -2;
		while (i < expression.length() && i != -1) {
			i = expression.indexOf(VAR_PREFIX, i);
			if (i != -1) {
				int end = expression.indexOf('(', i + 1);

				int parenthesis = expression.indexOf(')', i + 1);
				if (parenthesis != -1) {
					end = end == -1 ? parenthesis : Math.min(end, parenthesis);
				}
				int space = expression.indexOf(' ', i + 1);
				if (space != -1) {
					end = end == -1 ? space : Math.min(end, space);
				}

				if (end == -1) {
					end = expression.length();
				}

				String varName = expression.substring(i + 1, end);
				if (!(variables.contains(varName, false))) {
					variables.add(varName);
				}
				i = end + 1;
			}
		}
	}

	/**
	 * @return the given text with the given expressions substituted by their
	 *         value
	 */
	public String replaceTextExpressions(String text, Array<String> expressions) {
		for (String expression : expressions) {
			String value = evaluateExpression(expression) + "";
			text = text.replace(EXPRESSION_DELIMITER + expression
					+ EXPRESSION_DELIMITER, value);
		}
		return text;
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
