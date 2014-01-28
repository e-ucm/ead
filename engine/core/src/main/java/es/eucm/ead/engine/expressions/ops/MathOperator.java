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
 * Math operators. Operands must be either integer or floats, and will be casted
 * from integer to float if at least one of them is a float.
 * 
 * @author mfreire
 */
public abstract class MathOperator extends Operator {

	public MathOperator(int minArity, int maxArity) {
		super(minArity, maxArity);
	}

	public MathOperator() {
		super(2, Integer.MAX_VALUE);
	}

	@Override
	public VariableDef.Type getValueType(Node node) throws ExpressionException {
		boolean hasFloat = false;
		for (Node child : node.getChildren()) {
			switch (child.getValueType()) {
			case BOOLEAN:
				throw new ExpressionException("Boolean not allowed in " + this,
						child);
			case STRING:
				throw new ExpressionException("String not allowed in " + this,
						child);
			case FLOAT:
				hasFloat = true;
				break;
			}
		}
		return (hasFloat) ? VariableDef.Type.FLOAT : VariableDef.Type.INTEGER;
	}

	public static class Add extends MathOperator {

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			if (node.getValueType() == VariableDef.Type.INTEGER) {
				int total = 0;
				for (Node child : node.getChildren()) {
					total += (Integer) singleEval(child,
							VariableDef.Type.INTEGER, context);
				}
				return total;
			} else {
				float total = 0;
				for (Node child : node.getChildren()) {
					total += (Float) singleEval(child, VariableDef.Type.FLOAT,
							context);
				}
				return total;
			}
		}
	}

	public static class Mul extends MathOperator {

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			if (node.getValueType() == VariableDef.Type.INTEGER) {
				int total = 1;
				for (Node child : node.getChildren()) {
					total *= (Integer) singleEval(child,
							VariableDef.Type.INTEGER, context);
				}
				return total;
			} else {
				float total = 1;
				for (Node child : node.getChildren()) {
					total *= (Float) singleEval(child, VariableDef.Type.FLOAT,
							context);
				}
				return total;
			}
		}
	}

	public static class Sqrt extends MathOperator {
		public Sqrt() {
			super(1, 1);
		}

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			Node child = node.getChildren().get(0);
			Object arg = singleEval(child, node.getValueType(), context);
			if (node.getValueType() == VariableDef.Type.INTEGER) {
				if ((Integer) arg < 0) {
					throw new ExpressionException("Square-root of " + arg,
							child);
				} else {
					return (int) Math.sqrt((Integer) arg);
				}
			} else {
				if ((Float) arg < 0) {
					throw new ExpressionException("Square-root of " + arg,
							child);
				} else {
					return (float) Math.sqrt((Float) arg);
				}
			}
		}
	}
}
