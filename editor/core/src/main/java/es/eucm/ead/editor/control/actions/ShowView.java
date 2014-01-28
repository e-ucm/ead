package es.eucm.ead.editor.control.actions;

public class ShowView extends EditorAction {

	public static final String NAME = "showview";

	public ShowView() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.view((String) args[0]);
	}
}
