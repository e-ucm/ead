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
package es.eucm.ead.editor.view.listeners;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeProjectTitle;

public class ActionForTextFieldListener implements TextFieldListener {

	private Controller controller;
	private Class action;
	private Object[] args;
	private TextChangedListener listener;
	private TextField target;

	public ActionForTextFieldListener(TextField target, Controller controller,
			Class action, Object... args) {
		this.controller = controller;
		this.target = target;
		this.action = action;
		this.args = args;
	}

	public ActionForTextFieldListener(TextField target,
			TextChangedListener listener, Controller controller, Class action,
			Object... args) {
		this.controller = controller;
		this.listener = listener;
		this.target = target;
		this.action = action;
		this.args = args;
	}

	@Override
	public void keyTyped(TextField textField, char key) {
		if (key == '\n' || key == '\r') {
			if (this.action.equals(ChangeProjectTitle.class)) {
				this.controller.action(this.action, this.target.getText());
			} else {
				this.controller.action(this.action, this.args);
			}
			if (this.listener != null) {
				this.listener.onTextChanged();
			}
		}
	}

	public interface TextChangedListener {

		/**
		 * Invoked when the text has changed.
		 */
		void onTextChanged();
	}

}
