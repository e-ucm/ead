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
 * Operators evaluate their children nodes to yield a single result. The
 * type-safety of the operator can be checked "statically" (without evaluation).
 * However, note that some operators will also impose run-time constraints
 * (divide-by-zero, negative-square-root, ...).
 * 
 * @author mfreire
 */
public abstract class Operator {

	private String name;
	private final int minArity;
	private final int maxArity;

	/**
	 * Creates a new operator. The resulting operator will accept from minArity
	 * to maxArity operands, inclusive. Attempting to operate with less or more
	 * will throw an error.
	 * 
	 * @param minArity
	 * @param maxArity
	 */
	public Operator(int minArity, int maxArity) {
		if (minArity < 0 || maxArity < minArity) {
			throw new IllegalArgumentException(
					"Bad arity in operator definition");
		}
		this.minArity = minArity;
		this.maxArity = maxArity;
	}

	public boolean isCorrectArity(Node node) {
		int n = node.getChildren().size();
		return minArity <= n && n <= maxArity;
	}

	/**
	 * Sets the operator name. Used when pretty-printing and serializing
	 * expressions.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Evaluates a node using this operator. Note that the node need not be of
	 * this same operator.
	 * 
	 * @param node
	 *            with this operator
	 * @param context
	 *            for variable resolution
	 * @return result
	 * @throws es.eucm.ead.engine.expressions.ops.ExpressionException
	 *             on error
	 */
	public abstract Object eval(Node node, VarsContext context)
			throws ExpressionException;

	/**
	 * Evaluates the output valueType of this operator. This is requires
	 * knowledge of the valueTypes of all operands
	 * 
	 * @param node
	 *            with this operator
	 * @return output value type, or null if bad operand types
	 * @throws es.eucm.ead.engine.expressions.ops.ExpressionException
	 *             on error
	 */
	public abstract VariableDef.Type getValueType(Node node)
			throws ExpressionException;

	/**
	 * Returns the operator's minimal arity.
	 * 
	 * @return minimal number of operands (> 0)
	 */
	public int getMinArity() {
		return minArity;
	}

	/**
	 * Returns the operator's maximal arity.
	 * 
	 * @return maximal number of operands (>= minArity, possibly Integer.
	 */
	public int getMaxArity() {
		return maxArity;
	}

	/**
	 * Evaluates a single node, regardless of whether or not it is an expression
	 * node.
	 * 
	 * @param node
	 *            with this operator
	 * @param to
	 *            target type
	 * @param context
	 *            for variable resolution
	 * @return value of node, after any suitable casts
	 * @throws es.eucm.ead.engine.expressions.ops.ExpressionException
	 *             on error
	 */
	public static Object singleEval(Node node, VariableDef.Type to,
			VarsContext context) throws ExpressionException {
		switch (node.getNodeType()) {
		case EXPRESSION:
			return convert(((Operator) node.getValue()).eval(node, context),
					node.getValueType(), to);
		case LITERAL:
			return convert(node.getValue(), node.getValueType(), to);
		case REFERENCE:
			try {
				return convert(context.getValue((String) node.getValue()),
						node.getValueType(), to);
			} catch (NullPointerException npe) {
				throw new ExpressionException("Var not found: "
						+ node.getValue(), node);
			}
		default:
			throw new IllegalArgumentException("Bad node type: "
					+ node.getNodeType());
		}
	}

	/**
	 * Contains safe conversions.
	 * 
	 * @param from
	 *            a type
	 * @param to
	 *            a type
	 * @return true if 'from' can safely be converted to 'to'
	 */
	public static boolean canSafelyConvert(VariableDef.Type from,
			VariableDef.Type to) {
		return (to == from)
				|| (to == VariableDef.Type.STRING)
				|| (to == VariableDef.Type.FLOAT && from == VariableDef.Type.INTEGER);
	}

	/**
	 * Conversion & casting.
	 * 
	 * @param o
	 *            object to convert
	 * @param from
	 *            source type
	 * @param to
	 *            target type
	 * @return converted object. Never fails (even if information loss occurs).
	 *         Use @see #canSafelyConvert to avoid problems
	 */
	public static Object convert(Object o, VariableDef.Type from,
			VariableDef.Type to) {
		if (to == from) {
			return o;
		}
		switch (to) {
		case STRING:
			return "" + o;
		case FLOAT: {
			switch (from) {
			case BOOLEAN:
				return (Boolean) o ? 1f : 0f;
			case INTEGER:
				return Float.valueOf((Integer) o);
			case STRING:
				return Float.valueOf((String) o);
			}
			break;
		}
		case INTEGER: {
			switch (from) {
			case BOOLEAN:
				return (Boolean) o ? 1 : 0;
			case FLOAT:
				return ((Float) o).intValue();
			case STRING:
				return Integer.valueOf((String) o);
			}
			break;
		}
		case BOOLEAN: {
			switch (from) {
			case INTEGER:
				return ((Integer) o).intValue() != 0;
			case FLOAT:
				return ((Float) o).floatValue() != 0f;
			case STRING:
				return ((String) o).toLowerCase().equals("true");
			}
			break;
		}
		}
		return null;
	}
}
