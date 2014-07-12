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
package es.eucm.ead.editor.control.actions.model.parameters;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;
import es.eucm.ead.schemax.FieldName;

/**
 * Sets the value of a parameter in an {@link Parameters}. If the parameter does
 * not exist, it is automatically created.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Parameters}</em> Object with the
 * parameters</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> Parameter name</dd>
 * <dd><strong>args[2]</strong> <em>{@link String}</em> Parameter value</dd>
 * </dl>
 */
public class SetParameter extends ModelAction {

	public SetParameter() {
		super(true, false, Parameters.class, String.class, String.class);
	}

	@Override
	public Command perform(Object... args) {
		Parameters parent = (Parameters) args[0];
		Array<Parameter> parameters = parent.getParameters();
		String field = (String) args[1];
		String value = (String) args[2];

		for (Parameter parameter : parameters) {
			if (parameter.getName().equals(field)) {
				return new FieldCommand(parameter, FieldName.VALUE, value);
			}
		}

		Parameter parameter = new Parameter();
		parameter.setName(field);
		parameter.setValue(value);

		return new AddToListCommand(parent, parameters, parameter);
	}
}
