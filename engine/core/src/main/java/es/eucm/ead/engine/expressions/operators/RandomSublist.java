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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.variables.VarsContext;

import java.util.Random;

/**
 * Creates a list with n random elements from a another list
 */
class RandomSublist extends Operation {

	private Array<Integer> indexes = new Array<Integer>();

	private Random random = new Random();

	public RandomSublist() {
		super(2, 2);
	}

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		Object o1 = first().evaluate(context, lazy);
		Object o2 = second().evaluate(context, lazy);
		if (o1 instanceof Array && o2 instanceof Number) {
			Array<Object> list = (Array<Object>) o1;
			Array<Object> randomList = new Array<Object>();
			indexes.clear();
			for (int i = 0; i < ((Number) o2).intValue() && i < list.size; i++) {
				int index;
				do {
					index = random.nextInt(list.size);
				} while (indexes.contains(index, false));
				randomList.add(list.get(index));
				indexes.add(index);
			}
			return randomList;
		} else {
			throw new ExpressionEvaluationException(
					"Expected array as first operand and a number as second",
					this);
		}
	}
}
