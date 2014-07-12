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
package es.eucm.ead.editor.control.actions.model.generic;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;

/**
 * Adds an element to an array
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Object}</em> Parent of the list</dd>
 * <dd><strong>args[1]</strong> <em>{@link Array}</em> The list</dd>
 * <dd><strong>args[2]</strong> <em>{@link Object}</em> the element to add to
 * the list</dd>
 * <dd><strong>args[3]</strong> <em>{@link Integer} (optional)</em> the index
 * where to add the object. If this argument is not present, is added to the end
 * of the list</dd>
 * </dl>
 */
public class AddToArray extends ModelAction {

	public AddToArray() {
		super(true, false, new Class[] { Object.class, Array.class,
				Object.class }, new Class[] { Object.class, Array.class,
				Object.class, Integer.class });
	}

	@Override
	public Command perform(Object... args) {
		Object parent = args[0];
		Array array = (Array) args[1];
		Object item = args[2];
		int index = -1;
		if (args.length == 4) {
			index = (Integer) args[3];
		}
		return new AddToListCommand(parent, array, item, index);
	}
}
