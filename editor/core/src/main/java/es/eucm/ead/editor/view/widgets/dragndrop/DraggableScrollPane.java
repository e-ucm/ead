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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

/**
 * A {@link ScrollPane} that has drag'n drop functionality. Has a
 * {@link DragAndDrop} object where you can add {@link Target targets} and
 * {@link Source sources}. When an item is being dragged and the mouse position
 * is near the edges the pane will automatically scroll in that direction.
 */
public class DraggableScrollPane extends ScrollPane {

	private static final Vector2 TMP = new Vector2();

	private static final float DEFAULT_ACTION_ZONE = 75F;
	private static final float DEAFULT_SPEED = 15F;

	/**
	 * The width/height of the left/up and right/down zone on which the scroll
	 * is automatically increased/decreased.
	 */
	private float actionZone;

	/**
	 * The factor that proportionally increases the scroll speed while in the
	 * {@link #actionZone}.
	 */
	private float scrollSpeed;

	private DragAndDrop drag;

	public DraggableScrollPane(Actor widget) {
		this(widget, DEFAULT_ACTION_ZONE, DEAFULT_SPEED);
	}

	public DraggableScrollPane(Actor widget, DragAndDrop dragAndDrop) {
		this(widget, dragAndDrop, DEFAULT_ACTION_ZONE, DEAFULT_SPEED);
	}

	public DraggableScrollPane(Actor widget, float zone, float speed) {
		this(widget, new DragAndDrop(), DEFAULT_ACTION_ZONE, DEAFULT_SPEED);
	}

	public DraggableScrollPane(Actor widget, DragAndDrop dragAndDrop,
			float zone, float speed) {
		super(widget);
		actionZone = zone;
		drag = dragAndDrop;
		scrollSpeed = speed;
		setFlingTime(0.0f);
		setFadeScrollBars(false);
		setOverscroll(false, false);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// Detect if we're dragging via Drag'n Drop and if we're inside the
		// ACTION_ZONE so we start scrolling
		if (drag.isDragging()) {

			getStage().getRoot().stageToLocalCoordinates(
					TMP.set(Gdx.input.getX(), Gdx.graphics.getHeight()
							- Gdx.input.getY()));

			float x = getX();
			if (TMP.x < x + actionZone) {
				float deltaX = (1f - (TMP.x - x) / (actionZone));
				setScrollX(getScrollX() - deltaX * scrollSpeed);

			} else if (TMP.x > x + getWidth() - actionZone) {
				float deltaX = (1f - ((x + getWidth()) - TMP.x) / actionZone);
				setScrollX(getScrollX() + deltaX * scrollSpeed);

			}

			float y = getY();
			if (TMP.y < y + actionZone) {
				float deltaY = (1f - (TMP.y - y) / actionZone);
				setScrollY(getScrollY() + deltaY * scrollSpeed);

			} else if (TMP.y > y + getHeight() - actionZone) {
				float deltaY = (1f - ((y + getHeight()) - TMP.y) / actionZone);
				setScrollY(getScrollY() - deltaY * scrollSpeed);

			}
		}
	}

	public void addSource(final Source source) {
		drag.addSource(new Source(source.getActor()) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				drag.setDragActorPosition(-x, getActor().getHeight() - y);
				return source.dragStart(event, x, y, pointer);
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
				source.dragStop(event, x, y, pointer, payload, target);
			}

		});
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

	@Override
	public void clearChildren() {
		clearDrag();
		super.clearChildren();
	}

	public float getActionZone() {
		return actionZone;
	}

	public void setActionZone(float actionZone) {
		this.actionZone = actionZone;
	}

	public float getScrollSpeed() {
		return scrollSpeed;
	}

	public void setScrollSpeed(float scrollSpeed) {
		this.scrollSpeed = scrollSpeed;
	}

}
