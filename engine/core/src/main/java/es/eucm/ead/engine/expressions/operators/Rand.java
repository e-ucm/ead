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

package es.eucm.ead.engine.expressions.operators;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import java.util.Random;

/**
 * Random numbers. To initialize the sequence, use the static setSeed() method.
 * 
 * @author mfreire
 */
class Rand extends AsbractDyadicMathOperation {
	private final static Random rng = new Random();

	public static void setSeed(long seed) {
		rng.setSeed(seed);
	}

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		Object o = super.evaluate(context, lazy);
		isConstant = false; // never constant
		return o;
	}

	@Override
	protected float operate(float a, float b) {
		float low, high;
		if (a < b) {
			low = a;
			high = b;
		} else if (b < a) {
			low = b;
			high = a;
		} else {
			return a;
		}
		return rng.nextFloat() * (high - low) + low;
	}

	@Override
	protected int operate(int a, int b) {
		int low, high;
		if (a < b) {
			low = a;
			high = b;
		} else if (b < a) {
			low = b;
			high = a;
		} else {
			return a;
		}
		return rng.nextInt(high - low) + low;
	}

}
