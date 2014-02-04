package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.commands.AddSceneCommand;

public class AddScene extends EditorAction {

	public static final String NAME = "addScene";

	public AddScene() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.getCommands().command(new AddSceneCommand());
	}
}
