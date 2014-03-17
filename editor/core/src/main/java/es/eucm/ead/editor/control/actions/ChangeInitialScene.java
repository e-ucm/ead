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

import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.schema.editor.game.EditorGame;

/**
 * Action that changes the initial scene of the game (the first scene to be
 * launched).
 * 
 * The name of the new initial scene (e.g. "scene0") is provided as argument 0
 * (arg[0])
 * 
 * If this action receives less than one argument, an
 * {@link es.eucm.ead.editor.control.actions.EditorActionException} is thrown.
 * 
 * Also, if the first argument is null, is not a String or does not match any of
 * the sceneIds in the game, an exception is thrown.
 * 
 * If the new initial scene matches the current one, no command is created.
 * 
 * Created by Javier Torrente on 3/03/14.
 */
public class ChangeInitialScene extends EditorAction {

	@Override
	public void perform(Object... args) {
		// Check that the first argument exists and that it is a string
		// FIXME boilerplate code
		if (args.length < 1) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": This action requires at least one arguments of type String ");
		}

		if (args[0] == null || !(args[0] instanceof String)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": This action requires the first argument (args[0]) to be a valid, not null String representing the id of a scene in the game");
		}

		if (!controller.getModel().getScenes().containsKey(args[0])) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": The sceneId provided as the first argument (args[0]) does not match any of the scenes of this game.");
		}

		String currentInitialSceneId = controller.getModel().getGame()
				.getInitialScene();
		if ((currentInitialSceneId == null && args[0] != null)
				|| !currentInitialSceneId.equals(args[0])) {
			controller.command(new FieldCommand(
					controller.getModel().getGame(), FieldNames.INITIAL_SCENE,
					args[0], false));
		}
	}
}