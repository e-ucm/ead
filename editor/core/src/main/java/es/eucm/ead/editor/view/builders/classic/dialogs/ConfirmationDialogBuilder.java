package es.eucm.ead.editor.view.builders.classic.dialogs;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.controllers.DialogController;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.TextArea;
import es.eucm.ead.editor.view.widgets.TextField;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;
import es.eucm.ead.engine.I18N;

/**
 * Created by Javier Torrente on 17/03/14.
 */
public class ConfirmationDialogBuilder implements DialogBuilder {

    private ConfirmationDialogListener listener;

    private DialogController dialogController;

	public static interface ConfirmationDialogListener {
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

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		dialogController = new DialogController(skin);

		TopBottomLayout messageContainer = new TopBottomLayout();
		TextArea text = new TextArea(i18N.m("update.message",i18N.m("general.ok")),skin);
        text.setLineCharacters(200);
		text.setDisabled(true);
        text.setPreferredLines(3);
		messageContainer.addTop(text);
        messageContainer.layout();

		Dialog dialog = dialogController.title(i18N.m("update.title"))
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

    private void buttonActivated(boolean ok){
        listener.dialogClosed(ok);
        dialogController.close();
    }
}
