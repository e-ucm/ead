/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
	 * Create a {@link GridPanel GridPanel} with the specified style.
	 */
	@SuppressWarnings("unchecked")
	public GridPanel(Skin skin, int rows, int cols, float spacing) {
		super(skin);
		super.top();
		if (cols < 1 || rows < 1) {
			throw new IllegalArgumentException("cols or rows can't be zero.");
		}
		final float halfPad = spacing * .5f;
		pad(halfPad);
		this.lastCol = -1;
		this.lastRow = 0;
		this.cols = cols;
		this.cells = new Array<Array<Cell<T>>>(false, rows);
		this.defaults().expand().space(halfPad).uniform();
		for (int i = 0; i < rows; ++i) {
			this.cells.add(new Array<Cell<T>>(false, cols));
			Array<Cell<T>> col = this.cells.get(i);
			for (int j = 0; j < cols; ++j) {
				col.add(super.add());
			}
			super.row();
		}
	}

	/**
	 * Default method that works as expected.
	 * 
	 * @param actor
	 * @param row
	 * @param col
	 */
	public Cell<?> addItem(T t, int row, int col) {
		return this.cells.get(row).get(col).setWidget(t);
	}

	/**
	 * Add the actor in the next cell
	 * 
	 * @param actor
	 */
	public Cell<?> addItem(T t) {
		if (lastCol + 1 < cols) {
			lastCol++;
		} else {
			lastCol = 0;
			lastRow++;
		}

		return this.cells.get(lastRow).get(lastCol).setWidget(t);
	}

	/**
	 * Clears this panel and restores it to the original state.
	 */
	public void clear() {
		clear();
		this.cells.clear();
		this.lastRow = 0;
		this.lastCol = -1;
	}

	@Override
	public Cell<?> row() {
		throw new IllegalStateException(
				"Don't use this method, use addItem() instead!");
	}
}
