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
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.actors.SceneMetadata;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
		mockController.getProjectAssets().setLoadingPath(gameFolderPath);

		// Make dummy additions to game model
		for (int j = 0; j < 5; j++) {
			Scene scene = new Scene();
			SceneMetadata sceneMetadata = new SceneMetadata();
			sceneMetadata.setName("XXX");
			for (int i = 0; i < 3; i++) {
				SceneElement sceneElement = new SceneElement();
				scene.getChildren().add(sceneElement);
			}
			mockModel.getScenes().put("scene" + j, scene);
			mockModel.getScenesMetadata().put("scene" + j, sceneMetadata);
		}

		// Init editorIO
		EditorIO editorIO = new EditorIO(mockController);

		// Save the model
		editorIO.saveAll(mockModel);

		// Test all files were actually stored
		testFileExists(gameFolderPath, ProjectAssets.GAME_FILE);
		testFileExists(gameFolderPath, ProjectAssets.GAME_METADATA_FILE);
		for (int i = 0; i < 5; i++) {
			testFileExists(gameFolderPath, ProjectAssets.SCENES_PATH + "scene"
					+ i + ".json");
			testFileExists(gameFolderPath, ProjectAssets.SCENEMETADATA_PATH
					+ "scene" + i + ".json");
		}

		// Now, change the model. All scenes but one (scene3) will be removed. A
		// new scene2 will be created with 1 scene element.
		for (int i = 0; i < 5; i++) {
			if (i != 3) {
				mockModel.getScenes().remove("scene" + i);
				mockModel.getScenesMetadata().remove("scene" + i);
			}
		}

		Scene scene2 = new Scene();
		SceneMetadata sceneMetadata2 = new SceneMetadata();
		sceneMetadata2.setName("XXX");
		SceneElement sceneElement = new SceneElement();
		scene2.getChildren().add(sceneElement);
		mockModel.getScenes().put("scene2", scene2);
		mockModel.getScenesMetadata().put("scene2", sceneMetadata2);

		// To test saveAll does not remove files that have extension != .json,
		// create an empty image file
		File imagesDir = new File(gameFolderPath, ProjectAssets.IMAGES_FOLDER);
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

		// Test new persistent state. game.json, project.json,
		// scenes/scene2.json and scenes/scene3.json (and the associated scene
		// metadata files) should be the only files in
		// the directory, plus image1.png.
		testFileExists(gameFolderPath, ProjectAssets.GAME_FILE);
		testFileExists(gameFolderPath, ProjectAssets.GAME_METADATA_FILE);
		testFileExists(gameFolderPath, ProjectAssets.SCENES_PATH
				+ "scene2.json");
		testFileExists(gameFolderPath, ProjectAssets.SCENES_PATH
				+ "scene3.json");
		testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENES_PATH
				+ "scene0.json");
		testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENES_PATH
				+ "scene1.json");
		testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENES_PATH
				+ "scene4.json");

		testFileExists(gameFolderPath, ProjectAssets.SCENEMETADATA_PATH
				+ "scene2.json");
		testFileExists(gameFolderPath, ProjectAssets.SCENEMETADATA_PATH
				+ "scene3.json");
		testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENEMETADATA_PATH
				+ "scene0.json");
		testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENEMETADATA_PATH
				+ "scene1.json");
		testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENEMETADATA_PATH
				+ "scene4.json");

		if (imageFile != null)
			assertTrue(imageFile.exists());

		// Now, test scene 2 has only 1 scene element
		mockController.action(OpenGame.class, new File(gameFolderPath,
				ProjectAssets.GAME_METADATA_FILE).getAbsolutePath());
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
