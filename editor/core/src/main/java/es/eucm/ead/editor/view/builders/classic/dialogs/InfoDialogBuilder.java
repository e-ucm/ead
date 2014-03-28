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
 * Created by Angel-E-UCM on 28/03/14.
 */
public class InfoDialogBuilder implements DialogBuilder{


    private DialogController dialogController;

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Dialog build(Controller controller, Object... arguments) {

        // First argument is the title of the dialog
       // String dialogTitle = (String) arguments[0];
        // Second and last argument is the body of the dialog
        String dialogMessage = (String) arguments[0];

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

        /*if (checkboxListener != null) {
            final CheckBox checkBox = new CheckBox(checkboxText, skin);
            checkBox.addListener(new EventListener() {
                @Override
                public boolean handle(Event event) {
                    checkboxListener.checkboxChanged(checkBox.isChecked());
                    return true;
                }
            });
            messageContainer.addTop(checkBox);
        }*/

        messageContainer.layout();

        Dialog dialog = dialogController.title("")
                .root(messageContainer).getDialog();

        /*dialogController.button(i18N.m("general.ok"), true,
                new DialogController.DialogButtonListener() {
                    @Override
                    public void selected() {
                        buttonActivated(true);
                    }
                });
        dialogController.closeButton(i18N.m("general.cancel"),
                new DialogController.DialogButtonListener() {
                    @Override
                    public void selected() {
                        buttonActivated(false);
                    }
                });*/

        return dialog;
    }
}
