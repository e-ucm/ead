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
package es.eucm.ead.engine.expressions.operators;

import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.variables.VarsContext;

/**
 * Find the minimum of integers or mixed integers and floats
 * 
 * @author mfreire
 */
class Min extends AbstractMathOperation {

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		if (lazy && isConstant) {
			return value;
		}

		int intMin = Integer.MAX_VALUE;
		float floatMin = Float.POSITIVE_INFINITY;
		boolean floatsDetected = false;
		isConstant = true;
		for (Expression child : children) {
			Object o = child.evaluate(context, lazy);
			isConstant &= child.isConstant();
			floatsDetected = needFloats(o.getClass(), floatsDetected);
			if (floatsDetected) {
				Float f = (Float) convert(o, o.getClass(), Float.class);
				if (floatMin > f) {
					floatMin = f;
				}
			} else {
				if (intMin > (Integer) o) {
					intMin = (Integer) o;
					floatMin = intMin;
				}
			}
		}
		if (floatsDetected) {
			value = floatMin;
		} else {
			value = Integer.valueOf(intMin);
		}
		return value;
	}

}
