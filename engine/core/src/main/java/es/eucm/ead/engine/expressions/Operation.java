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

import java.util.ArrayList;

/**
 * Operators evaluate their children nodes. Example operators are Add,
 * GreaterThan, ...
 * 
 * @author mfreire
 */
public abstract class Operation extends Expression {

	private String name;

	private final int minArity;
	private final int maxArity;

	protected ArrayList<Expression> children = new ArrayList<Expression>();

	/**
	 * Creates a new operator. The resulting operator will accept from minArity
	 * to maxArity operands, inclusive. Attempting to operate with less or more
	 * will throw an error.
	 * 
	 * @param minArity
	 *            minimal accepted operands (included)
	 * @param maxArity
	 *            maximum accepted operands (included)
	 */
	public Operation(int minArity, int maxArity) {
		if (minArity < 0 || maxArity < minArity) {
			throw new IllegalArgumentException(
					"Bad arity in operator definition");
		}
		this.minArity = minArity;
		this.maxArity = maxArity;
	}

	/**
	 * Returns the children of this expression. When finished adding, remember
	 * to call isCorrectArity to validate that they are not too many or too few.
	 * 
	 * @return the children
	 */
	public ArrayList<Expression> getChildren() {
		return children;
	}

	@Override
	protected StringBuilder buildString(StringBuilder sb,
			boolean updateTokenPositions) {
		if (updateTokenPositions) {
			tokenPosition = sb.length();
		}
		sb.append("( ").append(name).append(" ");
		for (Expression n : children) {
			n.buildString(sb, updateTokenPositions).append(" ");
		}
		sb.append(")");
		return sb;
	}

	public Expression first() {
		return children.get(0);
	}

	public Expression second() {
		return children.get(1);
	}

	public boolean isCorrectArity(Expression node) {
		int n = children.size();
		return minArity <= n && n <= maxArity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

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
	 * Contains safe conversions.
	 * 
	 * @param from
	 *            a type
	 * @param to
	 *            a type
	 * @return true if 'from' can safely be converted to 'to'
	 */
	public static boolean canSafelyConvert(Class<?> from, Class<?> to) {
		return to.equals(from) || to.equals(String.class)
				|| (to.equals(Float.class) && from.equals(Integer.class));
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
	public static Object convert(Object o, Class<?> from, Class<?> to) {
		if (to.equals(from)) {
			return o;
		}

		if (to.equals(String.class)) {
			return o.toString();
		} else if (to.equals(Boolean.class)) {
			if (from.equals(String.class)) {
				return Boolean.valueOf((String) o);
			} else if (from.equals(Float.class)) {
				return ((Float) o).floatValue() != 0f;
			} else if (from.equals(Integer.class)) {
				return ((Integer) o).intValue() != 0;
			}
		} else if (to.equals(Float.class)) {
			if (from.equals(String.class)) {
				return Float.valueOf((String) o);
			} else if (from.equals(Boolean.class)) {
				return (Boolean) o ? 1f : 0f;
			} else if (from.equals(Integer.class)) {
				return Float.valueOf((Integer) o);
			}
		} else if (to.equals(Integer.class)) {
			if (from.equals(String.class)) {
				return Integer.parseInt((String) o);
			} else if (from.equals(Boolean.class)) {
				return (Boolean) o ? 1 : 0;
			} else if (from.equals(Float.class)) {
				return ((Float) o).intValue();
			}
		}
		return null;
	}
}
