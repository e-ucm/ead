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
package es.eucm.ead.editor;

import com.badlogic.gdx.files.FileHandle;

import com.badlogic.gdx.utils.Json;
import es.eucm.ead.GameStructure;
import es.eucm.ead.editor.exporter.ExportCallback;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.effects.TemporalEffect;
import es.eucm.ead.schema.renderers.Image;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link es.eucm.ead.editor.exporter.Exporter}.
 * 
 * Created by Javier Torrente on 22/03/14.
 */
public class ExporterTest {

	/**
	 * {@link es.eucm.ead.schema.game.ModelEntity} properties stored to disk by
	 * {@link #testExportAsJAR()}
	 */
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	public static final String INITIAL_SCENE = "scene2";
	public static final int DURATION = 10;

	/**
	 * {@link es.eucm.ead.schema.editor.game.EditorGame} properties set by
	 * {@link #testExportAsJAR()}
	 */
	public static final String EDIT_SCENE = "scene3";
	public static final String APP_VERSION = "9.9.9";
	public static final String MODEL_VERSION = "157";

	/**
	 * Relative path of a "redux" version of the engine library used in this
	 * test
	 */
	private static final String ENGINE_LIB_PATH = "export/engine-desktop-lite.jr";

	/**
	 * Relative paths to images that are dynamically written to disk during this
	 * fake exportation process (using ImageIO)
	 */
	private static final String USED_IMAGE_01 = "used_image_01.png";
	private static final String USED_IMAGE_02 = "used_image_02.png";
	private static final String UNUSED_IMAGE_01 = "unused_image_01.png";
	private static final String[] TEST_IMAGES = new String[] { USED_IMAGE_01,
			USED_IMAGE_02, UNUSED_IMAGE_01 };

	/**
	 * The number of entries that should appear on the output jar file. Used for
	 * comparison (if the number of entries generated do not match this, the
	 * test fails)
	 */
	private static final int N_JAR_ENTRIES = 99;

	/**
	 * This is what the game.json file should contain once put into the target
	 * jar file. If the contents read do not match those, the test fails.
	 */
	private static final String EXPORTED_GAMEFILE_CONTENTS = "{width:1200,height:800,initialScene:scene2}";

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.control.Exporter#exportAsJar(String, String, String)},
	 * which indirectly serves for testing
	 * {@link es.eucm.ead.editor.actions.ExportGame}, which is the way the editor
	 * uses the exporter to generate jar files (internal use), and also
	 * {@link es.eucm.ead.editor.exporter.ExporterApplication}, a simple tool
	 * that allows exporting the games through bash (external use).
	 *
	 * This test does as follows:
	 * 1) Creates a new EditorGame that has set properties
	 *    that are defined either in {@link es.eucm.ead.schema.game.ModelEntity} or
	 *    in {@link es.eucm.ead.schema.editor.game.EditorGame}. Also creates
	 *    a scene map with 5 scenes that also have set properties from
	 *    {@link es.eucm.ead.schema.actors.Scene} and
	 *    {@link es.eucm.ead.schema.editor.actors.EditorScene}. The scenes
	 *    have scene elements that reference images (see {@link #TEST_IMAGES}).
	 *
	 * 2) Saves the game and scenes to disk using a simple {@link com.badlogic.gdx.utils.Json} object.
	 *    Also creates several fake images using BufferedImage, which are referenced in the
	 *    game definition created in (1), and copies them to the temp
	 *    directory the game and scenes are saved to. This tests that
	 *    {@link es.eucm.ead.editor.exporter.Exporter#copyNonJsonFiles(com.badlogic.gdx.files.FileHandle, com.badlogic.gdx.files.FileHandle)}
	 *    works properly.
	 *
	 * 3) Calls {@link Exporter#exportAsJar(String, String, String)} which in turns
	 *    exports the game.
	 *
	 *
	 * 4) VALIDATION. Once the exportation process completes, the number of entries in the target jar file is
	 *    compared to those expected. Also, the contents of the game.json file present in the target jar
	 *    file produced are read and compared to those expected, to test that no editor
	 *    information is included. If any of these comparisons fail, this test fails.
	 */
	public void testExportAsJAR() {
		// Make initialization of the model
		EditorGame editorGame = new EditorGame();
		// Set some of the properties in game that belong to class Game
		editorGame.setInitialScene(INITIAL_SCENE);
		editorGame.setWidth(WIDTH);
		editorGame.setHeight(HEIGHT);
		// Set some of the properties declared in EditorGame and which should
		// not be saved to disk
		editorGame.setModelVersion(MODEL_VERSION);
		editorGame.setAppVersion(APP_VERSION);
		editorGame.setEditScene(EDIT_SCENE);

		// Create five scenes
		Map<String, EditorScene> editorScenes = new HashMap<String, EditorScene>();
		for (int j = 0; j < 5; j++) {
			EditorScene scene = new EditorScene();
			// Set editor properties (not to be saved)
			scene.setName("XXX-" + j);
			Note note = new Note();
			note.setTitle("Title");
			note.setDescription("Description");
			scene.setNotes(note);
			// Set scene properties (to be saved)
			// Add 2 children
			for (int i = 0; i < 2; i++) {
				// Create the scene element. All its properties must be saved
				SceneElement sceneElement = new SceneElement();
				Image renderer = new Image();
				if (i == 0)
					renderer.setUri(USED_IMAGE_01.substring(
							USED_IMAGE_01.indexOf("/") + 1,
							USED_IMAGE_01.length()));
				else
					renderer.setUri(USED_IMAGE_02.substring(
							USED_IMAGE_02.indexOf("/") + 1,
							USED_IMAGE_02.length()));
				sceneElement.setRenderer(renderer);
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
			editorScenes.put("scene" + j, scene);
			editorGame.getSceneorder().add("scene" + j);
		}

		// Save the model
		final FileHandle tempDir = FileHandle.tempDirectory("eadtemp-export-");
		Json json = new Json();
		json.toJson(editorGame, tempDir.child(GameStructure.GAME_FILE));
		FileHandle sceneDir = tempDir.child(GameStructure.SCENES_PATH);
		sceneDir.mkdirs();
		for (String editorSceneId : editorScenes.keySet()) {
			json.toJson(editorScenes.get(editorSceneId),
					sceneDir.child(editorSceneId.toLowerCase()
							.endsWith(".json") ? editorSceneId : editorSceneId
							+ ".json"));
		}

		// Copy images to the tempDir
		FileHandle imagesFolder = tempDir.child(GameStructure.IMAGES_FOLDER);
		imagesFolder.mkdirs();
		Random random = new Random();
		for (String imagePath : TEST_IMAGES) {
			BufferedImage bufferedImage = new BufferedImage(40, 40,
					BufferedImage.TRANSLUCENT);
			Graphics2D graphics2D = bufferedImage.createGraphics();
			graphics2D.setColor(new Color(random.nextInt(256), random
					.nextInt(256), random.nextInt(256)));
			graphics2D.fillRect(10, 10, 30, 30);
			graphics2D.dispose();
			try {
				ImageIO.write(bufferedImage, "PNG",
						imagesFolder.child(imagePath).file());
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}

		// Copy jar lite file to a temp location
		FileHandle sourceJarLite = new SimpleFilesForTesting()
				.internal(ENGINE_LIB_PATH);

		FileHandle tempJarLite = FileHandle.tempFile("eadexport-jar");
		String newName = tempJarLite.name().substring(0,
				tempJarLite.name().lastIndexOf("."))
				+ ".jar";
		tempJarLite = tempJarLite.parent().child(newName);
		sourceJarLite.copyTo(tempJarLite);

		// Create temp File for the exported jar
		final FileHandle destinyJAR = FileHandle.tempFile("eadtemp-export-");

		// Get the game exported
		Exporter exporter = new Exporter(new Json());
		final FileHandle finalTempJarLite = tempJarLite;
		exporter.exportAsJar(destinyJAR.file().getAbsolutePath(), tempDir
				.file().getAbsolutePath(),
				tempJarLite.file().getAbsolutePath(), editorGame, editorScenes,
				new ExportCallback() {
					@Override
					public void error(String errorMessage) {
						fail(errorMessage);
						tempDir.deleteDirectory();
						destinyJAR.delete();
						finalTempJarLite.delete();
					}

					@Override
					public void progress(int percentage, String currentTask) {
						System.out.println("ExporterTest: progress exporting="
								+ percentage + " " + currentTask);
					}

					@Override
					public void complete(String completionMessage) {
						System.out.println("ExporterTest: completionMessage="
								+ completionMessage);
						// Check the game exported correctly by checking the
						// number of entries
						int nEntries = 0;
						JarInputStream inputStream = null;
						try {
							inputStream = new JarInputStream(destinyJAR.read());
							JarEntry entry = null;
							while ((entry = inputStream.getNextJarEntry()) != null) {
								// IF the entry read is the "game.json" file,
								// then read its contents to check
								// no editor data is present
								if (entry.getName().equals(
										GameStructure.JAR_GAME_FOLDER
												+ GameStructure.GAME_FILE)) {
									JarFile jarFile = new JarFile(destinyJAR
											.file());
									InputStream gameIS = jarFile
											.getInputStream(entry);
									BufferedReader reader = new BufferedReader(
											new InputStreamReader(gameIS));
									String line = null;
									String contents = "";
									while ((line = reader.readLine()) != null) {
										contents += line;
									}
									assertTrue(
											"The contents read from the game.json file found in the target jar file do not match the expected. May the cast from EditorGame to Game be wrong?",
											contents.equals(EXPORTED_GAMEFILE_CONTENTS));
									reader.close();
								}
								nEntries++;
							}
							inputStream.close();
						} catch (IOException e) {
							System.out
									.println("ExporterTest: Error checking output jar");
							e.printStackTrace();
							fail();

						} finally {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (IOException e) {
									System.out
											.println("ExporterTest + Error checking output jar");
									e.printStackTrace();
									fail();
								}
							}
						}

						assertEquals("The number of jar entries read ("
								+ nEntries + ") is not the expected ("
								+ N_JAR_ENTRIES + ")", nEntries, N_JAR_ENTRIES);

						tempDir.deleteDirectory();
						destinyJAR.delete();
						finalTempJarLite.delete();

					}
				});

	}
}
