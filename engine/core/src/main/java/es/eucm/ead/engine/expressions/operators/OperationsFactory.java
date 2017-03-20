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

import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.schemax.OperationsNames;

/**
 * A factory class for all recognized operations.
 * 
 * @author mfreire
 */
public class OperationsFactory {

	private Accessor accessor;

	private GameView gameView;

	private GameLoop gameLoop;

	public OperationsFactory(GameLoop gameLoop, Accessor accessor,
			GameView gameView) {
		this.accessor = accessor;
		this.gameView = gameView;
		this.gameLoop = gameLoop;
	}

	public OperationsFactory() {

	}

	public Operation createOperation(String name) {
		Operation op = null;
		if (OperationsNames.AND.equals(name)) {
			op = new And();
		} else if (OperationsNames.OR.equals(name)) {
			op = new Or();
		} else if (OperationsNames.NOT.equals(name)) {
			op = new Not();
		} else if (OperationsNames.XOR.equals(name)) {
			op = new Xor();
		} else if (OperationsNames.SUM.equals(name)) {
			op = new Add();
		} else if (OperationsNames.SUBTRACTION.equals(name)) {
			op = new Sub();
		} else if (OperationsNames.MULTIPLICATION.equals(name)) {
			op = new Mul();
		} else if (OperationsNames.DIVISION.equals(name)) {
			op = new Div();
		} else if (OperationsNames.MODULUS.equals(name)) {
			op = new Mod();
		} else if (OperationsNames.POW.equals(name)) {
			op = new Pow();
		} else if (OperationsNames.SQRT.equals(name)) {
			op = new Sqrt();
		} else if (OperationsNames.MIN.equals(name)) {
			op = new Min();
		} else if (OperationsNames.MAX.equals(name)) {
			op = new Max();
		} else if (OperationsNames.RAND.equals(name)) {
			op = new Rand();
		} else if (OperationsNames.EQUALS.equals(name)) {
			op = new EquivalenceOperation();
		} else if (OperationsNames.DIFFERENT.equals(name)) {
			op = new DifferentOperation();
		} else if (OperationsNames.LOWER_EQUALS.equals(name)) {
			op = new LowerEqual();
		} else if (OperationsNames.LOWER_THAN.equals(name)) {
			op = new LowerThan();
		} else if (OperationsNames.GREATER_EQUALS.equals(name)) {
			op = new GreaterEqual();
		} else if (OperationsNames.GREATER_THAN.equals(name)) {
			op = new GreaterThan();
		} else if (OperationsNames.INTEGER.equals(name)) {
			op = new AsInt();
		} else if (OperationsNames.FLOAT.equals(name)) {
			op = new AsFloat();
		} else if (OperationsNames.BOOLEAN.equals(name)) {
			op = new AsBoolean();
		} else if (OperationsNames.STRING.equals(name)) {
			op = new AsString();
		} else if (OperationsNames.CONCATENATION.equals(name)) {
			op = new Concat();
		} else if (OperationsNames.ENTITY_HAS_TAG.equals(name)) {
			op = new HasTag();
		} else if (OperationsNames.PROPERTY_IN_OBJECT.equals(name)) {
			op = new AccessProperty(accessor);
		} else if (OperationsNames.GET_LAYER.equals(name)) {
			op = new GetLayerOperation(gameView);
		} else if (OperationsNames.ENTITY_COLLECTION.equals(name)) {
			op = new EntityCollection(gameLoop);
		} else if (OperationsNames.GET_FROM_COLLECTION.equals(name)) {
			op = new GetFromCollection();
		} else if (OperationsNames.COLLECTION_SIZE.equals(name)) {
			op = new CollectionSize();
		} else if (OperationsNames.LIST.equals(name)) {
			op = new List();
		} else if (OperationsNames.RANDOM_LIST.equals(name)) {
			op = new RandomList();
		} else if (OperationsNames.TERNARY_OPERATOR.equals(name)) {
			op = new IfOperator();
		} else if (OperationsNames.ENTITY_ID.equals(name)) {
			op = new Id();
		} else if (OperationsNames.GET_ENTITY_FROM_ID.equals(name)) {
			op = new FromId(gameLoop);
		} else if (OperationsNames.ENTITIES_INTERSECT.equals(name)) {
			op = new Intersects(gameLoop);
		} else if (OperationsNames.ENTITY_INTERSECTION_SET.equals(name)) {
			op = new Intersection(gameLoop);
		}

		if (op != null) {
			op.setName(name);
			return op;
		} else {
			throw new IllegalArgumentException("No operation named '" + name
					+ "'");
		}
	}
}
