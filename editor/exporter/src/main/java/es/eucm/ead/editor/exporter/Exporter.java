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
import es.eucm.ead.schema.components.Initialization;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.JsonExtension;
import es.eucm.ead.schemax.Layer;

import java.io.*;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.zip.*;

/**
 * This class contains the functionality for exporting a given game project to a
 * JAR file. It is placed into a separate project so it can be used either from
 * the eAdventure game editor (through an action) and also from an external
 * application (through {@link ExporterApplication}).
 */
public class Exporter {

	/**
	 * Builds an {@link Initialization} component to the entity from editors'
	 * {@link GameData} and {@link Variables} editor components. The
	 * initialization component is automatically added to the game entity. If no
	 * gameData or variables component is found, init component is not added.
	 * 
	 * @param modelEntity
	 *            The entity
	 */
	public static void createInitComponent(ModelEntity modelEntity) {
		GameData gameData = null;
		Variables variables = null;
		Initialization init = null;

		for (ModelComponent modelComponent : modelEntity.getComponents()) {
			if (modelComponent instanceof GameData) {
				gameData = (GameData) modelComponent;
			} else if (modelComponent instanceof Variables) {
				variables = (Variables) modelComponent;
			} else if (modelComponent instanceof Initialization) {
				init = (Initialization) modelComponent;
			}
		}

		if (gameData == null && variables == null) {
			return;
		}

		if (init == null) {
			init = new Initialization();
		} else {
			init.getEffects().clear();
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
				init.getEffects().add(changeVar);
			}
		}

		if (gameData != null) {
			// Load initial scene
			AddEntity loadSceneContent = new AddEntity();
			loadSceneContent.setEntityUri(gameData.getInitialScene());
			loadSceneContent.setTarget("(layer s"
					+ Layer.SCENE_CONTENT.toString() + ")");
			init.getEffects().add(loadSceneContent);

			// Load initial scene
			AddEntity loadHud = new AddEntity();
			loadHud.setEntityUri(gameData.getHud());
			loadHud.setTarget("(layer s" + Layer.HUD.toString() + ")");
			init.getEffects().add(loadHud);

			// Set viewport
			SetViewport setViewport = new SetViewport();
			setViewport.setWidth(gameData.getWidth());
			setViewport.setHeight(gameData.getHeight());
			init.getEffects().add(setViewport);
		}

		modelEntity.getComponents().add(init);
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
			String engineLibPath,
			Iterable<Map.Entry<String, ModelEntity>> entities,
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
			mergeZipsAndDirsToJar(zipOutputStream, engineJarFile, tempDir);

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
			Iterable<Map.Entry<String, ModelEntity>> entities) {

		// Iterate through model entities and save them to disk
		for (Map.Entry<String, ModelEntity> currentEntry : entities) {
			ModelEntity currentEntity = currentEntry.getValue();
			// Create init component
			createInitComponent(currentEntity);

			// Remove all editor components
			ModelEntity simplifiedEntity = cloneEntityExcludingEditorComponents(currentEntity);
			FileHandle entityFile = destiny.child(currentEntry.getKey());
			entityFile.parent().mkdirs();
			// Save
			json.toJson(simplifiedEntity, entityFile);
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
			if (field.getName().equals(FieldNames.COMPONENTS.toString())) {
				for (ModelComponent sourceComponent : source.getComponents()) {
					if (!sourceComponent.getClass().getCanonicalName()
							.contains(EDITOR_COMPONENTS_PACKAGE)) {
						clone.getComponents().add(sourceComponent);
					}
				}
			} else if (field.getName().equals(FieldNames.CHILDREN.toString())) {
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

	/**
	 * Copies the contents of the {@code sources} file handles provided as
	 * arguments to the given output stream {@code destiny}.
	 * 
	 * Sources can be either directories, zip files or jar files. Sources that
	 * do not comply with these restrictions are just skipped.
	 * 
	 * @param destiny
	 *            The output stream where to write the contents
	 * @param sources
	 *            Jars, Zips or directories to copy contents from
	 */
	private void mergeZipsAndDirsToJar(ZipOutputStream destiny,
			FileHandle... sources) {
		try {
			for (FileHandle source : sources) {
				// If it is a jar or zip file
				if (hasZipOrJarExtension(source)) {
					InputStream fis = source.read();
					CheckedInputStream checksum = new CheckedInputStream(fis,
							new Adler32());
					ZipInputStream zis = new ZipInputStream(
							new BufferedInputStream(checksum));
					ZipEntry entry = null;

					// Write the contents of the origin zip file to the destiny
					// output
					while ((entry = zis.getNextEntry()) != null) {
						// write the files to the disk
						JarEntry newEntry = new JarEntry(entry.getName());

						destiny.putNextEntry(newEntry);
						byte[] readBuffer = new byte[1024];
						int bytesIn = 0;
						while ((bytesIn = zis.read(readBuffer)) != -1) {
							destiny.write(readBuffer, 0, bytesIn);
						}
						// close the Stream
						destiny.closeEntry();
					}
					zis.close();
				}
				// IF it is a dir
				else if (source.isDirectory()) {
					writeDirectoryToZip(destiny, source, "");
				}
			}
		} catch (Exception e) {
			Gdx.app.debug("EditorIO.mergeZipsAndDirsToJar",
					"An error occurred while exporting: mergeZipsAndDirsToJar",
					e);
		}
	}

	private boolean hasZipOrJarExtension(FileHandle fileHandle) {
		return hasExtension("zip", fileHandle)
				|| hasExtension("jar", fileHandle);
	}

	private boolean hasExtension(String extension, FileHandle fileHandle) {
		return extension.equals(fileHandle == null ? null : fileHandle
				.extension().toLowerCase());
	}

	/**
	 * Writes the given directory {@code source} to the given zip output stream
	 * {@code destiny}. Since this method works recursively, it needs as an
	 * argument the relative path of source's parent inside the zip file to
	 * create the new {@link java.util.zip.ZipEntry}. The first call to this
	 * method should can pass a null or blank {@code relPath}.
	 * 
	 * This method does not check if {@code source} is actually a directory.
	 * 
	 * @param destiny
	 *            The output stream where to write the contents
	 * @param source
	 *            The source directory to copy from
	 * @param relPath
	 *            The relative path of the source file's parent in the zip file.
	 *            (e.g. "/root", "/" or "")
	 * 
	 */
	private void writeDirectoryToZip(ZipOutputStream destiny,
			FileHandle source, String relPath) {

		try {
			FileHandle[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				FileHandle child = children[i];
				String childName = child.name();

				// If it's a directory, make recursive call
				if (child.isDirectory()) {
					String childRelativePath = canonicalizeChild(relPath,
							childName);
					writeDirectoryToZip(destiny, child, childRelativePath);
				}
				// If not a directory, create Zip Entry and write it to the
				// output stream
				else {

					InputStream fis = child.read();

					// Take the path of the file relative to the source
					String entryName = canonicalizeChild(relPath, childName);
					ZipEntry anEntry = new ZipEntry(entryName);

					// Write the file into the ZIP. It is surrounded by a
					// try-catch block to allow the loop to continue if the file
					// cannot be written (Otherwise the external try-catch will
					// capture the exception and no more files in the directory
					// would be put into the ZIP
					try {
						destiny.putNextEntry(anEntry);
						byte[] readBuffer = new byte[1024];
						int bytesIn = 0;
						while ((bytesIn = fis.read(readBuffer)) != -1) {
							destiny.write(readBuffer, 0, bytesIn);
						}
					} catch (ZipException zipException) {
						Gdx.app.error("EditorIO.writeDirectoryToZip",
								"Error exporting: writeDirectoryToZip",
								zipException);
					}

					// close the Stream
					fis.close();
					destiny.closeEntry();
				}
			}
		} catch (Exception e) {
			// handle exception
			Gdx.app.error("EditorIO.writeDirectoryToZip",
					"Error exporting: writeDirectoryToZip", e);
		}
	}

	/**
	 * Returns a canonical relative path by appending {@code relPath} and
	 * {@code childName}. Example: canonicalizeChild("parent\dir\relpath",
	 * "a_child") returns: "parent/dir/relpath/a_child"
	 * 
	 * @param relPath
	 *            The relative path of the parent folder. If it contains back
	 *            slashes (\), these are replaced by /
	 * @param childName
	 *            The file name of the file child to be appended to relPath
	 * @return a canonical version of relPath+"/"childName
	 */
	private String canonicalizeChild(String relPath, String childName) {
		String canonical;

		if (relPath != null && !relPath.equals("")) {
			relPath = relPath.replaceAll("\\\\", "/");
			canonical = relPath.endsWith("/") ? relPath + childName : relPath
					+ "/" + childName;
		} else {
			canonical = childName;
		}
		return canonical;
	}

}
