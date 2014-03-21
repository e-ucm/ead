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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.EditorTest;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.editor.control.actions.editor.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Time;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Trigger;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.effects.ChangeRenderer;
import es.eucm.ead.schema.effects.TemporalEffect;
import es.eucm.ead.schema.game.Game;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class is meant to test whether the methods in
 * {@link es.eucm.ead.editor.control.EditorIO} that deal with saving the game to
 * disk work OK. Currently, these are two methods:
 * 
 * 1)
 * {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)}
 * . This is the method invoked when the user hits Ctrl+S. 2)
 * {@link es.eucm.ead.editor.control.EditorIO#saveGameForExport(com.badlogic.gdx.files.FileHandle, es.eucm.ead.editor.model.Model)}
 * . This is the method that saves the game as to be readable by the engine
 * (removes all editor's data). This method is used for exporting the game as a
 * final release through the editor.
 * 
 * Created by Javier Torrente on 5/03/14.
 */
public class SaveGameTest extends EditorActionTest implements
		Model.ModelListener<LoadEvent> {

	/**
	 * {@link es.eucm.ead.schema.game.Game} properties stored to disk by
	 * {@link #testSaveGameForExport()}
	 */
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	public static final String INITIAL_SCENE = "scene2";
	public static final int DURATION = 10;

	/**
	 * {@link es.eucm.ead.schema.editor.game.EditorGame} properties set by
	 * {@link #testSaveGameForExport()}
	 */
	public static final String EDIT_SCENE = "scene3";
	public static final String APP_VERSION = "9.9.9";
	public static final String MODEL_VERSION = "157";

	@Test
	/**
	 * Tests {@link EditorIO#saveGameForExport(es.eucm.ead.editor.model.Model)}
	 *
	 * This test does as follows:
	 * 1) Initializes the model with a new EditorGame that has set properties
	 *    that are defined either in {@link es.eucm.ead.schema.game.Game} or
	 *    in {@link es.eucm.ead.schema.editor.game.EditorGame}. Also puts into
	 *    the model 5 scenes that also have set properties from
	 *    {@link es.eucm.ead.schema.actors.Scene} and
	 *    {@link es.eucm.ead.schema.editor.actors.EditorScene}.
	 *
	 * 2) Saves the model to disk using {@link EditorIO#saveGameForExport(es.eucm.ead.editor.model.Model)}.
	 *
	 * 3) Checks that the saved game can be loaded by the engine. This
	 *    guarantees that no field defined in editor's schema was actually
	 *    stored to disk.
	 *
	 * 4) Opens the recently saved game in the editor and checks that the
	 *    properties defined in the engine's schema were stored OK by
	 *    matching them with the initial values of the model.
	 */
	public void testSaveGameForExport() {
		// Make initialization of the model
		mockModel.setGame(new EditorGame());
		// Set some of the properties in game that belong to class Game
		mockModel.getGame().setInitialScene(INITIAL_SCENE);
		mockModel.getGame().setWidth(WIDTH);
		mockModel.getGame().setHeight(HEIGHT);
		// Set some of the properties declared in EditorGame and which should
		// not be saved to disk
		mockModel.getGame().setModelVersion(MODEL_VERSION);
		mockModel.getGame().setAppVersion(APP_VERSION);
		mockModel.getGame().setEditScene(EDIT_SCENE);

		// Create five scenes
		for (int j = 0; j < 5; j++) {
			EditorScene scene = new EditorScene();
			// Set editor properties (not to be saved)
			scene.setName("XXX-" + j);
			Note note = new Note();
			note.setTitle("Title");
			note.setDescription("Description");
			scene.setNotes(note);
			// Set scene properties (to be saved)
			// Add 3 children
			for (int i = 0; i < 3; i++) {
				// Create the scene element. All its properties must be saved
				SceneElement sceneElement = new SceneElement();
				sceneElement.setEnable(false);
				sceneElement.setVisible(false);

				List<Behavior> behaviorList = new ArrayList<Behavior>();
				Behavior b = new Behavior();
				Touch trigger = new Touch();
				trigger.setType(Touch.Type.ENTER);
				TemporalEffect temporalEffect = new TemporalEffect();
				temporalEffect.setDuration(DURATION);
				b.setEffect(temporalEffect);
				b.setTrigger(trigger);
				behaviorList.add(b);
				sceneElement.setBehaviors(behaviorList);

				scene.getChildren().add(sceneElement);
			}
			mockModel.getScenes().put("scene" + j, scene);
			mockModel.getGame().getSceneorder().add("scene" + j);
		}

		// Init editorIO
		EditorIO editorIO = new EditorIO(mockController);

		// Save the model
		FileHandle tempDir = FileHandle.tempDirectory("eadtemp-export-");
        Method[] methods = EditorIO.class.getDeclaredMethods();
        for (Method method: methods){
            if (method.getName().equals("saveGameForExport")){
                try {
                    method.setAccessible(true);
                    method.invoke(editorIO, tempDir, mockModel );
                    method.setAccessible(false);
                } catch (IllegalAccessException e) {
                    Gdx.app.error(SaveGameTest.class.getCanonicalName(), "Error testing saveGameForExport", e);
                    fail();
                } catch (InvocationTargetException e) {
                    Gdx.app.error(SaveGameTest.class.getCanonicalName(), "Error testing saveGameForExport", e);
                    fail();
                }
                Gdx.app.debug(SaveGameTest.class.getCanonicalName(), tempDir.path());
            }
        }


		// Create an engine that loads the game
		try {
			MockGame mockGame = new MockGame(tempDir.path());
			mockGame.act();
		} catch (SerializationException e) {
			Gdx.app.debug(
					this.getClass().getCanonicalName(),
					"Error reading the game exported. This game cannot be loaded in the game engine",
					e);
			fail("Error reading the game exported. This game cannot be loaded in the game engine");
		}

		mockController.getModel().addLoadListener(this);
		mockController.action(OpenGame.class, tempDir.path());
		loadAllPendingAssets();

		tempDir.deleteDirectory();
	}

	@Override
	/*
	 * Method that gets invoked when the new game is loaded in the editor: Check
	 * that Game fields are available but not EditorGame fields
	 */
	public void modelChanged(LoadEvent event) {
		// Fields in Game that should be initialized
		assertEquals(this.getClass().getCanonicalName()
				+ ": The game model is not the expected", INITIAL_SCENE,
				mockModel.getGame().getInitialScene());
		assertTrue(this.getClass().getCanonicalName()
				+ ": The game model is not the expected", mockModel.getGame()
				.getWidth() == WIDTH);
		assertTrue(this.getClass().getCanonicalName()
				+ ": The game model is not the expected", mockModel.getGame()
				.getHeight() == HEIGHT);
		// Fields in EditorGame that should not be initialized
		assertNull(
				this.getClass().getCanonicalName()
						+ ": The editor game model is not the expected (should be just blank)",
				mockModel.getGame().getEditScene());
		assertNull(
				this.getClass().getCanonicalName()
						+ ": The editor game model is not the expected (should be just blank)",
				mockModel.getGame().getModelVersion());
		assertNull(
				this.getClass().getCanonicalName()
						+ ": The editor game model is not the expected (should be just blank)",
				mockModel.getGame().getAppVersion());

		for (String sceneId : mockModel.getScenes().keySet()) {
			EditorScene scene = mockModel.getScenes().get(sceneId);
			Behavior behavior = scene.getChildren().get(0).getBehaviors()
					.get(0);
			// Fields in Scene that should be initialized
			assertFalse(this.getClass().getCanonicalName()
					+ ": The game model is not the expected", scene
					.getChildren().get(0).isEnable());
			assertFalse(this.getClass().getCanonicalName()
					+ ": The game model is not the expected", scene
					.getChildren().get(0).isVisible());
			assertTrue(
					this.getClass().getCanonicalName()
							+ ": The game model is not the expected",
					((TemporalEffect) behavior.getEffect()).getDuration() == DURATION);
			assertTrue(
					this.getClass().getCanonicalName()
							+ ": The game model is not the expected",
					((Touch) behavior.getTrigger()).getType() == Touch.Type.ENTER);
			assertTrue(this.getClass().getCanonicalName()
					+ ": The game model is not the expected", scene
					.getChildren().size() == 3);
			// Fields in EditorScene that should not be initialized
			assertEquals(
					this.getClass().getCanonicalName()
							+ ": The editor game model is not the expected (should be just blank)",
					sceneId, scene.getName());
			assertTrue(
					this.getClass().getCanonicalName()
							+ ": The editor game model is not the expected (should be just blank)",
					scene.getNotes() != null);
			assertNull(
					this.getClass().getCanonicalName()
							+ ": The editor game model is not the expected (should be just blank)",
					scene.getNotes().getDescription());
			assertNull(
					this.getClass().getCanonicalName()
							+ ": The editor game model is not the expected (should be just blank)",
					scene.getNotes().getTitle());
		}
	}

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)}.
	 * This method should remove all json files from disk before performing any save operation.
	 * This test put special emphasis on checking this aspect.
	 */
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

	@Override
	// Does nothing. Required because this class extends EditorActionTest to
	// take
	// advantage of some of the superclass' functionality
	protected Class getEditorAction() {
		return null;
	}

}
