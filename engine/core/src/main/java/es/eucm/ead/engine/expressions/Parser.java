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

import es.eucm.ead.engine.expressions.ops.ExpressionException;
import es.eucm.ead.engine.expressions.ops.Operator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An expression parser. Uses a simplified, typed, lisp-like syntax.
 * 
 * @author mfreire
 */
public class Parser {

	private static final Pattern operatorPattern = Pattern
			.compile("\\s*([a-zA-Z_+*/~^%/&|-]+)");

	/**
	 * Parses an expression into an expression-node. Full static analysis is
	 * performed to avoid generating invalid nodes.
	 * 
	 * @param s
	 *            the expression to parse
	 * @param registry
	 *            for operator lookup
	 * @return the resulting node, or an IllegalArgumentException if errors
	 *         during parsing. Note that arity and type-safety problems also
	 *         result in errors.
	 */
	public static Node parse(String s, OperatorRegistry registry) {
		Stack<Node> stack = new Stack<Node>();

		int pos = 0;
		boolean inString = false;
		StringBuilder sb = new StringBuilder();
		Node result = null;
		try {
			while (pos < s.length() && result == null) {
				char next = s.charAt(pos++);
				if (next == '\\') {
					// escape chars
					sb.append(s.charAt(pos++));
				} else if (next == '"') {
					// strings cannot contain unquoted quotes
					if (inString) {
						inString = false;
						Node n = Node.createAtom(sb.toString());
						sb.setLength(0);
						if (!stack.isEmpty()) {
							stack.peek().add(n);
						} else if (pos == s.length()) {
							result = n;
						} else {
							throw new IllegalArgumentException(
									"Missing a trailing ')' at position " + pos);
						}
					} else if (sb.length() == 1) {
						inString = true;
					} else {
						throw new IllegalArgumentException(
								"Unquoted quote in non-string literal '" + sb
										+ "'");
					}
				} else if (inString) {
					// continue string (strings can contain anything)
					sb.append(next);
				} else if (next == ' ' || next == '(' || next == ')') {
					// close previous word, if any
					if (sb.length() > 0) {
						stack.peek().add(Node.createAtom(sb.toString()));
						sb.setLength(0);
					}
					if (next == '(') {
						// open new child with next word
						Matcher m = operatorPattern.matcher(s);
						if (m.find(pos)) {
							Operator op = registry.getOperator(m.group(1));
							if (op == null) {
								throw new IllegalArgumentException(
										"No such operator: '" + m.group(1)
												+ "'");
							}
							stack.push(Node.createExpression(op));
							pos = m.end();
						} else {
							throw new IllegalArgumentException(
									"Expected an operator");
						}
					} else if (next == ')') {
						// close child & add to parent
						Node child = stack.pop();
						if (child.getNodeType() == Node.Type.EXPRESSION) {
							Operator op = (Operator) child.getValue();
							int arity = child.getChildren().size();
							if (arity > op.getMaxArity()
									|| arity < op.getMinArity()) {
								throw new IllegalArgumentException(
										"Bad argument count " + arity + " for "
												+ op.getClass().getSimpleName());
							}
						}
						if (!stack.isEmpty()) {
							stack.peek().add(child);
						} else if (pos == s.length()) {
							// finished !; check type consistency before
							// returning
							result = child;
							result.updateValueType();
						} else {
							throw new IllegalArgumentException(
									"Missing a trailing ')' at position " + pos);
						}
					}
				} else {
					// append to (possibly new) word
					sb.append(next);
				}
			}
		} catch (ExpressionException e) {
			throw new IllegalArgumentException(e.getMessage() + ": "
					+ e.showError(result), e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Parse error: '"
					+ s.substring(0, pos) + "^" + s.substring(pos) + "' "
					+ e.getMessage(), e);
		}

		if (result == null) {
			if (!stack.isEmpty() || sb.length() == 0) {
				throw new IllegalArgumentException(
						"Ran out of characters while " + "parsing '" + s + "'");
			} else {
				result = Node.createAtom(sb.toString());
				// no need to updateValueType, since it is an atom
			}
		}

		return result;
	}
}
