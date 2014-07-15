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
package es.eucm.ead.editor.actions;

import org.junit.After;
import org.junit.Before;

import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Parent class for all tests related to {@link es.eucm.ead.editor.actions}. It
 * creates a mock controller and platform.
 * 
 * Provides convenient methods to test actions. Also, provides
 * {@link ActionTest#openEmpty()} to load an empty games for those actions that
 * need it.
 * 
 */
public abstract class ActionTest extends EditorTest {

	public static final String SCENE0 = ResourceCategory.SCENE
			.getCategoryPrefix()
			+ ResourceCategory.SCENE.getNamePrefix()
			+ "0.json";

	protected FileHandle emptyGamePath = null;

	/**
	 * Loads an empty game. The game has the following structure: - A game.json
	 * file with only one scene: scene0.json, which is empty, stored in subpath
	 * /scenes/scene0.json - A game.json file that only specifies the scene
	 * being edited (editScene): scene0
	 * 
	 * Subclasses of ActionTest may want to call this method the first in set up
	 */
	protected void openEmpty() {
		emptyGamePath = FileHandle.tempDirectory("ead-actiontest-");
		controller.getEditorGameAssets().setLoadingPath(
				emptyGamePath.file().getAbsolutePath());

		// Create empty game
		ModelEntity game = new ModelEntity();
		GameData gameData = Q.getComponent(game, GameData.class);
		gameData.setInitialScene(SCENE0);
		EditState editState = Q.getComponent(game, EditState.class);
		editState.getSceneorder().add(SCENE0);
		Q.getComponent(game, Note.class);

		// Create empty scene 0
		ModelEntity scene = new ModelEntity();
		Q.getComponent(scene, Note.class);
		Q.getComponent(scene, Documentation.class).setName(SCENE0);
		model.putResource(ResourceCategory.GAME.getCategoryPrefix(), game);
		model.putResource(SCENE0, scene);

		// Create the cell in the map for the scene 0
		SceneMap sceneMap = Q.getComponent(game, SceneMap.class);
		Cell cell = new Cell();
		cell.setRow(0);
		cell.setColumn(0);
		cell.setSceneId(SCENE0);
		sceneMap.getCells().add(cell);

		// Save game
		controller.action(ForceSave.class);

		// Load game in the model
		controller.action(OpenGame.class, emptyGamePath.file()
				.getAbsolutePath());
	}

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().reset();
		controller.getCommands().pushStack();
	}

	protected void loadAllPendingAssets() {
		controller.getEditorGameAssets().finishLoading();
	}

	@After
	/**
	 * If an empty game was created and loaded, it must be deleted after the test
	 */
	public void clearEmpty() {
		if (emptyGamePath != null) {
			emptyGamePath.deleteDirectory();
		}
	}
}
