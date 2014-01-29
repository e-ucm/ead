package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.platform.Platform.StringListener;

public class OpenGame extends EditorAction implements StringListener {

	public static final String NAME = "openGame";

	public OpenGame() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.action(ChooseFolder.NAME, this);
	}

	@Override
	public void string(String result) {
		controller.setGamePath(result);
	}
}
