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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.utils.ZipUtils;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.JsonExtension;
import es.eucm.ead.schemax.ModelStructure;

import java.util.*;

/**
 * This class is meant to be a convenient utility for exporting games from the
 * command shell and batch exportation.
 * 
 * This class provides a method for exporting the game provided in a given path
 * to a given destiny (
 * {@link #exportAsJar(String, String, Integer, Integer, String)}). Underneath
 * it just loads the game project and then get it exported.
 * 
 * It also provides a main method ({@link #main(String[])}) to be used from the
 * command shell. It lets providing the path for the engine library used, the
 * format(s) to export to, and a path or a list of paths pointing to the
 * location of the project or projects that must be exported and the location of
 * the resulting files.
 * 
 * Created by Javier Torrente on 23/03/14.
 */
public class ExporterApplication {

	/**
	 * Used for separating lists of paths specified from the command shell.
	 */
	public static final String SEPARATOR = ",";

	/**
	 * Exports the given game project to the given destination as a standalone
	 * Android APK. It uses Maven.
	 * 
	 * @param projectPath
	 *            The full path to the game project. Cannot be null. It is
	 *            expected to be a folder with an editor-valid game project.
	 *            E.g.: ("/Users/aUser/eadgames/agame/")
	 * @param mavenPath
	 *            The full path to the system directory where maven is installed
	 *            (e.g. "/dev/maven/"). If null, exporter tries to resolve it
	 *            from system's environment variables
	 * @param assetsProjectPath
	 *            The absolute path to the directory where the ead/assets
	 *            project lives. If null, exporter tries to resolve if from
	 *            gameAssets. It is needed to copy bindings.json and engine's
	 *            skin.
	 * @param packageName
	 *            The main package for the application. It is important that
	 *            this package had not been used before for other standalone
	 *            mokap, as Google Play do not allow two apps with the same main
	 *            package. If null, a package name is automatically generated
	 *            from the appName.
	 * @param artifactId
	 *            The artifactId used for the pom. If {@code null}, an
	 *            artifactId containing only lowercase letters, dashes and
	 *            digits is generated automatically from the appName.
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
	 *            If true, the game occupies the whole screen but there is no
	 *            guarantee that game aspect ratio will be respected.
	 * @param destinationPath
	 *            The full path to export the game to. Cannot be null. E.g.:
	 *            "/Users/aUser/eadexports/techdemo.apk"
	 * @return True if the exportation completed successfully, false otherwise
	 */
	public static boolean exportAsApk(String projectPath, String mavenPath,
			String assetsProjectPath, String packageName, String artifactId,
			String appName, String pathToAppIcons, boolean fullScreen,
			String destinationPath) {

		return new ExportToApk(projectPath, destinationPath, packageName,
				artifactId, appName, pathToAppIcons, mavenPath,
				assetsProjectPath, fullScreen).run();
	}

	/**
	 * Exports the given game project to the given destination using the engine
	 * library passed as an argument.
	 * 
	 * @param projectPath
	 *            The full path to the game project. Cannot be null. It is
	 *            expected to be a folder with an editor-valid game project.
	 *            E.g.: ("/Users/aUser/eadgames/agame/")
	 * @param engineJarPath
	 *            The full path to the engine library used. Cannot be null.
	 *            Usually, this will be a Maven-generated jar with dependencies.
	 *            E.g.: (
	 *            "/Users/aUser/.m2/repository/es/e-ucm/ead/engine-desktop/1.0-SNAPSHOT/engine-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar"
	 *            )
	 * @param windowWidth
	 * @param windowHeight
	 *            Optional (can be null) values to specify a fixed window size
	 * @param destinationPath
	 *            The full path to export the game to. Cannot be null. E.g.:
	 *            "/Users/aUser/eadexports/techdemo.jar"
	 * @return True if the exportation completed successfully, false otherwise
	 */
	public static boolean exportAsJar(String projectPath, String engineJarPath,
			Integer windowWidth, Integer windowHeight, String destinationPath) {
		return new ExportToJar(projectPath, destinationPath, engineJarPath,
				windowWidth, windowHeight).run();
	}

	private static void loadAllEntities(Json json, FileHandle directory,
			Map<String, Object> entities) {
		loadAllEntities(json, directory, entities, "");
	}

	/**
	 * Iterates recursively through the given {@code directory} loading any
	 * {@link ModelEntity} found, which is placed into the {@code entities}. To
	 * determine if a file is an entity, it just checks that it has json
	 * extension.
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
			Map<String, Object> entities, String prefix) {
		if (directory == null || !directory.exists()
				|| !directory.isDirectory())
			throw new RuntimeException(
					"The directory provided is not valid (null, does not exist or it is not a directory): "
							+ (directory != null ? directory.file()
									.getAbsolutePath() : null));

		for (FileHandle child : directory.list()) {
			if (child.isDirectory()) {
				loadAllEntities(json, child, entities, prefix + child.name()
						+ "/");
			} else if (JsonExtension.hasJsonExtension(child.extension())
					&& !child.name().toLowerCase()
							.equals(ModelStructure.DESCRIPTOR_FILE)) {
				ModelEntity newScene = json.fromJson(ModelEntity.class, null,
						child);
				entities.put(prefix + child.name(), newScene);
			}

		}
	}

	/**
	 * See {@link #printUsage()} for instructions on how to use this utility
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Common properties
		String projectPath = null;
		String projectPathList = null;

		String destinyPath = null;
		String destinyPathList = null;

		String formatList = "jar";

		boolean help = false;

		// Jar properties
		String engineLibPath = null;
		Integer windowWidth = null, windowHeight = null;

		// Apk properties
		String packageName = null, artifactId = null, appName = null, appNameList = null, pathToAppIcons = null, pathToAppIconsList = null, mavenPath = null, assetsProjectPath = null;
		boolean fullScreen = true;

		if (args != null) {

			for (int i = 0; i < args.length; i++) {
				if (args[i].toLowerCase().equals("-help")) {
					help = true;
				} else if (args[i].toLowerCase().equals("-engine-lib")
						&& i + 1 < args.length) {
					engineLibPath = args[i + 1];
				} else if (args[i].toLowerCase().equals("-window-width")
						&& i + 1 < args.length) {
					try {
						windowWidth = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						System.out
								.println("[WARNING] WindowWidth specified is not valid: "
										+ windowWidth
										+ ". Default window size will be used.");
					}
				} else if (args[i].toLowerCase().equals("-window-height")
						&& i + 1 < args.length) {
					try {
						windowHeight = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException e) {
						System.out
								.println("[WARNING] WindowHeight specified is not valid: "
										+ windowHeight
										+ ". Default window size will be used.");
					}
				} else if (args[i].toLowerCase().equals("-maven-dir")
						&& i + 1 < args.length) {
					mavenPath = args[i + 1];
				} else if (args[i].toLowerCase().equals("-assets-project-dir")
						&& i + 1 < args.length) {
					assetsProjectPath = args[i + 1];
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
					formatList = args[i + 1];
				} else if (args[i].toLowerCase().equals("-format-list")
						&& i + 1 < args.length) {
					formatList = args[i + 1];
				} else if (args[i].toLowerCase().equals("-package")
						&& i + 1 < args.length) {
					packageName = args[i + 1];
				} else if (args[i].toLowerCase().equals("-artifact")
						&& i + 1 < args.length) {
					artifactId = args[i + 1];
				} else if (args[i].toLowerCase().equals("-app-name")
						&& i + 1 < args.length) {
					appName = args[i + 1];
				} else if (args[i].toLowerCase().equals("-app-name-list")
						&& i + 1 < args.length) {
					appNameList = args[i + 1];
				} else if (args[i].toLowerCase().equals("-app-icons")
						&& i + 1 < args.length) {
					pathToAppIcons = args[i + 1];
				} else if (args[i].toLowerCase().equals("-app-icons-list")
						&& i + 1 < args.length) {
					pathToAppIconsList = args[i + 1];
				} else if (args[i].toLowerCase().equals("-full-screen")
						&& i + 1 < args.length) {
					fullScreen = "true".equals(args[i + 1])
							|| "TRUE".equals(args[i + 1])
							|| "yes".equals(args[i + 1])
							|| "YES".equals(args[i + 1])
							|| "y".equals(args[i + 1])
							|| "y".equals(args[i + 1]);
				}
			}
		}

		// If there are missing arguments, or if the "-help" argument was
		// introduced, print out usage instructions and return
		if (help
				|| projectPath == null
				&& projectPathList == null
				|| destinyPath == null
				&& destinyPathList == null
				|| formatList.toLowerCase().contains("jar")
				&& engineLibPath == null
				|| formatList.toLowerCase().contains("apk")
				&& (appName == null && appNameList == null || pathToAppIcons == null
						&& pathToAppIconsList == null)) {
			printUsage();
		} else {

			List<String> projects = createListFromArg(projectPath,
					projectPathList);
			List<String> targets = createListFromArg(destinyPath,
					destinyPathList);
			List<String> appNames = createListFromArg(appName, appNameList);
			List<String> appIcons = createListFromArg(pathToAppIcons,
					pathToAppIconsList);

			if (windowWidth == null || windowHeight == null) {
				windowWidth = windowHeight = null;
			}

			// Check that in total, the number of projects and the number of
			// target files match. Otherwise print out usage instructions and
			// return.
			if (projects.size() != targets.size()) {
				printUsage();
			} else {

				for (int i = 0; i < projects.size(); i++) {
					FileHandle fh = new FileHandle(projects.get(i));
					String path = null;
					if (!fh.isDirectory()
							&& fh.extension().toLowerCase().equals("zip")) {
						FileHandle dir = FileHandle
								.tempDirectory("eadexportapp");
						dir.mkdirs();
						ZipUtils.unzip(fh, dir);
						path = dir.path();
					} else if (fh.isDirectory()) {
						path = projects.get(i);
					} else {
						throw new RuntimeException(
								"[Error] Unsupported source path: \""
										+ projects.get(i)
										+ "\" is neither a folder or a zip file!");
					}

					if (formatList.toLowerCase().contains("jar")) {
						ExporterApplication.exportAsJar(path, engineLibPath,
								windowWidth, windowHeight, targets.get(i));
					}

					if (formatList.toLowerCase().contains("apk")) {
						if (projects.size() == 1) {
							ExporterApplication.exportAsApk(path, mavenPath,
									assetsProjectPath, packageName, artifactId,
									appName, pathToAppIcons, fullScreen,
									targets.get(i));
						} else {
							ExporterApplication.exportAsApk(path, mavenPath,
									assetsProjectPath, null, null,
									appNames.get(i), appIcons.get(i),
									fullScreen, targets.get(i));
						}
					}
				}

			}
		}

	}

	private static List<String> createListFromArg(String singleArg,
			String listArg) {
		List<String> toReturn = new ArrayList<String>();
		if (singleArg != null) {
			toReturn.add(singleArg);
		}
		if (listArg != null) {
			for (String arg : listArg.split(SEPARATOR)) {
				toReturn.add(arg);
			}
		}
		return toReturn;
	}

	private static void printUsage() {
		System.out
				.println("This utility allows exporting one or several mokap game projects to JAR or APK format at the same time.");
		System.out.println();
		System.out.println("\tNotes:");
		System.out.println("----------");
		System.out
				.println("\t\tTake into account that to export as APK you need to have Maven installed and configured in your system!");
		System.out.println("\tSyntax:");
		System.out.println("----------");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication [OPTIONS] -project PATH_TO_THE_GAME_PROJECT -target PATH_TO_THE_OUTPUT_FILE");
		System.out.println("\tOr:");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication [OPTIONS] -project-list COMMA-SEPARATED_PATHS_TO_THE_GAME_PROJECTS -target-list COMMA-SEPARATED_PATHS_TO_THE_OUTPUTS");
		System.out.println();
		System.out.println("\tWhere [OPTIONS] has the next syntax:");
		System.out
				.println("\t\t[-format-list LIST_OF_FORMATS] [-engine-lib PATH_TO_THE_ENGINE_LIB] [-window-width WINDOW_WIDTH] [-window-height WINDOW_HEIGHT] [-full-screen TRUE|FALSE] [-maven-dir PATH_TO_MAVEN_INSTALLATION_DIR] [-assets-project-dir PATH_TO_ASSETS_PROJECT_DIR] [-app-name APP_NAME_FOR_APK] [-app-name-list COMMA-SEPARATED_LIST_OF_APPNAMES_FOR_APK] [-app-icons PATH_TO_ICONS_FOR_APK] [-app-icons-list COMMA-SEPARATED_LIST_OF_PATH_TO_ICONS_FOR_APK] [-package NAME_OF_APK_PACKAGE] [-artifact MAVEN_ARTIFACT_OF_APK]  ");
		System.out
				.println("\t\t\t-format-list LIST_OF_FORMATS\t\tMust provide the exportation formats. Possible options: jar | apk | jar,apk. If not specified, only jar format is produced");
		System.out
				.println("\t\t\t-engine-lib PATH_TO_THE_ENGINE_LIB\t\tNeeded only if JAR format is selected. Must point to the jar file containing the engine plus all dependencies");
		System.out
				.println("\t\t\t-window-width WINDOW_WIDTH\t\t(Optional). Needed only if JAR format is selected. Specifies a fixed window size for the game");
		System.out
				.println("\t\t\t-window-height WINDOW_HEIGHT\t\t(Optional). Needed only if JAR format is selected. Specifies a fixed window size for the game");
		System.out
				.println("\t\t\t-full-screen TRUE|FALSE\t\t(Optional). Needed only if APK format is selected. Specifies if the game must run fullscreen (true) or keep aspect ratio (false). By default this is set to true");
		System.out
				.println("\t\t\t-maven-dir PATH_TO_MAVEN_INSTALLATION_DIR\t\tNeeded only if APK format is selected. Must point to the path where maven is installed in the system. Can be omitted if MAVEN_HOME or MVN_HOME environment variables are set up");
		System.out
				.println("\t\t\t-assets-project-dir PATH_TO_ASSETS_PROJECT_DIR\t\t(Optional). Needed only if APK format is selected. Must point to the path where the ead/assets project lives in the system. Can be omitted if it can be resolved through GameAssets");
		System.out
				.println("\t\t\t-app-name APP_NAME_FOR_APK\t\tNeeded only if APK format is selected. Provides a user-friendly name for the application (e.g. Game Of Thrones)");
		System.out
				.println("\t\t\t-app-name-list COMMA-SEPARATED_LIST_OF_APPNAMES_FOR_APK\t\tList alternative to -app-name if multiple targets are used");
		System.out
				.println("\t\t\t-app-icons PATH_TO_ICONS_FOR_APK\t\tNeeded only if APK format is selected. Provides an absolute path to a PNG image, or a directory containing PNG images, to be used to generate icons for the APK.");
		System.out
				.println("\t\t\t-app-icons-list COMMA-SEPARATED_LIST_OF_PATH_TO_ICONS_FOR_APK\t\tNeeded only if APK format is selected. List alternative to -app-icons if multiple targets are used");
		System.out
				.println("\t\t\t-package NAME_OF_APK_PACKAGE\t\tOptional. Specify only if you want to override auto-generated package name for APK exportation (Google Play uses this package to identify apps)");
		System.out
				.println("\t\t\t-artifact MAVEN_ARTIFACT_OF_APK\t\tOptional. Specify only for APK exportation.");
		System.out.println();
		System.out.println("\tExamples:");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -format-list apk -app-name \"Game Of Thrones\" -app-icons /User/Home/got/icons -project /User/Home/got/game -target /User/Home/got/app.apk");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -engine-lib \"C:/Users/Javier Torrente/.m2/repository/es/e-ucm/ead/engine-desktop/1.0-SNAPSHOT/engine-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar\" -project \"C:\\Users\\Javier Torrente\\GIT_REPOS\\ead\\engine\\desktop\\src\\test\\"
						+ "resources\\techdemo\\\" -target \"C:\\Users\\Javier Torrente\\Downloads\\Exports\\techdemo.jar\"");
		System.out
				.println("\t\tjava es.eucm.ead.editor.exporter.ExporterApplication -engine-lib \"C:/Users/Javier Torrente/.m2/repository/es/e-ucm/ead/engine-desktop/1.0-SNAPSHOT/engine-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar\" -project-list \"C:\\Users\\Javier Torrente\\GIT_REPOS\\ead\\engine\\desktop\\src\\test\\resources\\techdemo,C:\\Users\\Javier Torrente\\GIT_REPOS\\parity2.0\\src\\main\\resources\\parity2.0\" -target-list \"C:\\Users\\Javier Torrente\\Downloads\\Exports\\techdemo.jar,C:\\Users\\Javier Torrente\\Downloads\\Exports\\parity.jar\"");
	}

	private abstract static class ExportMethod {

		protected static boolean exported = false;

		private String projectPath;
		private String destinationPath;

		public ExportMethod(String projectPath, String destinationPath) {
			this.projectPath = projectPath;
			this.destinationPath = destinationPath;
		}

		public boolean run() {
			exported = false;
			Map<String, Object> entities = new HashMap<String, Object>();
			FileHandle projectFileHandle = new FileHandle(projectPath);

			// Try to load all game entities
			GameAssets gameAssets = new GameAssets(new ExporterFiles(),
					new ExporterImageUtils()) {
				protected FileHandle[] resolveBindings() {
					return new FileHandle[] { resolve("bindings.json"),
							resolve("editor-bindings.json") };
				}
			};
			try {
				loadAllEntities(gameAssets, projectFileHandle, entities);
			} catch (SerializationException serializationException) {
				System.err
						.println("[ERROR] A serialization exception occurred while exporting "
								+ projectPath
								+ ". The project could not be exported.");
				return false;
			}

			// Export
			Exporter exporter = new Exporter(gameAssets);
			doExport(projectPath, destinationPath, entities, gameAssets,
					exporter, new DefaultCallback());
			return exported;
		}

		protected abstract void doExport(String projectPath,
				String destinationPath, Map<String, Object> entities,
				GameAssets gameAssets, Exporter exporter,
				ExportCallback callback);

		protected class DefaultCallback implements ExportCallback {
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
				exported = true;
			}
		}
	}

	private static class ExportToApk extends ExportMethod {

		private String packageName;
		private String artifactId;
		private String appName;
		private String pathToAppIcons;
		private String mavenPath;
		private String assetsProjectPath;
		private boolean fullScreen;

		private ExportToApk(String projectPath, String destinationPath,
				String packageName, String artifactId, String appName,
				String pathToAppIcons, String mavenPath,
				String assetsProjectPath, boolean fullScreen) {
			super(projectPath, destinationPath);
			this.packageName = packageName;
			this.artifactId = artifactId;
			this.appName = appName;
			this.pathToAppIcons = pathToAppIcons;
			this.mavenPath = mavenPath;
			this.assetsProjectPath = assetsProjectPath;
			this.fullScreen = fullScreen;
		}

		@Override
		protected void doExport(String projectPath, String destinationPath,
				Map<String, Object> entities, GameAssets gameAssets,
				Exporter exporter, ExportCallback callback) {
			String currentAssetsProjectPath = null;
			if (assetsProjectPath != null) {
				currentAssetsProjectPath = assetsProjectPath;
			} else {
				currentAssetsProjectPath = gameAssets.resolve("assets").path();
			}

			System.out
					.println("-------------------------------------------------------------------");
			System.out.println("  Starting generation of APK from "
					+ projectPath);
			System.out
					.println("-------------------------------------------------------------------");
			exporter.exportAsApk(destinationPath, projectPath, mavenPath,
					currentAssetsProjectPath, packageName, artifactId, appName,
					pathToAppIcons, fullScreen, entities.entrySet(), callback);
		}
	}

	private static class ExportToJar extends ExportMethod {

		private String engineJarPath;

		private Integer windowWidth;

		private Integer windowHeight;

		private ExportToJar(String projectPath, String destinationPath,
				String engineJarPath, Integer windowWidth, Integer windowHeight) {
			super(projectPath, destinationPath);
			this.engineJarPath = engineJarPath;
			this.windowHeight = windowHeight;
			this.windowWidth = windowWidth;
		}

		@Override
		protected void doExport(String projectPath, String destinationPath,
				Map<String, Object> entities, GameAssets gameAssets,
				Exporter exporter, ExportCallback callback) {
			System.out
					.println("-------------------------------------------------------------------");
			System.out.println("  Starting generation of JAR from "
					+ projectPath);
			System.out
					.println("-------------------------------------------------------------------");
			exporter.exportAsJar(destinationPath, projectPath, engineJarPath,
					entities.entrySet(), windowWidth, windowHeight,
					new DefaultCallback());
		}
	}

	private static class ExporterImageUtils implements GameAssets.ImageUtils

	{

		@Override
		public boolean imageSize(FileHandle fileHandle, Vector2 size) {
			Pixmap pixmap = new Pixmap(fileHandle);
			size.set(pixmap.getWidth(), pixmap.getHeight());
			return true;
		}

		@Override
		public boolean validSize(Vector2 size) {
			return true;
		}

		@Override
		public float scale(FileHandle src, FileHandle target) {
			src.copyTo(target);
			return 1.0f;
		}
	}
}
