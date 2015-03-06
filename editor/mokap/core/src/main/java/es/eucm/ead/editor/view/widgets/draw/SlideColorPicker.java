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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Slider;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;

/**
 * A color picker that uses {@link Slider}s to display HSB colors.
 * 
 * @author Rotaru Dan Cristian
 * 
 */
public class SlideColorPicker extends AbstractWidget {

	/**
	 * HUE is the color type (such as red, blue, or yellow).
	 * 
	 * Ranges from 0 to 360° in most applications. (each value corresponds to
	 * one color : 0 is red, 45 is a shade of orange and 55 is a shade of
	 * yellow).
	 */
	private static final float SLIDER_MAX_VALUE = 359;
	private static final float HEIGHT_TIMES_KNOB = 1.1f;
	private static final float WIDTH_TIMES_KNOB = 8f;

	private static final InputListener listener = new InputListener() {

		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			return true;
		};

		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			if (pointer == 0) {
				Slider slider = (Slider) event.getListenerActor();
				SlideColorPicker picker = (SlideColorPicker) slider
						.getUserObject();
				picker.updateColor();
				picker.updateAllTexturesExcept(slider);
				picker.fireColorChanged(true);
			}
		}

		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			Slider slider = (Slider) event.getListenerActor();
			SlideColorPicker picker = (SlideColorPicker) slider.getUserObject();
			picker.updateColor();
			picker.updateAllTexturesExcept(slider);
			picker.fireColorChanged(false);
		};
	};

	private final float[] tempValues = new float[3];
	private final Color color = new Color();

	private float pad, sliderSpace;

	private Pixmap huePixmap;
	private Pixmap saturationPixmap;
	private Pixmap brightnessPixmap;

	private Slider hueSlider;
	private Slider saturationSlider;
	private Slider brightnessSlider;

	private Texture hueTexture;
	private Texture saturationTexture;
	private Texture brightnessTexture;

	/**
	 * Create a {@link SlideColorPicker} with default style.
	 * 
	 * @param skin
	 *            the skin to use
	 */
	public SlideColorPicker(Skin skin) {
		this(skin.get(SlideColorPickerStyle.class));
	}

	/**
	 * @see {@link #SlideColorPicker(SlideColorPickerStyle)}.
	 * @param skin
	 * @param styleName
	 */
	public SlideColorPicker(Skin skin, String styleName) {
		this(skin.get(styleName, SlideColorPickerStyle.class));
	}

	/**
	 * Create a {@link SlideColorPicker} with defined style.
	 * 
	 * @param slideColorPickerStyle
	 *            the style to use
	 */
	public SlideColorPicker(SlideColorPickerStyle slideColorPickerStyle) {

		SliderStyle sliderStyle = slideColorPickerStyle.slider;
		setSliderSpace(slideColorPickerStyle.sliderSpace);
		setPad(slideColorPickerStyle.pad);

		hueSlider = new Slider(0, SLIDER_MAX_VALUE, 1, false, new SliderStyle(
				sliderStyle)) {
			@Override
			public float getPrefHeight() {
				return 0;
			}
		};
		hueSlider.setValue(SLIDER_MAX_VALUE);
		hueSlider.setUserObject(this);
		hueSlider.addInputListener(listener);

		saturationSlider = new Slider(0, SLIDER_MAX_VALUE, 1, false,
				new SliderStyle(sliderStyle)) {
			@Override
			public float getPrefHeight() {
				return 0;
			}
		};
		saturationSlider.setValue(SLIDER_MAX_VALUE);
		saturationSlider.setUserObject(this);
		saturationSlider.addInputListener(listener);

		brightnessSlider = new Slider(0, SLIDER_MAX_VALUE, 1, false,
				new SliderStyle(sliderStyle)) {
			@Override
			public float getPrefHeight() {
				return 0;
			}
		};
		brightnessSlider.setValue(SLIDER_MAX_VALUE);
		brightnessSlider.setUserObject(this);
		brightnessSlider.addInputListener(listener);
		color.set(Color.RED);

		addActor(hueSlider);
		addActor(saturationSlider);
		addActor(brightnessSlider);
	}

	public void setPickedColor(Color color) {
		this.color.set(color);
		updateSlidersTo(color);
	}

	/**
	 * Generate the slider background.
	 */
	public void initialize() {
		if (huePixmap == null) {
			int width = MathUtils.round(backgroundWidth()), height = MathUtils
					.round(backgroundHeight());

			huePixmap = new Pixmap(width, height, Format.RGBA8888);
			saturationPixmap = new Pixmap(width, height, Format.RGBA8888);
			brightnessPixmap = new Pixmap(width, height, Format.RGBA8888);

			float saturation = 1f, brightness = 1f;
			int minWidth = getClearColorWidth();
			int colorMaxPosition = width - minWidth;
			float colorWidth = width - minWidth * 2f;
			for (int i = minWidth; i < colorMaxPosition; ++i) {
				float percentageCompletion = (i - minWidth) / colorWidth;
				float[] rgb = HSBtoRGB(percentageCompletion, saturation,
						brightness, tempValues);

				drawColor(huePixmap, i, rgb[0], rgb[1], rgb[2], 1f);
			}

			hueTexture = new Texture(huePixmap);
			saturationTexture = new Texture(saturationPixmap);
			brightnessTexture = new Texture(brightnessPixmap);

			hueSlider.getStyle().background = new TextureRegionDrawable(
					new TextureRegion(hueTexture));
			saturationSlider.getStyle().background = new TextureRegionDrawable(
					new TextureRegion(saturationTexture = new Texture(
							saturationPixmap)));
			brightnessSlider.getStyle().background = new TextureRegionDrawable(
					new TextureRegion(brightnessTexture));

			invalidateHierarchy();
			updateAllTexturesExcept(null);
		}
	}

	public void release() {
		if (huePixmap != null) {
			huePixmap.dispose();
			huePixmap = null;
			saturationPixmap.dispose();
			brightnessPixmap.dispose();

			hueTexture.dispose();
			saturationTexture.dispose();
			brightnessTexture.dispose();
		}
	}

	private void updateColor() {
		float[] rgb = HSBtoRGB(getValue(hueSlider), getValue(saturationSlider),
				getValue(brightnessSlider), tempValues);
		color.set(rgb[0], rgb[1], rgb[2], 1f);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.setColor(1f, 1f, 1f, 1f);
	}

	/**
	 * Updates the {@link #hueSlider} {@link #saturationSlider} and
	 * {@link #brightnessSlider} and their textures to their new position
	 * dictated by the color argument. Also fires that the color has changed.
	 * 
	 * @param color
	 */
	public void updateSlidersTo(Color color) {
		this.color.set(color);

		updateSlidersPosition();

		updateAllTexturesExcept(null);

		fireColorChanged(false);
	}

	private void updateSlidersPosition() {
		float[] hsb = RGBtoHSB(MathUtils.round(color.r * 255f),
				MathUtils.round(color.g * 255f),
				MathUtils.round(color.b * 255f), tempValues);
		float h = hsb[0];
		float s = hsb[1];
		float b = hsb[2];

		setValue(hueSlider, h);
		setValue(saturationSlider, s);
		setValue(brightnessSlider, b);
	}

	private void updateAllTexturesExcept(Slider slider) {

		if (huePixmap != null) {
			int width = brightnessPixmap.getWidth();
			int minWidth = getClearColorWidth();
			int colorMaxPosition = width - minWidth;
			float colorWidth = width - minWidth * 2f;

			float h = getValue(hueSlider);
			float s = getValue(saturationSlider);
			float b = getValue(brightnessSlider);

			for (int i = minWidth; i < colorMaxPosition; ++i) {
				float percentageCompletion = (i - minWidth) / colorWidth;

				if (slider != saturationSlider) {
					float[] rgb = HSBtoRGB(h, percentageCompletion, b,
							tempValues);

					drawColor(saturationPixmap, i, rgb[0], rgb[1], rgb[2], 1f);
				}

				if (slider != brightnessSlider) {
					float[] rgb = HSBtoRGB(h, s, percentageCompletion,
							tempValues);
					drawColor(brightnessPixmap, i, rgb[0], rgb[1], rgb[2], 1f);
				}
			}

			if (slider != saturationSlider) {
				saturationTexture.draw(saturationPixmap, 0, 0);
			}
			if (slider != brightnessSlider) {
				brightnessTexture.draw(brightnessPixmap, 0, 0);
			}
		}
	}

	private void drawColor(Pixmap pixmap, int x, float r, float g, float b,
			float a) {
		pixmap.setColor(r, g, b, a);
		pixmap.drawLine(x, 0, x, pixmap.getHeight());
	}

	/**
	 * 
	 * @return the width of what will be transparent at the left/right border of
	 *         the sliders.
	 */
	private int getClearColorWidth() {
		return MathUtils.round(hueSlider.getStyle().knob.getMinWidth() * .5f);
	}

	private void fireColorChanged(boolean isDragging) {
		ColorEvent event = Pools.obtain(ColorEvent.class);
		event.color = color;
		event.dragging = isDragging;
		fire(event);
		Pools.free(event);
	}

	private float getValue(Slider slider) {
		return MathUtils
				.clamp(slider.getValue() / slider.getMaxValue(), 0f, 1f);
	}

	private void setValue(Slider slider, float value) {
		slider.setValue(value * slider.getMaxValue());
	}

	@Override
	public void layout() {
		float width = backgroundWidth(), sliderHeight = backgroundHeight();

		setBounds(brightnessSlider, pad, pad, width, sliderHeight);
		setBounds(saturationSlider, pad, brightnessSlider.getY() + sliderSpace
				+ brightnessSlider.getHeight(), width, sliderHeight);
		setBounds(hueSlider, pad, saturationSlider.getY() + sliderSpace
				+ saturationSlider.getHeight(), width, sliderHeight);
	}

	private float backgroundHeight() {
		return hueSlider.getStyle().knob.getMinHeight() * HEIGHT_TIMES_KNOB;
	}

	private float backgroundWidth() {
		return hueSlider.getStyle().knob.getMinWidth() * WIDTH_TIMES_KNOB;
	}

	@Override
	public float getPrefHeight() {
		return backgroundHeight() * 3 + 2 * sliderSpace + 2 * pad;
	}

	@Override
	public float getPrefWidth() {
		return backgroundWidth() + 2 * pad;
	}

	public Color getPickedColor() {
		return color;
	}

	/**
	 * 
	 * @param pad
	 *            the padding of the whole panel, must be in DP.
	 */
	public void setPad(float pad) {
		this.pad = WidgetBuilder.dpToPixels(pad);
	}

	/**
	 * 
	 * @param space
	 *            the space between the sliders, must be in DP.
	 */
	public void setSliderSpace(float space) {
		this.sliderSpace = WidgetBuilder.dpToPixels(space);
	}

	public float[] HSBtoRGB(float hue, float saturation, float brightness) {
		return HSBtoRGB(hue, saturation, brightness, tempValues);
	}

	/**
	 * Converts the components of a color, as specified by the HSB model, to an
	 * equivalent set of values for the default RGB model.
	 * <p>
	 * The <code>saturation</code> and <code>brightness</code> components should
	 * be floating-point values between zero and one (numbers in the range
	 * 0.0-1.0). The <code>hue</code> component can be any floating-point
	 * number. The floor of this number is subtracted from it to create a
	 * fraction between 0 and 1. This fractional number is then multiplied by
	 * 360 to produce the hue angle in the HSB color model.
	 * <p>
	 * The integer that is returned by <code>HSBtoRGB</code> encodes the value
	 * of a color in bits 0-23 of an integer value that is the same format used
	 * by the method {@link #getRGB() <code>getRGB</code>}. This integer can be
	 * supplied as an argument to the <code>Color</code> constructor that takes
	 * a single integer argument.
	 * 
	 * @param hue
	 *            the hue component of the color
	 * @param saturation
	 *            the saturation of the color
	 * @param brightness
	 *            the brightness of the color
	 * @return the RGB value of the color with the indicated hue, saturation,
	 *         and brightness.
	 */
	public static float[] HSBtoRGB(float hue, float saturation,
			float brightness, float[] values) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		values[0] = r / 255f;
		values[1] = g / 255f;
		values[2] = b / 255f;
		return values;
	}

	/**
	 * Converts the components of a color, as specified by the default RGB
	 * model, to an equivalent set of values for hue, saturation, and brightness
	 * that are the three components of the HSB model.
	 * <p>
	 * If the <code>hsbvals</code> argument is <code>null</code>, then a new
	 * array is allocated to return the result. Otherwise, the method returns
	 * the array <code>hsbvals</code>, with the values put into that array.
	 * 
	 * @param r
	 *            the red component of the color
	 * @param g
	 *            the green component of the color
	 * @param b
	 *            the blue component of the color
	 * @param hsbvals
	 *            the array used to return the three HSB values, or
	 *            <code>null</code>
	 * @return an array of three elements containing the hue, saturation, and
	 *         brightness (in that order), of the color with the indicated red,
	 *         green, and blue components.
	 */
	public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
		float hue, saturation, brightness;
		if (hsbvals == null) {
			hsbvals = new float[3];
		}
		int cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		int cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;

		brightness = ((float) cmax) / 255.0f;
		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	/**
	 * Base class to listen to {@link ColorEvent}s produced by
	 * {@link SlideColorPicker}.
	 */
	public static class ColorListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (event instanceof ColorEvent) {
				colorChanged((ColorEvent) event);
			}
			return true;
		}

		/**
		 * The color has changed.
		 */
		public void colorChanged(ColorEvent event) {

		}
	}

	public static class ColorEvent extends Event {

		private Color color;

		private boolean dragging;

		public Color getColor() {
			return color;
		}

		@Override
		public void reset() {
			super.reset();
			this.color = null;
		}

		public boolean isDragging() {
			return dragging;
		}
	}

	/**
	 * The style for a {@link SlideColorPicker}.
	 * 
	 * @author Rotaru Dan Cristian
	 */
	static public class SlideColorPickerStyle {

		public SliderStyle slider;

		/**
		 * Optional in DP.
		 */
		public float pad, sliderSpace;

		public SlideColorPickerStyle() {
		}

		public SlideColorPickerStyle(SliderStyle slider) {
			this.slider = slider;
		}

		public SlideColorPickerStyle(SlideColorPickerStyle style) {
			this.slider = style.slider;
		}
	}
}