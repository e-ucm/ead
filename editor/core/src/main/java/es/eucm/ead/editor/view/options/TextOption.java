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
package es.eucm.ead.editor.view.options;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.control.Command;
import es.eucm.ead.editor.control.commands.ChangeFieldCommand;
import es.eucm.ead.editor.model.DependencyNode;

public class TextOption extends AbstractOption<String> {

	protected TextField textField;
	protected int minWidth = 100;

	public TextOption(String title, String toolTipText, DependencyNode... nodes) {
		super(title, toolTipText, nodes);
	}

	@Override
	public String getControlValue() {
		return textField.getText();
	}

	@Override
	public void setControlValue(String newValue) {
		textField.setText(newValue);
	}

	@Override
	public Actor createControl() {
		textField = new TextField("", skin);
		textField.setText(accessor.read());
		textField.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				update();
				return true;
			}
		});
		return textField;
	}

	@Override
	protected void decorate(boolean valid) {
		String sytleName = valid ? "default" : "invalid";
		TextFieldStyle style = Editor.assets.getSkin().get(sytleName,
				TextFieldStyle.class);
		// textField.setStyle(style);
	}

	@Override
	protected Command createUpdateCommand() {
		// Users expect to undo/redo entire words, rather than
		// character-by-character
		return new ChangeFieldCommand<String>(getControlValue(), accessor,
				changed) {
			@Override
			public boolean likesToCombine(String nextValue) {
				return nextValue.startsWith(newValue)
						&& nextValue.length() == newValue.length() + 1
						&& !Character.isWhitespace(nextValue.charAt(nextValue
								.length() - 1));
			}
		};
	}
}
