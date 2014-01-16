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
package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.mockup.core.control.listeners.FocusListener;
import es.eucm.ead.mockup.core.control.screens.AbstractScreen;

public class Panel extends Table implements FocusListener {
	/**
	 * Set this value to 0 for no {@link show show()} or {@link hide hide()} animation.
	 */
	public static float fadeDuration = .4f;

	private PanelStyle style;
	private Vector2 temp;
	private boolean isModal;

	/**
	 * Create a {@link Panel panel} with default style.
	 * 
	 * @param skin the skin to use
	 */
	public Panel(Skin skin) {
		this(skin, "default");
	}

	/**
	 * Create a {@link Panel panel} with specified style.
	 * 
	 * @param skin the skin to use
	 * @param styleName the style to use
	 */
	public Panel(Skin skin, String styleName) {
		super(skin);
		this.temp = new Vector2();
		setStyle(skin.get(styleName, PanelStyle.class));
		setTouchable(Touchable.enabled);

		addListener(new InputListener() {
			Rectangle rtmp = new Rectangle();

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				localToStageCoordinates(/* in/out */temp.set(x, y));
				rtmp.set(getX(), getY(), getWidth(), getHeight());
				if (!rtmp.contains(temp.x, temp.y)) {
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
				System.out.println("keydown");
				return isModal;
			}

			public boolean keyUp(InputEvent event, int keycode) {
				System.out.println(keycode);
				return isModal;
			}

			public boolean keyTyped(InputEvent event, char character) {
				System.out.println(character);
				return isModal;
			}
		});
	}

	/**
	 * Apply a {@link PanelStyle style}.
	 * 
	 * @param style the style to apply
	 * @throws IllegalArgumentException if the style is null
	 */
	public void setStyle(PanelStyle style) {
		if (style == null)
			throw new IllegalArgumentException("style cannot be null");

		this.style = style;
		this.setBackground(style.background);

		invalidateHierarchy();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha) {
		Drawable stageBG = style.stageBackground;
		if (stageBG != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			//Stage s = getStage();
			//float x = getX(), y = getY();*/
			//stageToLocalCoordinates(/* in/out */temp.set(0, 0));
			//stageToLocalCoordinates(/* in/out */tmpSize.set(s.getWidth(), s.getHeight()));
			//stageBG.draw(batch, x + temp.x, y + temp.y, x + tmpSize.x, y + tmpSize.y);
			stageBG.draw(batch, 0, 0, AbstractScreen.stagew,
					AbstractScreen.stageh);

		}
		super.drawBackground(batch, parentAlpha);
	}

	/**
	 * @return the current style of this {@link Panel panel}.
	 */
	public PanelStyle getStyle() {
		return style;
	}

	public Actor hit(float x, float y, boolean touchable) {
		Actor hit = super.hit(x, y, touchable);
		if ((hit == null && (!touchable || getTouchable() == Touchable.enabled))) {
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

	public void show() {
		if (fadeDuration > 0) {
			getColor().a = 0;

			addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(
					fadeDuration, Interpolation.fade)));
		}
	}

	public void hide() {
		if (fadeDuration > 0) {
			addAction(Actions.sequence(Actions.fadeOut(fadeDuration,
					Interpolation.fade), Actions.visible(false)));
		}
	}

	/**
	 * Define the style of a {@link Panel panel}.
	 * 
	 */
	static public class PanelStyle {

		/** Optional. */
		public Drawable background;
		/** Optional. */
		public Drawable stageBackground;

		public PanelStyle() {
		}

		public PanelStyle(Drawable background, Drawable stageBackground) {
			this.background = background;
			this.stageBackground = stageBackground;
		}

		public PanelStyle(PanelStyle style) {
			this.background = style.background;
			this.stageBackground = style.stageBackground;
		}
	}
}