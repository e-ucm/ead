package es.eucm.ead.editor.control.actions;

public class EditScene extends EditorAction {

	public static final String NAME = "editScene";

	public EditScene() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.getModel().editScene(args[0].toString());
	}
}
