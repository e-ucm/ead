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

import es.eucm.ead.editor.control.commands.Command;

/**
 * This class encapsulates an action, triggered by the user, that performs a
 * direct modification over the model. The result of an {@link ModelAction}
 * being performed is a {@link Command}.
 * 
 * Some examples: add/remove a scene, add/remove a scene element, etc.
 */
public abstract class ModelAction extends Action {

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
	public ModelAction(boolean initialEnable, boolean allowNullArguments,
			Class... validArguments) {
		super(initialEnable, allowNullArguments, validArguments);
	}

	public ModelAction() {
		super(true, true);
	}

	@Override
	public boolean validate(Object... args) {
		return true;
	}

	/**
	 * Executes the action with the given arguments
	 * 
	 * @param args
	 *            arguments for the action. This arguments will be validated
	 *            through {@link Action#validate(Object...)} before being passed
	 *            to perform
	 */
	public abstract Command perform(Object... args);

}
