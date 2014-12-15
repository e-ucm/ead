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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import es.eucm.ead.editor.view.widgets.baseview.BaseView.BaseViewStyle;

/**
 * Navigation panel, with a background that slowly appears/dissapear when the
 * panel shows/hide
 */
class Navigation extends Panel {

	private BaseViewStyle style;

	private Container background;

	private Actor navigation;

	public Navigation(BaseViewStyle s) {
		this.style = s;

		addActor(background = new Container());
		background.setBackground(style.navigationBackground);
		background.setVisible(false);
		background.getColor().a = 0.0f;

		addListener(new DragListener() {

			private boolean clickCancelled;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickCancelled = event.getTarget() != background;
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void dragStart(InputEvent event, float x, float y,
					int pointer) {
				clickCancelled = true;
			}

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				event.stop();
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!clickCancelled) {
					hide();
				}
				super.touchUp(event, x, y, pointer, button);
			}
		});

	}

	public void setNavigation(Actor navigation) {
		if (this.navigation != null) {
			this.navigation.remove();
		}
		this.navigation = navigation;
		if (navigation != null) {
			addActor(navigation);
		}
	}

	public void toggle() {
		if (navigation != null) {
			if (isHidden()) {
				show();
			} else {
				hide();
			}
		}
	}

	@Override
	public void show() {
		super.show();
		setVisible(true);
		background.clearActions();
		background.addAction(Actions.sequence(Actions
				.touchable(Touchable.enabled), Actions.show(), Actions.alpha(
				style.navigationBackgroundAlpha, BaseView.HIDE_TIME,
				Interpolation.exp5Out)));
		BaseView.moveTo(navigation, 0, navigation.getY());
	}

	@Override
	public void hide() {
		super.hide();
		background.clearActions();
		background.addAction(Actions.sequence(
				Actions.touchable(Touchable.disabled),
				Actions.alpha(0.0f, BaseView.HIDE_TIME, Interpolation.exp5Out),
				Actions.hide()));
		BaseView.moveTo(navigation, -getPrefWidth(navigation),
				navigation.getY());
	}

	public void hideRightAway() {
		background.setVisible(false);
		setVisible(false);
		background.getColor().a = 0.0f;
		float height = Math.max(getPrefHeight(navigation), getHeight());
		setBounds(navigation, -getPrefWidth(navigation), getHeight() - height,
				getPrefWidth(navigation), height);
		hidden = true;
	}

	@Override
	public void displace(float deltaX, float deltaY) {
		if (!isVisible()) {
			setVisible(true);
		}
		navigation.setX(Math.max(Math.min(0, navigation.getX() + deltaX),
				-navigation.getWidth()));

		background.setVisible(true);
		background.getColor().a = Math.max(
				0,
				(navigation.getWidth() + navigation.getX())
						/ navigation.getWidth())
				* style.navigationBackgroundAlpha;

	}

	@Override
	public void layout() {
		super.layout();
		if (navigation != null) {
			float height = Math.max(getPrefHeight(navigation), getHeight());
			float x = MathUtils.isEqual(navigation.getWidth(),
					getPrefWidth(navigation), 1.1f) ? navigation.getX()
					: -getPrefWidth(navigation);
			setBounds(navigation, x, getHeight() - height,
					getPrefWidth(navigation), height);
		}
		setBounds(background, 0, 0, getWidth(), getHeight());
	}

	@Override
	public boolean isVelocityToHide(float velocityX, float velocityY) {
		return velocityX < -cmToXPixels(BaseView.FLING_MIN_VELOCITY_CM);
	}

	@Override
	public boolean isHalfShown() {
		return navigation.getX() > -navigation.getWidth() / 2.0f;
	}

	@Override
	public boolean hasContent() {
		return navigation != null;
	}
}
