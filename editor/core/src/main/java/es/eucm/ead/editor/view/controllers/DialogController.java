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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import es.eucm.ead.editor.view.widgets.Dialog;

/**
 * There are two constructors: one for creating default dialog (modal and with
 * maximize button )
 * 
 */
public class DialogController {

	private Dialog dialog;

	/**
	 * Creates the default dialog: modal and with maximize button.
	 * 
	 * @param skin
	 */
	public DialogController(Skin skin) {
		dialog = new Dialog(skin);
	}

	/**
	 * Creates a modal dialog
	 * 
	 * @param skin
	 * 
	 * @param isModal
	 *            sets if the dialog will be modal or not
	 * 
	 * @param maximizable
	 *            sets if the dialog will include maximize button ot not
	 * 
	 */
	public DialogController(Skin skin, boolean isModal, boolean hasMaximizer) {
		dialog = new Dialog(skin, hasMaximizer);
		dialog.setModal(isModal);
	}

	public DialogController title(String title) {
		dialog.title(title);
		return this;
	}

	public DialogController root(WidgetGroup root) {
		dialog.root(root);
		return this;
	}

	public Dialog getDialog() {
		return dialog;
	}

	public DialogController button(String text, boolean main,
			final DialogButtonListener dialogListener) {
		dialog.button(text, main).addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				dialogListener.selected();
				return true;
			}
		});
		return this;
	}

	public void closeButton(String text) {
		closeButton(text, new DialogButtonListener() {
			@Override
			public void selected() {
				close();
			}
		});
	}

	public void closeButton(String text, DialogButtonListener listener) {
		button(text, false, listener);
	}

	public void close() {
		dialog.remove();
	}

	public void setModal(boolean isModal) {
		dialog.setModal(isModal);
	}

	public boolean isModal() {
		return dialog.isModal();
	}

	public boolean hasMaximizer() {
		return dialog.isMaximizable();
	}

	public interface DialogButtonListener {
		// FIXME This needs a new name, or doc, or both!!
		void selected();
	}
}
