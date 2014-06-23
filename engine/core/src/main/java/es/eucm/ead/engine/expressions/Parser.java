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
package es.eucm.ead.engine.expressions;

import es.eucm.ead.engine.expressions.operators.OperatorFactory;

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

	private static Expression createAtom(String s) {
		if (s.startsWith(VariableRef.REF_PREFIX)) {
			return new VariableRef(s.substring(1));
		} else {
			return new Literal(s);
		}
	}

	/**
	 * Parses an expression into an expression-node.
	 * 
	 * @param s
	 *            the expression to parse
	 * @param registry
	 *            for operator lookup
	 * @return the resulting node, or an IllegalArgumentException if errors
	 *         during parsing. Note that arity is checked, but type-problems are
	 *         left for run-time.
	 */
	public static Expression parse(String s, OperatorFactory registry) {
		Stack<Operation> stack = new Stack<Operation>();

		int pos = 0;
		boolean inString = false;
		StringBuilder sb = new StringBuilder();
		Expression result = null;
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
						Expression n = new Literal(sb.toString());
						sb.setLength(0);
						if (!stack.isEmpty()) {
							stack.peek().getChildren().add(n);
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
						stack.peek().getChildren()
								.add(createAtom(sb.toString()));
						sb.setLength(0);
					}
					if (next == '(') {
						// open new child with next word
						Matcher m = operatorPattern.matcher(s);
						if (m.find(pos)) {
							Operation op = registry.createOperation(m.group(1));
							if (op == null) {
								throw new IllegalArgumentException(
										"No such operator: '" + m.group(1)
												+ "'");
							}
							stack.push(op);
							pos = m.end();
						} else {
							throw new IllegalArgumentException(
									"Expected an operator");
						}
					} else if (next == ')') {
						// close child & add to parent
						Operation closed = stack.pop();
						int arity = closed.getChildren().size();

						if ((arity > closed.getMaxArity() || arity < closed
								.getMinArity())
								&& (arity != 1 || closed.getMinArity() != 2 || closed
										.getMaxArity() != Integer.MAX_VALUE)) {
							// operations should respect arity, except for
							// variadic (2-to-infinite argument) operations
							// handling a single collection argument
							throw new IllegalArgumentException(
									"Bad argument count " + arity + " for "
											+ closed.getClass().getSimpleName());
						}
						if (!stack.isEmpty()) {
							stack.peek().getChildren().add(closed);
						} else if (pos == s.length()) {
							// finished !; check type consistency before
							// returning
							result = closed;
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
				result = createAtom(sb.toString());
			}
		}

		return result;
	}
}
