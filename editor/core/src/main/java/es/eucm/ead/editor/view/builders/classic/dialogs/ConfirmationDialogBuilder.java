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
package es.eucm.ead.editor.view.builders.classic.dialogs;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.TextArea;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;
import es.eucm.ead.engine.I18N;

/**
 * Creates a basic confirmation dialog with two options: OK and Cancel. This
 * dialog expects at least three arguments:
 * 
 * 0) The string with the title for the dialog.
 * 
 * 1) The string with the message describing the operation the user has to
 * accept or deny.
 *
 * 2) A
 * {@link es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder.ConfirmationDialogClosedListener}
 * , that is notified on the user's decision (OK or Cancel)
 *
 * Optionally, the dialog accepts two more arguments that, if present, make
 * the builder add a checkbox after the dialog message. This comes handy to
 * add common "Don't show again" or "Remember this choice" features. In that case,
 * the dialog would have arguments 3 and 4:
 *
 * 3) A {@link es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder.ConfirmationDialogCheckboxListener}
 * that gets notified when the checkbox is marked or unnmarked.
 *
 * 4) The String with the text for the checkbox.
 *
 * Created by Javier Torrente on 17/03/14.
 */
public class ConfirmationDialogBuilder implements DialogBuilder {

	// The listener that is notified after user's decision (args[0])
	private ConfirmationDialogClosedListener closedListener;
    // The listener that is notified after the checkbox changes. May be null if the checkbox is not shown
    private ConfirmationDialogCheckboxListener checkboxListener;

	private DialogController dialogController;

	/**
	 * The callback that is invoked after the user accepts or denies the
	 * operation presented by this dialog. An object of type
	 * {@link es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder.ConfirmationDialogClosedListener}
	 * should be passed to this dialog as an argument when it is built.
	 */
	public static interface ConfirmationDialogClosedListener {
		/**
		 * @param accepted
		 *            True if the user accepted the operation, false otherwise
		 */
		public void dialogClosed(boolean accepted);
	}

    /**
     * This optional callback is invoked when the user marks or unmarks
     * the checkbox associated to this dialog. This only happens if the
     * checkbox is actually built - a situation that only happens when
     * the dialog receives a valid {@link es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder.ConfirmationDialogCheckboxListener}
     * as an argument when it is built.
     */
    public static interface ConfirmationDialogCheckboxListener {
        /**
         * @param marked
         *            True if the user accepted the operation, false otherwise
         */
        public void checkboxChanged(boolean marked);
    }

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public Dialog build(Controller controller, Object... arguments) {
        // First argument is the title of the dialog
        String dialogTitle = (String) arguments[0];
        // Second and last argument is the body of the dialog
        String dialogMessage = (String) arguments[1];

        // Third argument should be a ConfirmationDialogClosedListener
		closedListener = (ConfirmationDialogClosedListener) arguments[2];

        // Fourth argument is optional. If present, should be a ConfirmationDialogClosedListener,
        // meaning a checkbox should be added to the dialog.
        String checkboxText = null;
        if (arguments.length > 3){
            checkboxListener = (ConfirmationDialogCheckboxListener)arguments[3];
            //Fifth argument is also optional: the text for the checkbox
            checkboxText = (String)arguments[4];
        }

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		dialogController = new DialogController(skin);

		TopBottomLayout messageContainer = new TopBottomLayout();
		TextArea text = new TextArea(dialogMessage, skin);
		text.setLineCharacters(200);
		text.setDisabled(true);
		text.setPreferredLines(3);
		messageContainer.addTop(text);

        // If required, add a checkbox
        if (checkboxListener != null){
            final CheckBox checkBox = new CheckBox(checkboxText,skin);
            checkBox.addListener(new EventListener() {
                @Override
                public boolean handle(Event event) {
                    checkboxListener.checkboxChanged(checkBox.isChecked());
                    return true;
                }
            });
            messageContainer.addTop(checkBox);
        }

		messageContainer.layout();

		Dialog dialog = dialogController.title(dialogTitle)
				.root(messageContainer).getDialog();

		dialogController.closeButton(i18N.m("general.cancel"),
				new DialogController.DialogButtonListener() {
					@Override
					public void selected() {
						buttonActivated(false);
					}
				});
		dialogController.button(i18N.m("general.ok"), true,
				new DialogController.DialogButtonListener() {
					@Override
					public void selected() {
						buttonActivated(true);
					}
				});

		return dialog;

	}

	/**
	 * Invoked when either cancel or OK are pressed
	 * 
	 * @param ok
	 */
	private void buttonActivated(boolean ok) {
		closedListener.dialogClosed(ok);
		dialogController.close();
	}
}
