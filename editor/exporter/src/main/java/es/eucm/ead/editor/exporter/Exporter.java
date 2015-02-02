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
package es.eucm.ead.editor.exporter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.engine.utils.ZipUtils;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.ead.schemax.JsonExtension;
import es.eucm.ead.schemax.Layer;
import org.apache.maven.shared.invoker.*;

import javax.imageio.ImageIO;

/**
 * This class contains the functionality for exporting a given game project to a
 * JAR file. It is placed into a separate project so it can be used either from
 * the eAdventure game editor (through an action) and also from an external
 * application (through {@link ExporterApplication}).
 */
public class Exporter {

	private static final String INIT_BEHAVIOR_ID = "initBehavior";

	/**
	 * Builds a {@link Behavior} component with just one {@link Init} behavior
	 * to the entity from editors' {@link GameData} and {@link Variables} editor
	 * components. This will make initializations (effects) when the entity is
	 * loaded. If no gameData or variables component is found, no component is
	 * added.
	 * 
	 * @param modelEntity
	 *            The entity
	 */
	public static void createInitComponent(ModelEntity modelEntity) {
		createInitComponent(modelEntity, null);
	}

	/**
	 * Builds a {@link Behavior} component with just one {@link Init} behavior
	 * to the entity from editors' {@link GameData} and {@link Variables} editor
	 * components. This will make initializations (effects) when the entity is
	 * loaded. If no gameData or variables component is found, no component is
	 * added.
	 * 
	 * @param modelEntity
	 *            The entity
	 * @param initialScene
	 *            Where will be started the game
	 */
	public static void createInitComponent(ModelEntity modelEntity,
			String initialScene) {
		GameData gameData = null;
		Variables variables = null;

		Behavior initBehavior = null;
		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof GameData) {
				gameData = (GameData) modelComponent;
			} else if (modelComponent instanceof Variables) {
				variables = (Variables) modelComponent;
			}
			String id = modelComponent.getId();
			if (id != null && id.equals(INIT_BEHAVIOR_ID)) {
				initBehavior = (Behavior) modelComponent;
			}
		}

		if (gameData == null && variables == null) {
			return;
		}

		if (initBehavior == null) {
			initBehavior = new Behavior();
			Init init = new Init();
			initBehavior.setEvent(init);
			initBehavior.setId(INIT_BEHAVIOR_ID);
		} else {
			initBehavior.getEffects().clear();
		}

		// First, register global variables
		if (variables != null) {
			for (VariableDef variableDef : variables.getVariablesDefinitions()) {
				ChangeVar changeVar = new ChangeVar();
				changeVar.setVariable(variableDef.getName());
				String initialValue = variableDef.getInitialValue();
				if (initialValue.toLowerCase().equals("true")) {
					initialValue = "btrue";
				} else if (initialValue.toLowerCase().equals("false")) {
					initialValue = "bfalse";
				} else {
					try {
						Integer.parseInt(initialValue);
						initialValue = "i" + initialValue;
					} catch (NumberFormatException e) {
						try {
							Float.parseFloat(initialValue);
							initialValue = "f" + initialValue;
						} catch (NumberFormatException e2) {
							if (!initialValue.startsWith("(")) {
								initialValue = "s" + initialValue;
							}
						}
					}
				}
				changeVar.setExpression(initialValue);
				changeVar.setContext(ChangeVar.Context.GLOBAL);
				initBehavior.getEffects().add(changeVar);
			}
		}

		if (gameData != null) {
			// Load initial scene
			AddEntity loadSceneContent = new AddEntity();
			if (initialScene == null) {
				initialScene = gameData.getInitialScene();
			}
			loadSceneContent.setEntityUri(initialScene);
			loadSceneContent.setTarget("(layer s"
					+ Layer.SCENE_CONTENT.toString() + ")");
			initBehavior.getEffects().add(loadSceneContent);

			// Load HUD
			AddEntity loadHud = new AddEntity();
			loadHud.setEntityUri(gameData.getHud());
			loadHud.setTarget("(layer s" + Layer.HUD.toString() + ")");
			initBehavior.getEffects().add(loadHud);

			// Set viewport
			SetViewport setViewport = new SetViewport();
			setViewport.setWidth(gameData.getWidth());
			setViewport.setHeight(gameData.getHeight());
			initBehavior.getEffects().add(setViewport);
		}

		Array<ModelComponent> components = modelEntity.getComponents();
		if (!components.contains(initBehavior, true)) {
			components.add(initBehavior);
		}
	}

	/**
	 * All components belonging to this package are ignored when the game is
	 * exported
	 */
	private static final String EDITOR_COMPONENTS_PACKAGE = "es.eucm.ead.schema.editor.components";

	/**
	 * Used for reading and writing game.json and scene.json files. When the
	 * exporter is invoked from the editor-core this argument will be
	 * editorGameAssets. When it is invoked from {@link ExporterApplication} it
	 * will be a simple JSON object.
	 */
	private Json json;

	public Exporter(Json json) {
		this.json = json;
	}

	/**
	 * Exports the given game project, defined as a collection of entities, as a
	 * single self-contained apk file that can be deployed and run on standalone
	 * mode in any Android device.
	 * 
	 * @param destiny
	 *            The full path of the destiny APK file where the output is to
	 *            be saved.
	 * @param source
	 *            The full path of the folder containing the game project. This
	 *            is required to copy the source images and other binaries.
	 * @param mavenPath
	 *            The full path to the system directory where maven is installed
	 *            (e.g. "/dev/maven/"). If null, exporter tries to resolve it
	 *            from system's MAVEN_HOME and MVN_HOME environment variables
	 * @param assetsProjectPath
	 *            Full (absolute) path to the folder that contains bindings.json
	 *            and skins/engine, as those assets must be added to the
	 *            application.
	 * @param packageName
	 *            The main package for the application. It is important that
	 *            this package had not been used before for other standalone
	 *            mokap, as Google Play do not allow two apps with the same main
	 *            package. If null, a package name is automatically generated
	 *            from the appName
	 * @param artifactId
	 *            The artifactId used for the pom. If {@code null}, an
	 *            artifactId containing only lowercase letters, dashes and
	 *            digits is generated automatically from the appName
	 * @param appName
	 *            The name of the application, in a user-friendly format (e.g.
	 *            Game Of Thrones). Cannot be {@code null}.
	 * @param pathToAppIcons
	 *            Full (absolute) path to either a PNG file or a directory
	 *            containing several PNG files that will be used to generate the
	 *            launcher icons for the apk. If {@code pathToAppIcons} is a
	 *            directory, the PNG images it contains are scanned and assigned
	 *            to the most suitable version (dpi) of the launcher icon. This
	 *            way it is possible to provide alternative versions of the
	 *            launcher icon. To the extent that is possible, available icons
	 *            are scaled down to create any missing icons of lower
	 *            resolution. Icons of higher resolution are never
	 *            auto-generated, as no image is ever scaled up.
	 * @param fullScreen
	 *            If true, the resulting App will run in fullscreen mode. It is
	 *            not guaranteed that game aspect ratio will be respected under
	 *            such circumstances. If false, the App forces the canvas to be
	 *            exactly the same size of the game.
	 * @param entities
	 *            An iterator to access in read-only mode all the
	 *            {@link ModelEntity}s of the game (scenes, game, etc.)
	 * @param callback
	 *            A simple callback to provide updates on the exportation
	 *            progress. May be {@code null}.
	 */
	public void exportAsApk(String destiny, String source, String mavenPath,
			String assetsProjectPath, String packageName, String artifactId,
			String appName, String pathToAppIcons, boolean fullScreen,
			Iterable<Map.Entry<String, Object>> entities,
			ExportCallback callback) {
		try {
			if (callback != null) {
				callback.progress(0, "  1) Generating temp maven project");
			}

			// Create basic structure for Maven project
			Dimension gameDim = null;
			if (!fullScreen) {
				gameDim = getGameDimension(entities);
			}
			FileHandle mavenProjectDir = createMavenProject(packageName,
					artifactId, appName, fullScreen ? -1 : gameDim.getWidth(),
					fullScreen ? -1 : gameDim.getHeight());
			if (callback != null) {
				callback.progress(5,
						"    Maven project successfully created at: "
								+ mavenProjectDir.path());
			}

			// Save game to assets subdir
			if (callback != null) {
				callback.progress(5, "  2) Saving game to assets/ ");
			}
			FileHandle assetsDir = mavenProjectDir.child("assets");
			assetsDir.mkdirs();
			saveGameForExport(assetsDir, entities);
			FileHandle sourceFile = new FileHandle(source);
			copyNonJsonFiles(assetsDir, sourceFile);
			if (callback != null) {
				callback.progress(15, "    Game saved correctly ");
			}

			// Copy engine skin and bindings
			if (callback != null) {
				callback.progress(15,
						"  3) Copying bindings.json and skins/engine to assets/ ");
			}
			FileHandle assetsProjectFileHandle = new FileHandle(
					assetsProjectPath);
			assetsProjectFileHandle.child("bindings.json").copyTo(
					assetsDir.child("bindings.json"));
			assetsProjectFileHandle.child("skins/engine").copyTo(
					assetsDir.child("skins/engine"));
			if (callback != null) {
				callback.progress(20, "    Files copied correctly");
			}

			// Process and copy app icons
			if (callback != null) {
				callback.progress(20, "  4) Generating app icons from "
						+ pathToAppIcons);
			}
			String iconsGenerated = processAndCopyAppIcons(mavenProjectDir,
					pathToAppIcons);
			if (iconsGenerated == null) {
				if (callback != null) {
					callback.error("** NO ICONS WERE GENERATED **. Apk cannot be created without at least one launcher icon. Please revise you have specified at least one valid app icon in PNG format");
				}
				return;
			}

			if (callback != null) {
				callback.progress(30, "    Next icons successfully generated: "
						+ iconsGenerated);
			}

			// Compile using maven
			if (callback != null) {
				callback.progress(30,
						"  5) Invoking maven to create APK. This step may take a while ");
			}

			InvocationRequest request = new DefaultInvocationRequest();

			request.setPomFile(mavenProjectDir.child("pom.xml").file());
			request.setGoals(Collections.singletonList("install"));
			request.setProfiles(Collections.singletonList("android-build"));

			Invoker invoker = new DefaultInvoker();
			File mavenHome = findMavenDir(mavenPath);
			if (mavenHome != null) {
				invoker.setMavenHome(mavenHome);
			}
			InvocationResult result = invoker.execute(request);
			if (result.getExitCode() == 0) {

				if (callback != null) {
					callback.progress(95, "    APK successfully generated");
				}

				// Copy output to destiny
				if (callback != null) {
					callback.progress(95, "  6) Copying apk to " + destiny);
				}
				FileHandle output = mavenProjectDir.child("target").child(
						ApkResource.OUTPUT_FILENAME + ".apk");
				output.copyTo(new FileHandle(destiny));
				if (callback != null) {
					callback.progress(100,
							"    Apk successfully copied to destiny. Cleaning temp folder");
					mavenProjectDir.deleteDirectory();
					callback.complete("**** SUCCESS ****");
				}
			}
			// Maven build returned error
			else {
				callback.error("An error occurred when building maven project generated. Make sure you have Maven installed and configured correctly. Also make sure all ead and libgdx artifacts are correctly installed");
			}

		} catch (Exception e) {
			callback.error("Error occurred. Build aborted. Exception message: "
					+ e.getMessage());
			e.printStackTrace();
			return;
		}

	}

	private Dimension getGameDimension(
			Iterable<Map.Entry<String, Object>> entities) {
		Iterator iterator = entities.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) (iterator
					.next());
			if (entry.getValue() instanceof ModelEntity) {
				ModelEntity entity = (ModelEntity) entry.getValue();
				for (ModelComponent component : entity.getComponents()) {
					if (component instanceof GameData) {
						GameData gameData = (GameData) component;
						Dimension dimension = new Dimension();
						dimension.setWidth(gameData.getWidth());
						dimension.setHeight(gameData.getHeight());
						return dimension;
					} else if (component instanceof Behavior) {
						Behavior b = (Behavior) component;
						if (b.getEvent() instanceof Init) {
							for (Effect e : b.getEffects()) {
								if (e instanceof SetViewport) {
									SetViewport setViewport = (SetViewport) e;
									Dimension dimension = new Dimension();
									dimension.setWidth(setViewport.getWidth());
									dimension
											.setHeight(setViewport.getHeight());
									return dimension;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private File findMavenDir(String path) {
		if (path == null) {
			path = System.getenv("MAVEN_HOME");
		}
		if (path == null) {
			path = System.getenv("MVN_HOME");
		}
		if (path != null && path.toLowerCase().endsWith("bin")
				|| path.toLowerCase().endsWith("bin/")
				|| path.toLowerCase().endsWith("bin\\")) {
			path = path.substring(0,
					Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\")));
		}
		if (path != null) {
			return new File(path);
		}
		return null;
	}

	private FileHandle createMavenProject(String packageName,
			String artifactId, String appName, int canvasWidth, int canvasHeight) {
		// Create temp dir
		FileHandle mavenProjectDir = FileHandle.tempDirectory("exporter-apk");
		// Write pom.xml, AndroidManifest.xml, res/layout/main.xml and
		// res/values/strings.xml
		mavenProjectDir.child("pom.xml").writeString(
				ApkResource.getPom(artifactId, appName), false, "UTF-8");
		String manifest = ApkResource.getAndroidManifest(packageName, appName,
				canvasWidth, canvasHeight);
		mavenProjectDir.child("AndroidManifest.xml").writeString(manifest,
				false, "UTF-8");
		FileHandle resDir = mavenProjectDir.child("res");
		resDir.mkdirs();
		FileHandle layoutDir = resDir.child("layout");
		layoutDir.mkdirs();
		layoutDir.child("main.xml").writeString(ApkResource.getLayoutMain(),
				false, "UTF-8");
		FileHandle valuesDir = resDir.child("values");
		valuesDir.child("strings.xml").writeString(
				ApkResource.getValuesStrings(appName), false, "UTF-8");

		return mavenProjectDir;
	}

	/*
	 * Searches pathToAppIcons for PNG files. Uses these images to generate as
	 * many launcher icons as possible by copying and scaling down.
	 */
	private String processAndCopyAppIcons(FileHandle mavenProjectDir,
			String pathToAppIcons) {
		FileHandle[] availableIconFileHandles = new FileHandle[ApkIcon.values().length];
		BufferedImage[] availableIconImages = new BufferedImage[ApkIcon
				.values().length];

		FileHandle appIcon = new FileHandle(pathToAppIcons);
		if (appIcon.isDirectory()) {
			for (FileHandle icon : appIcon.list()) {
				if (icon.extension().toLowerCase().equals("png")) {
					processIcon(mavenProjectDir, icon,
							availableIconFileHandles, availableIconImages);
				}
			}
		} else if (appIcon.extension().toLowerCase().equals("png")) {
			processIcon(mavenProjectDir, appIcon, availableIconFileHandles,
					availableIconImages);
		}

		// Use buffered images to create non existing icons
		for (int i = availableIconFileHandles.length - 1; i >= 0; i--) {
			FileHandle bigIconFileHandle = availableIconFileHandles[i];
			if (bigIconFileHandle == null) {
				continue;
			}
			int j = i - 1;
			while (j >= 0 && availableIconFileHandles[j] == null) {
				ApkIcon targetIcon = ApkIcon.values()[j];
				int resolution = targetIcon.getResolution();
				BufferedImage targetImage = new BufferedImage(resolution,
						resolution, BufferedImage.TRANSLUCENT);
				targetImage.createGraphics().drawImage(
						availableIconImages[i].getScaledInstance(resolution,
								resolution, BufferedImage.SCALE_SMOOTH), 0, 0,
						null);
				targetImage.flush();
				availableIconImages[j] = targetImage;
				availableIconFileHandles[j] = writeIcon(targetImage,
						targetIcon, mavenProjectDir);
				j--;
			}
			i = j + 1;
		}

		String logMessageToReturn = null;
		for (int i = 0; i < availableIconFileHandles.length; i++) {
			if (availableIconFileHandles[i] != null) {
				logMessageToReturn = (logMessageToReturn == null ? ""
						: logMessageToReturn + ",")
						+ ApkIcon.values()[i].getName();
			}
		}
		return logMessageToReturn;
	}

	private void processIcon(FileHandle mavenProjectDir, FileHandle icon,
			FileHandle[] availableIconFileHandles,
			BufferedImage[] availableIconImages) {
		try {
			BufferedImage iconBI = ImageIO.read(icon.file());
			ApkIcon apkIcon = ApkIcon.fromResolution(iconBI.getWidth());

			if (apkIcon == null) {
				return;
			}
			int position = apkIcon.ordinal();
			FileHandle drawableDir = mavenProjectDir.child("res").child(
					apkIcon.getPath());
			drawableDir.mkdirs();

			if (apkIcon.getResolution() < iconBI.getWidth()) {
				BufferedImage tmp = new BufferedImage(apkIcon.getResolution(),
						apkIcon.getResolution(), BufferedImage.TRANSLUCENT);
				tmp.createGraphics().drawImage(

						iconBI.getScaledInstance(apkIcon.getResolution(),
								apkIcon.getResolution(),
								BufferedImage.SCALE_SMOOTH), 0, 0, null);
				tmp.flush();
				iconBI = tmp;
				availableIconFileHandles[position] = writeIcon(iconBI, apkIcon,
						mavenProjectDir);
			} else {
				availableIconFileHandles[position] = drawableDir
						.child(ApkResource.APP_ICON_NAME + ".png");
				icon.copyTo(availableIconFileHandles[position]);
			}

			availableIconImages[position] = iconBI;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private FileHandle writeIcon(BufferedImage targetImage, ApkIcon targetIcon,
			FileHandle mavenProjectDir) {
		FileHandle resDir = mavenProjectDir.child("res");
		FileHandle drawableDir = resDir.child(targetIcon.getPath());
		drawableDir.mkdirs();
		FileHandle targetIconFileHandle = drawableDir
				.child(ApkResource.APP_ICON_NAME + ".png");
		try {
			ImageIO.write(targetImage, "png", targetIconFileHandle.file());
			return targetIconFileHandle;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Exports the given game project, defined as a collection of entities, as a
	 * single self-contained jar file that can be run. The output jarfile
	 * generated embeds the engine library with dependencies and also the
	 * contents of the game.
	 * 
	 * @param destiny
	 *            The full path of the destiny jar file where the output is to
	 *            be saved.
	 * @param source
	 *            The full path of the folder containing the game project. This
	 *            is required to copy the source images and other binaries.
	 * @param engineLibPath
	 *            The full path to the jar file that contains the full engine
	 *            library. This engine library will typically be
	 *            "engine-with-dependencies" library generated by Maven.
	 * @param entities
	 *            An iterator to access in read-only mode all the
	 *            {@link ModelEntity}s of the game (scenes, game, etc.)
	 * @param windowWidth
	 * @param windowHeight
	 *            Not-null windowWidth and windowHeight specify a fixed window
	 *            size for the game. If null, the game will just run fullscreen.
	 * @param callback
	 *            A simple callback to provide updates on the exportation
	 *            progress. May be null.
	 */

	public void exportAsJar(String destiny, String source,
			String engineLibPath, Iterable<Map.Entry<String, Object>> entities,
			Integer windowWidth, Integer windowHeight, ExportCallback callback) {

		if (engineLibPath == null || !new FileHandle(engineLibPath).exists()) {
			if (callback != null)
				callback.error("export.error.libnotfound");
			Gdx.app.error(
					"EditorIO.exportAsJar",
					"Exportation failed: the engine library could not be resolved or was not defined");
			return;
		}

		// Create a temp directory that will hold the copy of the game (/)
		FileHandle tempDir = FileHandle.tempDirectory("ead-export-");
		tempDir.mkdirs();
		// If not null windowWidth and windowHeight are provided, create
		// apparguments file and store it
		if (windowWidth != null && windowHeight != null) {
			Properties properties = new Properties();
			properties.setProperty("WindowWidth", "" + windowWidth);
			properties.setProperty("WindowHeight", "" + windowHeight);
			FileHandle appArgumentsFile = tempDir.child("app_arguments.txt");
			try {
				properties.store(appArgumentsFile.write(false),
						"Settings for EngineJarGame");
			} catch (IOException e) {
				// If anything goes wrong, just delete it
				appArgumentsFile.delete();
			}
		}
		// Create a subfolder that means the root of the game in the Jar
		// (/assets/)
		FileHandle tempGameDir = tempDir.child(ModelStructure.JAR_GAME_FOLDER);
		tempGameDir.mkdirs();
		if (callback != null)
			callback.progress(15, "export.progress.saving");

		try {
			// 1) Save the game omitting any editor components
			saveGameForExport(tempGameDir, entities);

			// Copy non json files
			if (callback != null)
				callback.progress(30, "export.progress.copying");

			FileHandle sourceFile = new FileHandle(source);
			copyNonJsonFiles(tempGameDir, sourceFile);

			// Destiny file
			FileHandle destinyJarFile = new FileHandle(destiny);

			// Create output stream for destiny file
			OutputStream fileOutputStream = destinyJarFile.write(false);
			ZipOutputStream zipOutputStream = new ZipOutputStream(
					fileOutputStream);

			// Merge game directory and engine jar into output stream
			FileHandle engineJarFile = new FileHandle(engineLibPath);
			if (callback != null)
				callback.progress(60, "export.progress.zipping");
			ZipUtils.mergeZipsAndDirsToZip(zipOutputStream, engineJarFile,
					tempDir);

			zipOutputStream.close();
			if (callback != null) {
				callback.progress(100, "export.progress.completed");
				callback.complete("export.successful");
			}
		} catch (FileNotFoundException e) {
			if (callback != null) {
				callback.error("export.failed");
			}
			Gdx.app.error("EditorIO.exportAsJar",
					"The engine library was not found", e);
		} catch (IOException e) {
			if (callback != null) {
				callback.error("export.failed");
			}
			Gdx.app.error(
					"EditorIO.exportAsJar",
					"An error occurred while writing the jar file while exporting",
					e);
		}

	}

	// ///////////////////////////////////////////
	// / PRIVATE EXPORTATION METHODS
	// //////////////////////////////////////////

	/**
	 * Saves the given editorGame and editorScenes omitting any editor
	 * components. The output of this is saved into {@code destiny}, which is
	 * meant to be an empty temp directory.
	 */
	private void saveGameForExport(FileHandle destiny,
			Iterable<Map.Entry<String, Object>> entities) {

		// Iterate through model entities and save them to disk
		for (Map.Entry<String, Object> currentEntry : entities) {
			Object currentEntity = currentEntry.getValue();
			// Create init component
			if (currentEntity instanceof ModelEntity) {
				createInitComponent((ModelEntity) currentEntity);
				// Remove all editor components
				currentEntity = cloneEntityExcludingEditorComponents((ModelEntity) currentEntity);
			}
			FileHandle entityFile = destiny.child(currentEntry.getKey());
			entityFile.parent().mkdirs();
			// Save
			json.toJson(currentEntity, null, entityFile);
		}
	}

	/**
	 * Makes a shallow clone of the {@link ModelEntity} {@code source} passed as
	 * an argument but omitting any editor {@link ModelComponent} so the
	 * resulting entity can be parsed by the engine.
	 * 
	 * This method makes a recursive call to process each {@link ModelEntity}
	 * child in {@code source}.
	 * 
	 * @param source
	 *            The {@link ModelEntity} to clone. May represent a game, a
	 *            scene, a scene element, etc.
	 * @return The shallow clone without editor components.
	 */
	private ModelEntity cloneEntityExcludingEditorComponents(ModelEntity source) {
		ModelEntity clone = new ModelEntity();
		for (Field field : ClassReflection.getDeclaredFields(source.getClass())) {
			field.setAccessible(true);
			if (field.getName().equals(FieldName.COMPONENTS)) {
				for (ModelComponent sourceComponent : source.getComponents()) {
					if (!sourceComponent.getClass().getCanonicalName()
							.contains(EDITOR_COMPONENTS_PACKAGE)) {
						clone.getComponents().add(sourceComponent);
					}
				}
			} else if (field.getName().equals(FieldName.CHILDREN)) {
				for (ModelEntity child : source.getChildren()) {
					clone.getChildren().add(
							cloneEntityExcludingEditorComponents(child));
				}
			} else {
				try {
					field.set(clone, field.get(source));
				} catch (ReflectionException e) {
					Gdx.app.debug(
							"Exporter.cloneEntityExcludingEditorComponents",
							"Error while removing editor components from ModelEntitye "
									+ source, e);

				}
			}

		}
		return clone;
	}

	/**
	 * Copies recursively every single file that has no json extension in the
	 * {@code source} folder to the given {@code destiny} folder.
	 * 
	 * Internal directories in {@code source} are only created in
	 * {@code destiny} if they contain at least one file that has no json
	 * extension. This avoids creating unnecessary empty folders.
	 * 
	 * This method is used to copy assets (e.g. images) from the project
	 * directory to the temporal directory while exporting.
	 * 
	 * The method does not check if {@code source} or {@code destiny} are
	 * folders and if they exist.
	 * 
	 * @param destiny
	 *            The folder to copy files recursively to
	 * @param source
	 *            The folder to copy files from
	 */
	private void copyNonJsonFiles(FileHandle destiny, FileHandle source) {
		// Iterate children in source
		for (FileHandle child : source.list()) {
			// If child is a directory, check first if it has at least one file
			// with no json extension
			if (child.isDirectory()) {
				boolean nonJsonContent = false;
				for (FileHandle grandSon : child.list()) {
					if (!JsonExtension.hasJsonExtension(grandSon.extension())) {
						nonJsonContent = true;
						break;
					}
				}
				// If the child folder has some content that has to be copied,
				// create the subdirectory in
				// destiny and copy its contents recursively
				if (nonJsonContent) {
					FileHandle targetChild = destiny.child(child.name());
					targetChild.mkdirs();
					copyNonJsonFiles(targetChild, child);
				}
			}
			// If the child is a file with no json extension, just copy
			else if (!JsonExtension.hasJsonExtension(child.extension())) {
				FileHandle targetChild = destiny.child(child.name());
				child.copyTo(targetChild);
			}
		}
	}

}
