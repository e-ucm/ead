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
package es.eucm.ead.engine.expressions.ops;

import es.eucm.ead.engine.VarsContext;
import es.eucm.ead.engine.expressions.Node;
import es.eucm.ead.schema.components.VariableDef;

/**
 * Casting operator. Four delicious flavors.
 * 
 * @author mfreire
 */
public abstract class CastOperator extends Operator {

	protected VariableDef.Type targetType;

	public CastOperator(VariableDef.Type targetType) {
		super(1, 1);
		this.targetType = targetType;
	}

	@Override
	public VariableDef.Type getValueType(Node node) throws ExpressionException {
		return targetType;
	}

	@Override
	public Object eval(Node node, VarsContext context)
			throws ExpressionException {
		return singleEval(node.getChildren().get(0), targetType, context);
	}

	public static class AsInt extends CastOperator {
		public AsInt() {
			super(VariableDef.Type.INTEGER);
		}
	}

	public static class AsFloat extends CastOperator {
		public AsFloat() {
			super(VariableDef.Type.FLOAT);
		}
	}

	public static class AsBoolean extends CastOperator {
		public AsBoolean() {
			super(VariableDef.Type.BOOLEAN);
		}
	}

	public static class AsString extends CastOperator {
		public AsString() {
			super(VariableDef.Type.STRING);
		}
	}
}
