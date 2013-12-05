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
package es.eucm.ead.editor.gdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Value;

public class Spinner extends Table {

	private SpinnerStyle style;

	private TextField textField;

	private Button plusButton;

	private Button minusButton;

	private int step = 1;

	public Spinner(Skin skin) {
		this(skin.get(SpinnerStyle.class));
	}

	public Spinner(Skin skin, String styleName) {
		this(skin.get(styleName, SpinnerStyle.class));
	}

	public Spinner(SpinnerStyle style) {
		this.style = style;
		clearChildren();
		Table textFieldT = new Table();
		textFieldT.debug();
		add(textField = new TextField("", style)).width(
				Value.percentWidth(1.0f));
		Table table = new Table();
		table.debug();

		ButtonStyle plusStyle = new ButtonStyle();
		plusStyle.up = style.plusUp;
		plusStyle.down = style.plusDown;
		plusStyle.checked = style.plusChecked;
		plusStyle.disabled = style.plusDisabled;
		plusStyle.over = style.plusOver;

		ButtonStyle minusStyle = new ButtonStyle();
		minusStyle.up = style.minusUp;
		minusStyle.down = style.minusDown;
		minusStyle.checked = style.minusChecked;
		minusStyle.disabled = style.minusDisabled;
		minusStyle.over = style.minusOver;

		plusButton = new Button(plusStyle);
		plusButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				add(step);
				return false;
			}
		});
		minusButton = new Button(minusStyle);
		minusButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				add(-step);
				return false;
			}
		});

		table.add(plusButton).top().expand();
		table.row();
		table.add(minusButton).bottom().expand();
		add(table).expand().right();
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public void setStyle(SpinnerStyle style) {
		this.style = style;
		ButtonStyle plusStyle = plusButton.getStyle();
		plusStyle.up = style.plusUp;
		plusStyle.down = style.plusDown;
		plusStyle.checked = style.plusChecked;
		plusStyle.disabled = style.plusDisabled;
		plusStyle.over = style.plusOver;

		ButtonStyle minusStyle = minusButton.getStyle();
		minusStyle.up = style.minusUp;
		minusStyle.down = style.minusDown;
		minusStyle.checked = style.minusChecked;
		minusStyle.disabled = style.minusDisabled;
		minusStyle.over = style.minusOver;

		textField.setStyle(style);
	}

	public float getPrefWidth() {
		float width = super.getPrefWidth();
		width = Math.max(width, textField.getPrefWidth());
		if (style.background != null)
			width = Math.max(width, style.background.getMinWidth());
		return width;
	}

	public float getPrefHeight() {
		float height = super.getPrefHeight();
		height = Math.max(height, textField.getPrefHeight());
		if (style.background != null)
			height = Math.max(height, style.background.getMinHeight());
		return height;
	}

	public void add(int step) {
		try {
			int value = Integer.parseInt(textField.getText());
			value += step;
			textField.setText(value + "");
		} catch (NumberFormatException e) {
			textField.setText("0");
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	public String getText() {
		return textField.getText();
	}

	public void setText(String text) {
		this.textField.setText(text);
	}


	static public class SpinnerStyle extends TextFieldStyle {

		public Drawable plusUp;
		public Drawable plusDown;
		public Drawable plusChecked;
		public Drawable plusDisabled;
		public Drawable plusOver;
		public Drawable minusUp;
		public Drawable minusDown;
		public Drawable minusChecked;
		public Drawable minusDisabled;
		public Drawable minusOver;
	}
}
