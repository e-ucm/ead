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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.engine.gdx.AbstractWidget;

/**
 * This widget imitates a switch with check and unchecked states. Each state can
 * have an drawable associated.
 */
public class Switch extends AbstractWidget {

	private SwitchStyle style;

	private boolean checked;

	private Container<Image> knob;

	private Image knobImage;

	public Switch(Skin skin) {
		this(skin.get(SwitchStyle.class));
	}

	public Switch(Skin skin, String checkedDrawable, String uncheckedDrawable,
			Color drawableColor) {
		this(skin, skin.get(SwitchStyle.class), checkedDrawable,
				uncheckedDrawable, drawableColor);
	}

	public Switch(Skin skin, String style, String checkedDrawable,
			String uncheckedDrawable, Color drawableColor) {
		this(skin, skin.get(style, SwitchStyle.class), checkedDrawable,
				uncheckedDrawable, drawableColor);
	}

	public Switch(Skin skin, SwitchStyle switchStyle, String checkedDrawable,
			String uncheckedDrawable, Color drawableColor) {
		this(new SwitchStyle(switchStyle, skin.getDrawable(checkedDrawable),
				skin.getDrawable(uncheckedDrawable), drawableColor));
	}

	public Switch(Skin skin, String style) {
		this(skin.get(style, SwitchStyle.class));
	}

	public Switch(SwitchStyle style) {
		this.style = style;
		knob = new Container<Image>(knobImage = new Image());
		knob.setBackground(style.knob);
		addActor(knob);
		addListener(new InputListener() {

			private boolean drag;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				drag = false;
				event.stop();
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (!drag) {
					drag = true;
					getStage().cancelTouchFocusExcept(this, Switch.this);
				}

				knob.setX(Math.max(
						0,
						Math.min(getWidth() - knob.getWidth(),
								x - knob.getWidth() / 2.0f)));
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (drag) {
					setChecked(knobInCheckedPosition(), true);
				} else {
					setChecked(!isChecked(), true);
				}
			}
		});
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		boolean knobInCheckedPosition = knobInCheckedPosition();
		batch.setColor(knobInCheckedPosition ? style.checkedColor
				: style.uncheckedColor);
		style.background.draw(batch, 0,
				getHeight() / 2.0f - style.background.getMinHeight() / 2.0f,
				getWidth(), style.background.getMinHeight());
		knob.setColor(batch.getColor());

		if (style.checkedDrawable != null) {
			knobImage.setDrawable(knobInCheckedPosition ? style.checkedDrawable
					: style.uncheckedDrawable);
			knobImage
					.setColor(knobInCheckedPosition ? style.checkedDrawableColor
							: style.uncheckedDrawableColor);
		}
		super.drawChildren(batch, parentAlpha);
	}

	private boolean knobInCheckedPosition() {
		return knob.getX() > getWidth() / 2.0f - knob.getWidth() / 2.0f;
	}

	public void setChecked(boolean checked) {
		setChecked(checked, false);
	}

	private void setChecked(boolean checked, boolean fireEvent) {
		float x = checked ? getWidth() - knob.getWidth() : 0;
		knob.clearActions();
		knob.addAction(Actions2.moveToX(x, 0.2f, Interpolation.exp5Out));

		if (this.checked != checked) {
			this.checked = checked;

			if (fireEvent) {
				ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
				fire(changeEvent);
				Pools.free(changeEvent);
			}
		}
	}

	public boolean isChecked() {
		return checked;
	}

	@Override
	public void layout() {
		knob.clearActions();
		knob.pack();
		float x = checked ? getWidth() - knob.getWidth() : 0;
		knob.setPosition(x, getHeight() / 2.0f - knob.getHeight() / 2.0f);
	}

	@Override
	public float getPrefHeight() {
		return Math.max(style.background.getMinHeight(), getPrefHeight(knob));
	}

	@Override
	public float getPrefWidth() {
		return Math.max(style.background.getMinWidth(), getPrefHeight(knob));
	}

	public static class SwitchStyle {

		public Drawable background;

		public Drawable knob;

		public Color checkedColor;

		public Color uncheckedColor;

		/**
		 * Optional
		 */
		public Drawable checkedDrawable;

		/**
		 * Optional
		 */
		public Drawable uncheckedDrawable;

		public Color checkedDrawableColor;

		public Color uncheckedDrawableColor;

		public SwitchStyle() {
		}

		public SwitchStyle(SwitchStyle style, Drawable checkedDrawable,
				Drawable uncheckedDrawable, Color color) {
			this.background = style.background;
			this.knob = style.knob;
			this.checkedColor = style.checkedColor;
			this.uncheckedColor = style.uncheckedColor;
			this.checkedDrawable = checkedDrawable;
			this.uncheckedDrawable = uncheckedDrawable;
			this.checkedDrawableColor = this.uncheckedDrawableColor = color;
		}
	}
}
