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
package es.eucm.ead.editor.view.widgets.draw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel.ColorPickerPanelStyle;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorEvent;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker.ColorListener;

/**
 * A panel with a {@link ColorPickerPanel} and a slider to select the size, when
 * the selected size changes a {@link SizeEvent} is fired that can be captured
 * by a {@link SizeListener}.
 * 
 * @author Rotaru Dan Cristian
 * 
 */
public class BrushStrokesPicker extends ContextMenu {

	private static final float MIN_SIZE = 0, MAX_SIZE = 100, STEP_SIZE = 1;
	private Slider slider;
	private ColorPickerPanel colorPicker;

	public BrushStrokesPicker(Skin skin) {
		this(skin, skin.get(BrushStrokesPickerStyle.class));
	}

	public BrushStrokesPicker(Skin skin, BrushStrokesPickerStyle style) {
		setBackground(style.background);
		slider = new Slider(MIN_SIZE, MAX_SIZE, STEP_SIZE, false, skin,
				style.sizeSliderStyleName);
		slider.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				fireSizeChanged();
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				super.touchDragged(event, x, y, pointer);
				if (pointer == 0) {
					fireSizeChanged();
				}
			}

		});
		slider.setValue((MAX_SIZE - MIN_SIZE) * .5f);

		IconButton iconButton = WidgetBuilder.icon(SkinConstants.IC_CIRCLE,
				SkinConstants.STYLE_TOOLBAR);
		final Image icon = iconButton.getIcon();
		icon.setOrigin(Align.center);

		colorPicker = new ColorPickerPanel(skin, style.colorPickerStyle);
		colorPicker.addListener(new ColorListener() {
			@Override
			public void colorChanged(ColorEvent event) {
				icon.setColor(event.getColor());
			}
		});
		addListener(new SizeListener() {
			@Override
			public void sizeChanged(SizeEvent event) {
				icon.setScale(event.getCompletion());
			}
		});

		add(iconButton);
		add(slider).pad(WidgetBuilder.dpToPixels(style.sizeSliderPad))
				.expandX().fillX();
		row();
		add(colorPicker).colspan(2);
	}

	/**
	 * 
	 * @param value
	 *            a number between 0 and 1.
	 */
	public void setSizeValue(float value) {
		slider.setValue((MAX_SIZE - MIN_SIZE) * value);
		fireSizeChanged();
	}

	/**
	 * 
	 * @param color
	 *            the color picked.
	 */
	public void setPickedColor(Color color) {
		colorPicker.setPickedColor(color);
	}

	private float getCompletion() {
		return slider.getValue() / slider.getMaxValue();
	}

	private void fireSizeChanged() {
		SizeEvent event = Pools.obtain(SizeEvent.class);
		event.completion = MathUtils.clamp(getCompletion(), .1f, 1f);
		fire(event);
		Pools.free(event);
	}

	@Override
	public void show() {
		super.show();
		colorPicker.initResources();
	}

	@Override
	public void hide(Runnable runnable) {
		colorPicker.setUpPickedColor();
		SequenceAction hideAction = getHideAction(runnable);
		hideAction.addAction(Actions.run(colorPicker.getReleaseResources()));
		addAction(hideAction);
	}

	@Override
	public boolean hideAlways() {
		return false;
	}

	/**
	 * Base class to listen to {@link SizeEvent}s produced by
	 * {@link BrushStrokesPicker}.
	 */
	public static class SizeListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof SizeEvent) {
				sizeChanged((SizeEvent) event);
			}
			return true;
		}

		/**
		 * The size has changed.
		 */
		public void sizeChanged(SizeEvent event) {

		}
	}

	public static class SizeEvent extends Event {

		private float completion;

		/**
		 * 
		 * @return a value between 0 and 1 indicating completion value.
		 */
		public float getCompletion() {
			return completion;
		}

		@Override
		public void reset() {
			super.reset();
			this.completion = 0f;
		}
	}

	/**
	 * The style for a {@link BrushStrokesPicker}.
	 * 
	 * @author Rotaru Dan Cristian
	 */
	static public class BrushStrokesPickerStyle {

		public ColorPickerPanelStyle colorPickerStyle;

		public String sizeSliderStyleName;

		/** Optional */
		public Drawable background;

		/** Optional, in DP */
		public float sizeSliderPad;

		public BrushStrokesPickerStyle() {
		}

		public BrushStrokesPickerStyle(BrushStrokesPickerStyle style) {
			this.colorPickerStyle = style.colorPickerStyle;
			this.background = style.background;
		}

		public BrushStrokesPickerStyle(ColorPickerPanelStyle colorPickerStyle,
				Drawable background) {
			this.colorPickerStyle = colorPickerStyle;
			this.background = background;
		}
	}
}
