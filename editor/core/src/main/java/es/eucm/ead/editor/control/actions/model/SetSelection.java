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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.SelectionCommand;

/**
 * Sets the current selection
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> parent context id</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> context id</dd>
 * <dd><strong>args[2]</strong> <em>{@link Array}</em> list with the objects for
 * the selection.
 * <dd>or</dd>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> parent context id</dd>
 * <dd><strong>args[1]</strong> <em>{@link String}</em> context id</dd>
 * <dd><strong>args[2..n]</strong> <em>{@link Object}</em> objects for the
 * selection. Cannot be null.</dd>
 * </dl>
 */
public class SetSelection extends ModelAction {

	@Override
	public boolean validate(Object... args) {
		if ((args[0] == null || args[0] instanceof String)
				&& args[1] instanceof String) {
			for (int i = 2; i < args.length; i++) {
				if (args[i] == null) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public Command perform(Object... args) {
		if (args.length == 3 && args[2] instanceof Array) {
			return new SelectionCommand(controller.getModel(),
					(String) args[0], (String) args[1],
					((Array) args[2]).toArray());
		} else {
			Object[] selection = new Object[args.length - 2];
			System.arraycopy(args, 2, selection, 0, selection.length);
			return new SelectionCommand(controller.getModel(),
					(String) args[0], (String) args[1], selection);
		}
	}
}
