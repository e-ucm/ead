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
package es.eucm.ead.engine.utils;

import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;

public class EngineUtils {

	public static <T extends Parameters> T buildWithParameters(Assets assets,
			VariablesManager variablesManager, T parameters) {
		if (parameters.getParameters().size > 0) {
			Class clazz = parameters.getClass();
			Parameters clone = (Parameters) assets.fromJson(clazz,
					assets.toJson(parameters, clazz));
			for (Parameter parameter : clone.getParameters()) {
				Object value = variablesManager.evaluateExpression(parameter
						.getValue());
				variablesManager.getAccessor().set(clone, parameter.getName(),
						value);
			}
			return (T) clone;
		} else {
			return parameters;
		}
	}
}
