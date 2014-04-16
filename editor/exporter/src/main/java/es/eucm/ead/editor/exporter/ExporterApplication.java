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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.*;

/**
 * This class is meant to be a convenient utility for exporting games from the
 * command shell and batch exportation.
 * 
 * This class provides a method for exporting the game provided in a given path
 * to a given destiny ({@link #exportAsJar(String, String, String)}). Underneath
 * it just loads the game project and then it exports it.
 * 
 * It also provides a main method ({@link #main(String[])}) to be used from the
 * command shell. It lets providing the path for the engine library used, the
 * format(s) to export to, and a path or a list of paths pointing to the
 * location of the project or projects that must be EXPORTED and the location of
 * the resulting files.
 * 
 * Created by Javier Torrente on 23/03/14.
 */
public class ExporterApplication {

	/**
	 * Used for separating lists of paths specified from the command shell.
	 */
	public static final String SEPARATOR = ",";

	private static boolean EXPORTED = false;

	/**
	 * Exports the given game project to the given destination using the engine
	 * library passed as an argument.
	 * 
	 * @param projectPath
	 *            The full path to the game project. Cannot be null. It is
	 *            expected to be a folder with an editor-valid game project.
	 *            E.g.: ("C:\Users\Javier
	 *            Torrente\GIT_REPOS\ead\engine\desktop\src
	 *            \test\resources\techdemo")
	 * @param engineJarPath
	 *            The full path to the engine library used. Cannot be null.
	 *            Usually, this will be a Maven-generated jar with dependencies.
	 *            E.g.: (
	 *            "C:/Users/Javier Torrente/.m2/repository/es/e-ucm/ead/engine-desktop/1.0-SNAPSHOT/engine-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar"
	 *            )
	 * @param destinyPath
	 *            The full path to export the game to. Cannot be null. E.g.:
	 *            "C:\Users\Javier Torrente\Downloads\Exports\techdemo.jar"
	 * @return True if the exportation completed successfully, false otherwise
	 * @throws InterruptedException
	 */
	public static boolean exportAsJar(String projectPath, String engineJarPath,
			String destinyPath) throws InterruptedException {

		EXPORTED = false;

		Map<String, ModelEntity> entities = new HashMap<String, ModelEntity>();
		FileHandle projectFileHandle = new FileHandle(projectPath);

		// Try to load all game entities
		Json json = new Json();
		try {
			loadAllEntities(json, projectFileHandle, entities);
		} catch (SerializationException serializationException) {
			System.err
					.println("[ERROR] A serialization exception occurred while exporting "
							+ projectPath
							+ ". The project could not be EXPORTED.");
			return false;
		}

		// Export
		Exporter exporter = new Exporter(json);
		exporter.exportAsJar(destinyPath, projectPath, engineJarPath, entities
				.entrySet().iterator(), new ExportCallback() {
			@Override
			public void error(String errorMessage) {
				System.err.println("[ERROR] " + errorMessage);
			}

			@Override
			public void progress(int percentage, String currentTask) {
				System.out.println("[" + percentage + "] " + currentTask);
			}

			@Override
			public void complete(String completionMessage) {
				System.out.println("[EXPORTATION COMPLETE] "
						+ completionMessage);
				EXPORTED = true;
			}
		});

		return EXPORTED;
	}

	/**
	 * Iterates recursively through the given {@code directory} loading any
	 * {@link ModelEntity} found, which is placed into the {@code entities}. To
	 * determine if a file is an entity, it just checks that it extension
	 * isOfCategory {@link GameStructure#JSON_EXTENSION}.
	 * 
	 * @param json
	 *            The {@link Json} object provided by LibGDX to parse json files
	 *            into ModelEntities.
	 * @param directory
	 *            The directory that may contain {@link ModelEntity}s. If it is
	 *            {@code null} or it is not a directory, a
	 *            {@link RuntimeException} is thrown.
	 * @param entities
	 *            The map loaded entities are stored into.
	 * @throws RuntimeException
	 *             If {@code directory} is not valid
	 */
	private static void loadAllEntities(Json json, FileHandle directory,
			Map<String, ModelEntity> entities) {
		if (directory == null || !directory.exists()
				|| !directory.isDirectory())
			throw new RuntimeException(
					"The directory provided is not valid (null, does not exist or it is not a directory): "
							+ (directory != null ? directory.file()
									.getAbsolutePath() : null));

		for (FileHandle child : directory.list()) {
			if (child.isDirectory()) {
				loadAllEntities(json, child, entities);
			} else if (child.file().getAbsolutePath().toLowerCase()
					.endsWith(GameStructure.JSON_EXTENSION.toLowerCase())) {
				ModelEntity newScene = json.fromJson(ModelEntity.class, child);
				entities.put(child.nameWithoutExtension(), newScene);
			}

		}
	}

	/**
	 * See {@link #printUsage()} for instructions on how to use this utility
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		printUsage();

		String engineLibPath = null;

		String projectPath = null;
		String projectPathList = null;

		String destinyPath = null;
		String destinyPathList = null;

		String format = "jar";
		String formatList = null;

		boolean help = false;

		if (args != null) {

			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("-help")) {
					help = true;
				} else if (args[i].toLowerCase().equals("-engine-lib")
						&& i + 1 < args.length) {
					engineLibPath = args[i + 1];
				} else if (args[i].toLowerCase().equals("-project")
						&& i + 1 < args.length) {
					projectPath = args[i + 1];
				} else if (args[i].toLowerCase().equals("-project-list")
						&& i + 1 < args.length) {
					projectPathList = args[i + 1];
				} else if (args[i].toLowerCase().equals("-target")
						&& i + 1 < args.length) {
					destinyPath = args[i + 1];
				} else if (args[i].toLowerCase().equals("-target-list")
						&& i + 1 < args.length) {
					destinyPathList = args[i + 1];
				} else if (args[i].toLowerCase().equals("-format")
						&& i + 1 < args.length) {
					format = args[i + 1];
				} else if (args[i].toLowerCase().equals("-format-list")
						&& i + 1 < args.length) {
					formatList = args[i + 1];
				}
			}

		}

		// If there are missing arguments, or if the "-help" argument was
		// introduced, print out usage instructions and return
		if (help || engineLibPath == null || projectPath == null
				&& projectPathList == null || destinyPath == null
				&& destinyPathList == null) {
			printUsage();
		} else {

			List<String> projects = new ArrayList<String>();
			List<String> targets = new ArrayList<String>();

			if (projectPath != null) {
				projects.add(projectPath);
			}
			if (destinyPath != null) {
				targets.add(destinyPath);
			}

			if (projectPathList != null) {
				for (String path : projectPathList.split(SEPARATOR)) {
					projects.add(path);
				}
			}

			if (destinyPathList != null) {
				for (String path : destinyPathList.split(SEPARATOR)) {
					targets.add(path);
				}
			}

			// Check that in total, the number of projects and the number of
			// target files match. Otherwise print out usage instructions and
			// return.
			if (projects.size() != targets.size()) {
				printUsage();
			} else {

				for (int i = 0; i < projects.size(); i++) {
					ExporterApplication.exportAsJar(projects.get(i),
							engineLibPath, targets.get(i));
				}

			}
		}

	}

	private static void printUsage() {
		System.out
				.println("This utility allows exporting one or several eAdventure2 game projects to JAR format at the same time.");
		System.out.println();
		System.out.println("\tSyntax:");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -engine-lib [PATH TO THE ENGINE LIB] -project [PATH TO THE GAME PROJECT] -target [PATH TO THE JAR OUTPUT]");
		System.out.println("\tOr:");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -engine-lib [PATH TO THE ENGINE LIB] -project-list [COMMA-SEPARATED PATHS TO THE GAME PROJECTS] -target-list [COMMA-SEPARATED PATHS TO THE JAR OUTPUTS]");
		System.out.println();
		System.out.println("\tExamples:");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -engine-lib \"C:/Users/Javier Torrente/.m2/repository/es/e-ucm/ead/engine-desktop/1.0-SNAPSHOT/engine-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar\" -project \"C:\\Users\\Javier Torrente\\GIT_REPOS\\ead\\engine\\desktop\\src\\test\\"
						+ "resources\\techdemo\\\" -target \"C:\\Users\\Javier Torrente\\Downloads\\Exports\\techdemo.jar\"");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -engine-lib \"C:/Users/Javier Torrente/.m2/repository/es/e-ucm/ead/engine-desktop/1.0-SNAPSHOT/engine-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar\" -project-list \"C:\\Users\\Javier Torrente\\GIT_REPOS\\ead\\engine\\desktop\\src\\test\\resources\\techdemo,C:\\Users\\Javier Torrente\\GIT_REPOS\\parity2.0\\src\\main\\resources\\parity2.0\" -target-list \"C:\\Users\\Javier Torrente\\Downloads\\Exports\\techdemo.jar,C:\\Users\\Javier Torrente\\Downloads\\Exports\\parity.jar\"");
	}
}
