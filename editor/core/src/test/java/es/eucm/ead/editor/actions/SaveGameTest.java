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

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.RenameScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.editor.components.Versions;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class is meant to test {@link Save} action . This is the action invoked
 * when the user hits Ctrl+S.
 * 
 * Created by Javier Torrente on 5/03/14.
 */
public class SaveGameTest extends ActionTest {

	@Test
	/**
	 * Tests {@link Model#save()}.
	 * This method should remove all json files from disk before performing any save operation.
	 * This test put special emphasis on checking this aspect.
	 */
	public void test() {
		// Create a temp directory for the project. This directory will be
		// initially empty
		String gameFolderPath = null;
		File tempDirPath = platform.createTempFile(true);
		gameFolderPath = tempDirPath.getAbsolutePath();
		new File(gameFolderPath).mkdirs();
		controller.getEditorGameAssets().setLoadingPath(gameFolderPath);

		// Make initialization of the model
		model.putResource(ResourceCategory.GAME.getCategoryPrefix(),
				new ModelEntity());

		// Make dummy additions to game model
		for (int j = 0; j < 5; j++) {
			ModelEntity scene = new ModelEntity();
			Q.getComponent(scene, Documentation.class).setName("XXX");
			for (int i = 0; i < 3; i++) {
				ModelEntity sceneElement = new ModelEntity();
				scene.getChildren().add(sceneElement);
			}
			model.putResource(EditorGameAssets.SCENES_PATH + "scene" + j
					+ ".json", scene);
			if (j == 0) {
				Q.getComponent(model.getGame(), GameData.class)
						.setInitialScene(
								EditorGameAssets.SCENES_PATH + "scene" + j
										+ ".json");
			}
			Q.getComponent(model.getGame(), EditState.class).getSceneorder()
					.add(EditorGameAssets.SCENES_PATH + "scene" + j + ".json");
		}

		// Create a dummy action so a new command is created so the Save action
		// actually does something
		controller.action(RenameScene.class, EditorGameAssets.SCENES_PATH
				+ "scene0.json", "scenes/XXX0");

		// Save the model
		controller.action(Save.class);

		// Test all files were actually stored
		testFileExists(gameFolderPath, EditorGameAssets.GAME_FILE);
		for (int i = 0; i < 5; i++) {
			testFileExists(gameFolderPath, EditorGameAssets.SCENES_PATH
					+ "scene" + i + ".json");
		}

		// Test the appVersion was updated
		assertNotNull("the appVersion of the game must be not null", Q
				.getComponent(model.getGame(), Versions.class).getAppVersion());

		// Test the modelVersion was updated
		assertNotNull("the modelVersion of the game must be not null", Q
				.getComponent(model.getGame(), Versions.class)
				.getModelVersion());

		// Now, change the model. All scenes but one (scene3) will be removed. A
		// new scene2 will be created with 1 scene element.
		for (int i = 0; i < 5; i++) {
			if (i != 3) {
				controller.action(DeleteScene.class,
						EditorGameAssets.SCENES_PATH + "scene" + i + ".json");
			}
		}

		ModelEntity scene2 = new ModelEntity();
		Q.getComponent(scene2, Documentation.class).setName("XXX");
		ModelEntity sceneElement = new ModelEntity();
		scene2.getChildren().add(sceneElement);
		model.putResource(EditorGameAssets.SCENES_PATH + "scene2.json", scene2);

		// To test save() does not remove files that have extension != .json,
		// create an empty image file
		File imagesDir = new File(gameFolderPath,
				EditorGameAssets.IMAGES_FOLDER);
		imagesDir.mkdirs();
		File imageFile = null;
		try {
			imageFile = new File(imagesDir, "image1.png");
			imageFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception in SaveGameTest: " + e.toString());
		}

		// Save the model again
		controller.action(Save.class);

		// Test new persistent state. game.json,
		// scenes/scene2.json and scenes/scene3.json (and the associated scene
		// data files) should be the only files in
		// the directory, plus image1.png.
		testFileExists(gameFolderPath, EditorGameAssets.GAME_FILE);
		testFileExists(gameFolderPath, EditorGameAssets.SCENES_PATH
				+ "scene2.json");
		testFileExists(gameFolderPath, EditorGameAssets.SCENES_PATH
				+ "scene3.json");
		testFileDoesNotExist(gameFolderPath, EditorGameAssets.SCENES_PATH
				+ "scene0.json");
		testFileDoesNotExist(gameFolderPath, EditorGameAssets.SCENES_PATH
				+ "scene1.json");
		testFileDoesNotExist(gameFolderPath, EditorGameAssets.SCENES_PATH
				+ "scene4.json");

		assertTrue(imageFile.exists());

		// Now, test scene 2 has only 1 scene element
		controller.action(OpenGame.class,
				new File(gameFolderPath).getAbsolutePath());
		assertTrue(((ModelEntity) controller.getModel()
				.getResources(ResourceCategory.SCENE)
				.get(EditorGameAssets.SCENES_PATH + "scene2.json"))
				.getChildren().size == 1);

		// Finally, delete temp dir
		deleteDirectoryRecursively(new File(gameFolderPath));
	}

	private void testFileExists(String gameFolderPath, String subPath) {
		File file = new File(gameFolderPath, subPath);
		assertTrue(file.exists() && file.length() > 0);
	}

	private void testFileDoesNotExist(String gameFolderPath, String subPath) {
		File file = new File(gameFolderPath, subPath);
		assertFalse(file.exists());
	}

	private void deleteDirectoryRecursively(File directory) {
		// Delete dir contents
		for (File child : directory.listFiles()) {
			if (child.isDirectory()) {
				deleteDirectoryRecursively(child);
			} else {
				child.delete();
			}

		}

		// Remove the directory now that's empty.
		directory.delete();
	}

	@Test
	public void testIgnoredComponent() {
		File folder = platform.createTempFile(true);
		controller.getEditorGameAssets().setLoadingPath(
				folder.getAbsolutePath());

		model.putResource("game.json", new ModelEntity());

		ModelEntity modelEntity = new ModelEntity();
		Q.getComponent(modelEntity, Parent.class).setParent(null);

		model.putResource("scenes/myentity.json", modelEntity);

		controller.action(Save.class);

		ModelEntity read = controller.getEditorGameAssets().fromJsonPath(
				ModelEntity.class, "scenes/myentity.json");

		assertEquals(read.getComponents().size, 0);

	}

}
