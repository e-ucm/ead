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
package es.eucm.ead.editor.components;

import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.EditableLabel;
import es.eucm.ead.engine.components.controls.TextComponent;
import es.eucm.ead.schema.components.controls.Label;

public class EditableLabelComponent extends TextComponent<EditableLabel> {

	private static final int DEFAULT_TAP_COUNT = 1;
	
	private EditableLabel label;

	private Label text;

	private Controller controller;

	public void initialize(Controller controller, Label text) {
		this.controller = controller;
		this.text = text;
	}

	public void setStyle(TextFieldStyle style) {
		if (label == null) {
			label = new EditableLabel(text.getText(), style, DEFAULT_TAP_COUNT);
			label.initLabelListener(controller, text);
		} else {
			label.setStyle(style);
		}
	}

	@Override
	protected void updateText(String newText) {
		label.setText(newText);
		label.setSize(label.getPrefWidth(), label.getPrefHeight());
	}

	@Override
	public EditableLabel getControl() {
		return label;
	}
}
