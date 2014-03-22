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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.commands.ModelCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.ead.schema.editor.game.EditorGame;
import es.eucm.ead.schema.game.Game;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.zip.*;

public class EditorIO implements LoadedCallback {

	/**
	 * Internal folder where the game is stored when it is exported as a Jar.
	 * This constant should be the same than the one defined in EngineJarGame,
	 * the class that launches jar games.
	 * 
	 * All the game contents (e.g. "scenes/", "game.json") should be placed
	 * under this folder in the jar file generated.
	 */
	private static final String JAR_GAME_FOLDER = "assets/";

	private static final String JSON_EXTENSION = ".json";

	private Controller controller;

	private EditorGameAssets editorGameAssets;

	private EditorGame game;

	private Map<String, EditorScene> scenes;

	public EditorIO(Controller controller) {
		this.controller = controller;
		this.editorGameAssets = controller.getEditorGameAssets();
	}

	/**
	 * This method starts the loading process of the game project stored in the
	 * given {@code loadingPath}. It should be invoked by
	 * {@link es.eucm.ead.editor.control.Controller#loadGame(String, boolean)}
	 * 
	 * {@link #load(String, boolean)} returns before the whole project is loaded
	 * because of inter-file dependencies: Before loading
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#GAME_FILE}, all
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#SCENES_PATH} have to be
	 * already loaded.
	 * 
	 * The loading process completes once
	 * {@link #finishedLoading(com.badlogic.gdx.assets.AssetManager, String, Class)}
	 * is invoked by {@link es.eucm.ead.editor.assets.EditorSceneLoader} and the
	 * number of scenesMetadata and scenes match (in this case it is assumed
	 * that all sceneMetadatas are already available). Then,
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#loadGame(com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback)}
	 * is invoked, which finishes the loading process.
	 * 
	 * @param loadingPath
	 *            The full path of the project to be loaded. Cannot be null.
	 * @param internal
	 *            Additional parameter required by
	 *            {@link es.eucm.ead.editor.assets.EditorGameAssets} to resolve
	 *            files. If true, the root path is the classpath
	 */
	public void load(String loadingPath, boolean internal) {
		game = null;
		scenes = new HashMap<String, EditorScene>();
		editorGameAssets.setLoadingPath(loadingPath, internal);
		// Game has, as dependencies, all data required
		editorGameAssets.loadGame(this);
	}

	/**
	 * Convenience method, saves a specified attribute from the {@link Model}.
	 * 
	 * @param target
	 *            {@link Game}, {@link EditorGame}, {@link Map} of Scenes or
	 *            {@link Model} (saves all it's attributes).
	 */
	public void save(Object target) {
		if (target == null)
			return;
		if (target instanceof EditorGame) {
			saveGame((EditorGame) target);
		} else if (target instanceof Map) {
			saveScenes((Map<String, EditorScene>) target);
		} else if (target instanceof Model) {
			saveAll((Model) target);
		} else {
			Gdx.app.error("EditorIO", "Couldn't save " + target.toString());
		}
	}

	/**
	 * Saves the whole model into disk. This method is the one invoked through
	 * the UI (e.g. when the user hits Ctrl+S)
	 * 
	 * @param model
	 *            The model that should be stored into disk
	 */
	public void saveAll(Model model) {
		// First of all, remove all json files persistently from disk
		removeAllJsonFilesPersistently();
		saveGame(model.getGame());
		saveScenes(model.getScenes());
	}

	/**
	 * Exports the given {@code model} embedded into the engine library as a
	 * single self-contained jar file.
	 * 
	 * @param destiny
	 *            The path of the destiny jar file where the output is to be
	 *            saved.
	 * @param model
	 *            The model representing the game to be saved.
	 * @param callback
	 *            A simple callback to provide updates on the exportation
	 *            progress. May be null.
	 */
	public void exportAsJar(String destiny, Model model, ExportCallback callback) {
		I18N i18N = controller.getApplicationAssets().getI18N();

		if (controller.getEngineLibPath() == null
				|| !new FileHandle(controller.getEngineLibPath()).exists()) {
			callback.error(i18N.m("export.error.libnotfound"));
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
		FileHandle tempGameDir = tempDir.child(JAR_GAME_FOLDER);
		tempGameDir.mkdirs();
		if (callback != null)
			callback.progress(15, i18N.m("export.progress.saving"));

		try {
			// 1) Save the game upcasted to the Engine's schema
			saveGameForExport(tempGameDir, model);

			// Copy non json files
			if (callback != null)
				callback.progress(30, i18N.m("export.progress.copying"));
			this.copyNonJsonFiles(tempGameDir);

			// Destiny file
			FileHandle destinyJarFile = new FileHandle(destiny);

			// Create output stream for destiny file
			OutputStream fileOutputStream = destinyJarFile.write(false);
			ZipOutputStream zipOutputStream = new ZipOutputStream(
					fileOutputStream);

			// Merge game directory and engine jar into output stream
			FileHandle engineJarFile = new FileHandle(
					controller.getEngineLibPath());
			if (callback != null)
				callback.progress(60, i18N.m("export.progress.zipping"));
			mergeZipsAndDirsToJar(zipOutputStream, engineJarFile, tempDir);

			zipOutputStream.close();
			if (callback != null) {
				callback.progress(100, i18N.m("export.progress.completed"));
				callback.complete(i18N.m("export.successful"));
			}
		} catch (FileNotFoundException e) {
			if (callback != null) {
				callback.error(i18N.m("export.failed"));
			}
			Gdx.app.error("EditorIO.exportAsJar",
					"The engine library was not found", e);
		} catch (IOException e) {
			if (callback != null) {
				callback.error(i18N.m("export.failed"));
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
	 * Saves the given {@code model} casted to the basic schema so all the
	 * editor's extra parameters are removed. The output of this is saved into
	 * {@code destiny}
	 * 
	 * This method is intended for exporting the game for final release only.
	 * 
	 * First, it casts the {@code model}'s editorgame to
	 * {@link es.eucm.ead.schema.game.Game} and saves it to the temp folder.
	 * 
	 * Second, it casts the {@code model}'s editorscenes to
	 * {@link es.eucm.ead.schema.actors.Scene} and saves them to the temp
	 * folder.
	 */
	private void saveGameForExport(FileHandle destiny, Model model) {
		// Save simplified game
		Game simplifiedGame = (Game) upcastToEngineSchema(model.getGame());
		editorGameAssets.toJson(simplifiedGame,
				destiny.child(EditorGameAssets.GAME_FILE));

		// Save simplified scenes
		FileHandle scenesFH = destiny.child(EditorGameAssets.SCENES_PATH);
		scenesFH.mkdirs();
		for (Map.Entry<String, EditorScene> entry : model.getScenes()
				.entrySet()) {
			Scene simplifiedScene = (Scene) upcastToEngineSchema(entry
					.getValue());

			String scenePath = entry.getKey();
			if (!scenePath.toLowerCase().endsWith(JSON_EXTENSION)) {
				scenePath += JSON_EXTENSION;
			}
			editorGameAssets.toJson(simplifiedScene, scenesFH.child(scenePath));
		}
	}

	/**
	 * This method creates a shallow copy of the given object using its
	 * superclass' constructor to instantiate the new object. For example, if
	 * the {@code object} given as an argument is of type
	 * {@link es.eucm.ead.schema.editor.game.EditorGame}, the returned object
	 * will be of type {@link es.eucm.ead.schema.game.Game}. The copy is created
	 * using reflection and introspection.
	 * 
	 * This is required by
	 * {@link #saveGameForExport(com.badlogic.gdx.files.FileHandle, es.eucm.ead.editor.model.Model)}
	 * . The underlying {@link com.badlogic.gdx.utils.Json} class invokes the
	 * object's getClass() method which always returns the class used to
	 * instantiate the object and therefore simple upcasting does not work.
	 * 
	 * @param object
	 *            The editor's schema object that has to be cloned as an
	 *            engine's schema object. Note: this object should exposed a
	 *            Java Bean exposing all its properties through getters and
	 *            setters.
	 * 
	 * @return The engine's schema object containing a shallow copy of
	 *         {@code object}. May be null if either {@code object} is null or
	 *         if an internal reflection exception is thrown.
	 */
	private Object upcastToEngineSchema(Object object) {
		if (object == null)
			return null;

		final Class clazz = object.getClass().getSuperclass();

		try {
			Object copy = clazz.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : properties) {
				Method getter = property.getReadMethod();
				Method setter = property.getWriteMethod();
				if (getter != null && setter != null) {
					setter.invoke(copy, getter.invoke(object));
				}
			}
			return copy;
		} catch (InstantiationException e) {
			Gdx.app.debug("EditorIO.upcastToEngineSchema",
					"Error while upcasting object of type " + object.getClass()
							+ " to type " + object.getClass().getSuperclass(),
					e);
		} catch (IllegalAccessException e) {
			Gdx.app.debug("EditorIO.upcastToEngineSchema",
					"Error while upcasting object of type " + object.getClass()
							+ " to type " + object.getClass().getSuperclass(),
					e);
		} catch (IntrospectionException e) {
			Gdx.app.debug("EditorIO.upcastToEngineSchema",
					"Error while upcasting object of type " + object.getClass()
							+ " to type " + object.getClass().getSuperclass(),
					e);
		} catch (InvocationTargetException e) {
			Gdx.app.debug("EditorIO.upcastToEngineSchema",
					"Error while upcasting object of type " + object.getClass()
							+ " to type " + object.getClass().getSuperclass(),
					e);
		}
		return null;
	}

	/**
	 * Copies recursively the contents of the project to the given
	 * {@code destiny} folder using
	 * {@link #copyNonJsonFiles(com.badlogic.gdx.files.FileHandle, com.badlogic.gdx.files.FileHandle)}
	 * .
	 */
	private void copyNonJsonFiles(FileHandle destiny) {
		FileHandle source = new FileHandle(editorGameAssets.getLoadingPath());
		copyNonJsonFiles(destiny, source);
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
					if (!grandSon.path().toLowerCase().endsWith(JSON_EXTENSION)) {
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
			else if (!child.path().toLowerCase().endsWith(JSON_EXTENSION)) {
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
				if (source.path().toLowerCase().endsWith(".zip")
						|| source.path().toLowerCase().endsWith(".jar")) {
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
					String childRelativePath = "";
					if (relPath != null && !relPath.equals("")) {
						relPath = relPath.replaceAll("\\\\", "/");
						childRelativePath = relPath.endsWith("/") ? relPath
								+ childName : relPath + "/" + childName;
					} else {
						childRelativePath = childName;
					}
					writeDirectoryToZip(destiny, child, childRelativePath);
				}
				// If not a directory, create Zip Entry and write it to the
				// output stream
				else {

					InputStream fis = child.read();

					// Take the path of the file relative to the source
					String entryName = "";
					if (relPath != null && !relPath.equals("")) {
						relPath = relPath.replaceAll("\\\\", "/");
						entryName = relPath.endsWith("/") ? relPath + childName
								: relPath + "/" + childName;
					} else {
						entryName = childName;
					}

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

	private void saveGame(Object game) {
		// Update the appVersion and model version for this game
		((EditorGame) game).setAppVersion(controller.getAppVersion());
		((EditorGame) game).setModelVersion(controller.getModelVersion());
		editorGameAssets.toJsonPath(game, EditorGameAssets.GAME_FILE);
	}

	private void saveScenes(Map<String, EditorScene> scenes) {
		for (Map.Entry<String, EditorScene> entry : scenes.entrySet()) {
			editorGameAssets.toJsonPath(entry.getValue(),
					editorGameAssets.convertSceneNameToPath(entry.getKey()));
		}
	}

	/**
	 * Removes all json files from disk under the
	 * {@link es.eucm.ead.editor.assets.EditorGameAssets#getLoadingPath()}
	 * folder.
	 * 
	 * 
	 * NOTE: This method should only be invoked from
	 * {@link #saveAll(es.eucm.ead.editor.model.Model)}, before the model is
	 * saved to disk
	 */
	private void removeAllJsonFilesPersistently() {
		String loadingPath = controller.getEditorGameAssets().getLoadingPath();
		deleteJsonFilesRecursively(controller.getEditorGameAssets().absolute(
				loadingPath));
	}

	/**
	 * Deletes the json files from a directory recursively
	 * 
	 * @param directory
	 *            The file object pointing to the root directory from where json
	 *            files must be deleted
	 */
	private void deleteJsonFilesRecursively(FileHandle directory) {
		// Delete dir contents
		if (!directory.exists() || !directory.isDirectory())
			return;

		for (FileHandle child : directory.list()) {
			if (child.isDirectory()) {
				deleteJsonFilesRecursively(child);
			} else {
				if ("json".equals(child.extension())) {
					child.delete();
				}
			}

		}

		// Remove the directory if it's empty.
		if (directory.list().length == 0) {
			directory.deleteDirectory();
		}
	}

	@Override
	public void finishedLoading(AssetManager assetManager, String fileName,
			Class type) {
		if (type == EditorScene.class) {
			String sceneId = editorGameAssets.resolve(fileName)
					.nameWithoutExtension();
			EditorScene scene = assetManager.get(fileName);
			scenes.put(sceneId, scene);
			// Once scenes have been loaded, load
		} else if (type == EditorGame.class) {
			game = assetManager.get(fileName);
		}

		// If everything is loaded, trigger to load command
		if (editorGameAssets.isDoneLoading()) {
			// FIXME commands should only be created in actions
			controller.command(new ModelCommand(controller.getModel(), game,
					scenes));
		}
	}

	/**
	 * Callback for getting updates on the exportation process
	 */
	public interface ExportCallback {

		public void error(String errorMessage);

		public void progress(int percentage, String currentTask);

		public void complete(String completionMessage);
	}
}
