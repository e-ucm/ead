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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

public class DraggableScrollPane extends ScrollPane {

	private static final Vector2 TMP = new Vector2();

	/**
	 * The width/height of the left/up and right/down zone on which the scroll
	 * is automatically increased/decreased.
	 */
	private static final float ACTION_ZONE = 90F;

	/**
	 * The factor that proportionally increases the scroll speed while in the
	 * {@link #ACTION_ZONE}.
	 */
	private static final float SCROLL_SPEED_MULTIPLIER = 10F;

	private DragAndDrop drag;

	public DraggableScrollPane(Actor widget) {
		super(widget);
		drag = new DragAndDrop();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// Detect if we're dragging via Drag'n Drop and if we're inside the
		// ACTION_ZONE so we start scrolling
		if (drag.isDragging()) {

			getStage().getRoot().stageToLocalCoordinates(
					TMP.set(Gdx.input.getX(), Gdx.input.getY()));

			if (TMP.x < ACTION_ZONE) {
				float deltaX = (1f - TMP.x / ACTION_ZONE);
				setScrollX(getScrollX() - deltaX * SCROLL_SPEED_MULTIPLIER);

			} else if (TMP.x > getWidth() - ACTION_ZONE) {
				float deltaX = (1f - (getWidth() - TMP.x) / ACTION_ZONE);
				setScrollX(getScrollX() + deltaX * SCROLL_SPEED_MULTIPLIER);

			}

			if (TMP.y < ACTION_ZONE) {
				float deltaY = (1f - TMP.y / ACTION_ZONE);
				setScrollY(getScrollY() - deltaY * SCROLL_SPEED_MULTIPLIER);

			} else if (TMP.y > getHeight() - ACTION_ZONE) {
				float deltaY = (1f - (getHeight() - TMP.y) / ACTION_ZONE);
				setScrollY(getScrollY() + deltaY * SCROLL_SPEED_MULTIPLIER);

			}
		}
	}

	public void addSource(Source source) {
		drag.addSource(source);
	}

	public void addTarget(Target target) {
		drag.addTarget(target);
	}

	public void removeSource(Source source) {
		drag.removeSource(source);
	}

	public void removeTarget(Target target) {
		drag.removeTarget(target);
	}

	protected void clearDrag() {
		drag.clear();
	}

}
