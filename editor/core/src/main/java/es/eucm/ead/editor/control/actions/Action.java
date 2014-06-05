/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionListener;

/**
 * This class is the common ancestor for editor actions and model actions.
 * Provides support for enabled state and listeners associated.
 * 
 * The {@link Action#validate(Object...)} checks the validity of the arguments
 * passed every time the {@link Action} is invoked. This validation covers those
 * cases where the number of arguments is constant (could include different
 * possibilities, but all possibilities has a constant number of attributes).
 * 
 * For this reason, if the action holds a variable number of arguments (i.e. the
 * action could be properly called with 1, 2, or 3 arguments) the method
 * {@link Action#validate(Object...)} should be overridden including each the
 * particularities of the number/class of the arguments.
 */
public abstract class Action {

	protected Controller controller;

	private Array<ActionListener> listeners;

	private boolean enabled;

	private Class[][] validArguments;

	private boolean allowNullArguments;

	/**
	 * Creates the action with only one valid arguments possibilities
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 * @param allowNullArguments
	 *            if null arguments must be allowed during validation
	 * @param validArguments
	 *            the classes of the expected argument. Will be checked in
	 *            {@link Action#validate(Object...)}
	 */
	public Action(boolean initialEnable, boolean allowNullArguments,
			Class... validArguments) {
		this(initialEnable, allowNullArguments,
				new Class[][] { validArguments });
	}

	/**
	 * Creates the action waiting for a two-dimensional array as parameter.
	 * 
	 * This kind of parameter is only use when the {@link Action} will include
	 * more than one valid arguments possibilities
	 * 
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 * @param allowNullArguments
	 *            if null arguments must be allowed during validation
	 * @param validArguments
	 *            array of the classes of the expected arguments. Will be check
	 *            in {@link Action#validate(Object...)}
	 */
	public Action(boolean initialEnable, boolean allowNullArguments,
			Class[]... validArguments) {
		this.validArguments = validArguments;
		this.allowNullArguments = allowNullArguments;
		this.listeners = new Array<ActionListener>();
		enabled = initialEnable;

	}

	/**
	 * Creates the action without arguments
	 * 
	 * 
	 * @param initialEnable
	 *            if the action is enabled when the editor starts
	 * @param allowNullArguments
	 *            if null arguments must be allowed during validation
	 */
	public Action(boolean initialEnable, boolean allowNullArguments) {
		this(initialEnable, allowNullArguments, new Class[][] {});
	}

	/**
	 * Initializes the action
	 */
	public void initialize(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Sets whether this action is enabled and can be invoked from the editor
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			for (ActionListener listener : listeners) {
				listener.enableChanged(getClass(), this.enabled);
			}
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
	 * @return if the arguments for the action are valid checking all the
	 *         attributes possibilities for the action
	 */
	public boolean validate(Object... args) {
		if (validArguments == null || args == null) {
			return true;
		}

		for (int i = 0; i < validArguments.length; i++) {

			if (args.length == validArguments[i].length) {
				for (int j = 0; j < args.length; j++) {
					if (args[j] == null && !allowNullArguments) {
						return false;
					} else if (!ClassReflection.isAssignableFrom(
							validArguments[i][j], args[j].getClass())) {
						return false;
					}
				}
				return true;

			} else {
				continue;
			}

		}

		if (args.length == validArguments.length) {
			return true;
		} else {
			return false;
		}

	}

}
