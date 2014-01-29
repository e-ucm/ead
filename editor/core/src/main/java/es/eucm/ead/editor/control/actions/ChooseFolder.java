package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.platform.Platform.StringListener;

public class ChooseFolder extends EditorAction {

	public static final String NAME = "chooseFolder";

	public ChooseFolder() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		Platform platform = controller.getPlatform();
		platform.askForFolder((StringListener) args[0]);
	}

}
