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

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Action for reordering elements on a list.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>Array<{@link ModelEntity}></em> The list of
 * the entities to order</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> or
 * <em>Array<{@link Integer}></em>The final position of the object in the list.
 * It can be either relative or absolute depending on an optional args[2]. It
 * can be a list, in this case, it will be treated circularly</dd>
 * <dd><strong>args[2]</strong> <em>{@link Boolean}</em> Is a boolean that
 * indicates whether the reordering is relative</dd>
 * <dd><strong>args[3]</strong> <em>Array<{@link ModelEntity}</em> may be the
 * list. It is optional since subclasses may know what list has to be used. If
 * args[3] is null or a String, the action relies on
 * {@link #findListById(String)} to find the list.</dd>
 * </dl>
 */
public class ReorderMultiple extends ModelAction {

	private Reorder reorder;

	public ReorderMultiple() {
		super(true, false, ModelEntity.class, Array.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		reorder = controller.getActions().getAction(Reorder.class);
	}

	@Override
	public CompositeCommand perform(Object... args) {

		if (args.length < 2) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": cannot execute reorder action with less than 3 arguments");
		}

		CompositeCommand compositeCommand = new CompositeCommand();
		Array<Object> listToOrder = (Array) args[0];
		Array<Integer> positions = new Array<Integer>();

		if (!(args[1] instanceof Integer)) {
			if (!(args[1] instanceof Array) || ((Array) args[1]).size < 1
					|| !(((Array) args[1]).get(0) instanceof Integer)) {
				throw new EditorActionException(
						"Error in action "
								+ this.getClass().getCanonicalName()
								+ ": the second argument have to be a Integer or not empty Array<Integer>");
			} else {
				positions = (Array) args[1];
			}
		} else {
			positions.add((Integer) args[1]);
		}

		Object parentList = null;
		boolean relative = false;

		if (args.length == 3 && !(args[2] instanceof Boolean)) {
			parentList = args[2];
		} else if (!(args[2] instanceof Boolean)) {
			throw new EditorActionException("Error in action "
					+ this.getClass().getCanonicalName()
					+ ": the second argument have to be a Boolean");
		} else {
			relative = (Boolean) args[2];
			if (args.length == 4) {
				parentList = args[3];
			}
		}

		int executedCommands = 0;
		int commands = 0;
		for (Object object : listToOrder) {
			Command command = reorder.perform(object,
					positions.get(commands % positions.size), relative,
					parentList);
			// The reorder can be null (object not reordered)
			if (command != null) {
				compositeCommand.addCommand(command);
				executedCommands++;
			}
			commands++;
		}

		// If the number of reorders is zero, return null and not an empty
		// compositeCommand
		if (executedCommands > 0) {
			return compositeCommand;
		} else {
			return null;
		}
	}
}