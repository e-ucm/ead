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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class GroupEditor extends AbstractWidget {

	private Drawable background;

	private Group rootGroup;

	private Group selectionLayer;

	public GroupEditor() {
		addListener(new GroupEditorListener());
		selectionLayer = new Group();
		addActor(selectionLayer);

		addListener(new GroupEditorListener());
		// Drags moving objects
		addListener(new DragListener() {
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				for (Actor actor : selectionLayer.getChildren()) {
					if (actor instanceof SelectionBox) {
						SelectionBox selectionBox = (SelectionBox) actor;
						if (selectionBox.isMoving()) {
							selectionBox.setPosition(selectionBox.getX()
									- getDeltaX(), selectionBox.getY()
									- getDeltaY());
						}
					}
				}
			}
		});
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the group being edited
	 */
	public void setRootGroup(Group rootGroup) {
		if (this.rootGroup != null) {
			this.rootGroup.remove();
		}
		this.rootGroup = rootGroup;
		addActorAt(0, rootGroup);
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (background != null) {
			background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	/**
	 * Clears the current selection
	 */
	public void clearSelection() {
		for (Actor selectionBox : selectionLayer.getChildren()) {
			Pools.free(selectionBox);
		}
		selectionLayer.clearChildren();
	}

	private Actor getDirectChild(Group parent, Actor child) {
		if (!child.isDescendantOf(parent)) {
			return null;
		}

		Actor firstChild = child;
		while (firstChild.getParent() != parent) {
			firstChild = firstChild.getParent();
		}
		return firstChild;
	}

	public class GroupEditorListener extends ActorGestureListener {

		private TouchDownTask task = new TouchDownTask();

		private Vector2 tmp = new Vector2();

		private boolean pinching = false;

		@Override
		public void touchDown(InputEvent event, float x, float y, int pointer,
				int button) {
			if (!event.isHandled() && pointer == 0) {
				Actor target = getDirectChild(selectionLayer, event.getTarget());
				if (target instanceof SelectionBox) {
					((SelectionBox) target).moving();
				} else {
					task.cancel();
					task.eventTarget = event.getTarget();
					if (selectionLayer.getChildren().size == 0) {
						task.run();
					} else {
						Timer.schedule(task, 0.2f);
					}
				}
			}
		}

		@Override
		public void pinch(InputEvent event, Vector2 initialPointer1,
				Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			task.cancel();
			float angle = tmp.set(pointer1.x - pointer2.x,
					pointer1.y - pointer2.y).angle();
			for (Actor selectionBox : selectionLayer.getChildren()) {
				if (selectionBox instanceof SelectionBox
						&& ((SelectionBox) selectionBox).isSelected()) {
					if (!pinching) {
						((SelectionBox) selectionBox)
								.setInitialPinchRotation(angle);
					} else {
						((SelectionBox) selectionBox).updateRotation(angle);
					}
				}
			}
			pinching = true;
		}

		@Override
		public boolean longPress(Actor actor, float x, float y) {
			Actor target = selectionLayer.hit(x, y, true);
			if (target instanceof SelectionBox) {
				((SelectionBox) target).moving();
				return true;
			}
			return false;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			if (!event.isHandled() && pointer == 0) {
				if (pinching) {
					pinching = false;
					return;
				}
				if (task.isScheduled()) {
					task.run();
					task.cancel();
				}
				Actor target = selectionLayer.hit(x, y, true);
				if (target instanceof SelectionBox) {
					((SelectionBox) target).selected();
				} else {
					clearSelection();
				}
			}
		}
	}

	public class TouchDownTask extends Task {

		private Actor eventTarget;

		@Override
		public void run() {
			Actor target = getDirectChild(rootGroup, eventTarget);
			if (target != null) {
				clearSelection();
				SelectionBox selectionBox = Pools.obtain(SelectionBox.class);
				selectionBox.setTarget(target, background);
				selectionLayer.addActor(selectionBox);
			}
		}
	}

	/**
	 * Actor to represent the selection box over the selected objects
	 */
	public static class SelectionBox extends Group {

		private static final Color pressedColor = Color.GRAY;

		private static final Color selectedColor = Color.CYAN;

		private static final Color movingColor = Color.BLUE;

		private static final int PRESSED = 0, SELECTED = 1, MOVING = 2;

		private Actor target;

		private int state;

		private Drawable selectionBackground;

		private float initialPinchRotation;

		private float initialRotation;

		public SelectionBox() {

		}

		public void setTarget(Actor target, Drawable selectionBackground) {
			this.target = target;
			this.selectionBackground = selectionBackground;
			this.state = PRESSED;

			setBounds(target.getX(), target.getY(), target.getWidth(),
					target.getHeight());
			setScale(target.getScaleX(), target.getScaleY());
			setRotation(target.getRotation());
			setOrigin(target.getOriginX(), target.getOriginY());
		}

		public void setInitialPinchRotation(float initialPinchRotation) {
			this.initialPinchRotation = initialPinchRotation;
			this.initialRotation = getRotation();
		}

		public void selected() {
			this.state = SELECTED;
		}

		public void moving() {
			Gdx.input.vibrate(100);
			this.state = MOVING;
		}

		public boolean isSelected() {
			return state == SELECTED;
		}

		public boolean isMoving() {
			return state == MOVING;
		}

		public void updateRotation(float degrees) {
			super.setRotation(initialRotation + degrees - initialPinchRotation);
			target.setRotation(getRotation());
		}

		@Override
		public void setPosition(float x, float y) {
			super.setPosition(x, y);
			target.setPosition(x, y);
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			switch (state) {
			case PRESSED:
				batch.setColor(pressedColor);
				break;
			case SELECTED:
				batch.setColor(selectedColor);
				break;
			case MOVING:
				batch.setColor(movingColor);
				break;
			}
			selectionBackground.draw(batch, 0, 0, getWidth(), getHeight());
			batch.setColor(Color.WHITE);
			super.drawChildren(batch, parentAlpha);
		}

	}
}
