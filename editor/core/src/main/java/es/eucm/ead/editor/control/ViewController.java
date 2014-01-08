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
package es.eucm.ead.editor.control;

import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.view.dialogs.MessageDialog;
import es.eucm.ead.editor.view.dialogs.OptionsDialog;
import es.eucm.ead.editor.view.dialogs.OptionsDialog.DialogListener;
import es.eucm.ead.editor.view.options.OptionsPanel;

/**
 * Class controlling all the views in the editor
 */
public class ViewController {

	private MessageDialog messageDialog;

	private OptionsDialog optionsDialog;

	public ViewController() {
		messageDialog = new MessageDialog(Editor.assets.getSkin());
		optionsDialog = new OptionsDialog(Editor.assets.getSkin());
	}

	/**
	 * Shows an info dialog. If there's already one showing, the message is
	 * added as a new line in the dialog
	 * 
	 * @param message
	 *            the i18n key of the message
	 */
	public void showInfo(String message) {
		messageDialog.showMessage(MessageDialog.info, message);
	}

	/**
	 * Shows a warning dialog. If there's already one showing, the message is
	 * added as a new line in the dialog
	 * 
	 * @param message
	 *            the i18n key of the message
	 */
	public void showWarning(String message) {
		messageDialog.showMessage(MessageDialog.warning, message);
	}

	/**
	 * Shows an error dialog. If there's already one showing, the message is
	 * added as a new line in the dialog
	 * 
	 * @param message
	 *            the i18n key of the message
	 */
	public void showError(String message) {
		messageDialog.showMessage(MessageDialog.error, message);
	}

	/**
	 * Shows in a modal dialog the given options panel
	 * 
	 * @param optionsPanel
	 *            the options panel to show
	 * @param dialogListener
	 *            a listener that will transmit the button pressed (represented
	 *            by one of the keys passed in buttonsKey) in the dialog
	 * @param buttonsKey
	 *            the i18n keys for the buttons of the dialog. Each key creates
	 *            a button that closes the dialog
	 */
	public void showOptionsDialog(OptionsPanel optionsPanel,
			DialogListener dialogListener, String... buttonsKey) {
		optionsDialog.show(optionsPanel, dialogListener, buttonsKey);
	}

}
