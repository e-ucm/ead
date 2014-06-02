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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Panel is a generic lightweight container with {@link #show()} and
 * {@link #hide()} methods. Sizes and positions children using table constraint.
 */
public class HiddenPanel extends Table {

	private static final String STAGE_BACKGROUND_DEFAULT_DRAWABLE = "dialogDimMediumAlpha";

	/**
	 * Change this value to 0 if you want no animation.
	 */
	protected float fadeDuration = .3f;

	private Vector2 temp;
	private boolean isModal;
	protected Drawable stageBackground;
	private boolean hideOnExternalTouch;

	public HiddenPanel(Skin skin) {
		super(skin);
		setBackground("blueBlackMedium");
		initialize(skin);
	}

	public HiddenPanel(Skin skin, String drawableBackground) {
		super(skin);
		setBackground(drawableBackground);
		initialize(skin);
	}

	protected void initialize(Skin skin) {
		this.stageBackground = skin
				.getDrawable(HiddenPanel.STAGE_BACKGROUND_DEFAULT_DRAWABLE);
		this.temp = new Vector2();
		this.hideOnExternalTouch = true;
		this.isModal = true;
		setTouchable(Touchable.enabled);

		addListener(new InputListener() {
			private final Rectangle rtmp = new Rectangle();

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				localToParentCoordinates(/* in/out */HiddenPanel.this.temp.set(
						x, y));
				this.rtmp.set(getX(), getY(), getWidth(), getHeight());
				if (HiddenPanel.this.hideOnExternalTouch
						&& !this.rtmp.contains(HiddenPanel.this.temp.x,
								HiddenPanel.this.temp.y)) {
					hide();
				}
				return HiddenPanel.this.isModal;
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				return HiddenPanel.this.isModal;
			}

			@Override
			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				return HiddenPanel.this.isModal;
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				return HiddenPanel.this.isModal;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				return HiddenPanel.this.isModal;
			}

			@Override
			public boolean keyTyped(InputEvent event, char character) {
				return HiddenPanel.this.isModal;
			}
		});
	}

	public void show() {
		if (!isVisible()) {
			setVisible(true);
			if (this.fadeDuration > 0) {
				getColor().a = 0f;
				addAction(Actions.fadeIn(this.fadeDuration, Interpolation.fade));
			}
		}
	}

	public void hide() {
		if (isVisible()) {
			if (this.fadeDuration > 0) {
				addAction(Actions.sequence(
						Actions.fadeOut(this.fadeDuration, Interpolation.fade),
						Actions.run(this.hideRunnable)));
			} else {
				hideWithoutAnimation();
			}
		}
	}

	protected void hideWithoutAnimation() {
		setVisible(false);
		onFadedOut();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x,
			float y) {
		if (this.stageBackground != null) {
			final Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			final Stage stage = getStage();
			this.stageBackground.draw(batch, 0, 0, stage.getWidth(),
					stage.getHeight());

		}
		super.drawBackground(batch, parentAlpha, x, y);
	}

	private final Vector2 xy = new Vector2();

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		final Actor hit = super.hit(x, y, touchable);
		if ((hit == null && this.isModal && (!touchable || getTouchable() == Touchable.enabled))) {
			localToParentCoordinates(this.xy.set(x, y));
			if (contains(getParent().getX(), getParent().getY(), getParent()
					.getWidth(), getParent().getHeight(), this.xy.x, this.xy.y)) {
				return this;
			}
		}
		return hit;
	}

	/**
	 * @param x
	 *            point x coordinate
	 * @param y
	 *            point y coordinate
	 * @return whether the point is contained in the rectangle
	 */
	private boolean contains(float rectangleX, float rectangleY,
			float rectangleWidth, float rectangleHeight, float x, float y) {
		return rectangleX <= x && rectangleX + rectangleWidth >= x
				&& rectangleY <= y && rectangleY + rectangleHeight >= y;
	}

	public void setModal(boolean isModal) {
		this.isModal = isModal;
	}

	public boolean isModal() {
		return this.isModal;
	}

	public void setHideOnExternalTouch(boolean hideOnExternalTouch) {
		this.hideOnExternalTouch = hideOnExternalTouch;
	}

	public void setStageBackground(Drawable stageBackground) {
		this.stageBackground = stageBackground;
	}

	/**
	 * Executed when the {@link #hide()} animation finished.
	 */
	protected void onFadedOut() {

	}

	protected final Runnable hideRunnable = new Runnable() {
		@Override
		public void run() {
			setVisible(false);
			onFadedOut();
		}
	};
}
