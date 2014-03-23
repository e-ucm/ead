package es.eucm.ead.editor.control.actions;

/**
 * This class encapsulates an action, unchained by the user, that executes an
 * operation in the editor, without modifying the model.
 * 
 * Some examples: undo/redo, save, export, etc.
 */
public abstract class EditorAction extends Action {

	protected EditorAction(boolean initialEnable, boolean allowNullArguments,
			Class... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
	}

	/**
	 * Executes the action with the given arguments
	 * 
	 * @param args
	 *            the arguments, previously validated by
	 *            {@link Action#validate(Object...)}
	 */
	public abstract void perform(Object... args);
}
