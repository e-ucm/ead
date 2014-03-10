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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;

/**
 * A panel with a defined number of rows and cols.
 */
public class GridPanel<T extends Actor> extends Table {

	private Array<Array<Cell<T>>> cells;

	private int cols;

	private int lastRow, lastCol;

	/**
	 * Create a {@link GridPanel GridPanel} with the specified style. To add
	 * elements use {@link #addItem(Actor)} method.
	 */
	public GridPanel(int cols, float spacing) {
		super();
		super.top();
		if (cols < 1) {
			throw new IllegalArgumentException("cols or rows can't be zero.");
		}
		pad(spacing);
		this.lastCol = -1;
		this.lastRow = 0;
		this.cols = cols;
		this.cells = new Array<Array<Cell<T>>>(false, 8);
		this.defaults().space(spacing);
		addNewRowOfCells();
	}

	/**
	 * Adds a new row of cells.
	 */
	@SuppressWarnings("unchecked")
	private void addNewRowOfCells() {
		Array<Cell<T>> col = new Array<Cell<T>>(false, this.cols);
		this.cells.add(col);
		for (int j = 0; j < this.cols; ++j) {
			col.add(super.add());
		}
		super.row();
	}

	/**
	 * Add the actor in the next cell. If there is no next cell available, the
	 * panel automatically resizes itself to accept it.
	 * 
	 * @param actor
	 */
	public Cell<?> addItem(T t) {
		if (this.lastCol + 1 < this.cols) {
			this.lastCol++;
		} else {
			this.lastRow++;
			this.lastCol = 0;
			if (this.lastRow >= this.cells.size) {
				addNewRowOfCells();
			}
		}

		return this.cells.get(this.lastRow).get(this.lastCol).setWidget(t);
	}

	/**
	 * Clears this panel and restores it to the original state.
	 */
	public void clear() {
		super.clearChildren();
		this.cells.clear();
		this.lastRow = 0;
		this.lastCol = -1;
		addNewRowOfCells();
	}

	@Override
	public Cell<?> row() {
		throw new IllegalStateException(
				"Don't use this method, use GridPanel#addItem(Actor) instead!");
	}

	@Override
	public Cell<?> add() {
		throw new IllegalStateException(
				"Don't use this method, use GridPanel#addItem(Actor) instead!");
	}

	@Override
	public Cell<?> add(Actor actor) {
		throw new IllegalStateException(
				"Don't use this method, use GridPanel#addItem(Actor) instead!");
	}

	@Override
	public void add(Actor... actors) {
		throw new IllegalStateException(
				"Don't use this method, use GridPanel#addItem(Actor) instead!");
	}
}
