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

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand;
import es.eucm.ead.editor.control.commands.RootEntityCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.classic.dialogs.InfoDialogBuilder;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

/**
 * Deletes an scene given the scene id (args[0]). It only removes it from the
 * model, the .json file is kept on disk until the game is saved to disk again.
 * 
 * This action won't have effect if there are only one scene in the game. In
 * that case a
 * {@link es.eucm.ead.editor.view.builders.classic.dialogs.InfoDialogBuilder}
 * dialog will appear explaining it.
 * 
 * Optionally a second argument should be provided, which is the ModelEntity to
 * be removed (args[1]).
 * 
 * 
 * Created by Javier Torrente on 3/03/14.
 */
public class DeleteScene extends ModelAction {

	@Override
	public Command perform(Object... args) {
		String id = (String) args[0];
		// This is a hotfix for avoiding show information when actions are
		// called
		// from test
		boolean verbose = true;

		if (args.length > 1 && args[1] instanceof Boolean) {
			verbose = (Boolean) args[1];

		}

		// If there's only one scene, then this action cannot be done and
		// the
		// user must be warned.
		if (controller.getModel().getEntities(ModelEntityCategory.SCENE).size() == 1) {

			if (verbose) {
				// Select InfoDialogBuilder as dialog for showing a message
				// explaining why this scene won't be deleted

				controller.getViews().showDialog(
						InfoDialogBuilder.NAME,
						controller.getApplicationAssets().getI18N()
								.m("scene.delete.error-message"));
			}
		}
		// There are more than only one scene
		else {
			ModelEntity game = controller.getModel().getGame();
			Array<Command> commandList = new Array<Command>();
			// The action of deleting an scene involves the next commands:
			// 1) If the scene is the "editScene", change the editscene
			String alternateScene = null;
			EditState editState = Model.getComponent(game, EditState.class);
			if (editState.getEditScene().equals(id)) {
				alternateScene = findAlternateScene(id);
				commandList.add(new FieldCommand(editState,
						FieldNames.EDIT_SCENE, alternateScene, false));
			}

			// 2) If the scene is the "initialscene", change the initial one
			GameData gameData = Model.getComponent(controller.getModel()
					.getGame(), GameData.class);
			if (gameData.getInitialScene().equals(id)) {
				if (alternateScene == null) {
					alternateScene = findAlternateScene(id);
				}
				commandList.add(new FieldCommand(gameData,
						FieldNames.INITIAL_SCENE, alternateScene, false));
			}

			// 3) Delete the scene properly speaking
			commandList
					.add(new RootEntityCommand.RemoveRootEntityCommand(
							controller.getModel(),
							id,
							args.length == 2 && args[1] instanceof ModelEntity ? (ModelEntity) args[1]
									: null, ModelEntityCategory.SCENE));

			// 4) Delete the sceneId from gameMetadata.getSceneorder()
			commandList.add(new ListCommand.RemoveFromListCommand(editState,
					editState.getSceneorder(), id));

			// Execute the composite command
			CompositeCommand deleteSceneCommand = new CompositeCommand(
					commandList);
			return deleteSceneCommand;
		}
		return null;
	}

	/**
	 * Method that returns the name of a scene that is different from the one
	 * given as a parameter. In case there's only one scene, it will return null
	 * 
	 * @param sceneId
	 *            The id of the scene that should not be returned (e.g.
	 *            "scene0")
	 * @return The id of a scene that is not equals to the given one (e.g.
	 *         "scene0")
	 */
	private String findAlternateScene(String sceneId) {
		String alternateScene = null;
		for (String sid : controller.getModel()
				.getEntities(ModelEntityCategory.SCENE).keySet()) {
			if (!sid.equals(sceneId)) {
				alternateScene = sid;
				break;
			}
		}
		return alternateScene;
	}
}
