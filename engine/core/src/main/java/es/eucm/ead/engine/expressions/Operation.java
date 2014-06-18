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

import java.util.ArrayList;
import java.util.Map;

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
		return to.equals(from) || String.class.equals(to)
				|| (Float.class.equals(to) && Integer.class.equals(from));
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
	 * 
	 * @throws NullPointerException
	 *             if {@code o}, {@code from} or {@code to} are {@code null}.
	 */
	public static Object convert(Object o, Class<?> from, Class<?> to) {
		if (o == null) {
			throw new NullPointerException("o must not be null");
		}
		if (from == null) {
			throw new NullPointerException("from must not be null");
		}
		if (to == null) {
			throw new NullPointerException("to must not be null");
		}
		if (to.equals(from)) {
			return o;
		}

		if (String.class.equals(to)) {
			return o.toString();
		} else if (Boolean.class.equals(to)) {
			if (String.class.equals(from)) {
				return Boolean.valueOf((String) o);
			} else if (Float.class.equals(from)) {
				return ((Float) o).floatValue() != 0f;
			} else if (Integer.class.equals(from)) {
				return ((Integer) o).intValue() != 0;
			}
		} else if (Float.class.equals(to)) {
			if (String.class.equals(from)) {
				return Float.valueOf((String) o);
			} else if (Boolean.class.equals(from)) {
				return (Boolean) o ? 1f : 0f;
			} else if (Integer.class.equals(from)) {
				return Float.valueOf((Integer) o);
			}
		} else if (Integer.class.equals(to)) {
			if (String.class.equals(from)) {
				return Integer.parseInt((String) o);
			} else if (Boolean.class.equals(from)) {
				return (Boolean) o ? 1 : 0;
			} else if (Float.class.equals(from)) {
				return ((Float) o).intValue();
			}
		}
		return null;
	}

	/**
	 * Returns a child iterator, transparently returning an iterator to
	 * the first child's contents if it happens to be a collection.
	 * @return
	 */
	protected Iterable<Expression> childIterator() {
		if (children.size() == 1 && isCollection(children.get(0))) {
			return getIteratorFor(children.get(0));
		} else {
			return children;
		}
	}

	/**
	 * @param o an object to test
	 * @return 'true' if argument is a collection
	 */
	public static boolean isCollection(Object o) {
		if (o == null || o.getClass().isPrimitive()) {
			return false;
		} else {
			return (o instanceof Iterable || o instanceof Map);
		}
	}

	/**
	 * @param collection to obtain an iterable from
	 * @return an iterable for this collection.
	 */
	public static Iterable<Expression> getIteratorFor(Object collection) {
		if (collection instanceof Iterable) {
			return (Iterable<Expression>)collection;
		} else if (collection instanceof Map) {
			throw new IllegalArgumentException("collection must contain expressions (this one is a map)");
		} else {
			throw new IllegalArgumentException("argument must be a collection");
		}
	}
}
