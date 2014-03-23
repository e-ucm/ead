package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.editor.control.Controller;

/**
 * This class is the common ancestor for editor actions and model actions.
 * Provides support for enabled state and listeners associated.
 */
public abstract class Action {

	protected Controller controller;

	private Array<ActionListener> listeners;

	private boolean enabled;

	private Class[] validArguments;

	private boolean allowNullArguments;

	/**
	 * Creates the action
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 * @param allowNullArguments
	 *            if null arguments must be allowed during validation
	 * @param validArguments
	 *            the classes of the expected arguments. Will be check in
	 *            {@link Action#validate(Object...)}
	 */
	public Action(boolean initialEnable, boolean allowNullArguments,
			Class... validArguments) {
		this.validArguments = validArguments;
		this.allowNullArguments = allowNullArguments;
		this.listeners = new Array<ActionListener>();
		enabled = initialEnable;
	}

	/**
	 * Creates the action
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 */
	public Action(boolean initialEnable) {
		this(initialEnable, false);
	}

	/**
	 * Creates the action (initially enabled)
	 */
	public Action() {
		this(true);
	}

	/**
	 * Sets the controller for the action
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Sets whether this action is enabled and can be invoked from the editor
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (ActionListener listener : listeners) {
			listener.enableChanged(getClass(), this.enabled);
		}
	}

	/**
	 * 
	 * @return if this action is enabled and can be invoked by from the editor
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Adds a listener to the action
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * @return if the arguments for the action are valid
	 */
	public boolean validate(Object... args) {
		if (validArguments == null || args == null) {
			return true;
		}

		if (args.length == validArguments.length) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] == null && !allowNullArguments) {
					return false;
				} else if (!ClassReflection.isAssignableFrom(
						args[i].getClass(), validArguments[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * General interface to listen changes in actions' state
	 */
	public interface ActionListener {

		/**
		 * The state of the action changed
		 * 
		 * @param actionClass
		 *            the action class
		 * @param enable
		 *            if the action is enabled
		 */
		void enableChanged(Class actionClass, boolean enable);
	}

}
