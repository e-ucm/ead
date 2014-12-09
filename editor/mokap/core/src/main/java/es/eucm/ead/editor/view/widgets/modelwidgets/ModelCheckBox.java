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
package es.eucm.ead.editor.view.widgets.modelwidgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.components.ModelComponent;

public class ModelCheckBox extends Table {

	private CheckBox checkBox;

	private TextField text;

	private DataType input;

	private Object defaultValue;

	private ModelComponent component;

	public enum DataType {
		INT, FLOAT, TEXT, NONE
	}

	public ModelCheckBox(Skin skin, String name, DataType input, Object defVal,
			String textStyle, String checkStyle) {
		super();
		this.defaultValue = defVal;
		this.input = input;

		checkBox = new CheckBox(name, skin, checkStyle);
		text = new TextField(defaultValue.toString(), skin, textStyle) {
			@Override
			public float getPrefWidth() {
				return WidgetBuilder.dpToPixels(48);
			}
		};
		text.setColor(skin.getColor("gray"));

		if (input.equals(DataType.NONE)) {
			text.setVisible(false);
		}

		add(checkBox).expandX().left();
		add(text).padLeft(WidgetBuilder.dpToPixels(16)).padRight(
				WidgetBuilder.dpToPixels(16));

		checkBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (checkBox.isChecked() && component != null) {
					doAction();
				}

			}
		});

		text.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				return super.keyDown(event, keycode);
			}

			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if (checkBox.isChecked() && component != null) {
					doAction();
				}
				text.setText(defaultValue.toString());
				return super.keyTyped(event, character);
			}
		});

	}

	public void actualizeCurrentValue() {
		try {
			if (input.equals(DataType.INT)) {
				defaultValue = Integer.valueOf(text.getText());
			} else if (input.equals(DataType.FLOAT)) {
				defaultValue = Float.valueOf(text.getText());
			} else if (input.equals(DataType.TEXT)) {
				defaultValue = text.getText();
			} else {
				return;
			}
			if (defaultValue instanceof Integer && (Integer) defaultValue < 0) {
				defaultValue = 0;
			} else if (defaultValue instanceof Float
					&& (Float) defaultValue < 0) {
				defaultValue = 0.0f;
			}
		} catch (NumberFormatException e) {

		}
	}

	public Object getCurrentValue() {
		return defaultValue;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void loadComponent(ModelComponent component, Object value) {
		this.component = component;
		text.setText(value.toString());
		actualizeCurrentValue();
		text.setText(defaultValue.toString());
	}

	public ModelComponent getComponent() {
		return component;
	}

	public void doAction() {
		actualizeCurrentValue();
	}
}
