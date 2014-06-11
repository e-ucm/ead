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
import es.eucm.ead.editor.control.commands.ListCommand.ReorderInListCommand;

import java.util.List;

/**
 * 
 * Action for reordering elements on a list.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Object}</em> parent of the list</dd>
 * <dd><strong>args[1]</strong> <em>{@link List}</em> the list that contains the
 * object to be reordered
 * <dd><strong>args[2]</strong> <em>{@link Object}</em> element to be reordered
 * in the list.</dd>
 * <dd><strong>args[3]</strong> <em>{@link Integer}</em> the final position of
 * the object in the list.</dd>
 * <dd><strong>args[4]</strong> <em>{@link Boolean}</em> (Optional) if the final
 * position is relative. If true, the object is moved as many spaces in the list
 * as specified by args[3]. If not present, default value is set to false</dd>
 * </dl>
 * 
 * Created by Javier Torrente on 9/03/14.
 */
public class Reorder extends ModelAction {

	public Reorder() {
		super(true, false, Object.class, List.class, Object.class,
				Integer.class, Boolean.class);
	}

	public boolean validate(Object... args) {
		if (super.validate(args)) {
			if (args[0] != null && args[1] != null && args[2] != null
					&& args[3] != null) {
				List list = (List) args[1];
				Object elementToBeReordered = args[2];
				return list.contains(elementToBeReordered);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public Command perform(Object... args) {
		Object parent = args[0];
		List list = (List) args[1];
		Object elementToBeReordered = args[2];
		Integer destinyPosition = (Integer) args[3];
		boolean relative = args.length == 5 && args[4] instanceof Boolean ? (Boolean) args[4]
				: false;
		int sourcePosition = list.indexOf(elementToBeReordered);

		if (relative) {
			destinyPosition += sourcePosition;
		}

		if (destinyPosition < 0) {
			destinyPosition = 0;
		} else if (destinyPosition >= list.size()) {
			destinyPosition = list.size() - 1;
		}

		if (sourcePosition != destinyPosition) {
			return new ReorderInListCommand(parent, list, elementToBeReordered,
					destinyPosition);
		}
		return null;
	}
}
