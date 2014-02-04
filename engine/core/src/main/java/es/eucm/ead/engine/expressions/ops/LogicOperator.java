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
import es.eucm.ead.schema.components.VariableDef;

/**
 * Abstract logical operators. Always return doubles.
 * 
 * @author mfreire
 */
public abstract class LogicOperator extends Operator {

	public LogicOperator() {
		super(2, 2);
	}

	@Override
	public VariableDef.Type getValueType(Node node) throws ExpressionException {
		return VariableDef.Type.BOOLEAN;
	}

	protected VariableDef.Type safeSuperType(Node c1, Node c2)
			throws ExpressionException {
		VariableDef.Type a = c1.getValueType();
		VariableDef.Type b = c2.getValueType();
		if (canSafelyConvert(a, b)) {
			return b;
		} else if (canSafelyConvert(b, a)) {
			return a;
		} else {
			throw new ExpressionException("Cannot convert between " + a
					+ " and " + b, c1);
		}
	}
}
