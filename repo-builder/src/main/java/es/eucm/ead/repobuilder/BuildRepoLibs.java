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
package es.eucm.ead.repobuilder;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.demobuilder.img.ImgMagickUtils;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.repobuilder.libs.*;
import es.eucm.ead.repobuilder.libs.sound.SoundImageOrg;
import es.eucm.ead.repobuilder.libs.sound.SwampSounds;
import es.eucm.ead.repobuilder.libs.timemokapsule.TimeMokapsule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Javier Torrente on 26/09/14.
 */
public class BuildRepoLibs {

	private static final String[] LIBRARIES_IN_REPO = {
			MockupIconsLib.class.getName(), VectorCharacters.class.getName(),
			Monejos.class.getName(), FindingAJob.class.getName(),
			EatingOut.class.getName(), TimeMokapsule.class.getName(),
			SoundImageOrg.class.getName(), SwampSounds.class.getName() };

	public static void main(String[] args) {
		String outDir = null;
		String[] librariesToExport = null;
		String imageMagickDir = null;
		String engineLib = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				continue;
			}

			String arg = args[i].toLowerCase();
			if (arg.equals("/?") || arg.equals("/h") || arg.equals("-h")
					|| arg.equals("-help")) {
				usage();
			} else if (arg.equals("-out")) {
				if (i + 1 < args.length && args[i + 1] != null) {
					outDir = args[i + 1];
				} else {
					System.err
							.println("[Error] Valid directory path expected after -out");
				}
			} else if (arg.equals("-libs")) {
				if (i + 1 < args.length && args[i + 1] != null) {
					librariesToExport = args[i + 1].split(",");
					for (int j = 0; j < librariesToExport.length; j++) {
						for (int k = 0; k < LIBRARIES_IN_REPO.length; k++) {
							if (LIBRARIES_IN_REPO[k].toLowerCase().endsWith(
									librariesToExport[j].toLowerCase())) {
								librariesToExport[j] = LIBRARIES_IN_REPO[k];
								break;
							}
						}
					}
				} else {
					System.err
							.println("[Error] Comma-separated lib (classes) names expected after -libs");
				}
			} else if (arg.equals("-all")) {
				librariesToExport = LIBRARIES_IN_REPO;
			} else if (arg.equals("-imagemagick") && i < args.length - 1) {
				imageMagickDir = args[i + 1];
			} else if (arg.equals("-engine-lib") && i < args.length - 1) {
				engineLib = args[i + 1];
			}
		}

		if (outDir == null) {
			System.err.println("[Error] No valid directory path defined");
			usage();
			return;
		} else if (librariesToExport == null) {
			System.err.println("[Error] No libraries selected to export");
			usage();
			return;
		}

		if (imageMagickDir == null || !new FileHandle(imageMagickDir).exists()
				|| !new FileHandle(imageMagickDir).isDirectory()) {
			System.err
					.println("[Error] No directory for image magick was selected. This app cannot run without image magick");
			usage();
			return;
		}

		String version = version();

		if (!outDir.endsWith("/") && !outDir.endsWith("\\")) {
			outDir += "/" + version + "/";
		} else {
			outDir += version + "/";
		}

		LwjglNativesLoader.load();
		MockApplication.initStatics();

		ImgMagickUtils.imageMagickPath(imageMagickDir);

		System.out.println();

		int nitems = 0;
		List<FileHandle> previewGames = new ArrayList<FileHandle>();
		for (int i = 0; i < librariesToExport.length; i++) {
			String libName = librariesToExport[i];
			Class clazz = null;
			try {
				clazz = Class.forName(libName);
				RepoLibraryBuilder libBuilder = (RepoLibraryBuilder) (clazz
						.newInstance());
				libBuilder.setCommonProperty(RepoLibraryBuilder.VERSION,
						version);
				libBuilder.setCommonProperty(
						RepoLibraryBuilder.ENGINE_JAR_FOR_PREVIEW, engineLib);
				long time = System.currentTimeMillis();

				System.out.println("Exporting library " + (i + 1) + "/"
						+ librariesToExport.length + ": " + libName
						+ " ............");
				previewGames.add(libBuilder.export(outDir));
				nitems += libBuilder.getNumberOfItems();
				System.out.println("Library " + (i + 1) + "/"
						+ librariesToExport.length + ": " + libName
						+ " exported in " + (System.currentTimeMillis() - time)
						/ 1000F + " seconds.");
				System.out.println();

			} catch (ClassNotFoundException e) {
				System.err
						.println("[Error] Could not load library: " + libName);
			} catch (InstantiationException e) {
				System.err.println("[Error] Could not load instantiate: "
						+ libName);
			} catch (IllegalAccessException e) {
				System.err.println("[Error] Could not load instantiate: "
						+ libName);
			}
		}

		System.out.println("Export complete!!! " + nitems
				+ " elements in the repository. Opening previewer ...");
		LibPreview.main(new String[] { outDir });
	}

	private static void usage() {
		System.out.println("***********************");
		System.out.println("******* USAGE *********");
		System.out.println("***********************");

		System.out
				.println("BuildRepoLibs -out output/directory -libs LIBS_TO_GENERATE -imagemagick IMAGE_MAGICK_INSTALLATION_DIR [-engine-lib PATH_TO_ENGINE_JAR_WITH_DEPENDENCIES]");
		System.out.println("\tWhere LIBS_TO_GENERATE:");
		System.out.println("\t\t-all\t\tGenerate the whole repo");
		System.out
				.println("\t\tClassName1,ClassName2...ClassNameN\t\tGenerate libs associated to those class names");
		System.out.println("\tIMAGE_MAGICK_INSTALLATION_DIR;");
		System.out
				.println("\t\tShould be the absolute path to the installation dir of ImageMagick.");
		System.out.println("\t\tE.g.: \"C:\\ImageMagick\\\"");
		System.out
				.println("\t\tImageMagick is a free image conversion software this app uses for thumbnail creation and more. If you don't have it installed, you can download it for free at http://www.imagemagick.org/");
		System.out
				.println("\t\t PATH_TO_ENGINE_JAR_WITH_DEPENDENCIES is an optional parameter. If specified, it should point to the path of the engine jar-with-dependencies file that the exporter needs. If it is provided, a runnable jar file will be produced to preview the elements created for each library.");
	}

	private static String version() {
		return fillDigits(
				Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), 4)
				+ fillDigits(
						Integer.toString(Calendar.getInstance().get(
								Calendar.MONTH) + 1), 2)
				+ fillDigits(
						Integer.toString(Calendar.getInstance().get(
								Calendar.DAY_OF_MONTH)), 2)
				+ fillDigits(
						Integer.toString(Calendar.getInstance().get(
								Calendar.HOUR_OF_DAY)), 2);
	}

	private static String fillDigits(String str, int places) {
		while (str.length() < places) {
			str = "0" + str;
		}
		return str;
	}
}
