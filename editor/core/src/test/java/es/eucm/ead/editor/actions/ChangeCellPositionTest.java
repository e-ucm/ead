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
package es.eucm.ead.editor.actions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.eucm.ead.editor.control.actions.model.ChangeCellPosition;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;

public class ChangeCellPositionTest extends ActionTest {

	@Test
	public void testChangeCellPosition() {
		openEmpty();

		Model model = controller.getModel();

		SceneMap sceneMap = Q.getComponent(model.getGame(), SceneMap.class);
		Cell cell = sceneMap.getCells().first();
		final int oldRow = cell.getRow();
		final int oldColumn = cell.getColumn();

		final int newRow = oldRow + 1;
		final int newColumn = oldColumn + 1;

		model.addListListener(sceneMap.getCells(),
				new ModelListener<ListEvent>() {

					@Override
					public void modelChanged(ListEvent event) {
						Cell cell = (Cell) event.getElement();
						switch (event.getType()) {
						case REMOVED:
							assertTrue(
									"Failed to remove the cell from the map.",
									cell.getRow() == oldRow
											&& cell.getColumn() == oldColumn);
							break;
						case ADDED:
							assertTrue("The added cell position is wrong.",
									cell.getRow() == newRow
											&& cell.getColumn() == newColumn);
							break;
						}

					}
				});

		controller.action(ChangeCellPosition.class, oldRow, oldColumn, newRow,
				newColumn);

		clearEmpty();
	}

}
