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

import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;

/**
 * Multiply integers or mixed integers and floats.
 * 
 * @author mfreire
 */
class Mul extends AbstractMathOperation {

	@Override
	public Object evaluate(VarsContext context)
			throws ExpressionEvaluationException {

		int intTotal = 1;
		float floatTotal = 1;
		boolean floatsDetected = false;
		for (Expression child : childIterator(context)) {
			Object o = child.evaluate(context);
			floatsDetected = needFloats(o.getClass(), floatsDetected);
			if (floatsDetected) {
				floatTotal *= (Float) convert(o, o.getClass(), Float.class);
			} else {
				intTotal *= (Integer) o;
				floatTotal = intTotal;
			}
		}
		if (floatsDetected) {
			return floatTotal;
		} else {
			return Integer.valueOf(intTotal);
		}
	}

}
