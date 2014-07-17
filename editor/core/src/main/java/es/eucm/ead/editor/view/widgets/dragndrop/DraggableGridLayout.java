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
package es.eucm.ead.editor.view.widgets.dragndrop;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.layouts.GridLayout;
import es.eucm.ead.editor.view.widgets.layouts.GridLayout.Cell;

/**
 * A widget that it's used to display widgets in a {@link GridLayout grid}. It
 * allows the user to drag and drop these widgets with the help of a
 * {@link DraggableScrollPane}.
 */
public class DraggableGridLayout extends DraggableScrollPane {

	private static final int INITIAL_ROWS = 1;
	private static final int INITIAL_COLUMNS = 1;

	private GridLayout gridLayout;

	public DraggableGridLayout() {
		this(INITIAL_ROWS, INITIAL_COLUMNS);
	}

	public DraggableGridLayout(int initialRows, int initialColumns) {
		super(null);
		setWidget(gridLayout = new GridLayout(initialRows, initialColumns));
		addTarget(createTarget());
	}

	/**
	 * Adds an actor to the grid. The actor will be draggable between the other
	 * empty cells of the layout. May be null in order to add an empty cell.
	 * 
	 * @param actor
	 *            may be null.
	 * @return the cell in order to adjust constraints such as fill, align, etc.
	 */
	public Cell add(Actor actor) {
		if (actor != null) {
			addSource(newSource(actor));
		}
		return gridLayout.add(actor);
	}

	public Cell addAt(int row, int col, Actor actor) {
		if (actor != null) {
			addSource(newSource(actor));
		}
		return gridLayout.addAt(row, col, actor);
	}

	public Cell getCellAt(int row, int column) {
		return gridLayout.getCellAt(row, column);
	}

	/**
	 * Fills the layout with empty cells.
	 * 
	 * @param rows
	 * @param columns
	 */
	protected void reset(int rows, int columns) {
		gridLayout.setRows(rows);
		gridLayout.setColumns(columns);
		gridLayout.clear();
		clearDrag();
		addTarget(createTarget());
	}

	private Source newSource(Actor widget) {
		return new Source(widget) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				setCancelTouchFocus(false);
				cancel();

				Actor actor = getActor();
				Cell sourceCell = gridLayout.getCellFromActor(actor);
				Payload payload = new Payload();
				payload.setObject(sourceCell);
				payload.setDragActor(actor);

				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
				setCancelTouchFocus(true);
				if (target == null) {
					Cell sourceCell = (Cell) payload.getObject();
					sourceCell.setWidget(getActor());
				}
			}
		};
	}

	private Target createTarget() {
		return new Target(gridLayout) {

			@Override
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				Cell sourceCell = (Cell) payload.getObject();
				for (Cell newCell : gridLayout.getCells()) {
					Actor actor = newCell.getWidget();
					if (actor == null && newCell != sourceCell
							&& newCell.contains(x, y)) {
						Actor sourceActor = source.getActor();
						newCell.setWidget(sourceActor);
						fireDrop(sourceActor, sourceCell.getRow(),
								sourceCell.getColumn(), newCell.getRow(),
								newCell.getColumn());
						return;
					}
				}
				sourceCell.setWidget(source.getActor());
			}

			/**
			 * Fires that some actor has been dropped
			 */
			private void fireDrop(Actor actor, int oldRow, int oldColumn,
					int newRow, int newColumn) {
				DropGridEvent dropEvent = Pools.obtain(DropGridEvent.class);
				dropEvent.setActor(actor);
				dropEvent.setOldRow(oldRow);
				dropEvent.setNewRow(newRow);
				dropEvent.setOldColumn(oldColumn);
				dropEvent.setNewColumn(newColumn);
				fire(dropEvent);
				Pools.free(dropEvent);
			}
		};
	}

	/**
	 * Stores information of the old and new positions of a given actor in the
	 * grid.
	 */
	public static class DropGridEvent extends Event {

		private Actor actor;
		private int oldRow, oldColumn, newRow, newColumn;

		public Actor getActor() {
			return actor;
		}

		@Override
		public void reset() {
			super.reset();
			this.setActor(null);
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}

		public int getOldRow() {
			return oldRow;
		}

		public void setOldRow(int oldRow) {
			this.oldRow = oldRow;
		}

		public int getOldColumn() {
			return oldColumn;
		}

		public void setOldColumn(int oldColumn) {
			this.oldColumn = oldColumn;
		}

		public int getNewRow() {
			return newRow;
		}

		public void setNewRow(int newRow) {
			this.newRow = newRow;
		}

		public int getNewColumn() {
			return newColumn;
		}

		public void setNewColumn(int newColumn) {
			this.newColumn = newColumn;
		}
	}
}
