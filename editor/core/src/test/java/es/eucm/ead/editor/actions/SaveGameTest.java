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

import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * This class is meant to test whether the
 * {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)}
 * works OK. This is the method invoked when the user hits Ctrl+S.
 * 
 * {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)}
 * should remove all json files from disk before performing any save operation.
 * This test will emphasize checking this aspect.
 * 
 * Created by Javier Torrente on 5/03/14.
 */
public class SaveGameTest extends EditorTest {

	@Test
	public void testSaveAll() {
		// Create a temp directory for the project. This directory will be
		// initially empty
		String gameFolderPath = null;
		File tempDirPath = mockPlatform.createTempFile(true);
		gameFolderPath = tempDirPath.getAbsolutePath();
		new File(gameFolderPath).mkdirs();
		mockController.getEditorGameAssets().setLoadingPath(gameFolderPath);

		// Make initialization of the model
		mockModel.setGame(new EditorGame());

		// Make dummy additions to game model
		for (int j = 0; j < 5; j++) {
			EditorScene scene = new EditorScene();
			scene.setName("XXX");
			for (int i = 0; i < 3; i++) {
				SceneElement sceneElement = new SceneElement();
				scene.getChildren().add(sceneElement);
			}
			mockModel.getScenes().put("scene" + j, scene);
		}

		// Init editorIO
		EditorIO editorIO = new EditorIO(mockController);

		// Save the model
		editorIO.saveAll(mockModel);

		// Test all files were actually stored
		testFileExists(gameFolderPath, EditorGameAssets.GAME_FILE);
		for (int i = 0; i < 5; i++) {
			testFileExists(gameFolderPath, EditorGameAssets.SCENES_PATH
					+ "scene" + i + ".json");
		}

		// Test the appVersion was updated
		assertNotNull("the appVersion of the game must be not null", mockModel
				.getGame().getAppVersion());

		// Test the modelVersion was updated
		assertNotNull("the modelVersion of the game must be not null",
				mockModel.getGame().getModelVersion());

		// Now, change the model. All scenes but one (scene3) will be removed. A
		// new scene2 will be created with 1 scene element.
		for (int i = 0; i < 5; i++) {
			if (i != 3) {
				mockModel.getScenes().remove("scene" + i);
			}
		}

		EditorScene scene2 = new EditorScene();
		scene2.setName("XXX");
		SceneElement sceneElement = new SceneElement();
		scene2.getChildren().add(sceneElement);
		mockModel.getScenes().put("scene2", scene2);

		// To test saveAll does not remove files that have extension != .json,
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
		editorIO.saveAll(mockModel);

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

		if (imageFile != null)
			assertTrue(imageFile.exists());

		// Now, test scene 2 has only 1 scene element
		mockController.action(OpenGame.class, new File(gameFolderPath,
				EditorGameAssets.GAME_FILE).getAbsolutePath());
		assertTrue(mockController.getModel().getScenes().get("scene2")
				.getChildren().size() == 1);

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
}
