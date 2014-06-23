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

import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schemax.Layer;

/**
 * Simple operation that retrieves the {@link EngineEntity} associated to the
 * layer with the given id. It is case insensitive.
 * 
 * Examples: (layer shud) (layer sSCENE_CONTENT) ...
 * 
 * Created by Javier Torrente on 28/05/14.
 */
public class GetLayerOperation extends Operation {

	private GameView gameView;

	public GetLayerOperation(GameView gameView) {
		super(1, 1);
		this.gameView = gameView;
	}

	@Override
	public Object evaluate(VarsContext context)
			throws ExpressionEvaluationException {

		Object operand = first().evaluate(context);
		if (!(operand instanceof String)) {
			throw new ExpressionEvaluationException(
					"Expected String operand (game layer name) in " + getName(),
					this);
		}

		String layerName = (String) operand;
		try {
			Layer layer = Layer.fromValue(layerName);
			return gameView.getLayer(layer);
		} catch (IllegalArgumentException e) {
			// Try lowercase
			try {
				Layer layer = Layer.fromValue(layerName.toLowerCase());
				return gameView.getLayer(layer);
			} catch (IllegalArgumentException e2) {
				// Try uppercase
				try {
					Layer layer = Layer.fromValue(layerName.toUpperCase());
					return gameView.getLayer(layer);
				} catch (IllegalArgumentException e3) {
					throw new ExpressionEvaluationException(
							"String operand provided does not match any layer name in "
									+ getName() + ". Should be one of these: "
									+ Layer.values(), this);
				}
			}
		}
	}
}
