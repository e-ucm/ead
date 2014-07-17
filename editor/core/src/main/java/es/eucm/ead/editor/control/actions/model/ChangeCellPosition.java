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
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;

/**
 * Changes the position of a given cell. perform(Object... args) will return
 * null if no cell is found at the given position.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Integer}</em> The current row of the
 * cell.</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> The current column of
 * the cell.</dd>
 * <dd><strong>args[2]</strong> <em>{@link Integer}</em> The new row of the
 * cell.</dd>
 * <dd><strong>args[3]</strong> <em>{@link Integer}</em> The new column of the
 * cell.</dd>
 * </dl>
 */
public class ChangeCellPosition extends ModelAction {

	public ChangeCellPosition() {
		super(true, false, Integer.class, Integer.class, Integer.class,
				Integer.class);
	}

	@Override
	public Command perform(Object... args) {

		SceneMap sceneMap = Q.getComponent(controller.getModel().getGame(),
				SceneMap.class);
		CompositeCommand composite = new CompositeCommand();
		Array<Cell> cells = sceneMap.getCells();
		int currentColumn = (Integer) args[1];
		int currentRow = (Integer) args[0];

		for (Cell cell : cells) {
			if (cell.getRow() == currentRow
					&& cell.getColumn() == currentColumn) {

				// Remove the current cell and add a new one in the new position
				// in order to get notified of the list events.

				Cell newCell = new Cell();
				newCell.setSceneId(cell.getSceneId());
				newCell.setRow((Integer) args[2]);
				newCell.setColumn((Integer) args[3]);

				composite.addCommand(new RemoveFromListCommand(sceneMap, cells,
						cell));
				composite.addCommand(new AddToListCommand(sceneMap, cells,
						newCell));
				return composite;
			}
		}

		return null;
	}
}
