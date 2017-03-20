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

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.TagsComponent;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.variables.VarsContext;

/**
 * Operator that fetches an entity by its internal id.
 * 
 * @author jtorrente
 */
class FromId extends Operation {

	private GameLoop engine;

	public FromId(GameLoop engine) {
		super(1, 1);
		this.engine = engine;
	}

	@Override
	public Object evaluate(VarsContext context)
			throws ExpressionEvaluationException {

		// Check first argument is Entity and retrieve it
		Object o1 = first().evaluate(context);
		if (o1 == null || (o1.getClass() != String.class)) {
			throw new ExpressionEvaluationException(
					"Expected String operand in " + getName(), this);
		}

		String id = (String) o1;
		Entity entity = null;
		try {
			long lid = Long.parseLong(id);
			entity = engine.getEntity(lid);
		} catch (NumberFormatException e) {
		}

		if (entity == null) {
			throw new ExpressionEvaluationException("No entity with id " + id
					+ " could be found", this);
		}
		return entity;
	}
}
