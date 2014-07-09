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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.tooltips.Tooltip;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

/**
 * Represents a row in an {@link OptionsPanel}. Contains a label, an optional
 * tooltip (in the left column) and the option widget itself (in the right
 * column).
 */
public class Option extends AbstractWidget {

	private OptionStyle style;

	private Label title;

	private ImageTooltip tooltipButton;

	private Label errorMessage;

	private Actor optionWidget;

	private float leftWidth;
	private boolean valid;

	/**
	 * Creates an empty option
	 * 
	 * @param style
	 *            the style for the option
	 */
	public Option(OptionStyle style) {
		this(null, null, null, style);
	}

	/**
	 * Creates an option
	 * 
	 * @param label
	 *            the label for the option (can be null)
	 * @param tooltip
	 *            the tooltip for the option (can be null)
	 * @param optionWidget
	 *            the option widget (can be null)
	 * @param style
	 *            the style for the option
	 */
	public Option(String label, String tooltip, Actor optionWidget,
			OptionStyle style) {
		this.style = style;

		if (label != null) {
			label(label);
		}

		if (tooltip != null) {
			tooltip(tooltip);
		}

		if (optionWidget != null) {
			option(optionWidget);
		}
	}

	/**
	 * 
	 * @return if the option is valid in the current state
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            Sets if the option is valid in the current state. Also
	 *            shows/hide the error message
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
		if (errorMessage != null) {
			if (valid) {
				errorMessage.remove();
			} else {
				addActor(errorMessage);
			}
		}
	}

	/**
	 * 
	 * @return the option widget
	 */
	public Actor getOptionWidget() {
		return optionWidget;
	}

	/**
	 * Sets the text label for the option
	 * 
	 * @param label
	 *            the text for the label
	 * @return this option
	 */
	public Option label(String label) {
		if (title == null) {
			title = new Label(label,
					new LabelStyle(style.font, style.fontColor));
			addActor(title);
		}
		title.setText(label);
		return this;
	}

	/**
	 * Sets the tooltip for the option
	 * 
	 * @param text
	 *            the text showed in the tooltip
	 * @return this option
	 */
	public Option tooltip(String text) {
		if (tooltipButton == null) {
			tooltipButton = new ImageTooltip(style.tooltipIcon);
			addActor(tooltipButton);
		}
		tooltipButton.setTooltip(text);
		return this;
	}

	public Option errorMessage(String message) {
		if (errorMessage == null) {
			LabelStyle errorStyle = new LabelStyle(
					style.errorFont == null ? style.font : style.errorFont,
					style.errorFontColor == null ? style.fontColor
							: style.errorFontColor);
			errorMessage = new Label(message, errorStyle);
			addActor(errorMessage);
		}
		errorMessage.setText(message);
		return this;
	}

	/**
	 * Sets the option widget for the option
	 * 
	 * @param option
	 *            the widget
	 * @return this option
	 */
	public Option option(Actor option) {
		if (this.optionWidget != null) {
			optionWidget.remove();
		}
		this.optionWidget = option;
		addActor(option);
		return this;
	}

	/**
	 * Sets the width for the left column of the option
	 * 
	 * @param leftWidth
	 *            the width
	 */
	void setLeftWidth(float leftWidth) {
		this.leftWidth = leftWidth;
	}

	/**
	 * @return Returns the preferred width for the left column of this option
	 */
	float getLeftPrefWidth() {
		return title.getPrefWidth()
				+ (tooltipButton == null ? 0 : tooltipButton.getPrefWidth())
				+ style.pad + style.margin / 2.0f;
	}

	public float getRightPrefWidth() {
		return getPrefWidth(optionWidget) + style.pad + style.margin / 2.0f;
	}

	@Override
	public float getPrefWidth() {
		return getLeftPrefWidth() + getRightPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		return Math.max(title.getPrefHeight(), getPrefHeight(optionWidget))
				+ style.pad * 2;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (!isValid()) {
			style.invalidBackground.draw(batch, getX(), getY(), getWidth(),
					getHeight());
		}
		super.draw(batch, parentAlpha);
	}

	@Override
	public void layout() {
		// Left side
		// Title
		float width = title.getPrefWidth();
		float x = leftWidth - width;
		float height = title.getPrefHeight();
		float y = (getHeight() - height) / 2.0f;
		setBounds(title, x, y, width, height);

		// Tooltip
		if (tooltipButton != null) {
			width = tooltipButton.getPrefWidth();
			x = leftWidth - width - title.getWidth();
			height = tooltipButton.getPrefHeight();
			y = (getHeight() - height) / 2.0f;
			setBounds(tooltipButton, x, y, width, height);
		}
		// Option
		width = Math.max(getWidth() - leftWidth - style.pad - style.margin
				/ 2.0f, getPrefWidth(optionWidget));
		x = leftWidth + style.margin / 2.0f;
		height = getPrefHeight(optionWidget);
		y = (getHeight() - height) / 2.0f;
		optionWidget.setBounds(x, y, width, height);
		// Error
		if (errorMessage != null) {
			errorMessage.setPosition(leftWidth + style.margin / 2.0f, 0);
		}
	}

	public static class OptionStyle {
		/**
		 * Image for the tooltip icon
		 */
		public Drawable tooltipIcon;

		/**
		 * Background drawn when the option is invalid
		 */
		public Drawable invalidBackground;

		public BitmapFont font, errorFont;
		public Color fontColor, errorFontColor;

		/**
		 * f* Padding of the option as a row
		 */
		public float pad = 10.0f;

		/**
		 * Margin between the label and the option widget
		 */
		public float margin = 10.0f;
	}

	public class ImageTooltip extends Image implements Tooltip {

		private String tooltip;

		public ImageTooltip(Drawable drawable) {
			super(drawable);
		}

		public void setTooltip(String tooltip) {
			this.tooltip = tooltip;
		}

		@Override
		public String getTooltip() {
			return tooltip;
		}

		@Override
		public float getXOffset() {
			return 0;
		}

		@Override
		public float getYOffset() {
			return 0;
		}
	}

}
