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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.ChangeProjectTitle;
import es.eucm.ead.editor.control.actions.model.ChangeNote;
import es.eucm.ead.editor.control.actions.model.RenameScene;

/**
 * This {@link TextFieldListener} executes it's action when NL('\n') or LE('\r')
 * was pressed. Also invokes {@link Stage#unfocusAll()} to hide the
 * on-screen-keyboard.
 */
public class ActionForTextFieldListener implements TextFieldListener {
	private Controller controller;
	private Class<?> action;
	private Object[] args;
	private TextChangedListener listener;

	public ActionForTextFieldListener(Controller controller, Class<?> action,
			Object... args) {
		this.controller = controller;
		this.action = action;
		this.args = args;
	}

	public ActionForTextFieldListener(TextChangedListener listener,
			Controller controller, Class<?> action, Object... args) {
		this.controller = controller;
		this.listener = listener;
		this.action = action;
		this.args = args;
	}

	@Override
	public void keyTyped(TextField textField, char key) {
		if (key == '\n' || key == '\r') {
			if (this.action.equals(ChangeProjectTitle.class)) {
				final String text = textField.getText();
				this.controller.action(this.action, replaceLineSeparator(text));
			} else if (this.action.equals(RenameScene.class)) {
				final String text = textField.getText();
				this.controller.action(ChangeNote.class, args[0], args[1],
						replaceLineSeparator(text));
			} else {
				this.controller.action(this.action, this.args);
			}
			if (this.listener != null) {
				this.listener.onTextChanged();
			}
			textField.getStage().unfocusAll();
			Gdx.input.setOnscreenKeyboardVisible(false);
		}
	}

	private String replaceLineSeparator(String text) {
		return text.replaceAll("\\r|\\n", "");
	}

	public interface TextChangedListener {

		/**
		 * Invoked when the text has changed.
		 */
		void onTextChanged();
	}

}
