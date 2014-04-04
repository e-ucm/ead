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
 * Created by angel on 8/04/14.
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

	public void addListener(VariableListener variableListener) {
		listeners.add(variableListener);
	}

	public void setValue(String variable, String expression) {
		pendingToEvaluate.add(variable);
		pendingToEvaluate.add(expression);
		this.sleeping = false;
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

	private void notify(String variable, Object value) {
		for (VariableListener listener : listeners) {
			if (listener.listensTo(variable)) {
				listener.variableChanged(variable, value);
			}
		}
	}

	public void clear() {
		varsContext.clear();
	}

	public void registerVariables(List<VariableDef> variablesDefinitions) {
		varsContext.registerVariables(variablesDefinitions);
		for (Entry<String, Variable> e : varsContext.getVariables().entrySet()) {
			notify(e.getKey(), e.getValue().getValue());
		}
	}

	public interface VariableListener {

		boolean listensTo(String variableName);

		void variableChanged(String variable, Object value);

	}
}
