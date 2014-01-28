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

import es.eucm.ead.engine.expressions.ops.BooleanOperator.And;
import es.eucm.ead.engine.expressions.ops.BooleanOperator.Not;
import es.eucm.ead.engine.expressions.ops.BooleanOperator.Or;
import es.eucm.ead.engine.expressions.ops.BooleanOperator.Xor;
import es.eucm.ead.engine.expressions.ops.CastOperator.AsBoolean;
import es.eucm.ead.engine.expressions.ops.CastOperator.AsFloat;
import es.eucm.ead.engine.expressions.ops.CastOperator.AsInt;
import es.eucm.ead.engine.expressions.ops.CastOperator.AsString;
import es.eucm.ead.engine.expressions.ops.ComparisonOperator.Ge;
import es.eucm.ead.engine.expressions.ops.ComparisonOperator.Gt;
import es.eucm.ead.engine.expressions.ops.ComparisonOperator.Le;
import es.eucm.ead.engine.expressions.ops.ComparisonOperator.Lt;
import es.eucm.ead.engine.expressions.ops.DyadicMathOperator.Div;
import es.eucm.ead.engine.expressions.ops.DyadicMathOperator.Mod;
import es.eucm.ead.engine.expressions.ops.DyadicMathOperator.Pow;
import es.eucm.ead.engine.expressions.ops.DyadicMathOperator.Rand;
import es.eucm.ead.engine.expressions.ops.DyadicMathOperator.Sub;
import es.eucm.ead.engine.expressions.ops.Eq;
import es.eucm.ead.engine.expressions.ops.MathOperator.Add;
import es.eucm.ead.engine.expressions.ops.MathOperator.Mul;
import es.eucm.ead.engine.expressions.ops.MathOperator.Sqrt;
import es.eucm.ead.engine.expressions.ops.Operator;
import java.util.HashMap;

/**
 * 
 * @author mfreire
 */
public class OperatorRegistry {
	private final HashMap<String, Operator> nameToOp = new HashMap<String, Operator>();

	private void put(String name, Operator op) {
		if (nameToOp.put(name, op) == null) {
			op.setName(name);
		} else {
			throw new IllegalArgumentException("Operator name '" + name
					+ "' was already registered");
		}
	}

	public OperatorRegistry() {
		put("and", new And());
		put("or", new Or());
		put("not", new Not());
		put("xor", new Xor());

		put("-", new Sub());
		put("+", new Add());
		put("*", new Mul());
		put("/", new Div());
		put("%", new Mod());

		put("pow", new Pow());
		put("sqrt", new Sqrt());
		put("rand", new Rand());

		put("eq", new Eq());
		put("le", new Le());
		put("lt", new Lt());
		put("ge", new Ge());
		put("gt", new Gt());

		put("int", new AsInt());
		put("float", new AsFloat());
		put("bool", new AsBoolean());
		put("string", new AsString());
	}

	public Operator getOperator(String name) {
		return nameToOp.get(name);
	}
}
