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

import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand;
import es.eucm.ead.editor.control.commands.MapCommand.PutToMapCommand;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;

import java.util.Map;

public class AddScene extends EditorAction {

	@Override
	public void perform(Object... args) {
		Map<String, EditorScene> scenes = controller.getModel().getScenes();

		int counter = scenes.keySet().size();
		String sceneId = "scene" + counter;
		while (scenes.keySet().contains(sceneId)) {
			counter++;
			sceneId = "scene" + counter;
		}

		EditorScene scene;
		if (args.length == 1) {
			scene = (EditorScene) args[0];
		} else {
			scene = new EditorScene();
			scene.setName(sceneId);
			scene.setNotes(new Note());
		}

		// Create scene data files
		controller.getProjectAssets().addAsset(
				controller.getProjectAssets().convertSceneNameToPath(sceneId),
				EditorScene.class, scene);

		// Execute the command for adding the action. This involves:
		// 1 map command for adding the new scene to the map
		// 1 list command for adding the id of the new scene to the
		// game.getSceneorder() list
		// 1 field command for setting the edit scene to the new scene created
		// NOTE: Each time a new command is added here, AddSceneTest should be
		// updated
		controller.command(new CompositeCommand(new PutToMapCommand(scenes,
				sceneId, scene), new ListCommand.AddToListCommand(controller
				.getModel().getGame().getSceneorder(), sceneId),
				new FieldCommand(controller.getModel().getGame(),
						FieldNames.EDIT_SCENE, sceneId, true)));

	}
}
