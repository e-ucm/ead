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

package es.eucm.ead.engine.expressions.ops;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.Node;
import static es.eucm.ead.engine.expressions.ops.Operator.singleEval;
import es.eucm.ead.schema.components.VariableDef;

/**
 * 
 * @author mfreire
 */
public abstract class ComparisonOperator extends LogicOperator {

	protected abstract boolean compare(float a, float b);

	protected abstract boolean compare(String a, String b);

	@Override
	public Object eval(Node node, VarsContext context)
			throws ExpressionException {
		Node c1 = node.getChildren().get(0);
		Node c2 = node.getChildren().get(1);
		VariableDef.Type sst = safeSuperType(c1, c2);
		if (sst == VariableDef.Type.STRING) {
			return compare((String) singleEval(c1, sst, context),
					(String) singleEval(c2, sst, context));
		} else {
			VariableDef.Type t = VariableDef.Type.FLOAT;
			return compare((Float) singleEval(c1, t, context),
					(Float) singleEval(c2, t, context));
		}
	}

	@Override
	public VariableDef.Type getValueType(Node node) throws ExpressionException {
		Node c1 = node.getChildren().get(0);
		Node c2 = node.getChildren().get(1);
		VariableDef.Type sst = safeSuperType(c1, c2);
		if ((sst == VariableDef.Type.STRING)
				&& (c1.getValueType() != c2.getValueType())) {
			throw new ExpressionException(
					"Comparing non-string and strings requires a cast", node);
		} else if (sst == VariableDef.Type.BOOLEAN) {
			throw new ExpressionException("Cannot compare booleans using "
					+ this, node);
		}
		return VariableDef.Type.BOOLEAN;
	}

	public static class Le extends ComparisonOperator {

		@Override
		protected boolean compare(float a, float b) {
			return a <= b;
		}

		@Override
		protected boolean compare(String a, String b) {
			return a.compareTo(b) <= 0;
		}
	}

	public static class Lt extends ComparisonOperator {

		@Override
		protected boolean compare(float a, float b) {
			return a < b;
		}

		@Override
		protected boolean compare(String a, String b) {
			return a.compareTo(b) < 0;
		}
	}

	public static class Ge extends ComparisonOperator {

		@Override
		protected boolean compare(float a, float b) {
			return a >= b;
		}

		@Override
		protected boolean compare(String a, String b) {
			return a.compareTo(b) >= 0;
		}
	}

	public static class Gt extends ComparisonOperator {

		@Override
		protected boolean compare(float a, float b) {
			return a > b;
		}

		@Override
		protected boolean compare(String a, String b) {
			return a.compareTo(b) >= 0;
		}
	}
}
