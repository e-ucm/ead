/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.control.commands.FieldCommand;

/**
 * Action that changes the initial scene of the game (the first scene to be
 * launched).
 * 
 * The name of the new initial scene (e.g. "scene0") is provided as argument 0
 * (arg[0])
 * 
 * Created by Javier Torrente on 3/03/14.
 */
public class InitialScene extends EditorAction {
	/**
	 * This is the name of the action. This field should be accessed from the
	 * View to generate InitialScene actions
	 */
	public static final String NAME = "initialScene";

	public InitialScene() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		if (!controller.getModel().getGame().getInitialScene().equals(args[0])) {
			controller.command(new FieldCommand(
                    controller.getModel().getGame(),
                    FieldNames.INITIAL_SCENE, args[0], false));
		}
	}
}