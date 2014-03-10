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
package es.eucm.ead.engine.effects;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperatorFactory;
import es.eucm.ead.schema.effects.ChangeVar;

public class ChangeVarEngineObject extends EffectEngineObject<ChangeVar> {

	private final OperatorFactory operators = new OperatorFactory();

	private Expression expression;
	private String varName;

	@Override
	protected boolean delegate(float delta) {
		VarsContext vc = gameLoop.getVarsContext();
		try {
			Object value = expression.evaluate(vc);

			// update variable
			if (!vc.hasVariable(varName)) {
				System.err.println("Initializing to " + value + " from "
						+ expression);
				vc.registerVariable(varName, value);
			} else {
				System.err.println("Setting to " + value + " from "
						+ expression);
				vc.setValue(varName, value);
			}
		} catch (ExpressionEvaluationException ee) {
			// FIXME: this must be dealt with upstream
			throw new RuntimeException(ee);
		}

		return true;
	}

	@Override
	public void initialize(ChangeVar schemaObject) {
		expression = Parser.parse(schemaObject.getExpression(), operators);
		varName = schemaObject.getVariable();
	}
}
