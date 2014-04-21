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

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand;
import es.eucm.ead.editor.control.commands.MapCommand.PutToMapCommand;
import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

import java.util.Map;

/**
 * {@link es.eucm.ead.editor.control.actions.model.AddScene} is a simple action
 * that adds a scene to the current game. It just creates a blank scene, with a
 * generated new id (e.g. "scene0"), and puts it to the scenes map.
 * 
 * The scene created has a blank
 * {@link es.eucm.ead.schema.editor.components.Note} and its
 * {@link es.eucm.ead.schema.editor.components.Note#title} is set with the
 * generated id (e.g. "scene0").
 * 
 * It adjusts the
 * {@link es.eucm.ead.schema.editor.components.EditState#editScene} field, to
 * make this scene appear on the edition view,
 * 
 * It also updates the
 * {@link es.eucm.ead.schema.editor.components.EditState#sceneorder} array,
 * adding the new scene id to the end.
 * 
 * NOTE: This action does not save the scene file to disk. The actual scene file
 * will not be created until the user saves the game.
 * 
 * Arguments and usage This action does not require any argument. However, an
 * optional boolean argument can be passed to indicate whether the operation is
 * undoable or not. This is intended for other actions that may want to invoke
 * {@link es.eucm.ead.editor.control.actions.model.AddScene} but which do not
 * want the operation to be undoable since it was not triggered by user
 * interaction. For example, this is the case of
 * {@link es.eucm.ead.editor.control.actions.editor.NewGame}, which creates a
 * blank scene in the recently created game. This operation cannot be undone
 * since it does not make sense to revert the creation of new projects.
 * 
 * This way, there are two recommended usage scenarios for this action: 1)
 * controller.action(AddScene.class) This is the way that views should be using
 * this action to create new scenes. The operation will be undoable 2)
 * controller.action(AddScene.class, false) This is the way that not-undoable
 * actions should be creating new
 * {@link es.eucm.ead.editor.control.actions.model.AddScene} actions. It
 * indicates that the add scene operation cannot be undone, since the former
 * action cannot be undone.
 * 
 */
public class AddScene extends ModelAction {

	@Override
	public Command perform(Object... args) {
		// Generate a new sceneId that does not exist
		String sceneId = buildNewSceneId();

		ModelEntity scene = null;
		if (args.length > 0) {
			if (args[0] instanceof ModelEntity) {
				scene = (ModelEntity) args[0];
			} else {
				throw new EditorActionException(
						"The action "
								+ AddScene.class.getCanonicalName()
								+ " accepts as an argument either an EditorScene or nothing. "
								+ "The first argument detected was of an incompatible type: "
								+ (args[0] == null ? null : args[0].getClass()
										.toString()));
			}
		}

		// Create the scene if it is not given as an argument
		if (scene == null) {
			scene = controller.getTemplates().createScene(sceneId);
		}

		// Create scene data files
		controller.getEditorGameAssets().addAsset(
				controller.getEditorGameAssets()
						.convertSceneNameToPath(sceneId), ModelEntity.class,
				scene);

		// Execute the command for adding the action. This involves:
		// 1 map command for adding the new scene to the map
		// 1 list command for adding the id of the new scene to the
		// game.getSceneorder() list
		// 1 field command for setting the edit scene to the new scene created
		// NOTE: Each time a new command is added here, AddSceneTest should be
		// updated
		Map<String, ModelEntity> scenes = controller.getModel().getEntities(
				ModelEntityCategory.SCENE);
		return new CompositeCommand(
				new PutToMapCommand(scenes, sceneId, scene),
				new ListCommand.AddToListCommand(Model.getComponent(
						controller.getModel().getGame(), EditState.class)
						.getSceneorder(), sceneId), new FieldCommand(
						Model.getComponent(controller.getModel().getGame(),
								EditState.class), FieldNames.EDIT_SCENE,
						sceneId, true));

	}

	/**
	 * Builds the id for the new scene. It determines what id to be used by
	 * appending to the "scene" prefix the first number that results in an id
	 * that has not been used yet for scenes.
	 * 
	 * @return The new scene id (e.g. "scene0", "scene1", etc.)
	 */
	private String buildNewSceneId() {
		Map<String, ModelEntity> scenes = controller.getModel().getEntities(
				ModelEntityCategory.SCENE);
		int counter = scenes.keySet().size();
		String sceneId = "scene" + counter;
		while (scenes.keySet().contains(sceneId)) {
			counter++;
			sceneId = "scene" + counter;
		}
		return sceneId;
	}
}
