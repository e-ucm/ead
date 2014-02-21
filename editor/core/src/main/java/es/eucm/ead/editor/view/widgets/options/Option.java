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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

public class Option extends AbstractWidget {

	private OptionStyle style;

	private Label title;

	private Image tooltipButton;

	private Label tooltipText;

	private Actor option;

	private float leftWidth;

	public Option(OptionStyle style) {
		this(null, null, null, style);
	}

	public Option(String label, String tooltip, Actor option, OptionStyle style) {
		this.style = style;
		if (label != null) {
			label(label);
		}

		if (tooltip != null) {
			tooltip(tooltip);
		}

		if (option != null) {
			option(option);
		}
	}

	public void setLeftWidth(float leftWidth) {
		this.leftWidth = leftWidth;
	}

	public Option label(String label) {
		title = new Label(label, new LabelStyle(style.font, style.fontColor));
		addActor(title);
		return this;
	}

	public Option tooltip(String text) {
		tooltipButton = new Image(style.tooltipIcon);
		addActor(tooltipButton);

		LabelStyle tooltipStyle = new LabelStyle();
		tooltipStyle.fontColor = style.tooltipFontColor == null ? style.fontColor
				: style.tooltipFontColor;
		tooltipStyle.font = style.tooltipFont == null ? style.font
				: style.tooltipFont;
		tooltipStyle.background = style.tooltipBackground;

		tooltipText = new Label(text, tooltipStyle);

		tooltipButton.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				Option.this.addActor(tooltipText);
				Option.this.toFront();
				tooltipText.toFront();
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer,
					Actor toActor) {
				tooltipText.remove();
			}
		});
		return this;
	}

	public Option option(Actor option) {
		this.option = option;
		addActor(option);
		return this;
	}

	public float getLeftPrefWidth() {
		return title.getPrefWidth()
				+ (tooltipButton == null ? 0 : tooltipButton.getPrefWidth())
				+ style.pad * 2;
	}

	@Override
	public float getPrefWidth() {
		return getChildrenTotalWidth() + style.pad * 4;
	}

	@Override
	public float getPrefHeight() {
		return Math.max(title.getPrefHeight(), getPrefHeight(option))
				+ style.pad * 2;
	}

	@Override
	public void layout() {
		// Left side
		// Title
		float width = title.getPrefWidth();
		float x = leftWidth - width - style.pad;
		float height = title.getPrefHeight();
		float y = (getHeight() - height) / 2.0f;
		title.setBounds(x, y - title.getStyle().font.getDescent() / 1.5f,
				width, height);

		// Tooltip
		if (tooltipButton != null) {
			width = tooltipButton.getPrefWidth();
			x = leftWidth - width - title.getWidth() - style.pad;
			height = tooltipButton.getPrefHeight();
			y = (getHeight() - height) / 2.0f;
			tooltipButton.setBounds(x, y, width, height);

			tooltipText.setPosition(x, y + height);
		}
		// Option
		width = Math.max(getWidth() - leftWidth, getPrefWidth(option))
				- style.pad * 2;
		x = leftWidth + style.pad;
		height = getPrefHeight(option);
		y = (getHeight() - height) / 2.0f;
		option.setBounds(x, y, width, height);
	}

	public float getRightPrefWidth() {
		return getPrefWidth(option);
	}

	public static class OptionStyle {
		public Drawable tooltipIcon;
		public Drawable tooltipBackground;
		public BitmapFont font, tooltipFont;
		public Color fontColor, tooltipFontColor;
		public float pad = 5.0f;
	}

}
