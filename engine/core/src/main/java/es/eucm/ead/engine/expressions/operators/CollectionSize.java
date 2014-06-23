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
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.variables.VarsContext;

import java.util.Collection;
import java.util.Map;

/**
 * Operation that retrieves the size of a "container". Expects one argument:
 * container
 * 
 * Supported containers: {@link com.badlogic.gdx.utils.Array},
 * {@link java.util.List}, {@link java.util.Collection}, {@link Iterable},
 * {@link java.util.Map}.
 * 
 * Created by Javier Torrente on 1/06/14.
 */
public class CollectionSize extends Operation {

	public CollectionSize() {
		super(1, 1);
	}

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		if (lazy && isConstant) {
			return value;
		}

		// First arg should be the container object
		// First argument should always be a collection or similar
		Object arg1 = first().evaluate(context, lazy);
		boolean resolved = true;

		try {
			if (arg1.getClass().isArray()) {
				value = ArrayReflection.getLength(arg1);
			} else if (arg1 instanceof Array) {
				Array array = (Array) arg1;
				value = array.size;
			} else if (arg1 instanceof Map) {
				Map map = (Map) arg1;
				value = map.size();
			} else if (arg1 instanceof Collection) {
				Collection collection = (Collection) arg1;
				value = collection.size();
			} else if (arg1 instanceof Iterable) {
				Iterable iterable = (Iterable) arg1;
				int i = 0;
				for (Object object : iterable) {
					i++;
				}
				value = i;
			} else {
				resolved = false;
			}
		} catch (Exception e) {
			resolved = false;
		}

		if (resolved) {
			return value;
		} else {
			throw new ExpressionEvaluationException(
					"Could not evaluate "
							+ getName()
							+ ". Revise the first argument is a collection, array, iterable or map.",
					this);
		}
	}
}
