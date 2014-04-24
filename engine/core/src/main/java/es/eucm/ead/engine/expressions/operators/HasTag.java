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

import ashley.core.Entity;
import es.eucm.ead.engine.components.TagsComponent;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.systems.variables.VarsContext;

/**
 * Operator that checks if the given entity has a specific tag.
 * 
 * @author jtorrente
 */
class HasTag extends AbstractBooleanOperation {

	public HasTag() {
		super(2, 2);
	}

	@Override
	public Object evaluate(VarsContext context, boolean lazy)
			throws ExpressionEvaluationException {
		if (lazy && isConstant) {
			return value;
		}

		value = false;

		// Check first argument is Entity and retrieve it
		Object o1 = first().evaluate(context, lazy);
		if (o1 == null || !Entity.class.isAssignableFrom(o1.getClass())) {
			throw new ExpressionEvaluationException(
					"Expected Entity operand in " + getName(), this);
		}

		Entity entity = (Entity) o1;
		if (entity != null && entity.hasComponent(TagsComponent.class)) {
			TagsComponent tags = entity.getComponent(TagsComponent.class);

			// Check second argument is String and retrieve it (tag name)
			Object o2 = second().evaluate(context, lazy);
			if (!o2.getClass().equals(String.class)) {
				throw new ExpressionEvaluationException(
						"Expected String operand in " + getName(), this);
			}

			for (String tag : tags.getTags()) {
				if (tag.equals(o2)) {
					value = true;
					break;
				}
			}
		}

		isConstant = first().isConstant() && second().isConstant();
		return value;
	}
}
