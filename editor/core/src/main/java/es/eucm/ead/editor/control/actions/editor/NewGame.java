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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.io.FileNotFoundException;

/**
 * New game creates an empty game. Expects exactly three parameters: arg[0]: a
 * valid path to a folder where the game should be created (String) arg[1]: a
 * not null {@link ModelEntity} object
 */
public class NewGame extends EditorAction {

	public NewGame() {
		super(true, false, String.class, ModelEntity.class);
	}

	@Override
	public void perform(Object... args) {
		String path = args[0] != null ? (String) args[0] : "";
		// Check all the slashes are /
		path = controller.getEditorGameAssets().toCanonicalPath(path);
		ModelEntity game = (ModelEntity) args[1];

		EditorGameAssets editorGameAssets = controller.getEditorGameAssets();
		FileHandle projectFolder = editorGameAssets.absolute(path);

		if (!projectFolder.exists()) {
			projectFolder.mkdirs();
		}

		if (projectFolder.exists()) {
			editorGameAssets.setLoadingPath(path);
			Model model = controller.getModel();
			model.reset();

			model.putResource(GameStructure.GAME_FILE, ResourceCategory.GAME,
					game);
			String initialScene = model.createId(ResourceCategory.SCENE);
			ModelEntity scene = controller.getTemplates().createScene(
					controller.getApplicationAssets().getI18N().m("initial"));
			model.putResource(initialScene, ResourceCategory.SCENE, scene);
			GameData gameData = Q.getComponent(game, GameData.class);
			gameData.setInitialScene(initialScene);

			controller.action(Save.class);
			controller
					.action(OpenGame.class, editorGameAssets.getLoadingPath());
		} else {
			throw new EditorActionException("Impossible to create project",
					new FileNotFoundException(path));
		}
	}
}
