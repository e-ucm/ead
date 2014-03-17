package es.eucm.ead.editor.view.builders.classic.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.TextField;
import es.eucm.ead.engine.I18N;

/**
 * Created by Javier Torrente on 17/03/14.
 */
public class ConfirmationDialogBuilder implements DialogBuilder {

    private ConfirmationDialogListener listener;

    public static interface ConfirmationDialogListener{
        public void dialogClosed(boolean accepted);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Dialog build(Controller controller, Object... arguments) {

        // First argument should be a ConfirmationDialogListener
        listener = (ConfirmationDialogListener)arguments[0];

        Skin skin = controller.getEditorAssets().getSkin();
        I18N i18N = controller.getEditorAssets().getI18N();
        final DialogController dialogController = new DialogController(skin);

        AbstractWidget messageContainer = new AbstractWidget();
        TextField text = new TextField("Would you like to update", skin);
        text.setDisabled(true);
        messageContainer.addActor(text);

        final Dialog dialog = dialogController.title(i18N.m("update.title"))
                .root(messageContainer).getDialog();

        dialogController.closeButton(i18N.m("general.cancel"), new DialogController.DialogButtonListener() {
            @Override
            public void selected() {
                listener.dialogClosed(false);
                dialog.remove();
            }
        });
        dialogController.button(i18N.m("general.ok"), true,
                new DialogController.DialogButtonListener() {
                    @Override
                    public void selected() {
                        listener.dialogClosed(true);
                        dialog.remove();
                    }
                });
        return dialog;

    }
}
