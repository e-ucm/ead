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

import es.eucm.ead.engine.expressions.Node;

/**
 * An exception thrown in the event of errors during expression type-checks or
 * evaluation.
 * 
 * @author mfreire
 */
public class ExpressionException extends Exception {

	private final Node errorNode;

	public ExpressionException(String message, Node errorNode) {
		super(message);
		this.errorNode = errorNode;
	}

	public ExpressionException(String message, Node errorNode, Throwable cause) {
		super(message, cause);
		this.errorNode = errorNode;
	}

	public Node getErrorNode() {
		return errorNode;
	}

	public String showError(Node root) {
		String s = root.updateTokenPositions();
		int pos = errorNode.getTokenPosition();
		return s.substring(0, pos) + "^" + s.substring(pos);
	}
}
