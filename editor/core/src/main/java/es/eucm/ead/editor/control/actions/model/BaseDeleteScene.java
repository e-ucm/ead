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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.ListCommand;
import es.eucm.ead.editor.control.commands.ResourceCommand.RemoveResourceCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Deletes an scene given the scene id (args[0]). If not set, takes the scene in
 * {@link Selection#MOKAP_RESOURCE}
 */
public class BaseDeleteScene extends ModelAction {

	private ChangeInitialScene changeInitialScene;

	public BaseDeleteScene() {
		super(true, false, new Class[] {}, new Class[] { String.class });
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		changeInitialScene = controller.getActions().getAction(
				ChangeInitialScene.class);
	}

	@Override
	public boolean isEnabled() {
		if (controller.getModel().getResources(ResourceCategory.SCENE).size() == 1) {
			notifyIsLastScene();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Command perform(Object... args) {
		String id = (String) (args.length == 0 ? controller.getModel()
				.getSelection().getSingle(Selection.MOKAP_RESOURCE) : args[0]);

		if (controller.getModel().getResourceCategory(id) != ResourceCategory.SCENE) {
			Gdx.app.error("DeleteScene", id
					+ "is not a scene, and cannot be removed.");
			return null;
		}

		CompositeCommand commands = new CompositeCommand();

		Model model = controller.getModel();
		ModelEntity game = model.getGame();
		EditState editState = Q.getComponent(game, EditState.class);

		// 2) If the scene is the "initialscene", change the initial one
		String alternateScene;
		GameData gameData = Q.getComponent(game, GameData.class);
		if (gameData.getInitialScene().equals(id)) {
			alternateScene = findAlternateScene(id);
			commands.addCommand(changeInitialScene.perform(alternateScene));
		}

		// 3) Delete the scene properly speaking
		commands.addCommand(new RemoveResourceCommand(
				model,
				id,
				(ModelEntity) controller.getModel().getResource(id).getObject(),
				ResourceCategory.SCENE));

		// Delete thumbnail
		FileHandle thumbnail = controller.getEditorGameAssets().resolve(
				Q.getThumbnailPath(id));
		if (thumbnail.exists()) {
			thumbnail.delete();
		}

		// 4) Delete the sceneId from gameMetadata.getSceneorder()
		commands.addCommand(new ListCommand.RemoveFromListCommand(editState,
				editState.getSceneorder(), id));

		// 5) Delete the cell from the scene map
		SceneMap sceneMap = Q.getComponent(game, SceneMap.class);
		Array<Cell> cells = sceneMap.getCells();
		commands.addCommand(new ListCommand.RemoveFromListCommand(sceneMap,
				cells, Q.getCellFromId(id, cells)));

		// Execute the composite command
		return commands;
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
				.getResources(ResourceCategory.SCENE).keySet()) {
			if (!sid.equals(sceneId)) {
				alternateScene = sid;
				break;
			}
		}
		return alternateScene;
	}

	protected void notifyIsLastScene() {

	}
}
