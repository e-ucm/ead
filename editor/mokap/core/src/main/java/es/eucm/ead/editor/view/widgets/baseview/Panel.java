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
package es.eucm.ead.editor.view.widgets.baseview;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import es.eucm.ead.editor.view.listeners.GestureListener;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.ScrollPane;

/**
 * A vertical panel, appearing from the side in {@link BaseView}
 */
abstract class Panel extends AbstractWidget {

	protected boolean hidden = true;

	public Panel() {
		setTouchable(Touchable.childrenOnly);
		addListener(new GestureListener() {

			private boolean dragStarted;

			private boolean xLocked;

			private float touchDownX;

			private float touchDownY;

			public void dragStart(float x, float y) {
				xLocked = Math.abs(touchDownX - x) < Math.abs(touchDownY - y);
				if (xLocked) {
					getStage().cancelTouchFocus(Panel.this);
					ScrollPane scrollPane = findScrollPane(Panel.this);
					if (scrollPane != null) {
						getStage().cancelTouchFocusExcept(
								scrollPane.getListeners().first(), scrollPane);
					}
				} else {
					getStage().cancelTouchFocusExcept(this, Panel.this);
				}
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (pointer == 0) {
					dragStarted = false;
					touchDownX = x;
					touchDownY = y;
				}
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void pan(float x, float y, float deltaX, float deltaY) {
				if (!dragStarted) {
					dragStart(x, y);
					dragStarted = true;
				}
				Panel.this.displace(xLocked ? 0 : deltaX, xLocked ? deltaY : 0);
			}

			@Override
			public void panStop(float x, float y, int pointer, int button) {
				if (dragStarted && pointer == 0) {
					Panel.this.dragStop();
				}
			}

			@Override
			public void fling(float vx, float vy, int button) {
				if (isVelocityToHide(vx, vy)) {
					hide();
				}
			}
		});
	}

	private ScrollPane findScrollPane(Group root) {
		for (int i = root.getChildren().size - 1; i >= 0; i--) {
			Actor actor = root.getChildren().get(i);
			if (actor instanceof Group) {
				ScrollPane scrollPane = findScrollPane((Group) actor);
				if (scrollPane != null) {
					return scrollPane;
				}
			}
		}
		if (root instanceof ScrollPane) {
			return (ScrollPane) root;
		}
		return null;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void show() {
		hidden = false;
	}

	public void hide() {
		hidden = true;
	}

	void dragStop() {
		if (isHalfShown()) {
			show();
		} else {
			hide();
		}
	}

	/**
	 * The panel has been dragged the given deltaX and deltaY
	 */
	public abstract void displace(float deltaX, float deltaY);

	public abstract boolean isVelocityToHide(float velocityX, float velocityY);

	public abstract boolean isHalfShown();

	public abstract boolean hasContent();
}
