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
package es.eucm.ead.editor.control.actions.model;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schemax.FieldName;

/**
 * Changes the documentation name or description
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Documentation}</em> target object
 * whose field will be changed</dd>
 * <dd><strong>args[1]</strong> <em>{@link Boolean}</em> whether it's the name
 * or the description that will be changed (if true then the name will be
 * changed)</dd>
 * <dd><strong>args[2]</strong> <em>{@link String}</em> new value for the field</dd>
 * </dl>
 */
public class ChangeDocumentation extends ModelAction {

	public ChangeDocumentation() {
		super(true, false, Documentation.class, Boolean.class, String.class);
	}

	@Override
	public Command perform(Object... args) {
		return new FieldCommand(args[0], ((Boolean) args[1]) ? FieldName.NAME
				: FieldName.DESCRIPTION, args[2], true);
	}
}
