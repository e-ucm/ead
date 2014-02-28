package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;

/**
 * A panel with defined rows and cols.
 */
public class GridPanel<T extends Actor> extends Table {

	private Array<Array<Cell<T>>> cells;
	
	private int ROWS, COLS;
	
	private int lastRow, lastCol;

	/**
	 * Create a {@link GridPanel GridPanel} with the specified style.
	 */
	@SuppressWarnings("unchecked")
	public GridPanel(Skin skin, int rows, int cols,
			float spacing) {
		super(skin);
		super.top();
		if (cols < 1 || rows < 1){
			throw new IllegalArgumentException("cols and rows can't be zero.");
		}
		final float halfPad = spacing * .5f;
		pad(halfPad);
		this.lastCol = -1;
		this.lastRow = 0;
		this.ROWS = rows;
		this.COLS = cols;
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
		if(lastCol + 1 < COLS){
			lastCol++;
		} else {
			lastCol = 0;
			lastRow++;
		}

		return this.cells.get(lastRow).get(lastCol).setWidget(t);
	}

	@Override
	public Cell<?> row() {
		throw new IllegalStateException("Don't use this method!");
	}
}
