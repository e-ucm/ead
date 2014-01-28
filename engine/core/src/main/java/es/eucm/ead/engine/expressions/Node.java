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

package es.eucm.ead.engine.expressions;

import es.eucm.ead.engine.expressions.ops.Operator;
import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.ops.ExpressionException;
import es.eucm.ead.schema.components.VariableDef;
import java.util.ArrayList;

/**
 * An expression node. Collaborates with parsing, and supports parser-compatible
 * stringification. Nodes are mostly immutable, excepting their tokenPosition
 * (used for pretty error-reporting in ExpressionExceptions) and their valueType
 * (which depends on operators and operands),
 * 
 * Expressions can be evaluated in a given variable context. Expression
 * operators are in charge of the actual evaluation.
 * 
 * @author mfreire
 */
public class Node {

	/**
	 * Type of the node, which determines the meaning of 'value'. literal-nodes
	 * are constants; reference-nodes contain variables; expression-nodes
	 * contain an operator and a list of children-nodes.
	 */
	public enum Type {
		LITERAL, REFERENCE, EXPRESSION
	}

	private final Type nodeType;

	/**
	 * children; only valid for EXPRESSION nodes
	 */
	private final ArrayList<Node> children;

	/**
	 * Naming convention for REFERENCE (if no $ is found, a LITERAL is assumed)
	 */
	private final static String REF_PREFIX = "$";

	/**
	 * Node value. Constant values for LITERAL, variable name for REFERENCE,
	 * Operator instance for EXPRESSION
	 */
	private final Object value;

	/**
	 * Used for debugging. See ExpressionException.
	 */
	private int tokenPosition;

	/**
	 * Value type. For expressions, this may depend on the types of its
	 * arguments; therefore, it is not generally valid until updateValueType has
	 * been called.
	 */
	private VariableDef.Type valueType;

	private Node(Object value, VariableDef.Type valueType, Type nodeType,
			ArrayList<Node> children) {
		this.value = value;
		this.valueType = valueType;
		this.nodeType = nodeType;
		this.children = children;
	}

	/**
	 * Creates a LITERAL or REFERENCE node during parsing.
	 * 
	 * @param prefixedString
	 *            where the first characters allow parameters for
	 *            createReference or createLiteral to be inferred.
	 * @return the generated node.
	 */
	public static Node createAtom(String prefixedString) {
		VariableDef.Type vt = charToType(prefixedString.charAt(0));
		boolean isRef = prefixedString.substring(1).startsWith(REF_PREFIX);
		if (isRef) {
			return createReference(vt, prefixedString.substring(2));
		} else {
			return createLiteral(vt, prefixedString.substring(1));
		}
	}

	/**
	 * Creates an EXPRESSION node
	 * 
	 * @param operator
	 *            to use
	 * @return the resulting node
	 */
	public static Node createExpression(Operator operator) {
		return new Node(operator, null, Type.EXPRESSION, new ArrayList<Node>());
	}

	/**
	 * Creates a LITERAL node. In other words, a constant.
	 * 
	 * @param valueType
	 *            of the constant
	 * @param value
	 *            of the constant
	 * @return the resulting node
	 */
	public static Node createLiteral(VariableDef.Type valueType, String value) {
		VarsContext.Variable v = new VarsContext.Variable(valueType, value);
		return new Node(v.getValue(), valueType, Type.LITERAL, null);
	}

	/**
	 * Creates a REFERENCE node. Its value will be looked up in a VarsContext
	 * when the expression is evaluated.
	 * 
	 * @param valueType
	 *            of the variable to reference
	 * @param name
	 *            of the variable
	 * @return the resulting node
	 */
	public static Node createReference(VariableDef.Type valueType, String name) {
		return new Node(name, valueType, Type.REFERENCE, null);
	}

	/**
	 * Type of the expression.
	 * 
	 * @return the type, or null if updateValueType has not been called yet, and
	 *         this is an EXPRESSION node.
	 */
	public VariableDef.Type getValueType() {
		return valueType;
	}

	/**
	 * Updates expression types.
	 * 
	 * @return the type.
	 * @throws ExpressionException
	 *             if a type-compatibility error is discovered while updating
	 *             types.
	 */
	public VariableDef.Type updateValueType() throws ExpressionException {
		if (nodeType == Type.EXPRESSION) {
			for (Node child : children) {
				// force value-type to be updated
				child.updateValueType();
			}
			valueType = ((Operator) value).getValueType(this);
		}
		return valueType;
	}

	/**
	 * @return the type of this expression-node
	 */
	public Type getNodeType() {
		return nodeType;
	}

	/**
	 * Returns the character (relative to the root of the expression) at which
	 * this Node starts. Only valid after updateTokenPositions has been called.
	 * 
	 * @return offset of the first char of this node in the larger expression
	 */
	public int getTokenPosition() {
		return tokenPosition;
	}

	/**
	 * Returns the contents of the node. This is only the same as a call to
	 * evaluate() for LITERAL nodes (constants).
	 * 
	 * @return Constant value for LITERAL, variable name for REFERENCE, Operator
	 *         instance for EXPRESSION.
	 */
	public Object getValue() {
		return value;
	}

	private static VariableDef.Type charToType(char c) {
		switch (c) {
		case 's':
			return VariableDef.Type.STRING;
		case 'b':
			return VariableDef.Type.BOOLEAN;
		case 'f':
			return VariableDef.Type.FLOAT;
		case 'i':
			return VariableDef.Type.INTEGER;
		default:
			throw new IllegalArgumentException("Unrecognized type prefix: " + c);
		}
	}

	/**
	 * Add a child node. Only valid for EXPRESSION nodes.
	 * 
	 * @param child
	 */
	public void add(Node child) {
		children.add(child);
	}

	/**
	 * Retrieve children. Only valid for EXPRESSION nodes.
	 * 
	 * @return children (if any), or null if not an expression node.
	 */
	public ArrayList<Node> getChildren() {
		return children;
	}

	private StringBuilder buildString(StringBuilder sb,
			boolean updateTokenPositions) {
		if (updateTokenPositions) {
			tokenPosition = sb.length();
		}
		if (children == null) {
			if (nodeType == Type.REFERENCE) {
				sb.append(REF_PREFIX);
			}
			switch (valueType) {
			case STRING:
				sb.append("s\"");
				break;
			case BOOLEAN:
				sb.append("b");
				break;
			case FLOAT:
				sb.append("f");
				break;
			case INTEGER:
				sb.append("i");
				break;
			}
			sb.append(value.toString());
			if (valueType == VariableDef.Type.STRING) {
				sb.append("\"");
			}
		} else {
			sb.append("( ");
			sb.append(value.toString()).append(" ");
			for (Node n : children) {
				n.buildString(sb, updateTokenPositions).append(" ");
			}
			sb.append(")");
		}
		return sb;
	}

	@Override
	public String toString() {
		return buildString(new StringBuilder(), false).toString();
	}

	/**
	 * Like toString, but resets all token positions to their actual offset in
	 * the result. Nice for debugging.
	 * 
	 * @return the resulting string.
	 */
	public String updateTokenPositions() {
		return buildString(new StringBuilder(), true).toString();
	}

	/**
	 * Evaluates this node in a given context.
	 * 
	 * @param context
	 *            to use for variable resolution. Can be null if there are no
	 *            variables to look up.
	 * @return the result of the evaluation
	 * @throws ExpressionException
	 *             in the case of run-time exceptions (such as divide-by-zero).
	 */
	public Object evaluate(VarsContext context) throws ExpressionException {
		return Operator.singleEval(this, updateValueType(), context);
	}
}
