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
package es.eucm.ead.editor.control.actions.model;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

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
public class ChangeInitialScene extends ModelAction {

	public ChangeInitialScene() {
		super(true, false, new Class[] {}, new Class[] { String.class });
	}

	@Override
	public Command perform(Object... args) {
		String sceneId = (String) (args.length == 0 ? controller.getModel()
				.getSelection().getSingle(Selection.RESOURCE) : args[0]);

		if (sceneId == null
				|| !controller.getModel().getResources(ResourceCategory.SCENE)
						.containsKey(sceneId)) {
			throw new EditorActionException(
					"Error in action "
							+ this.getClass().getCanonicalName()
							+ ": The sceneId provided does not match any of the scenes of this game.");
		}

		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);
		String currentInitialSceneId = gameData.getInitialScene();
		if (!sceneId.equals(currentInitialSceneId)) {
			return new FieldCommand(gameData, FieldName.INITIAL_SCENE, sceneId,
					false);
		}
		return null;
	}
}
