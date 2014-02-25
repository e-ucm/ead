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
 * Panel is a generic lightweight container with methods show and hide. Sizes
 * and positions children using table constraint.
 */
public class HiddenPanel extends Table {

	private static final String STAGE_BACKGROUND_DEFAULT_DRAWABLE = "dialogDimMediumAlpha";

	/**
	 * Change this value to 0 if you want no animation.
	 */
	protected float FADE_DURATION = .4f;

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

	private void initialize(Skin skin) {
		this.stageBackground = skin
				.getDrawable(STAGE_BACKGROUND_DEFAULT_DRAWABLE);
		this.temp = new Vector2();
		hideOnExternalTouch = true;
		isModal = true;
		setTouchable(Touchable.enabled);

		addListener(new InputListener() {
			Rectangle rtmp = new Rectangle();

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				localToStageCoordinates(/* in/out */temp.set(x, y));
				rtmp.set(getX(), getY(), getWidth(), getHeight());
				if (hideOnExternalTouch && !rtmp.contains(temp.x, temp.y)) {
					hide();
				}
				return isModal;
			}

			public boolean mouseMoved(InputEvent event, float x, float y) {
				return isModal;
			}

			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				return isModal;
			}

			public boolean keyDown(InputEvent event, int keycode) {
				return isModal;
			}

			public boolean keyUp(InputEvent event, int keycode) {
				return isModal;
			}

			public boolean keyTyped(InputEvent event, char character) {
				return isModal;
			}
		});
	}

	public void show() {
		setVisible(true);
		if (FADE_DURATION > 0) {
			getColor().a = 0f;
			addAction(Actions.fadeIn(FADE_DURATION, Interpolation.fade));
		}
	}

	public void hide() {
		if (FADE_DURATION > 0) {
			addAction(Actions.sequence(
					Actions.fadeOut(FADE_DURATION, Interpolation.fade),
					Actions.run(hideRunnable)));
		} else {
			setVisible(false);
		}
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x,
			float y) {
		if (stageBackground != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			Stage stage = getStage();
			stageBackground.draw(batch, 0, 0, stage.getWidth(),
					stage.getHeight());

		}
		super.drawBackground(batch, parentAlpha, x, y);
	}

	public Actor hit(float x, float y, boolean touchable) {
		Actor hit = super.hit(x, y, touchable);
		if ((hit == null && isModal && (!touchable || getTouchable() == Touchable.enabled))) {
			return this;
		}
		return hit;
	}

	public void setModal(boolean isModal) {
		this.isModal = isModal;
	}

	public boolean isModal() {
		return this.isModal;
	}

	public void setHideOnOutterTouch(boolean hideOnOutterTouch) {
		this.hideOnExternalTouch = hideOnOutterTouch;
	}

	public void setStageBackground(Drawable stageBackground) {
		this.stageBackground = stageBackground;
	}

	protected final Runnable hideRunnable = new Runnable() {
		@Override
		public void run() {
			setVisible(false);
		}
	};
}
