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
import es.eucm.ead.schema.components.VariableDef;

/**
 * 
 * @author mfreire
 */
public abstract class BooleanOperator extends Operator {

	public BooleanOperator(int minArity, int maxArity) {
		super(minArity, maxArity);
	}

	public BooleanOperator() {
		super(2, Integer.MAX_VALUE);
	}

	@Override
	public VariableDef.Type getValueType(Node node) {
		return VariableDef.Type.BOOLEAN;
	}

	public static class Not extends BooleanOperator {

		public Not() {
			super(1, 1);
		}

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			Node c1 = node.getChildren().get(0);
			return !(Boolean) singleEval(c1, VariableDef.Type.BOOLEAN, context);
		}
	}

	public static class Xor extends BooleanOperator {

		public Xor() {
			super(2, 2);
		}

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			boolean a = (Boolean) singleEval(node.getChildren().get(0),
					VariableDef.Type.BOOLEAN, context);
			boolean b = (Boolean) singleEval(node.getChildren().get(1),
					VariableDef.Type.BOOLEAN, context);
			return a != b;
		}
	}

	public static class And extends BooleanOperator {

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			for (Node child : node.getChildren()) {
				if (!(Boolean) singleEval(child, VariableDef.Type.BOOLEAN,
						context)) {
					return false;
				}
			}
			return true;
		}
	}

	public static class Or extends BooleanOperator {

		@Override
		public Object eval(Node node, VarsContext context)
				throws ExpressionException {
			for (Node child : node.getChildren()) {
				if ((Boolean) singleEval(child, VariableDef.Type.BOOLEAN,
						context)) {
					return true;
				}
			}
			return false;
		}
	}
}
