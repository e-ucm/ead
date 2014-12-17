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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class MultiRangeSlider extends AbstractWidget {

	private MultiRangeSliderStyle style;

	private float min, max;
	private float stepSize;

	private Interpolation animateInterpolation = Interpolation.linear;
	private float animateDuration, animateTime;
	private float animateFromValue;

	private float value;
	private float sliderPos;

	private float value2;
	private float sliderPos2;

	private final boolean vertical;

	private float[] snapValues;
	private float threshold;

	public MultiRangeSlider(float min, float max, float stepSize,
			boolean vertical, Skin skin) {
		this(min, max, stepSize, vertical, skin.get("default-"
				+ (vertical ? "vertical" : "horizontal"),
				MultiRangeSliderStyle.class));
	}

	public MultiRangeSlider(float min, float max, float stepSize,
			boolean vertical, Skin skin, String styleName) {
		this(min, max, stepSize, vertical, skin.get(styleName,
				MultiRangeSliderStyle.class));
	}

	public MultiRangeSlider(float min, float max, float stepSize,
			boolean vertical, MultiRangeSliderStyle style) {

		setStyle(style);

		this.min = min; // minimum value of the range of knob
		this.max = max; // maximum value of the range of knob

		this.stepSize = stepSize; // Determines how many steps the knob makes
									// between min and max

		this.vertical = vertical; // Determines if this is a vertical slider or
									// not

		this.value = min;
		this.value2 = max;

		pack();

		addListener(new InputListener() {
			boolean isKnobTouched = false;
			boolean isKnobTouched2 = false;

			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				if (isTouchingKnob(true, x)) {
					calculatePositionAndValue(x, y, true);
					isKnobTouched = true;
				} else {
					isKnobTouched = false;
				}

				if (isTouchingKnob(false, x)) {
					calculatePositionAndValue(x, y, false);
					isKnobTouched2 = true;
				} else {
					isKnobTouched2 = false;
				}

				return true;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				getStage().cancelTouchFocusExcept(this, MultiRangeSlider.this);

				if (isKnobTouched == true) {
					calculatePositionAndValue(x, y, true);
				}
				if (isKnobTouched2 == true) {
					calculatePositionAndValue(x, y, false);
				}
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {

				if (isKnobTouched == true) {
					if (!calculatePositionAndValue(x, y, true)) {
						ChangeEvent changeEvent = Pools
								.obtain(ChangeEvent.class);
						fire(changeEvent);
						Pools.free(changeEvent);
					}
				}

				if (isKnobTouched2 == true) {
					if (!calculatePositionAndValue(x, y, false)) {
						ChangeEvent changeEvent = Pools
								.obtain(ChangeEvent.class);
						fire(changeEvent);
						Pools.free(changeEvent);
					}
				}
			}

		});
	}

	public void setStyle(MultiRangeSliderStyle style) {
		if (style == null)
			throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		invalidateHierarchy();
	}

	/**
	 * Returns the slider's style. Modifying the returned style may not have an
	 * effect until {@link #setStyle(SliderStyle)} is called.
	 */
	public MultiRangeSliderStyle getStyle() {
		return style;
	}

	public void act(float delta) {
		super.act(delta);
		animateTime -= delta;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		final Drawable bg = style.background;
		final Drawable knobBefore = style.knobBefore;
		final Drawable knobAfter = style.knobAfter;
		final Drawable knob2Before = style.knob2Before;
		final Drawable knob2After = style.knob2After;

		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();

		float knobHeight = style.knob.getMinHeight();
		float knobWidth = style.knob.getMinWidth();
		float knob2Height = style.knob2.getMinHeight();
		float knob2Width = style.knob2.getMinWidth();

		float value = getVisualValue();

		float knobSize;
		float knob2Size;
		float sliderPosSize;

		if (vertical) {
			knobSize = knobHeight;
			knob2Size = knob2Height;
			sliderPosSize = height - (bg.getTopHeight() + bg.getBottomHeight());
			bg.draw(batch, x + (int) ((width - bg.getMinWidth()) * 0.5f), y,
					bg.getMinWidth(), height);
		} else {
			knobSize = knobWidth;
			knob2Size = knob2Width;
			sliderPosSize = width - (bg.getLeftWidth() + bg.getRightWidth());
			bg.draw(batch, x, y + (int) ((height - bg.getMinHeight()) * 0.5f),
					width, bg.getMinHeight());
		}

		if (min != max) {
			sliderPos = (value - min) / (max - min)
					* (sliderPosSize - knobSize);
			sliderPos = Math.max(0, sliderPos);
			sliderPos = Math.min(sliderPosSize - knobSize, sliderPos)
					+ bg.getBottomHeight();

			sliderPos2 = (value2 - min) / (max - min)
					* (sliderPosSize - knob2Size);
			sliderPos2 = Math.max(0, sliderPos2);
			sliderPos2 = Math.min(sliderPosSize - knob2Size, sliderPos2)
					+ bg.getBottomHeight();

			sliderPos = Math.min(sliderPos, sliderPos2 - knobSize);
			sliderPos2 = Math.max(sliderPos + knobSize, sliderPos2);
		}

		float knobSizeHalf = knobSize * 0.5f;
		float knobSize2Half = knob2Size * 0.5f;

		// KNOB BEFORE AND AFTER
		if (knobBefore != null) {
			knobBefore
					.draw(batch,
							x,
							y
									+ (int) ((height - knobBefore
											.getMinHeight()) * 0.5f),
							(int) (sliderPos + knobSizeHalf),
							knobBefore.getMinHeight());
		}
		if (knobAfter != null) {
			knobAfter.draw(batch, x + (int) (sliderPos + knobSizeHalf), y
					+ (int) ((height - knobAfter.getMinHeight()) * 0.5f), width
					- (int) (sliderPos + knobSizeHalf),
					knobAfter.getMinHeight());
		}

		style.knob
				.draw(batch, (int) (x + sliderPos),
						(int) (y + (height - knobHeight) * 0.5f), knobWidth,
						knobHeight);

		// KNOB2 BEFORE AND AFTER
		if (knob2Before != null) {
			knob2Before.draw(batch, x,
					y + (int) ((height - knob2Before.getMinHeight()) * 0.5f),
					(int) (sliderPos2 + knobSize2Half),
					knob2Before.getMinHeight());
		}
		if (knob2After != null) {
			knob2After.draw(batch, x + (int) (sliderPos2 + knobSize2Half), y
					+ (int) ((height - knob2After.getMinHeight()) * 0.5f),
					width - (int) (sliderPos2 + knobSize2Half),
					knob2After.getMinHeight());
		}

		style.knob2.draw(batch, (int) (x + sliderPos2),
				(int) (y + (height - knob2Height) * 0.5f), knob2Width,
				knob2Height);
	}

	private boolean calculatePositionAndValue(float x, float y,
			boolean firstKnob) {
		Drawable knob;

		if (firstKnob) {
			knob = style.knob;
		} else {
			knob = style.knob2;
		}

		final Drawable bg = style.background;

		float value;

		float sliderPosition;

		float size;
		float knobSize;
		if (vertical) {
			size = getHeight() - bg.getTopHeight() - bg.getBottomHeight();
			knobSize = knob.getMinHeight();

			sliderPosition = y - bg.getBottomHeight() - knobSize * 0.5f;
		} else {
			size = getWidth() - bg.getLeftWidth() - bg.getRightWidth();
			knobSize = knob.getMinWidth();

			sliderPosition = x - bg.getLeftWidth() - knobSize * 0.5f;
		}

		sliderPosition = Math.max(0, sliderPosition);
		sliderPosition = Math.min(size - knobSize, sliderPosition);

		if (firstKnob) {
			sliderPosition = Math.min(sliderPosition, sliderPos2 - knobSize);
		} else {
			sliderPosition = Math.max(sliderPos + knobSize, sliderPosition);
		}
		value = min + (max - min) * (sliderPosition / (size - knobSize));

		float oldValue = value;
		value = snap(MathUtils.clamp((Math.round(value / stepSize) * stepSize),
				min, max));
		boolean cancelled = true;
		if (value != oldValue) {
			float oldVisualValue = getVisualValue();
			ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
			cancelled = fire(changeEvent);
			if (cancelled)
				value = oldValue;
			else if (animateDuration > 0) {
				animateFromValue = oldVisualValue;
				animateTime = animateDuration;
			}
			Pools.free(changeEvent);
		}

		if (firstKnob) {
			this.value = value;
		} else {
			this.value2 = value;
		}

		return !cancelled;
	}

	private boolean isTouchingKnob(boolean firstKnob, float xpos) {
		Drawable knob;
		float sliderPosition;
		if (firstKnob) {
			knob = style.knob;
			sliderPosition = sliderPos;
		} else {
			knob = style.knob2;
			sliderPosition = sliderPos2;
		}

		if (xpos >= sliderPosition
				&& xpos <= sliderPosition
						+ (vertical ? knob.getMinHeight() : knob.getMinWidth())) {
			return true;
		}

		return false;

	};

	/**
	 * Return the value of the first knob
	 * 
	 * @return
	 */
	public float getValue() {
		return value;
	}

	/**
	 * Return the value of the second knob
	 * 
	 * @return
	 */
	public float getValue2() {
		return value2;
	}

	/**
	 * If {@link #setAnimateDuration(float) animating} the slider value, this
	 * returns the value current displayed.
	 */
	private float getVisualValue() {
		if (animateTime > 0) {
			return animateInterpolation.apply(animateFromValue, value, 1
					- animateTime / animateDuration);
		}
		return value;
	}

	public void setVals(float value, float value2) {
		MathUtils.clamp(value, min, max);
		MathUtils.clamp(value2, min, max);
	}

	/** Sets the step size of the slider */
	public void setStepSize(float stepSize) {
		if (stepSize <= 0)
			throw new IllegalArgumentException("steps must be > 0: " + stepSize);
		this.stepSize = stepSize;
	}

	// GET PREF WIDTH FOR KNOB
	public float getPrefWidth() {
		if (vertical) {
			return Math.max(style.knob.getMinWidth(),
					style.background.getMinWidth());
		} else {
			return style.knob.getMinHeight();
		}
	}

	// GET PREF WIDTH FOR KNOB 2
	public float getPrefWidth2() {
		if (vertical) {
			return Math.max(style.knob2.getMinWidth(),
					style.background.getMinWidth());
		} else {
			return style.knob2.getMinHeight();
		}
	}

	// GET PREF HEIGHT FOR KNOB
	public float getPrefHeight() {
		if (vertical) {
			return style.knob.getMinWidth();
		} else {
			return Math.max(style.knob.getMinHeight(),
					style.background.getMinHeight());
		}
	}

	// GET PREF HEIGHT FOR KNOB2
	public float getPrefHeight2() {
		if (vertical) {
			return style.knob2.getMinWidth();
		} else {
			return Math.max(style.knob2.getMinHeight(),
					style.background.getMinHeight());
		}
	}

	public float getMinValue() {
		return this.min;
	}

	public float getMaxValue() {
		return this.max;
	}

	public float getStepSize() {
		return this.stepSize;
	}

	/**
	 * If > 0, changes to the slider value via {@link #setValue(float)} will
	 * happen over this duration in seconds.
	 */
	public void setAnimateDuration(float duration) {
		this.animateDuration = duration;
	}

	/** Sets the interpolation to use for {@link #setAnimateDuration(float)}. */
	public void setAnimateInterpolation(Interpolation animateInterpolation) {
		if (animateInterpolation == null)
			throw new IllegalArgumentException(
					"animateInterpolation cannot be null.");
		this.animateInterpolation = animateInterpolation;
	}

	/**
	 * Will make this slider snap to the specified values, if the knob is within
	 * the threshold
	 */
	public void setSnapToValues(float[] values, float threshold) {
		this.snapValues = values;
		this.threshold = threshold;
	}

	/** Returns a snapped value, or the original value */
	private float snap(float value) {
		if (snapValues == null)
			return value;
		for (int i = 0; i < snapValues.length; i++) {
			if (Math.abs(value - snapValues[i]) <= threshold)
				return snapValues[i];
		}
		return value;
	}

	static public class MultiRangeSliderStyle {
		/** The slider background, stretched only in one direction. */
		public Drawable background;

		public Drawable knob, knob2;

		/** Optional. */
		public Drawable knobBefore, knobAfter;

		/** Optional. */
		public Drawable knob2Before, knob2After;

		public MultiRangeSliderStyle() {
		}

		public MultiRangeSliderStyle(Drawable background, Drawable knob,
				Drawable knob2) {
			this.background = background;
			this.knob = knob;
			this.knob2 = knob2;
		}

		public MultiRangeSliderStyle(MultiRangeSliderStyle style) {
			this.background = style.background;
			this.knob = style.knob;
			this.knob2 = style.knob2;
		}
	}
}
