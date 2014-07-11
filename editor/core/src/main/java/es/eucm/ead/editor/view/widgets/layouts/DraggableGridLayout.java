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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import es.eucm.ead.editor.view.widgets.DraggableScrollPane;
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

	public GridLayout getGridLayout() {
		return gridLayout;
	}

	/**
	 * Adds an actor to the grid. The actor will be draggable between the other
	 * empty cells of the layout. May be null in order to add an empty cell.
	 * 
	 * @param actor
	 *            may be null.
	 * @return the Container cell in order to adjust constraints such as fill,
	 *         align, etc.
	 */
	public Container add(Actor actor) {
		if (actor != null) {
			addSource(newSource(actor));
		}
		return gridLayout.add(actor);
	}

	@Override
	public void clearChildren() {
		clearDrag();
		super.clearChildren();
	}

	private Source newSource(Actor widget) {
		return new Source(widget) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				setCancelTouchFocus(false);
				cancel();

				Actor actor = getActor();
				Cell sourceTile = gridLayout.getCellFromActor(actor);
				sourceTile.setWidget(null);
				Payload payload = new Payload();
				payload.setObject(sourceTile);
				payload.setDragActor(actor);

				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
				setCancelTouchFocus(true);
				if (target == null) {
					Cell sourceTile = (Cell) payload.getObject();
					sourceTile.setWidget(getActor());
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
				for (Cell tile : gridLayout.getCells()) {
					Actor actor = tile.getWidget();
					if (actor == null && tile.contains(x, y)) {
						tile.setWidget(source.getActor());
						return;
					}
				}
				Cell sourceTile = (Cell) payload.getObject();
				sourceTile.setWidget(source.getActor());
			}
		};
	}
}
