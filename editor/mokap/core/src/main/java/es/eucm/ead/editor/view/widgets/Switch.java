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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

/**
 * Widget with two states, on and off. For change the state you can touch the
 * image contained or drag it to the opposite side.
 * 
 */
public class Switch extends Container<Actor> {

	private SwitchStyle style;

	private IconButton onImage;

	private IconButton offImage;

	private float offsetX;

	private Drawable current;

	private Drawable background;

	private boolean stateOn;

	private Color circleColor;

	private Color barColor;

	public Switch(Skin skin, String imageOn, String imageOff) {
		this(skin, skin.get(SwitchStyle.class), imageOn, imageOff);
	}

	public Switch(Skin skin, String imageOn, String imageOff, String style) {
		this(skin, skin.get(style, SwitchStyle.class), imageOn, imageOff);
	}

	public Switch(Skin skin, SwitchStyle switchStyle, String imageOn,
			String imageOff) {
		super();

		this.style = switchStyle;
		this.current = style.off;

		background = switchStyle.backgroundOff;

		onImage = new IconButton(imageOn, skin, switchStyle.iconStyle);
		onImage.pack();

		offImage = new IconButton(imageOff, skin, switchStyle.iconStyle);
		offImage.setDisabled(true);
		offImage.pack();

		align(Align.left);

		setStateOn(false);

		addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				setActor(null);
				if (x <= current.getMinWidth() / 2) {
					offsetX = 0;
				} else if (x < getWidth() - current.getMinWidth() / 2) {
					offsetX = x - current.getMinWidth() / 2;
				} else {
					offsetX = getWidth() - current.getMinWidth();
				}

				if (offsetX < (getWidth() - current.getMinWidth()) / 2) {
					current = style.off;
					background = style.backgroundOff;
					barColor = style.barColorOff;
					circleColor = style.circleColorOff;
				} else if (offsetX > (getWidth() - current.getMinWidth()) / 2) {
					current = style.on;
					background = style.backgroundOn;
					barColor = style.barColorOn;
					circleColor = style.circleColorOn;
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				setStateOn((offsetX > (getWidth() - current.getMinWidth()) / 2 && getActor() == null)
						|| (offsetX < (getWidth() - current.getMinWidth()) / 2 && getActor() != null));
			}
		});
	}

	public void setStateOn(boolean isOn) {
		if (isOn) {
			offsetX = getWidth() - style.on.getMinWidth();
			setActor(onImage);
			padLeft(getWidth() - offImage.getWidth()
					- (current.getMinWidth() - offImage.getWidth()) / 2);
			current = style.on;
			background = style.backgroundOn;
			barColor = style.barColorOn;
			circleColor = style.circleColorOn;

		} else if (!isOn) {
			offsetX = 0;
			setActor(offImage);
			padLeft((current.getMinWidth() - offImage.getWidth()) / 2);
			current = style.off;
			background = style.backgroundOff;
			barColor = style.barColorOff;
			circleColor = style.circleColorOff;
		}

		if (stateOn != isOn) {
			ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
			stateOn = isOn;
			fire(changeEvent);
			Pools.free(changeEvent);
		}
	}

	public boolean isStateOn() {
		return stateOn;
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x,
			float y) {
		batch.setColor(barColor != null ? barColor : Color.WHITE);
		background.draw(batch, x, y + (getHeight() - background.getMinHeight())
				/ 2, background.getMinWidth(), background.getMinHeight());
		batch.setColor(circleColor != null ? circleColor : Color.WHITE);
		current.draw(batch, x + offsetX,
				y + (getHeight() - current.getMinHeight()) / 2,
				current.getMinWidth(), current.getMinHeight());
	}

	@Override
	public float getPrefHeight() {
		return style.on.getMinHeight() > style.off.getMinHeight() ? style.on
				.getMinHeight() : style.off.getMinHeight();
	}

	@Override
	public float getPrefWidth() {
		return style.backgroundOn.getMinWidth() > style.backgroundOff
				.getMinWidth() ? style.backgroundOn.getMinWidth()
				: style.backgroundOff.getMinWidth();
	}

	public static class SwitchStyle {

		public Drawable backgroundOn;

		public Drawable backgroundOff;

		public Drawable on;

		public Drawable off;

		public String iconStyle;

		/**
		 * Optional
		 */
		public Color circleColorOn;

		/**
		 * Optional
		 */
		public Color circleColorOff;

		/**
		 * Optional
		 */
		public Color barColorOn;

		/**
		 * Optional
		 */
		public Color barColorOff;

	}
}
