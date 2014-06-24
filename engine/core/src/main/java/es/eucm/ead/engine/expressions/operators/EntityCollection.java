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
import ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.variables.VarsContext;

import java.util.Iterator;

/**
 * Operation that returns a collection (array) of entities that match a
 * condition (boolean expression) given.
 * 
 * The operation syntax is as follows: (collection matchingCondition)
 * 
 * where matchingCondition is the boolean expression that determines if an
 * entity is to be added to the collection or not.
 * 
 * The operation also works with two arguments, following the next syntax:
 * 
 * (collection sEntityVarName matchingCondition)
 * 
 * where EntityVarName is the name of a variable that points to the entity being
 * processed at a time. This allows matchingCondition refer to the entity being
 * processed. By default, when the one-argument version of the operation is
 * used, EntityVarName is set to {@value #DEFAULT_ITERATING_ENTITY_NAME}.
 * 
 * 
 * Examples:
 * 
 * <pre>
 *   One argument:
 *   (collection btrue) // returns all entities
 *   (collection (eq group.x 20)) //returns all entities located at
 *                                // position x=20
 * 
 *   Two arguments:
 *   (collection sAnEntity (not (eq $AnEntity $_this)))
 *                     // returns all entities but this
 *                     // expression's owner.
 *   The expression above is equivalent to this one-argument version:
 *   (collection (not (eq ${@value #DEFAULT_ITERATING_ENTITY_NAME} $_this)))
 * </pre>
 * 
 * Created by Javier Torrente on 29/05/14.
 */
public class EntityCollection extends Operation {

	public static final String DEFAULT_ITERATING_ENTITY_NAME = "entity";

	private GameLoop engine;

	public EntityCollection(GameLoop engine) {
		super(1, 2);
		this.engine = engine;
	}

	@Override
	public Object evaluate(VarsContext context)
			throws ExpressionEvaluationException {

		// Create context to register entity var pointing to the variable being
		// iterated
		VarsContext tempContext = Pools.obtain(VarsContext.class);
		tempContext.setParent(context);
		String varName = DEFAULT_ITERATING_ENTITY_NAME;

		// If there are two operands, the first one should be the name of the
		// temp variable used to refer to the entity being iterated
		if (children.size() > 1) {
			Object varNameObject = first().evaluate(context);
			if (!(varNameObject instanceof String)) {
				throw new ExpressionEvaluationException(
						"Expected string first operand in " + getName(), this);
			} else {
				varName = (String) varNameObject;
			}
		}

		// Register the variable (null initialization)
		tempContext.registerVariable(varName, null, Entity.class);

		// Iterate through entities
		Array<Entity> entities = new Array<Entity>();
		Iterator<Entity> allEntities = engine
				.getEntitiesFor(Family.getFamilyFor()).values().iterator();
		try {
			while (allEntities.hasNext()) {
				Entity otherEntity = allEntities.next();
				// Set entity
				tempContext.setValue(varName, otherEntity);
				// Evaluate expression
				Object expResult = (children.size() > 1 ? second() : first())
						.evaluate(tempContext);
				if (!(expResult instanceof Boolean)) {
					throw new ExpressionEvaluationException(
							"Expected condition (boolean expression) operand in "
									+ getName()
									+ ". The expression did not return a boolean",
							this);
				}
				Boolean matches = (Boolean) expResult;
				if (matches) {
					entities.add(otherEntity);
				}
			}
		} catch (Exception e) {
			throw new ExpressionEvaluationException(
					"Bloody hell! The expression was not well formed and therefore it was not possible to evaluate the "
							+ getName() + " operation. ", this);
		}
		// Free the temp context created to host "entity" var
		Pools.free(tempContext);

		return entities;
	}
}
