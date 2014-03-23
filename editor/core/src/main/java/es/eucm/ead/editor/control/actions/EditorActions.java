package es.eucm.ead.editor.control.actions;

/**
 * Controller for editor actions
 */
public class EditorActions {

	/**
	 * Performs the given action with the given arguments already validated
	 */
	public void perform(EditorAction action, Object... args) {
		action.perform(args);
	}

}
