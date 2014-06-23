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
import java.util.List;
import java.util.Map;

/**
 * Operation that retrieves a specific element from a "container". Can receive
 * one or two arguments. - First argument: container - Second argument: index or
 * key
 * 
 * Supported containers:
 * 
 * <pre>
 * {@link Array}, {@link List}, {@link Collection}, {@link Iterable}.
 *     In those cases, the second argument should be a number
 *     (position in the container to retrieve). If not present,
 *     0 is taken by default.
 * {@link Map}
 *     In this case, the second argument should be the object key
 *     of the map.
 * </pre>
 * 
 * Created by Javier Torrente on 1/06/14.
 */
public class GetFromCollection extends Operation {

	public GetFromCollection() {
		super(1, 2);
	}

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		if (lazy && isConstant) {
			return value;
		}

		// Second argument, if present, should be the index of the element to
		// retrieve.
		// If not present, the first (0) element is returned
		int index = 0;
		Object indexObject = null;
		if (children.size() > 1) {
			Object arg2 = second().evaluate(context, lazy);
			if (arg2 instanceof Number) {
				index = ((Number) arg2).intValue();
			} else {
				indexObject = arg2;
			}
		}

		// First argument should always be a collection or similar
		Object arg1 = first().evaluate(context, lazy);
		boolean found = true;

		try {
			if (arg1.getClass().isArray()) {
				value = ArrayReflection.get(arg1, index);
			} else if (arg1 instanceof Array) {
				Array array = (Array) arg1;
				value = array.get(index);
			} else if (arg1 instanceof List) {
				List list = (List) arg1;
				value = list.get(index);
			} else if (arg1 instanceof Map) {
				Map map = (Map) arg1;
				value = map.get(indexObject);
			} else if (arg1 instanceof Collection) {
				Collection collection = (Collection) arg1;
				value = collection.toArray()[index];
			} else if (arg1 instanceof Iterable) {
				Iterable iterable = (Iterable) arg1;
				int i = 0;
				for (Object object : iterable) {
					if (index == i++) {
						value = object;
						break;
					} else {
						found = false;
					}
				}
			} else {
				found = false;
			}
		} catch (Exception e) {
			found = false;
		}

		if (found) {
			return value;
		} else {
			throw new ExpressionEvaluationException(
					"Could not evaluate "
							+ getName()
							+ ". Revise the first argument is a collection, array, iterable or map, and the second argument is a valid element position (for collections, arrays, iterables) or key (for maps)",
					this);
		}
	}
}
