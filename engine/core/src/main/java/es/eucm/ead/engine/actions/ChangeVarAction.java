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
package es.eucm.ead.engine.actions;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.Node;
import es.eucm.ead.engine.expressions.OperatorRegistry;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.ops.ExpressionException;
import es.eucm.ead.schema.actions.ChangeVar;
import es.eucm.ead.schema.components.VariableDef;

public class ChangeVarAction extends AbstractAction<ChangeVar> {

	private final OperatorRegistry operators = new OperatorRegistry();

	@Override
	protected boolean delegate(float delta) {
		return true;
	}

	@Override
	public void initialize(ChangeVar schemaObject) {

		// parse and evaluate the expression
		VarsContext vc = gameLoop.getVarsContext();
		String unparsed = schemaObject.getExpression();
		Node expression = null;
		Object value = null;
		try {
			expression = Parser.parse(unparsed, operators);
			value = expression.evaluate(vc);
		} catch (ExpressionException ee) {
			// FIXME: this must be dealt with upstream
			throw new RuntimeException(ee);
		}

		// update variable
		if (!vc.hasVariable(schemaObject.getVariable())) {
			VariableDef v = new VariableDef();
			v.setName(schemaObject.getVariable());
			v.setType(expression.getValueType());
			v.setInitialValue(value.toString());
			vc.registerVariable(v);
		} else {
			vc.setValue(schemaObject.getVariable(), value);
		}
	}
}
