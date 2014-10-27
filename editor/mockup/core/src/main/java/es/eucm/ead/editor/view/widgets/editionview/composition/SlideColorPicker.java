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
package es.eucm.ead.editor.view.widgets.editionview.composition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SlideColorPicker extends Table {
	private static final int MIN_COLOR = 0;
	private static final int MAX_COLOR = 359;

	private static final float PREF_HEIGHT = .08f;
	private static final float PREF_WIDTH = .35f;

	private static final InputListener listener = new InputListener() {

		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			return true;
		}

		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			SlideColorPicker picker = (SlideColorPicker) event
					.getListenerActor().getUserObject();
			picker.updateColor();
			picker.updateBrightness();
			picker.colorChanged(picker.color);
		}

		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			touchDragged(event, 0f, 0f, 0);
		}
	};

	private final float[] tempValues = new float[3];
	private final Color color = new Color();
	private Texture colorTexture;
	private Texture brightnessTexture;
	private Pixmap colorPixmap;
	private Pixmap brightnessPixmap;
	private Slider colorSlider;
	private Slider brightnessSlider;

	/**
	 * Create a {@link SlideColorPicker} with default style.
	 * 
	 * @param skin
	 *            the skin to use
	 */
	public SlideColorPicker(Skin skin) {
		this(skin, "colorPicker-horizontal");
	}

	/**
	 * Create a {@link SlideColorPicker} with defined style.
	 * 
	 * @param skin
	 *            the skin to use
	 * @param styleName
	 *            the style to use
	 */
	public SlideColorPicker(Skin skin, String styleName) {
		super();
		SliderStyle sliderStyle = skin.get(styleName, SliderStyle.class);
		colorSlider = new Slider(MIN_COLOR, MAX_COLOR, 1, false,
				new SliderStyle(sliderStyle)) {
			@Override
			public float getPrefHeight() {
				return SlideColorPicker.this.getPrefHeight();
			}

			@Override
			public float getPrefWidth() {
				return SlideColorPicker.this.getPrefWidth();
			}
		};
		colorSlider.setUserObject(this);
		brightnessSlider = new Slider(MIN_COLOR, MAX_COLOR, 1, false,
				new SliderStyle(sliderStyle)) {
			@Override
			public float getPrefHeight() {
				return SlideColorPicker.this.getPrefHeight();
			}

			@Override
			public float getPrefWidth() {
				return SlideColorPicker.this.getPrefWidth();
			}
		};
		brightnessSlider.addListener(new DragListener() {

			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				updateColor();
				colorChanged(color);
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				touchDragged(event, 0f, 0f, 0);
			}
		});
		brightnessSlider.setValue(MAX_COLOR * .5f);
		colorSlider.addListener(listener);
		add(brightnessSlider);
		row();
		add(colorSlider);
		initialize();
	}

	/**
	 * Generate the slider background.
	 */
	private void initialize() {
		colorPixmap = new Pixmap((int) getPrefWidth(), (int) getPrefHeight(),
				Format.RGBA8888);
		int _colorMin = MIN_COLOR;
		int _colorMax = MAX_COLOR;
		int _colorCount = (_colorMax - _colorMin);
		float _scaleRatio = (float) _colorCount / colorPixmap.getWidth();

		for (int i = 0; i <= colorPixmap.getWidth(); i++) {
			int hsbToRGB = (0xff000000 | HSBtoRGB((i * _scaleRatio + _colorMin)
					/ ((float) _colorMax)));

			colorPixmap.setColor(((hsbToRGB >> 16) & 0xFF) / 256f,
					((hsbToRGB >> 8) & 0xFF) / 256f,
					((hsbToRGB >> 0) & 0xFF) / 256f, 1f);
			colorPixmap.drawLine(i, 0, i, colorPixmap.getHeight());
		}

		colorSlider.getStyle().background = new TextureRegionDrawable(
				new TextureRegion(colorTexture = new Texture(colorPixmap)));

		brightnessPixmap = new Pixmap((int) getPrefWidth(),
				(int) getPrefHeight(), Format.RGBA8888);

		brightnessSlider.getStyle().background = new TextureRegionDrawable(
				new TextureRegion(brightnessTexture = new Texture(
						brightnessPixmap)));

		invalidateHierarchy();
		updateColor();
		updateBrightness();
		colorChanged(color);
	}

	private void updateColor() {
		int pixel = colorPixmap.getPixel(
				(int) (colorPixmap.getWidth() * MathUtils.clamp(
						colorSlider.getValue() / colorSlider.getMaxValue(), 0f,
						.99f)), 0);

		Color.rgba8888ToColor(color, pixel);

		float[] hsl = RGBtoHSL(color, tempValues);
		HSLtoRGB(hsl[0], hsl[1], (1 - brightnessSlider.getValue()
				/ brightnessSlider.getMaxValue()), tempValues);
		color.set(tempValues[0], tempValues[1], tempValues[2], 1f);
	}

	public void updatePosition(Color color) {
		if (this.color.r == color.r && this.color.g == color.g
				&& this.color.b == color.b) {
			return;
		}
		this.color.set(color);
		int brightnessWidth = brightnessPixmap.getWidth();
		float[] hsl = RGBtoHSL(color, tempValues);
		brightnessSlider
				.setValue(-(hsl[2] * brightnessWidth) + brightnessWidth);

		HSLtoRGB(hsl[0], hsl[1], .5f, tempValues);

		int threshold = 10000000;
		int rgba8888 = Color.rgba8888(tempValues[0], tempValues[1],
				tempValues[2], 1f);
		int minColor = rgba8888 - threshold;
		int maxColor = rgba8888 + threshold;
		int roundI = -1;
		int difference = Integer.MAX_VALUE;
		int pixmapWidth = colorPixmap.getWidth();
		for (int i = 0; i < pixmapWidth; ++i) {
			int pixel = colorPixmap.getPixel(i, 0);
			if (pixel == rgba8888) {
				colorSlider.setValue((i / (float) pixmapWidth) * MAX_COLOR);
				roundI = -1;
				break;
			} else if (pixel > minColor && pixel < maxColor) {
				int currentDifference = Math.abs(pixel - rgba8888);
				if (currentDifference < difference) {
					difference = currentDifference;
					roundI = i;
				}
			}
		}

		if (roundI != -1) {
			colorSlider.setValue((roundI / (float) pixmapWidth) * MAX_COLOR);
		}

		updateBrightness();

		colorChanged(this.color);
	}

	private void updateBrightness() {

		int brightnessWidth = brightnessPixmap.getWidth();

		float[] hsl = RGBtoHSL(color, tempValues);
		float h = hsl[0];
		float s = hsl[1];
		for (int i = 0; i < brightnessWidth; i++) {
			float l = (brightnessWidth - i) / (float) brightnessWidth;

			HSLtoRGB(h, s, l, tempValues);
			brightnessPixmap.setColor(tempValues[0], tempValues[1],
					tempValues[2], 1f);
			brightnessPixmap.drawLine(i, 0, i, brightnessPixmap.getHeight());
		}

		brightnessTexture.draw(brightnessPixmap, 0, 0);
	}

	protected void colorChanged(Color newColor) {

	}

	public void updateTexture() {
		if (colorTexture != null) {
			colorTexture.draw(colorPixmap, 0, 0);
		}
		if (brightnessTexture != null) {
			brightnessTexture.draw(brightnessPixmap, 0, 0);
		}
	}

	@Override
	public float getPrefHeight() {
		return PREF_HEIGHT * Gdx.graphics.getHeight();
	}

	@Override
	public float getPrefWidth() {
		return PREF_WIDTH * Gdx.graphics.getWidth();
	}

	public Color getPickedColor() {
		return color;
	}

	private void HSLtoRGB(float h, float s, float l, float[] values) {
		float q = 0;
		l = MathUtils.clamp(l, .01f, .99f);

		if (l < 0.5) {
			q = l * (1 + s);
		} else {
			q = (l + s) - (s * l);
		}
		float p = 2 * l - q;

		float k = (1.0f / 3.0f);
		float r = Math.max(0, HueToRGB(p, q, h + k));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - k));

		values[0] = r;
		values[1] = g;
		values[2] = b;
	}

	private float HueToRGB(float p, float q, float h) {
		if (h < 0) {
			h += 1;
		}

		if (h > 1) {
			h -= 1;
		}

		if (6 * h < 1) {
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1) {
			return q;
		}

		if (3 * h < 2) {
			return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
		}

		return p;
	}

	private float[] RGBtoHSL(Color color, float[] values) {
		// Get RGB values in the range 0 - 1
		float r = color.r;
		float g = color.g;
		float b = color.b;

		// Minimum and Maximum RGB values are used in the HSL calculations
		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		// Calculate the Hue
		float h = 0;

		if (max == min) {
			h = 0;
		} else if (max == r) {
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		} else if (max == g) {
			h = (60 * (b - r) / (max - min)) + 120;
		} else if (max == b) {
			h = (60 * (r - g) / (max - min)) + 240;
		}
		// Calculate the Luminance
		float l = (max + min) / 2;

		// Calculate the Saturation
		float s = 0;

		if (max == min) {
			s = 0;
		} else if (l <= .5f) {
			s = (max - min) / (max + min);
		} else {
			s = (max - min) / (2 - max - min);
		}
		values[0] = h / 360f;
		values[1] = s;
		values[2] = l;
		return values;
	}

	private int HSBtoRGB(float hue) {
		int r = 0, g = 0, b = 0;
		float h = (hue - (float) Math.floor(hue)) * 6.0f;
		float f = h - (float) Math.floor(h);
		float p = 0.0f;
		float q = (1.0f - f);
		float t = f;
		switch ((int) h) {
		case 0:
			r = (int) (255.0f + 0.5f);
			g = (int) (t * 255.0f + 0.5f);
			b = (int) (p * 255.0f + 0.5f);
			break;
		case 1:
			r = (int) (q * 255.0f + 0.5f);
			g = (int) (255.0f + 0.5f);
			b = (int) (p * 255.0f + 0.5f);
			break;
		case 2:
			r = (int) (p * 255.0f + 0.5f);
			g = (int) (255.0f + 0.5f);
			b = (int) (t * 255.0f + 0.5f);
			break;
		case 3:
			r = (int) (p * 255.0f + 0.5f);
			g = (int) (q * 255.0f + 0.5f);
			b = (int) (255.0f + 0.5f);
			break;
		case 4:
			r = (int) (t * 255.0f + 0.5f);
			g = (int) (p * 255.0f + 0.5f);
			b = (int) (255.0f + 0.5f);
			break;
		case 5:
			r = (int) (255.0f + 0.5f);
			g = (int) (p * 255.0f + 0.5f);
			b = (int) (q * 255.0f + 0.5f);
			break;
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}
}
