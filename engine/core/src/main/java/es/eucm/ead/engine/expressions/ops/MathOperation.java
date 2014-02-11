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

import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.expressions.ExpressionException;

/**
 * Math operators. Operands must be either integer or floats, and will be casted
 * from integer to float if at least one of them is a float.
 * 
 * @author mfreire
 */
public abstract class MathOperation extends Operation {

	public MathOperation(int minArity, int maxArity) {
		super(minArity, maxArity);
	}

	public MathOperation() {
		super(2, Integer.MAX_VALUE);
	}

	protected boolean needFloats(Class<?> cc, boolean floatsDetected)
			throws ExpressionException {
		if (cc.equals(Boolean.class)) {
			throw new ExpressionException(
					"Boolean not allowed in " + getName(), this);
		} else if (cc.equals(String.class)) {
			throw new ExpressionException("String not allowed in " + getName(),
					this);
		} else if (cc.equals(Float.class)) {
			floatsDetected = true;
		}
		return floatsDetected;
	}
}
