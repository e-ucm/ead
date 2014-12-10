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

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.components.ModelComponent;

public class ModelTextField extends TextField {

	private ModelComponent component;

	private DataType inputType;

	public enum DataType {
		INT, FLOAT, TEXT
	}

	public ModelTextField(Skin skin, String style, String color, DataType type) {
		super("0", skin, style);
		setColor(skin.getColor(color));

		this.inputType = type;
	}

	public float getPrefWidth() {
		return WidgetBuilder.dpToPixels(48);
	}

	public void updateCurrentValue() {
		Object value = 0;
		try {
			if (inputType.equals(DataType.INT)) {
				value = Integer.valueOf(getText());
			} else if (inputType.equals(DataType.FLOAT)) {
				value = Float.valueOf(getText());
			} else if (inputType.equals(DataType.TEXT)) {
				value = getText();
			} else {
				return;
			}
			if (value instanceof Integer && (Integer) value < 0) {
				value = 0;
			} else if (value instanceof Float && (Float) value < 0) {
				value = 0.0f;
			}
		} catch (NumberFormatException e) {

		}
		setText(value.toString());
	}

	public void loadComponent(ModelComponent component, Object val) {
		this.component = component;
		setText(val.toString());
		updateCurrentValue();
	}

	public ModelComponent getComponent() {
		return component;
	}
}
