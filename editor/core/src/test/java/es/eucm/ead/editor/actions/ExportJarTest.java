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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.editor.control.Exporter;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.effects.TemporalEffect;
import es.eucm.ead.schema.renderers.Image;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This class tests
 * {@link es.eucm.ead.editor.control.EditorIO#exportAsJar(String, String, es.eucm.ead.editor.model.Model, es.eucm.ead.editor.control.EditorIO.ExportCallback)}
 * and all related EditorIO's method.
 * 
 * Created by Javier Torrente on 22/03/14.
 */
public class ExportJarTest implements Model.ModelListener<LoadEvent> {

	/**
	 * {@link es.eucm.ead.schema.game.Game} properties stored to disk by
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
	private static final String ENGINE_LIB_PATH = "export/engine-desktop-lite.jar";

	/**
	 * Relative paths to images that are copied to the test game project that is
	 * dynamically generated in this test
	 */
	private static final String USED_IMAGE_01 = "export/images/used_image_01.png";
	private static final String USED_IMAGE_02 = "export/images/used_image_02.png";
	private static final String UNUSED_IMAGE_01 = "export/images/unused_image_01.png";
	private static final String[] TEST_IMAGES = new String[] { USED_IMAGE_01,
			USED_IMAGE_02, UNUSED_IMAGE_01 };

	/**
	 * The names of the jar entries that should appear on the output jar file
	 */
	private static final String JAR_ENTRIES = "es/\n"
			+ "es/eucm/\n"
			+ "es/eucm/ead/\n"
			+ "es/eucm/ead/engine/\n"
			+ "es/eucm/ead/engine/actors/\n"
			+ "i18n/\n"
			+ "bindings.json\n"
			+ "es/eucm/ead/engine/AbstractEngineObject.class\n"
			+ "es/eucm/ead/engine/actors/ActorEngineObject.class\n"
			+ "es/eucm/ead/engine/actors/SceneElementEngineObject.class\n"
			+ "es/eucm/ead/engine/actors/SceneEngineObject.class\n"
			+ "es/eucm/ead/engine/Assets$1.class\n"
			+ "es/eucm/ead/engine/Assets$AssetManager.class\n"
			+ "es/eucm/ead/engine/Assets.class\n"
			+ "es/eucm/ead/engine/Engine$1.class\n"
			+ "es/eucm/ead/engine/Engine.class\n"
			+ "es/eucm/ead/engine/EngineGameLoop.class\n"
			+ "es/eucm/ead/engine/EngineObject.class\n"
			+ "es/eucm/ead/engine/GameAssets.class\n"
			+ "es/eucm/ead/engine/GameLoop.class\n"
			+ "es/eucm/ead/engine/GameState.class\n"
			+ "es/eucm/ead/engine/GameView.class\n"
			+ "es/eucm/ead/engine/I18N$1.class\n"
			+ "es/eucm/ead/engine/I18N$Lang.class\n"
			+ "es/eucm/ead/engine/I18N.class\n"
			+ "es/eucm/ead/engine/VarsContext$1.class\n"
			+ "es/eucm/ead/engine/VarsContext$Variable.class\n"
			+ "es/eucm/ead/engine/VarsContext.class\n"
			+ "i18n/i18n.properties\n"
			+ "i18n/messages.properties\n"
			+ "i18n/messages_en.properties\n"
			+ "es/eucm/ead/schema/\n"
			+ "es/eucm/ead/schema/actors/\n"
			+ "es/eucm/ead/schema/behaviors/\n"
			+ "es/eucm/ead/schema/components/\n"
			+ "es/eucm/ead/schema/effects/\n"
			+ "es/eucm/ead/schema/game/\n"
			+ "es/eucm/ead/schema/renderers/\n"
			+ "es/eucm/ead/schema/renderers/frameanimation/\n"
			+ "es/eucm/ead/schema/actors/Scene.class\n"
			+ "es/eucm/ead/schema/actors/SceneElement.class\n"
			+ "es/eucm/ead/schema/behaviors/Behavior.class\n"
			+ "es/eucm/ead/schema/behaviors/Time.class\n"
			+ "es/eucm/ead/schema/behaviors/Touch$Type.class\n"
			+ "es/eucm/ead/schema/behaviors/Touch.class\n"
			+ "es/eucm/ead/schema/behaviors/Trigger.class\n"
			+ "es/eucm/ead/schema/components/Bounds.class\n"
			+ "es/eucm/ead/schema/components/Color.class\n"
			+ "es/eucm/ead/schema/components/Dimension.class\n"
			+ "es/eucm/ead/schema/components/LinearSequence.class\n"
			+ "es/eucm/ead/schema/components/Polygon.class\n"
			+ "es/eucm/ead/schema/components/RandomSequence.class\n"
			+ "es/eucm/ead/schema/components/Sequence.class\n"
			+ "es/eucm/ead/schema/components/Transformation.class\n"
			+ "es/eucm/ead/schema/components/VariableDef$Type.class\n"
			+ "es/eucm/ead/schema/components/VariableDef.class\n"
			+ "es/eucm/ead/schema/effects/ApplyEffectToTags.class\n"
			+ "es/eucm/ead/schema/effects/ChangeRenderer.class\n"
			+ "es/eucm/ead/schema/effects/ChangeVar.class\n"
			+ "es/eucm/ead/schema/effects/Effect.class\n"
			+ "es/eucm/ead/schema/effects/EndGame.class\n"
			+ "es/eucm/ead/schema/effects/GoScene.class\n"
			+ "es/eucm/ead/schema/effects/GoSubgame.class\n"
			+ "es/eucm/ead/schema/effects/Spin.class\n"
			+ "es/eucm/ead/schema/effects/TemporalEffect.class\n"
			+ "es/eucm/ead/schema/effects/Transform.class\n"
			+ "es/eucm/ead/schema/effects/Video.class\n"
			+ "es/eucm/ead/schema/game/Game.class\n"
			+ "es/eucm/ead/schema/renderers/AtlasImage.class\n"
			+ "es/eucm/ead/schema/renderers/Circle.class\n"
			+ "es/eucm/ead/schema/renderers/frameanimation/Frame.class\n"
			+ "es/eucm/ead/schema/renderers/frameanimation/FrameAnimation.class\n"
			+ "es/eucm/ead/schema/renderers/frameanimation/Timed.class\n"
			+ "es/eucm/ead/schema/renderers/Image.class\n"
			+ "es/eucm/ead/schema/renderers/NinePatch.class\n"
			+ "es/eucm/ead/schema/renderers/PolygonRenderer.class\n"
			+ "es/eucm/ead/schema/renderers/Rectangle.class\n"
			+ "es/eucm/ead/schema/renderers/Renderer.class\n"
			+ "es/eucm/ead/schema/renderers/Shape.class\n"
			+ "es/eucm/ead/schema/renderers/Text.class\n"
			+ "es/eucm/ead/schema/renderers/TextStyle.class\n"
			+ "es/eucm/ead/engine/utils/\n"
			+ "es/eucm/ead/engine/EngineDesktop$1.class\n"
			+ "es/eucm/ead/engine/EngineDesktop$2.class\n"
			+ "es/eucm/ead/engine/EngineDesktop$3.class\n"
			+ "es/eucm/ead/engine/EngineDesktop.class\n"
			+ "es/eucm/ead/engine/EngineJarGame.class\n"
			+ "es/eucm/ead/engine/utils/SwingEDTUtils$RunnableCallable.class\n"
			+ "es/eucm/ead/engine/utils/SwingEDTUtils$SwingFuture.class\n"
			+ "es/eucm/ead/engine/utils/SwingEDTUtils.class\n"
			+ "assets/game.json\n" + "assets/images/unused_image_01.png\n"
			+ "assets/images/used_image_01.png\n"
			+ "assets/images/used_image_02.png\n"
			+ "assets/scenes/scene0.json\n" + "assets/scenes/scene1.json\n"
			+ "assets/scenes/scene2.json\n" + "assets/scenes/scene3.json\n"
			+ "assets/scenes/scene4.json\n";

	/**
	 * Instead of extending EditorActionTest, a specific controller's is needed
	 * as it has to provide a "fake" path for a dummy engine jar path
	 */
	protected static Controller mockController;
	protected static MockPlatform mockPlatform;
	protected static Model mockModel;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
		mockPlatform = new MockPlatform();
		mockController = new Controller(mockPlatform, new MockFiles(),
				new Group()) {
			public String getEngineLibPath() {
				return ENGINE_LIB_PATH;
			}
		};
		mockModel = mockController.getModel();
	}

	@AfterClass
	public static void tearDownClass() {
		mockPlatform.removeTempFiles();
	}

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.control.Exporter#exportAsJar(String, String, String)}, which indirectly serves for testing
	 * {@link es.eucm.ead.editor.actions.ExportGame} and
	 * {@link es.eucm.ead.editor.control.EditorIO#exportAsJar(String, String, es.eucm.ead.editor.model.Model, es.eucm.ead.editor.control.EditorIO.ExportCallback)}
	 *
	 * This test does as follows:
	 * 1) Initializes the model with a new EditorGame that has set properties
	 *    that are defined either in {@link es.eucm.ead.schema.game.Game} or
	 *    in {@link es.eucm.ead.schema.editor.game.EditorGame}. Also puts into
	 *    the model 5 scenes that also have set properties from
	 *    {@link es.eucm.ead.schema.actors.Scene} and
	 *    {@link es.eucm.ead.schema.editor.actors.EditorScene}.
	 *
	 * 2) Saves the model to disk using {@link es.eucm.ead.editor.control.EditorIO#saveGameForExport(es.eucm.ead.editor.model.Model)}.
	 *
	 * 3) Checks that the saved game can be loaded by the engine. This
	 *    guarantees that no field defined in editor's schema was actually
	 *    stored to disk.
	 *
	 * 4) Opens the recently saved game in the editor and checks that the
	 *    properties defined in the engine's schema were stored OK by
	 *    matching them with the initial values of the model.
	 *
	 * 5) Calls {@link Exporter#exportAsJar(String, String, String)} which in turns
	 *    export the game. Then it checks that the jar entries of the output file
	 *    are those expected.
	 */
	public void testExportAsJAR() {
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
			mockModel.getScenes().put("scene" + j, scene);
			mockModel.getGame().getSceneorder().add("scene" + j);
		}

		// Init editorIO
		EditorIO editorIO = new EditorIO(mockController);

		// Save the model
		FileHandle tempDir = FileHandle.tempDirectory("eadtemp-export-");
		Method[] methods = EditorIO.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals("saveGameForExport")) {
				try {
					method.setAccessible(true);
					method.invoke(editorIO, tempDir, mockModel);
					method.setAccessible(false);
				} catch (IllegalAccessException e) {
					Gdx.app.error("ExportJarTest",
							"Error testing saveGameForExport", e);
					fail();
				} catch (InvocationTargetException e) {
					Gdx.app.error("ExportJarTest",
							"Error testing saveGameForExport", e);
					fail();
				}
				Gdx.app.debug("ExportJarTest", tempDir.path());
			}
		}

		// Copy images to the tempDir
		tempDir.child("images/").mkdirs();
		for (String imagePath : TEST_IMAGES) {
			FileHandle sourceImage = mockController.getApplicationAssets()
					.resolve(imagePath);
			sourceImage.copyTo(tempDir.child("images/").child(
					sourceImage.name()));
		}

		// Copy jar lite file to a temp location
		FileHandle sourceJarLite = mockController.getApplicationAssets()
				.resolve(ENGINE_LIB_PATH);
		FileHandle tempJarLite = FileHandle.tempFile("eadexport-jar");
		String newName = tempJarLite.name().substring(0,
				tempJarLite.name().lastIndexOf("."))
				+ ".jar";
		tempJarLite = tempJarLite.parent().child(newName);
		sourceJarLite.copyTo(tempJarLite);

		// Create an engine that loads the game
		try {
			MockGame mockGame = new MockGame(tempDir.path());
			mockGame.act();
		} catch (SerializationException e) {
			Gdx.app.debug(
					"ExportJarTest",
					"Error reading the game exported. This game cannot be loaded in the game engine",
					e);
			fail("Error reading the game exported. This game cannot be loaded in the game engine");
		}

		mockController.getModel().addLoadListener(this);

		// Create temp File for the exported jar
		FileHandle destinyJAR = FileHandle.tempFile("eadtemp-export-");

		// Load the game to check it actually does not include any
		// editor-specific data
		// Load the game
		mockController.action(OpenGame.class, tempDir.path());
		mockController.getEditorGameAssets().finishLoading();

		// Get the game exported
		try {
			Exporter.exportAsJar(tempDir.file().getAbsolutePath(), tempJarLite
					.file().getAbsolutePath(), destinyJAR.file()
					.getAbsolutePath());
		} catch (InterruptedException e) {
			Gdx.app.error("ExportJarTest", "Error Exporting", e);
		}

		// Check the game exported correctly by checking the name entries
		String entriesNames = "";
		try {
			JarInputStream inputStream = new JarInputStream(destinyJAR.read());
			JarEntry entry = null;
			while ((entry = inputStream.getNextJarEntry()) != null) {
				entriesNames += entry.getName() + "\n";
			}
		} catch (IOException e) {
			Gdx.app.error("ExportJarTest", "Error checking output jar", e);
		}

		assertEquals("The jar entry names read are not the expected",
				entriesNames, JAR_ENTRIES);

		tempDir.deleteDirectory();
		destinyJAR.delete();
		tempJarLite.delete();
	}

	@Override
	/*
	 * Method that gets invoked when the new game is loaded in the editor: Check
	 * that Game fields are available but EditorGame fields are not
	 */
	public void modelChanged(LoadEvent event) {
		// Fields in Game that should be initialized
		assertEquals("The game model is not the expected", INITIAL_SCENE,
				mockModel.getGame().getInitialScene());
		assertTrue("The game model is not the expected", mockModel.getGame()
				.getWidth() == WIDTH);
		assertTrue("The game model is not the expected", mockModel.getGame()
				.getHeight() == HEIGHT);
		// Fields in EditorGame that should not be initialized
		assertNull(
				"The editor game model is not the expected (should be just blank)",
				mockModel.getGame().getEditScene());
		assertNull(
				"The editor game model is not the expected (should be just blank)",
				mockModel.getGame().getModelVersion());
		assertNull(
				"The editor game model is not the expected (should be just blank)",
				mockModel.getGame().getAppVersion());

		for (String sceneId : mockModel.getScenes().keySet()) {
			EditorScene scene = mockModel.getScenes().get(sceneId);
			Behavior behavior = scene.getChildren().get(0).getBehaviors()
					.get(0);
			// Fields in Scene that should be initialized
			assertFalse("The game model is not the expected", scene
					.getChildren().get(0).isEnable());
			assertFalse("The game model is not the expected", scene
					.getChildren().get(0).isVisible());
			assertTrue(
					"The game model is not the expected",
					((TemporalEffect) behavior.getEffect()).getDuration() == DURATION);
			assertTrue(
					"The game model is not the expected",
					((Touch) behavior.getTrigger()).getType() == Touch.Type.ENTER);
			assertTrue("The game model is not the expected", scene
					.getChildren().size() == 2);
			// Fields in EditorScene that should not be initialized
			assertEquals(
					"The editor game model is not the expected (should be just blank)",
					sceneId, scene.getName());
			assertTrue(
					"The editor game model is not the expected (should be just blank)",
					scene.getNotes() != null);
			assertNull(
					"The editor game model is not the expected (should be just blank)",
					scene.getNotes().getDescription());
			assertNull(
					"The editor game model is not the expected (should be just blank)",
					scene.getNotes().getTitle());
		}
	}

}
