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

/**
 * An exception thrown in the event of errors during expression type-checks or
 * evaluation.
 * 
 * @author mfreire
 */
public class ExpressionEvaluationException extends Exception {

	/**
	 * @see java.io.Serializable 
	 */
	private static final long serialVersionUID = 5701511800778176284L;
	
	private final Expression errorNode;

	public ExpressionEvaluationException(String message, Expression errorNode) {
		super(message);
		this.errorNode = errorNode;
	}

	public ExpressionEvaluationException(String message, Expression errorNode,
			Throwable cause) {
		super(message, cause);
		this.errorNode = errorNode;
	}

	/**
	 * Returns the expression part where the error was detected
	 * 
	 * @return node where the error was detected
	 */
	public Expression getErrorNode() {
		return errorNode;
	}

	/**
	 * Displays a human-readable account of the error.
	 * 
	 * @param root
	 *            of the expression
	 * @return a String representation, displaying the position where the error
	 *         was detected using a '^'
	 */
	public String showError(Expression root) {
		String s = root.updateTokenPositions();
		int pos = errorNode.getTokenPosition();
		return s.substring(0, pos) + "^" + s.substring(pos);
	}
}
