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
package es.eucm.ead.editor.view.widgets.layouts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableGridLayout;

/**
 * A layout that displays it's children in a grid. Has an {@link Array} of
 * {@link Cell} cell that positions their actors. It's used by the
 * {@link DraggableGridLayout} in order to be able to drag and drop and position
 * it's children. This layout can increase it's number of rows or columns at any
 * moment.
 * 
 * The cells are positioned by the following order: top-left first
 * 
 * <pre>
 *     _____ _____ _____
 *    |     |     |     |
 *    |  0  |  1  |  2  |
 *    |_____|_____|_____|
 *    |     |     |     |
 *    |  3  |  4  |  5  |
 *    |_____|_____|_____|
 *    |     |     |     |
 *    |  6  |  7  |  8  |
 *    |_____|_____|_____|
 * 
 * </pre>
 */
public class GridLayout extends AbstractWidget {

	private static final int INITIAL_ROWS = 1;
	private static final int INITIAL_COLUMNS = 1;

	private int rows;
	private Skin skin;
	private int columns;
	private Array<Cell> cells;

	/**
	 * Creates a layout with {@value #INITIAL_ROWS} rows and
	 * {@value #INITIAL_COLUMNS} columns.
	 */
	public GridLayout() {
		this(INITIAL_ROWS, INITIAL_COLUMNS, null);
	}

	public GridLayout(int initialRows, int initialColumns, Skin skin) {
		rows = initialRows;
		columns = initialColumns;
		cells = new Array<Cell>(rows * columns);
		this.skin = skin;
		clearCells();
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * Adds an actor in the first cell found empty. If there are no empty cells
	 * left a new row will be added at the end of the rows.
	 */
	public Cell add(Actor actor) {
		return add(actor, true);
	}

	/**
	 * Adds an actor in the first cell found empty.
	 * 
	 * @param actor
	 * @param increaseRow
	 *            if true and there are no empty tiles left a new row will be
	 *            added at the end of the rows, else a new column will be added
	 *            at the end of the columns
	 */
	public Cell add(Actor actor, boolean increaseRow) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				int index = i * columns + j;
				Cell cell = cells.get(index);
				if (cell.getActor() == null) {
					cell.setActor(actor);
					return cell;
				}
			}
		}
		if (increaseRow) {
			addRowAtTheEnd();
		} else {
			addColumnAtTheEnd();
		}
		return add(actor, increaseRow);
	}

	public Cell addAt(int row, int column, Actor actor) {
		int index = row * columns + column;
		Cell cell = cells.get(index);
		cell.setActor(actor);
		return cell;
	}

	public Cell getCellAt(int row, int column) {
		if (row >= rows || column >= columns) {
			return null;
		}
		return cells.get(row * columns + column);
	}

	public Cell getCellAt(float x, float y) {
		for (Cell cell : cells) {
			if (cell.contains(x, y)) {
				return cell;
			}
		}
		return null;
	}

	@Override
	public float getPrefWidth() {
		return getCellPrefWidth() * columns;
	}

	private float getCellPrefWidth() {
		float tilePrefWidth = 0f;
		for (Cell cell : cells) {
			tilePrefWidth = Math.max(tilePrefWidth, cell.getPrefWidth());
		}
		return tilePrefWidth;
	}

	@Override
	public float getPrefHeight() {
		return getCellPrefHeight() * rows;
	}

	private float getCellPrefHeight() {
		float tilePrefHeight = 0f;
		for (Cell cell : cells) {
			tilePrefHeight = Math.max(tilePrefHeight, cell.getPrefHeight());
		}
		return tilePrefHeight;
	}

	@Override
	public void layout() {

		float prefTileWidth = getCellPrefWidth();
		float prefTileHeight = getCellPrefHeight();

		for (Cell cell : cells) {
			float x = cell.column * prefTileWidth;
			float y = getHeight() - (cell.row + 1) * prefTileHeight;
			float width = prefTileWidth;
			float height = prefTileHeight;
			setBounds(cell, x, y, width, height);
			cell.validate();
		}
	}

	public void addRowAtTheBegining() {
		addRowAt(0);
	}

	public void addRowAtTheEnd() {
		addRowAt(rows);
	}

	/**
	 * Adds a new empty row at a given index.
	 * 
	 * @param row
	 */
	public void addRowAt(int row) {
		++rows;
		int beginIndex = row * columns;
		for (int j = 0; j < columns; ++j) {
			int index = beginIndex + j;
			Cell newCell = new Cell(null, row, j, skin);
			cells.insert(index, newCell);
			addActorAt(index, newCell);
		}
		for (int i = row + 1; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				Cell cell = cells.get(i * columns + j);
				cell.row = i;
				cell.column = j;
			}
		}
		invalidateHierarchy();
	}

	public void addColumnAtTheBegining() {
		addColumnAt(0);
	}

	public void addColumnAtTheEnd() {
		addColumnAt(columns);
	}

	/**
	 * Adds a new empty column at a given index.
	 * 
	 * @param column
	 */
	public void addColumnAt(int column) {
		++columns;
		for (int i = 0; i < rows; ++i) {
			int index = i * columns + column;
			Cell newCell = new Cell(null, i, column, skin);
			cells.insert(i * columns + column, newCell);
			addActorAt(index, newCell);
		}
		for (int i = 0; i < rows; ++i) {
			for (int j = column + 1; j < columns; ++j) {
				Cell cell = cells.get(i * columns + j);
				cell.row = i;
				cell.column = j;
			}
		}
		invalidateHierarchy();
	}

	@Override
	public void clearChildren() {
		super.clearChildren();
		clearCells();
	}

	private void clearCells() {
		cells.clear();
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				Cell cell = new Cell(null, i, j, skin);
				cells.add(cell);
				super.addActor(cell);
			}
		}
	}

	public Cell getCellFromActor(Actor act) {
		for (Cell cell : cells) {
			if (cell.getActor() == act) {
				return cell;
			}
		}
		return null;
	}

	public Array<Cell> getCells() {
		return cells;
	}

	/**
	 * Has a position and hold information about it's row and column. It may
	 * contain an actor. If the actor it's null the cell is considered empty by
	 * the layout.
	 */
	public static class Cell extends Container {

		private CellStyle style;
		private int column, row;

		public Cell() {
			this(null, 0, 0, null);
		}

		public Cell(Actor a, int row, int col, Skin skin) {
			setActor(a);
			this.column = col;
			this.row = row;
			if (skin != null) {
				style = skin.get(CellStyle.class);
				setColor(style.color);
			}
		}

		/**
		 * @param x
		 *            point x coordinate
		 * @param y
		 *            point y coordinate
		 * @return whether the point is contained in the rectangle
		 */
		public boolean contains(float x, float y) {
			return this.getX() <= x && this.getX() + this.getWidth() >= x
					&& this.getY() <= y && this.getY() + this.getHeight() >= y;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			validate();
			drawBackground(batch, parentAlpha, 0, 0);
			if (getActor() != null) {
				drawChildren(batch, parentAlpha);
			}
		}

		@Override
		protected void drawBackground(Batch batch, float parentAlpha, float x,
				float y) {
			if (style != null) {
				x = getX();
				y = getY();
				float width = getWidth();
				float height = getHeight();
				batch.setColor(getColor());
				style.background.draw(batch, x, y, width, height);
				style.border.draw(batch, x, y, width, height);
			}
		}

		@Override
		public String toString() {
			return "[ " + row + ", " + column + " ~> " + super.toString() + "]";
		}

		@Override
		public void setActor(Actor widget) {
			setPrefSize(widget);
			super.setActor(widget);
		}

		private void setPrefSize(Actor actor) {
			if (actor instanceof Layout) {
				Layout layout = ((Layout) actor);
				prefWidth(layout.getPrefWidth()).prefHeight(
						layout.getPrefHeight());
			} else if (actor != null) {
				prefWidth(actor.getWidth()).prefHeight(actor.getHeight());
			} else if (getActor() == null) {
				prefWidth(0f).prefHeight(0f);
			} else {
				setPrefSize(getActor());
			}
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}

		public static class CellStyle {

			private Drawable background;
			private Drawable border;
			private Color color;

			public CellStyle() {

			}

			public CellStyle(Drawable background, Drawable border, Color color) {
				this.background = background;
				this.border = border;
				this.color = color;
			}
		}
	}
}
