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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;

/**
 * A scrollable gallery of items
 */
public class Gallery extends ScrollPane {

	private Grid container;

	/**
	 * @param rowHeight
	 *            row height (in pixels)
	 * @param columns
	 *            number of columns for the gallery
	 */
	public Gallery(float rowHeight, int columns) {
		super(null);
		setWidget(container = new Grid(rowHeight, columns));
		setScrollingDisabled(true, false);
	}

	/**
	 * @param pad
	 *            pad between items
	 */
	public void pad(float pad) {
		container.pad(pad);
	}

	public void setBackground(Drawable background) {
		getStyle().background = background;
	}

	public Cell add(Actor actor) {
		Cell cell = new Cell(actor);
		container.addActor(cell);
		return cell;
	}

	@Override
	public void clearChildren() {
		container.clearChildren();
	}

	static class Grid extends AbstractWidget {

		private int columns;

		private float rowHeight;

		private float pad = WidgetBuilder.dpToPixels(8);

		Grid(float rowHeight, int columns) {
			this.columns = columns;
			this.rowHeight = rowHeight;
		}

		@Override
		public void layout() {
			float columnWidth = (getWidth() - pad) / columns;
			float y = Math.max(getPrefHeight(), getHeight() - rowHeight) - pad;
			int count = 0;
			float rowHeight = 0;
			for (Actor actor : getChildren()) {
				if (count % columns == 0) {
					rowHeight = rowHeight(count);
					y -= rowHeight;
				}
				setBounds(actor, pad + (count % columns) * columnWidth,
						y + pad, columnWidth - pad, rowHeight - pad);
				count++;
			}
		}

		private float rowHeight(int fromIndex) {
			float currentRowHeight = 0;
			for (int i = fromIndex; i < fromIndex + columns
					&& i < getChildren().size; i++) {
				Cell cell = (Cell) getChildren().get(i);
				float actorHeight;
				if (cell.usePrefHeight) {
					actorHeight = getPrefHeight(cell.actor);
				} else {
					actorHeight = rowHeight;
				}
				currentRowHeight = Math.max(currentRowHeight, actorHeight);
			}
			return currentRowHeight;
		}

		@Override
		public float getPrefWidth() {
			return getParent().getWidth();
		}

		@Override
		public float getPrefHeight() {
			float height = 0;
			int counter = 0;
			float currentRowHeight = 0;
			for (Actor cell : getChildren()) {
				if (counter % columns == 0) {
					height += currentRowHeight;
					currentRowHeight = 0;
				}

				float actorHeight;
				if (((Cell) cell).usePrefHeight) {
					actorHeight = getPrefHeight(((Cell) cell).actor);
				} else {
					actorHeight = rowHeight;
				}
				currentRowHeight = Math.max(currentRowHeight, actorHeight);
				counter++;
			}
			height += currentRowHeight;
			return Math.max(getParent().getHeight(), height + pad);
		}

		public void pad(float pad) {
			this.pad = pad;
		}
	}

	public static class Cell extends AbstractWidget {

		private static final float DELTA_Y = cmToYPixels(0.5f);

		private Actor actor;

		private boolean usePrefHeight;

		Cell(Actor actor) {
			this.actor = actor;
			addActor(actor);
		}

		public void usePrefHeight() {
			this.usePrefHeight = true;
		}

		@Override
		public void layout() {
			setBounds(actor, 0, -DELTA_Y, getWidth(), getHeight());
			actor.getColor().a = 0.0f;
			actor.addAction(Actions.parallel(
					Actions.moveTo(0, 0, 0.2f, Interpolation.exp5Out),
					Actions.alpha(1.0f, 0.5f, Interpolation.exp10Out)));
		}
	}
}
