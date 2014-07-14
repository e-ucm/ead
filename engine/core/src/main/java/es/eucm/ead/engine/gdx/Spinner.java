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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Pools;

public class Spinner extends Table {

	private TextField textField;

	private Button plusButton;

	private Button minusButton;

	private float step = 1;

	private float currentValue;

	public Spinner(Skin skin, float step) {
		this(skin.get(SpinnerStyle.class), step);
	}

	public Spinner(Skin skin, String styleName, float step) {
		this(skin.get(styleName, SpinnerStyle.class), step);
	}

	public Spinner(SpinnerStyle style, float spinnterStep) {
		this.step = spinnterStep;
		add(textField = new TextField("", style));
		setValue(0);

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
				setValue(currentValue + step);
				fireChange();
				return false;
			}
		});
		minusButton = new Button(minusStyle);
		minusButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				setValue(currentValue - step);
				fireChange();
				return false;
			}
		});

		textField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor,
					boolean focused) {
				if (!focused) {
					try {
						setValue(Float.parseFloat(textField.getText()));
						fireChange();
					} catch (NumberFormatException e) {
						setValue(currentValue);
					}
				}
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

	private void fireChange() {
		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	public void setValue(Number number) {
		currentValue = number.floatValue();
		textField.setText(number.toString());
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

	/**
	 * @return the current value in the spinner
	 */
	public float getValue() {
		return currentValue;
	}

	static public class SpinnerStyle extends TextFieldStyle {
		public Drawable plusUp;
		public Drawable plusDown;
		public Drawable minusUp;
		public Drawable minusDown;
	}
}
