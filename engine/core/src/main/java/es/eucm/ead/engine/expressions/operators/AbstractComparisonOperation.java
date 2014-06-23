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
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;

/**
 * Comparisons.
 * 
 * @author mfreire
 */
abstract class AbstractComparisonOperation extends AbstractLogicOperation {

	protected abstract boolean compare(float a, float b);

	protected abstract boolean compare(String a, String b);

	@Override
	public Object evaluate(VarsContext context)
			throws ExpressionEvaluationException {
		Object a = first().evaluate(context);
		Object b = second().evaluate(context);

		// check type-safety
		Class<?> safe = safeSuperType(a.getClass(), b.getClass());
		if (safe.equals(Boolean.class)) {
			throw new ExpressionEvaluationException(
					"Use a boolean operator instead of " + getName()
							+ " to compare booleans", this);
		} else if (safe.equals(Integer.class)) {
			safe = Float.class;
		}
		a = convert(a, a.getClass(), safe);
		b = convert(b, b.getClass(), safe);

		// compare using either floats or strings
		if (safe.equals(String.class)) {
			return compare((String) a, (String) b);
		} else {
			return compare((Float) a, (Float) b);
		}
	}
}
