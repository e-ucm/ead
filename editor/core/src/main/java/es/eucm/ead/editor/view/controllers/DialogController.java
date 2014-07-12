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
package es.eucm.ead.editor.view.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.view.widgets.Dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * There are two constructors: one for creating default dialog (modal and with
 * maximize button )
 * 
 */
public class DialogController {

	private Dialog dialog;

	private Map<String, ButtonInputListener> inputListenerMap;

	/**
	 * Creates the default dialog: modal and with maximize button.
	 */
	public DialogController(Skin skin) {
		this(skin, true, true);
	}

	/**
	 * Creates a modal dialog
	 * 
	 * @param isModal
	 *            sets if the dialog will be modal or not
	 * 
	 * @param hasMaximizer
	 *            sets if the dialog will include maximize button ot not
	 * 
	 */
	public DialogController(Skin skin, boolean isModal, boolean hasMaximizer) {
		dialog = new Dialog(skin, hasMaximizer);
		dialog.setModal(isModal);
		inputListenerMap = new HashMap<String, ButtonInputListener>();
	}

	/**
	 * Sets title of the dialog
	 */
	public DialogController title(String title) {
		dialog.title(title);
		return this;
	}

	/**
	 * Sets content of the dialog
	 */
	public DialogController content(Actor content) {
		dialog.root(content);
		return this;
	}

	/**
	 * Sets the listener for the button with the given text. If the button does
	 * not exist, it is created..
	 */
	public DialogController button(String text,
			DialogButtonListener dialogListener) {
		ButtonInputListener buttonInputListener = inputListenerMap.get(text);
		if (buttonInputListener == null) {
			buttonInputListener = new ButtonInputListener();
			dialog.button(text).addListener(buttonInputListener);
			inputListenerMap.put(text, buttonInputListener);
		}

		buttonInputListener.setDialogButtonListener(dialogListener);
		return this;
	}

	/**
	 * Add a button with the given text. When the button is pressed, it closes
	 * the dialog with no further consequences
	 */
	public void closeButton(String text) {
		button(text, new DialogButtonListener() {
			@Override
			public void selected() {
				hide();
			}
		});
	}

	/**
	 * 
	 Sets whether the dialog is modal
	 */
	public DialogController modal(boolean isModal) {
		dialog.setModal(isModal);
		return this;
	}

	public DialogController clearButtons() {
		inputListenerMap.clear();
		dialog.clearButtons();
		return this;
	}

	public Dialog getDialog() {
		return dialog;
	}

	/**
	 * Hides the dialog
	 */
	public void hide() {
		dialog.hide();
	}

	private class ButtonInputListener extends ClickListener {

		private DialogButtonListener dialogButtonListener;

		public void setDialogButtonListener(
				DialogButtonListener dialogButtonListener) {
			this.dialogButtonListener = dialogButtonListener;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (dialogButtonListener != null) {
				dialogButtonListener.selected();
			}
			hide();
		}
	}

	public interface DialogButtonListener {
		/**
		 * The button in the dialog has been pressed
		 */
		void selected();
	}
}
