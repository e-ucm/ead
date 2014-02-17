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

import es.eucm.ead.engine.VarsContext;

/**
 * An expression node. Collaborates with parsing, and supports parser-compatible
 * serialization.
 * 
 * Expressions can be evaluated for a given variable context. This caches
 * results for use in further evaluations; re-evaluation can be triggered by
 * calling updateEvaluation instead.
 * 
 * @author mfreire
 */
public abstract class Expression {
	/**
	 * Used for debugging. See ExpressionException.
	 */
	protected int tokenPosition;

	/**
	 * Previously-computed result, if any.
	 */
	protected Object value;

	/**
	 * Returns true if this whole expression is constant (and therefore, if the
	 * value exists, it need not be recalculated).
	 */
	protected boolean isConstant;

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
	 * @return true if the value of this expression cannot change in a different
	 *         evaluation.
	 */
	public boolean isConstant() {
		return isConstant;
	}

	/**
	 * Serializes this Expression node and all subnodes into a string. The
	 * string will be parseable with Parser.parse.
	 * 
	 * @param sb
	 *            to append to
	 * @param updateTokenPositions
	 *            if token positions should be updated
	 * @return the passed-in StringBuilder, fully appended to
	 */
	protected abstract StringBuilder buildString(StringBuilder sb,
			boolean updateTokenPositions);

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
	 * Evaluates this expression in a given context. Reuses previously-computed
	 * values whenever possible.
	 * 
	 * @param context
	 *            to use for variable resolution. Can be null if there are no
	 *            variables to look up.
	 * @return the result of the evaluation
	 * @throws ExpressionException
	 *             in the case of run-time exceptions (such as divide-by-zero).
	 */
	public Object evaluate(VarsContext context) throws ExpressionException {
		return evaluate(context, true);
	}

	/**
	 * Evaluates this expression in a given context. Allows control over
	 * previous-result reuse.
	 * 
	 * @param context
	 *            to use for variable resolution. Can be null if there are no
	 *            variables to look up.
	 * @param lazy
	 *            if previous, constant results are to be re-used.
	 * @return the result of the evaluation
	 * @throws ExpressionException
	 *             in the case of run-time exceptions (such as divide-by-zero).
	 */
	public abstract Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionException;

}
