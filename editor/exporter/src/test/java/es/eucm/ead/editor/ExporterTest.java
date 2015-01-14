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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import es.eucm.ead.editor.exporter.ExportCallback;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.exporter.ExporterApplication;
import es.eucm.ead.editor.exporter.ExporterFiles;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.components.Versions;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests {@link Exporter}.
 * 
 * Created by Javier Torrente on 22/03/14.
 */
public class ExporterTest {

	/**
	 * Editor properties stored to disk
	 */
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	public static final String INITIAL_SCENE = "scene2";

	/**
	 * Editor properties set
	 */
	public static final String APP_VERSION = "9.9.9";

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
	private static final String EXPORTED_GAMEFILE_CONTENTS = "{components:[{class:es.eucm.ead.schema.components.behaviors.Behavior,effects:[{class:es.eucm.ead.schema.effects.AddEntity,entityUri:"
			+ INITIAL_SCENE
			+ ",target:\"(layer sscene_content)\"},{class:es.eucm.ead.schema.effects.AddEntity,target:\"(layer shud)\"},{class:es.eucm.ead.schema.effects.SetViewport,width:"
			+ WIDTH
			+ ",height:"
			+ HEIGHT
			+ "}],event:{class:es.eucm.ead.schema.components.behaviors.events.Init}}]}";

	@Test
	public void test() {
		// Test width default and specific window sizes
		testExportAsJAR(null, null);
		testExportAsJAR(1280, 800);
	}

	/**
	 * Tests {@link Exporter#exportAsJar}, which indirectly serves for testing
	 * ExportGame action, which is the way the editor uses the exporter to
	 * generate jar files (internal use), and also {@link ExporterApplication},
	 * a simple tool that allows exporting the games through bash (external
	 * use).
	 * 
	 * This test does as follows: 1) Creates a new ModelEntity as editor game
	 * that has set properties that are defined either in {@link ModelEntity} or
	 * in the {@link GameData} component. Also creates a scene map with 5 scenes
	 * that also have engine and editor components. The scenes have scene
	 * elements that reference images (see {@link #TEST_IMAGES}).
	 * 
	 * 2) Saves the game and scenes to disk using a simple {@link Json} object.
	 * Also creates several fake images using BufferedImage, which are
	 * referenced in the game definition created in (1), and copies them to the
	 * temp directory the game and scenes are saved to. This tests that
	 * {@link Exporter#copyNonJsonFiles(FileHandle, FileHandle)} works properly.
	 * 
	 * 3) Calls {@link Exporter#exportAsJar} which in turns exports the game.
	 * 
	 * 4) VALIDATION. Once the exportation process completes, the number of
	 * entries in the target jar file is compared to those expected. Also, the
	 * contents of the game.json file present in the target jar file produced
	 * are read and compared to those expected, to test that no editor
	 * information is included. If any of these comparisons fail, this test
	 * fails.
	 */
	public void testExportAsJAR(final Integer windowWidth,
			final Integer windowHeight) {
		// Make initialization of the model
		Map<String, Object> modelEntityMap = new HashMap<String, Object>();
		ModelEntity editorGame = new ModelEntity();
		// Set some of the properties in game that belong to class Game
		GameData gameData = new GameData();
		gameData.setInitialScene(INITIAL_SCENE);
		gameData.setWidth(WIDTH);
		gameData.setHeight(HEIGHT);
		editorGame.getComponents().add(gameData);
		// Set some editor components which should
		// not be saved to disk
		Versions versions = new Versions();
		versions.setAppVersion(APP_VERSION);
		EditState editState = new EditState();
		editState.setSceneorder(new Array<String>());
		editorGame.getComponents().add(versions);
		editorGame.getComponents().add(editState);
		modelEntityMap.put(ResourceCategory.GAME.getCategoryPrefix(),
				editorGame);

		// Create five scenes
		for (int j = 0; j < 5; j++) {
			ModelEntity scene = new ModelEntity();
			// Set editor properties (not to be saved)
			Note noteComponent = new Note();
			noteComponent.setDescription("Description");
			noteComponent.setTitle("XXX-" + j);
			scene.getComponents().add(noteComponent);

			// Set scene properties (to be saved)
			// Add 2 children
			for (int i = 0; i < 2; i++) {
				// Create the scene element. All its properties must be saved
				ModelEntity sceneElement = new ModelEntity();
				Image renderer = new Image();
				if (i == 0)
					renderer.setUri(USED_IMAGE_01.substring(
							USED_IMAGE_01.indexOf("/") + 1,
							USED_IMAGE_01.length()));
				else
					renderer.setUri(USED_IMAGE_02.substring(
							USED_IMAGE_02.indexOf("/") + 1,
							USED_IMAGE_02.length()));
				sceneElement.getComponents().add(renderer);

				GoScene effect = new GoScene();
				effect.setSceneId("name");

				Behavior behavior = new Behavior();
				Touch touch = new Touch();
				behavior.setEvent(touch);
				Array<Effect> effects = new Array<Effect>();
				effects.add(effect);
				behavior.setEffects(effects);

				sceneElement.getComponents().add(behavior);

				scene.getChildren().add(sceneElement);
			}
			modelEntityMap.put(ResourceCategory.SCENES_PATH + "scene" + j
					+ ".json", scene);
			editState.getSceneorder().add(
					ResourceCategory.SCENES_PATH + "scene" + j + ".json");
		}

		// Save the model
		final FileHandle tempDir = FileHandle.tempDirectory("eadtemp-export-");
		final Json json = new Json();
		for (Entry<String, Object> entry : modelEntityMap.entrySet()) {
			FileHandle entityFile = tempDir.child(entry.getKey());
			entityFile.parent().mkdirs();
			json.toJson(entry.getValue(), entityFile);
		}

		// Copy images to the tempDir
		FileHandle imagesFolder = tempDir.child(ModelStructure.IMAGES_FOLDER);
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
		FileHandle sourceJarLite = new ExporterFiles()
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
		Exporter exporter = new Exporter(json);
		final FileHandle finalTempJarLite = tempJarLite;
		exporter.exportAsJar(destinyJAR.file().getAbsolutePath(), tempDir
				.file().getAbsolutePath(),
				tempJarLite.file().getAbsolutePath(),
				modelEntityMap.entrySet(), windowWidth, windowHeight,
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
								// no editor components are present
								if (entry.getName().equals(
										ModelStructure.JAR_GAME_FOLDER
												+ ModelStructure.GAME_FILE)) {
									JarFile jarFile = new JarFile(destinyJAR
											.file());
									InputStream gameIS = jarFile
											.getInputStream(entry);
									BufferedReader reader = new BufferedReader(
											new InputStreamReader(gameIS));
									String line;
									String contents = "";
									while ((line = reader.readLine()) != null) {
										contents += line;
									}
									reader.close();

									ModelEntity game = json.fromJson(
											ModelEntity.class, contents);
									Behavior behavior = (Behavior) game
											.getComponents().get(0);
									assertEquals(Init.class, behavior
											.getEvent().getClass());
									Array<Effect> effects = behavior
											.getEffects();
									AddEntity addScene = (AddEntity) effects
											.get(0);
									assertEquals(addScene.getTarget(),
											"(layer sscene_content)");
									assertEquals(addScene.getEntityUri(),
											"scene2");
									AddEntity addHud = (AddEntity) effects
											.get(1);
									assertEquals(addHud.getTarget(),
											"(layer shud)");
									SetViewport setViewport = (SetViewport) effects
											.get(2);
									assertEquals(setViewport.getWidth(), 1200);
									assertEquals(setViewport.getHeight(), 800);
								}
								// If the entry read is the "app_arguments.txt"
								// file, then check it had to be produced and
								// that it is OK
								if (entry.getName().equals("app_arguments.txt")) {

									if (windowWidth == null
											|| windowHeight == null) {
										fail("app_arguments.txt file should not be present, since window width and height are null");
									} else {
										// Read file
										JarFile jarFile = new JarFile(
												destinyJAR.file());
										InputStream appProperties = jarFile
												.getInputStream(entry);
										Properties properties = new Properties();
										properties.load(appProperties);
										assertEquals(
												"" + windowWidth,
												properties
														.getProperty("WindowWidth"));
										assertEquals(
												"" + windowHeight,
												properties
														.getProperty("WindowHeight"));
									}
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

						int nJarEntries = (windowWidth != null && windowHeight != null) ? N_JAR_ENTRIES + 1
								: N_JAR_ENTRIES;

						assertEquals("The number of jar entries read ("
								+ nEntries + ") is not the expected ("
								+ nJarEntries + ")", nEntries, nJarEntries);

						tempDir.deleteDirectory();
						destinyJAR.delete();
						finalTempJarLite.delete();

					}
				});

	}
}
