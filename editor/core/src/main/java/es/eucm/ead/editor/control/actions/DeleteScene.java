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
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.MapCommand;
import es.eucm.ead.schema.game.GameMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Deletes the given scene (args[0]). It only removes it from the model, the
 * .json file is kept on disk until
 * {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)}
 * is invoked. Created by Javier Torrente on 3/03/14.
 */
public class DeleteScene extends EditorAction {

	/**
	 * This is the name of the action. This field should be accessed from the
	 * View to generate DeleteScene actions
	 */
	public static final String NAME = "deleteScene";

	public DeleteScene() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		GameMetadata gameMetadata = controller.getModel().getGameMetadata();
		// If there's only one scene, then this action cannot be done and the
		// user must be warned.
		if (controller.getModel().getScenes().size() == 1) {
			// TODO This is to be done
		}
		// There are more than only one scene
		else {
			List<Command> commandList = new ArrayList<Command>();
			// The action of deleting an scene involves the next commands:
			// 1) If the scene is the "editScene", change the editscene
			if (gameMetadata.getEditScene().equals(args[0])) {
				commandList.add(new FieldCommand(gameMetadata,
						FieldNames.EDIT_SCENE,
						findAlternateScene((String) args[0]), false));
			}

			// 2) If the scene is the "initialscene", change the initial one
			if (controller.getModel().getGame().getInitialScene()
					.equals(args[0])) {
				commandList.add(new FieldCommand(controller.getModel()
						.getGame(), FieldNames.INITIAL_SCENE,
						findAlternateScene((String) args[0]), false));
			}

			// 3) Delete the scene properly speaking
			commandList.add(new MapCommand.RemoveFromMapCommand(controller
					.getModel().getScenes(), args[0]));
			CompositeCommand deleteSceneCommand = new CompositeCommand(
					commandList);
			controller.command(deleteSceneCommand);

		}
	}

	/**
	 * Method that returns the name of a scene that is different from the one
	 * given as a parameter. In case there's only one scene, it will return null
	 * 
	 * @param scene
	 *            The name of the scene that should not be returned
	 * @return The name of a scene that is not equals to the given one
	 */
	private String findAlternateScene(String scene) {
		String alternateScene = null;
		for (String sceneName : controller.getModel().getScenes().keySet()) {
			if (!sceneName.equals(scene)) {
				alternateScene = sceneName;
				break;
			}
		}
		return alternateScene;
	}
}
