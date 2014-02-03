package es.eucm.ead.editor.control.actions;

public class Save extends EditorAction {

	public static final String NAME = "save";

	public Save() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.getModel().save();
	}
}
