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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.physics.BoundingAreaComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.variables.VarsContext;

/**
 * Operator that checks if two given entities intersect
 * 
 * @author jtorrente
 */
abstract class AbstractEntityIntersection extends Operation {

	protected GameLoop gameLoop;

	public AbstractEntityIntersection(GameLoop gameLoop) {
		super(2, 2);
		this.gameLoop = gameLoop;
	}

	@Override
	public Object evaluate(VarsContext context)
			throws ExpressionEvaluationException {

		// Check first argument is Entity and retrieve it
		Object o1 = first().evaluate(context);
		Object o2 = second().evaluate(context);

		Array<EngineEntity> firstGroup = populateArray(o1, 1);
		Array<EngineEntity> secondGroup = populateArray(o2, 2);

		Array<EngineEntity> results = new Array<EngineEntity>();
		for (EngineEntity first : firstGroup) {
			BoundingAreaComponent bounding1 = getBoundingArea(first);
			for (EngineEntity second : secondGroup) {
				BoundingAreaComponent bounding2 = getBoundingArea(second);
				if (bounding1.overlaps(bounding2, true)) {
					results.add(second);
					if (!returnList()) {
						return processResults(results);
					}
				}
			}
		}

		return processResults(results);
	}

	protected abstract Object processResults(Array<EngineEntity> results);

	protected abstract boolean returnList();

	protected abstract boolean acceptsListOperand(int nOperand);

	private Array<EngineEntity> populateArray(Object value, int nOperand)
			throws ExpressionEvaluationException {
		if (value == null
				|| (!ClassReflection.isAssignableFrom(EngineEntity.class,
						value.getClass()) && (!acceptsListOperand(nOperand) || !ClassReflection
						.isAssignableFrom(Array.class, value.getClass())))) {
			throw new ExpressionEvaluationException("Expected Entity "
					+ (acceptsListOperand(nOperand) ? "or lists as operand "
							: "") + nOperand + " in " + getName(), this);
		}

		Array<EngineEntity> entities = new Array<EngineEntity>();
		if (ClassReflection.isAssignableFrom(EngineEntity.class,
				value.getClass())) {
			entities.add((EngineEntity) value);
		} else {
			Array valueArray = (Array) value;
			for (Object arrayElement : valueArray) {
				if (ClassReflection.isAssignableFrom(EngineEntity.class,
						arrayElement.getClass())) {
					entities.add((EngineEntity) arrayElement);
				}
			}
		}
		return entities;
	}

	private BoundingAreaComponent getBoundingArea(Object entity) {
		if (ClassReflection.isAssignableFrom(EngineEntity.class,
				entity.getClass())) {
			EngineEntity castedEntity = (EngineEntity) entity;
			BoundingAreaComponent bounding = gameLoop.addAndGetComponent(
					castedEntity, BoundingAreaComponent.class);
			// if (!bounding.isInit()) {
			bounding.update(castedEntity);
			bounding.updateConvexHull(castedEntity);
			// }
			return bounding;
		}
		return null;
	}
}
