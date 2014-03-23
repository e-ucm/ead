package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;

/**
 * This class is the common ancestor for editor actions and model actions.
 * Provides support for enabled state and listeners associated.
 */
public abstract class Action {

	protected Controller controller;

	private Array<ActionListener> listeners;

	private boolean enabled;

	/**
	 * Creates the action
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 */
	public Action(boolean initialEnable) {
		this.listeners = new Array<ActionListener>();
		enabled = initialEnable;
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
	public abstract boolean validate(Object... args);

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
