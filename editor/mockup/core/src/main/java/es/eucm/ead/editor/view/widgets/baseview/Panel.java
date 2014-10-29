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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import es.eucm.ead.editor.view.widgets.AbstractWidget;

/**
 * A vertical panel, appearing from the side in {@link BaseView}
 */
abstract class Panel extends AbstractWidget {

	public static final float FLING_TIME = 1.0f;

	protected boolean hidden;

	private float velocityY;

	private float flingTimer;

	public Panel() {
		setTouchable(Touchable.childrenOnly);
		addListener(new DragListener() {

			private boolean xLocked;

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				xLocked = Math.abs(getTouchDownX() - x) < Math
						.abs(getTouchDownY() - y);
			}

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				Panel.this.displace(event, xLocked ? 0 : getDeltaX(),
						xLocked ? getDeltaY() : 0);
			}

			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer) {
				Panel.this.dragStop();
			}

		});

		addListener(new ActorGestureListener() {
			@Override
			public void fling(InputEvent event, float vx, float vy, int button) {
				if (isVelocityToHide(vx, vy)) {
					hide();
				} else if (isVelocityToScroll(event, vy)) {
					flingTimer = FLING_TIME;
					velocityY = -vy;
				}
			}
		});
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (flingTimer > 0) {
			float alpha = flingTimer / FLING_TIME;
			displace(null, 0, velocityY * alpha * delta);

			flingTimer -= delta;
			if (flingTimer <= 0) {
				velocityY = 0;
			}
		}
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

	@Override
	public void layout() {
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
	public abstract void displace(InputEvent event, float deltaX, float deltaY);

	protected boolean isVelocityToScroll(InputEvent event, float vy) {
		return Math.abs(vy) > cmToPixels(BaseView.FLING_MIN_VELOCITY_CM);
	}

	public abstract boolean isVelocityToHide(float velocityX, float velocityY);

	public abstract boolean isHalfShown();
}
