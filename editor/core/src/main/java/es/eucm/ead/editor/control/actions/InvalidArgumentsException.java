package es.eucm.ead.editor.control.actions;

/**
 * Exception thrown when the editor tries to execute an action with invalid
 * arguments
 */
public class InvalidArgumentsException extends Exception {

	private Class actionClass;

	private Object[] args;

	public InvalidArgumentsException(Class actionClass, Object... args) {
		this.actionClass = actionClass;
		this.args = args;
	}

	/**
	 * @return the action tried to perform
	 */
	public Class getActionClass() {
		return actionClass;
	}

	/**
	 * @return the invalid arguments
	 */
	public Object[] getArgs() {
		return args;
	}
}
