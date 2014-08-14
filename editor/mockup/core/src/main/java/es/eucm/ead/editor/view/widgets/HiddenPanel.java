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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

/**
 * Panel is a generic lightweight container with {@link #show()} and
 * {@link #hide()} methods. Sizes and positions children using table constraint.
 */
public class HiddenPanel extends Table {

	private static final Vector2 temp = new Vector2();

	private boolean isModal;

	protected Drawable stageBackground;

	private boolean hideOnExternalTouch;

	/**
	 * {@link Array} of {@link Actor} that can be touched although {@link is
	 * modal}
	 */
	private Array<Actor> touchableActors;

	public HiddenPanel(Skin skin) {
		super(skin);
		initialize(skin, new Array<Actor>());
	}

	public HiddenPanel(Skin skin, String drawableBackground, String colorStage) {
		super(skin);
		this.stageBackground = skin.getDrawable(colorStage);
		setBackground(drawableBackground);
		initialize(skin, new Array<Actor>());

	}

	public HiddenPanel(Skin skin, String drawableBackground) {
		super(skin);
		setBackground(drawableBackground);
		initialize(skin, new Array<Actor>());
	}

	public HiddenPanel(Skin skin, Array<Actor> actors) {
		super(skin);
		initialize(skin, actors);
	}

	public HiddenPanel(Skin skin, String drawableBackground, String colorStage,
			Array<Actor> actors) {
		super(skin);
		this.stageBackground = skin.getDrawable(colorStage);
		setBackground(drawableBackground);
		initialize(skin, actors);

	}

	public HiddenPanel(Skin skin, String drawableBackground, Array<Actor> actors) {
		super(skin);
		setBackground(drawableBackground);
		initialize(skin, actors);
	}

	protected void initialize(Skin skin, Array<Actor> actors) {
		this.touchableActors = actors;

		this.hideOnExternalTouch = true;
		this.isModal = true;

		addListener(new InputListener() {
			private final Rectangle rtmp = new Rectangle();

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				localToParentCoordinates(temp.set(x, y));
				rtmp.set(getX(), getY(), getWidth(), getHeight());
				if (hideOnExternalTouch && !rtmp.contains(temp.x, temp.y)) {
					hide();
				}
				return isModal;
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				return isModal;
			}

			@Override
			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				return isModal;
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				return isModal;
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				return isModal;
			}

			@Override
			public boolean keyTyped(InputEvent event, char character) {
				return isModal;
			}
		});
	}

	public void show() {
		if (!isVisible()) {
			setVisible(true);
		}
	}

	public void hide() {
		if (isVisible()) {
			setVisible(false);
		}
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

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		final Actor hit = super.hit(x, y, touchable);
		if (!isModal || hit != null) {
			return hit;
		}

		for (Actor a : touchableActors) {
			localToStageCoordinates(temp.set(x, y));
			a.stageToLocalCoordinates(temp);
			if (a.hit(temp.x, temp.y, a.isTouchable()) != null) {
				return null;
			}
		}
		return this;
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
}