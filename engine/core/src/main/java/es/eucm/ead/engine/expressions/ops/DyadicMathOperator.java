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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.eucm.ead.engine.expressions.ops;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.Node;
import static es.eucm.ead.engine.expressions.ops.Operator.singleEval;
import es.eucm.ead.schema.components.VariableDef;
import java.util.Random;

/**
 * Math operators with two operands.
 * 
 * @author mfreire
 */
public abstract class DyadicMathOperator extends MathOperator {

	public DyadicMathOperator() {
		super(2, 2);
	}

	protected abstract float operate(float a, float b);

	protected abstract int operate(int a, int b);

	@Override
	public Object eval(Node node, VarsContext context)
			throws ExpressionException {
		Node c1 = node.getChildren().get(0);
		Node c2 = node.getChildren().get(1);
		Object a = null;
		Object b = null;
		try {
			if (node.getValueType() == VariableDef.Type.INTEGER) {
				a = singleEval(c1, VariableDef.Type.INTEGER, context);
				b = singleEval(c2, VariableDef.Type.INTEGER, context);
				return operate((Integer) a, (Integer) b);
			} else {
				a = singleEval(c1, VariableDef.Type.FLOAT, context);
				b = singleEval(c2, VariableDef.Type.FLOAT, context);
				return operate((Float) a, (Float) b);
			}
		} catch (ArithmeticException ae) {
			throw new ExpressionException("Illegal math: " + a
					+ ((Operator) node.getValue()).toString() + b, c2);
		}
	}

	public static class Sub extends DyadicMathOperator {

		@Override
		protected float operate(float a, float b) {
			return a - b;
		}

		@Override
		protected int operate(int a, int b) {
			return a - b;
		}
	}

	public static class Div extends DyadicMathOperator {

		@Override
		protected float operate(float a, float b) {
			return a / b;
		}

		@Override
		protected int operate(int a, int b) {
			return a / b;
		}
	}

	public static class Mod extends DyadicMathOperator {

		@Override
		public VariableDef.Type getValueType(Node n) throws ExpressionException {
			if (super.getValueType(n) == VariableDef.Type.FLOAT) {
				throw new ExpressionException(this
						+ " does not accept float arguments", n);
			}
			return VariableDef.Type.INTEGER;
		}

		@Override
		protected float operate(float a, float b) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected int operate(int a, int b) {
			return a % b;
		}
	}

	public static class Pow extends DyadicMathOperator {

		@Override
		protected float operate(float a, float b) {
			return (float) Math.pow(a, b);
		}

		@Override
		protected int operate(int a, int b) {
			return (int) Math.pow(a, b);
		}
	}

	public static class Rand extends DyadicMathOperator {

		private final Random rng = new Random();

		@Override
		protected float operate(float a, float b) {
			float low, high;
			if (a < b) {
				low = a;
				high = b;
			} else if (b < a) {
				low = b;
				high = a;
			} else {
				return a;
			}
			return rng.nextFloat() * (high - low) + low;
		}

		@Override
		protected int operate(int a, int b) {
			int low, high;
			if (a < b) {
				low = a;
				high = b;
			} else if (b < a) {
				low = b;
				high = a;
			} else {
				return a;
			}
			return rng.nextInt(high - low) + low;
		}
	}
}
