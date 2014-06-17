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
package es.eucm.ead.engine.gdx;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Spinner extends Table {

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
		add(textField = new TextField("", style));

		ButtonStyle plusStyle = new ButtonStyle();
		plusStyle.up = style.plusUp;
		plusStyle.down = style.plusDown;

		ButtonStyle minusStyle = new ButtonStyle();
		minusStyle.up = style.minusUp;
		minusStyle.down = style.minusDown;

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

		Table buttons = new Table();
		buttons.add(plusButton);
		buttons.row();
		buttons.add(minusButton);
		add(buttons);
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public void setStyle(SpinnerStyle style) {
		ButtonStyle plusStyle = plusButton.getStyle();
		plusStyle.up = style.plusUp;
		plusStyle.down = style.plusDown;

		ButtonStyle minusStyle = minusButton.getStyle();
		minusStyle.up = style.minusUp;
		minusStyle.down = style.minusDown;

		textField.setStyle(style);
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

	public String getText() {
		return textField.getText();
	}

	public void setText(String text) {
		this.textField.setText(text);
	}

	static public class SpinnerStyle extends TextFieldStyle {
		public Drawable plusUp;
		public Drawable plusDown;
		public Drawable minusUp;
		public Drawable minusDown;
	}
}
