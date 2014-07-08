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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.utils.ZipUtils;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.JsonExtension;
import es.eucm.ead.schemax.Layer;

import java.io.*;
import java.util.Map;
import java.util.zip.*;

/**
 * This class contains the functionality for exporting a given game project to a
 * JAR file. It is placed into a separate project so it can be used either from
 * the eAdventure game editor (through an action) and also from an external
 * application (through {@link ExporterApplication}).
 */
public class Exporter {

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
		GameData gameData = null;
		Variables variables = null;

		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof GameData) {
				gameData = (GameData) modelComponent;
			} else if (modelComponent instanceof Variables) {
				variables = (Variables) modelComponent;
			}
		}

		if (gameData == null && variables == null) {
			return;
		}

		Behavior initBehavior = new Behavior();
		Init init = new Init();
		initBehavior.setEvent(init);

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
			loadSceneContent.setEntityUri(gameData.getInitialScene());
			loadSceneContent.setTarget("(layer s"
					+ Layer.SCENE_CONTENT.toString() + ")");
			initBehavior.getEffects().add(loadSceneContent);

			// Load initial scene
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

		modelEntity.getComponents().add(initBehavior);
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
	 * Exports the given game project, defined as an {@code editorGame} plus a
	 * map of an {@code editorScenes}, as a single self-contained jar file that
	 * can be run. The output jarfile generated embeds the engine library with
	 * dependencies and also the contents of the game.
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
	 * @param callback
	 *            A simple callback to provide updates on the exportation
	 *            progress. May be null.
	 */

	public void exportAsJar(String destiny, String source,
			String engineLibPath, Iterable<Map.Entry<String, Object>> entities,
			ExportCallback callback) {

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
		// Create a subfolder that means the root of the game in the Jar
		// (/assets/)
		FileHandle tempGameDir = tempDir.child(GameStructure.JAR_GAME_FOLDER);
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
			json.toJson(currentEntity, entityFile);
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
			if (field.getName().equals(FieldName.COMPONENTS.toString())) {
				for (ModelComponent sourceComponent : source.getComponents()) {
					if (!sourceComponent.getClass().getCanonicalName()
							.contains(EDITOR_COMPONENTS_PACKAGE)) {
						clone.getComponents().add(sourceComponent);
					}
				}
			} else if (field.getName().equals(FieldName.CHILDREN.toString())) {
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
