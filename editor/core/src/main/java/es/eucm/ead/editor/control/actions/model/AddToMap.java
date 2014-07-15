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
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schemax.FieldName;

/**
 * Adds a new empty row/column at a given index in the {@link SceneMap}.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link Boolean}</em> Whether it should add a
 * new row or a new column. If true a new row will be added.</dd>
 * <dd><strong>args[1]</strong> <em>{@link Integer}</em> The index where to add
 * the new row/column. You can also pass {@link #BEGINING} as index to add the
 * new row/column at the index 0 or {@link #END} to add the new row/column at
 * the end.</dd>
 * </dl>
 */
public class AddToMap extends ModelAction {

	public static final int BEGINING = 0;
	public static final int END = Integer.MAX_VALUE;

	public AddToMap() {
		super(true, false, Boolean.class, Integer.class);
	}

	@Override
	public Command perform(Object... args) {

		SceneMap sceneMap = Q.getComponent(controller.getModel().getGame(),
				SceneMap.class);

		CompositeCommand composite = new CompositeCommand();
		int currentColumns = sceneMap.getColumns();
		Array<Cell> cells = sceneMap.getCells();
		int currentRows = sceneMap.getRows();
		boolean addRow = (Boolean) args[0];
		int index = (Integer) args[1];
		String cellFieldName = null;
		String mapFieldName = null;
		int mapFieldValue;

		if (addRow) {

			mapFieldValue = currentRows + 1;
			cellFieldName = FieldName.ROW;
			mapFieldName = FieldName.ROWS;
			if (index == END) {
				index = currentRows;
			}
		} else {

			mapFieldValue = currentColumns + 1;
			cellFieldName = FieldName.COLUMN;
			mapFieldName = FieldName.COLUMNS;
			if (index == END) {
				index = currentColumns;
			}
		}

		// Increase the row of the necessary cells
		for (Cell cell : cells) {
			int cellValue = addRow ? cell.getRow() : cell.getColumn();
			if (cellValue >= index) {
				composite.addCommand(new FieldCommand(cell, cellFieldName,
						cellValue + 1));
			}
		}
		composite.addCommand(new FieldCommand(sceneMap, mapFieldName,
				mapFieldValue));

		return composite;
	}
}
