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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SlideColorPicker extends Slider {
	private static final int MIN_COLOR = 0;
	private static final int MAX_COLOR = 359;

	private static final float PREF_HEIGHT = .07f;
	private static final float PREF_WIDTH = .29f;

	private static final ChangeListener listener = new ChangeListener() {

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			SlideColorPicker picker = (SlideColorPicker) event
					.getListenerActor();
			picker.updateColor();
		}
	};

	private final Color color = new Color();
	private Texture texture;
	private Pixmap pixmap;

	/**
	 * Create a {@link SlideColorPicker} with default style.
	 * 
	 * @param vertical
	 *            if the slider should be vertical
	 * @param skin
	 *            the skin to use
	 */
	public SlideColorPicker(Skin skin) {
		this(skin, "colorPicker-horizontal");
	}

	/**
	 * Create a {@link SlideColorPicker} with defined style.
	 * 
	 * @param vertical
	 *            if the slider should be vertical
	 * @param skin
	 *            the skin to use
	 * @param styleName
	 *            the style to use
	 */
	public SlideColorPicker(Skin skin, String styleName) {
		super(MIN_COLOR, MAX_COLOR, 1, false, new SliderStyle(skin.get(
				styleName, SliderStyle.class)));
		setValue(60);
		initialize();
		addListener(listener);
	}

	/**
	 * Generate the slider background.
	 */
	private void initialize() {
		pixmap = new Pixmap((int) getPrefWidth(), (int) getPrefHeight(),
				Format.RGBA8888);
		int _colorMin = MIN_COLOR;
		int _colorMax = MAX_COLOR;
		int _colorCount = (_colorMax - _colorMin);
		float _scaleRatio = (float) _colorCount / pixmap.getWidth();

		for (int i = 0; i <= pixmap.getWidth(); i++) {
			int hsbToRGB = (0xff000000 | HSBtoRGB((i * _scaleRatio + _colorMin)
					/ ((float) _colorMax)));

			pixmap.setColor(((hsbToRGB >> 16) & 0xFF) / 256f,
					((hsbToRGB >> 8) & 0xFF) / 256f,
					((hsbToRGB >> 0) & 0xFF) / 256f, 1f);
			pixmap.drawLine(i, 0, i, pixmap.getHeight());
		}

		getStyle().background = new TextureRegionDrawable(new TextureRegion(
				texture = new Texture(pixmap)));
		invalidateHierarchy();
		updateColor();
	}

	private void updateColor() {
		int pixel = pixmap.getPixel(
				MathUtils.round(pixmap.getWidth() * (getValue() / MAX_COLOR)),
				0);
		if (pixel == 0) {
			color.set(0f, 0f, 0f, 1f);
		} else {
			Color.rgba8888ToColor(color, pixel);
		}
		colorChanged(color);
	}

	public void updatePosition(Color color) {
		int threshold = 10000000;
		int rgba8888 = Color.rgba8888(color);
		int minColor = rgba8888 - threshold;
		int maxColor = rgba8888 + threshold;
		int roundI = -1;
		int difference = Integer.MAX_VALUE;
		int pixmapWidth = pixmap.getWidth();
		for (int i = 0; i < pixmapWidth; ++i) {
			int pixel = pixmap.getPixel(i, 0);
			if (pixel == rgba8888) {
				setValue((i / (float) pixmapWidth) * MAX_COLOR);
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
			setValue((roundI / (float) pixmapWidth) * MAX_COLOR);
		}

	}

	protected void colorChanged(Color newColor) {

	}

	public void updateTexture() {
		if (texture != null) {
			texture.draw(pixmap, 0, 0);
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

	@Override
	public void layout() {
		super.layout();
	}

	public Color getPickedColor() {
		return color;
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
