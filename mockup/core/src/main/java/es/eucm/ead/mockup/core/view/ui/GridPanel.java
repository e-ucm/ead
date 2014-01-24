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
package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.Cell;

/**
 * A panel with defined rows and cols.
 */
public class GridPanel<T extends Actor> extends Table {

	private Array<Array<Cell<T>>> cells;

	/**
	 * Create a {@link GridPanel GridPanel} with default style.
	 * 
	 * @param skin
	 *            the skin to use
	 */
	public GridPanel(Skin skin, int rows, int cols, float spacing) {
		this(skin, "default", rows, cols, spacing);
	}

	/**
	 * Create a {@link GridPanel GridPanel} with the specified style.
	 */
	@SuppressWarnings("unchecked")
	public GridPanel(Skin skin, String styleName, int rows, int cols,
			float spacing) {
		super(skin);
		super.top();
		final float halfPad = spacing * .5f;
		pad(halfPad);
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
		setStyle(skin.get(styleName, GridPanelStyle.class));
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
	 * Apply the style of this {@link GridPanel gridpanel}.
	 * 
	 * @param style
	 *            the style to apply
	 */
	public void setStyle(GridPanelStyle style) {
		// this.style = style;

		if (style.background != null)
			this.setBackground(style.background);

	}

	@Override
	public Cell<?> row() {
		throw new IllegalStateException("Don't use this method!");
	}

	/**
	 * Define the style of a {@link GridPanel gridpanel}.
	 */
	public static class GridPanelStyle {

		/** Optional */
		public Drawable background;

		public GridPanelStyle() {

		}

		public GridPanelStyle(Drawable background) {
			this.background = background;
		}

		public GridPanelStyle(GridPanelStyle style) {
			this.background = style.background;
		}
	}
}
