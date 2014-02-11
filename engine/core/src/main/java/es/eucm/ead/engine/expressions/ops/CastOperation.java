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
import es.eucm.ead.engine.VarsContext;

/**
 * Casting operator.
 * 
 * @author mfreire
 */
public abstract class CastOperation extends Operation {

	protected Class<?> targetType;

	public CastOperation(Class<?> targetType) {
		super(1, 1);
		this.targetType = targetType;
	}

	@Override
	public Object updateEvaluation(VarsContext context, boolean lazy)
			throws ExpressionException {
		if (lazy && isConstant) {
			return value;
		}
		Object o = first().updateEvaluation(context, lazy);
		isConstant = first().isConstant();
		value = convert(o, o.getClass(), targetType);
		return value;
	}
}
