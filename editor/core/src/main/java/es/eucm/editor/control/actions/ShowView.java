package es.eucm.editor.control.actions;

import es.eucm.editor.Editor;

public class ShowView extends EditorAction {
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void perform() {
	}

	@Override
	public void perform(String... args) {
		Editor.viewController.showView(args[0]);
	}
}
