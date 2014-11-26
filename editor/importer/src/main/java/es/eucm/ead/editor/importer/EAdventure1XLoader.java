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
package es.eucm.ead.editor.importer;

import es.eucm.eadventure.common.data.adventure.AdventureData;
import es.eucm.eadventure.common.data.animation.*;
import es.eucm.eadventure.common.data.animation.Frame;
import es.eucm.eadventure.common.data.chapter.Chapter;
import es.eucm.eadventure.common.data.chapter.elements.NPC;
import es.eucm.eadventure.common.data.chapter.resources.Resources;
import es.eucm.eadventure.common.data.chapter.scenes.Cutscene;
import es.eucm.eadventure.common.data.chapter.scenes.Slidescene;
import es.eucm.eadventure.common.loader.InputStreamCreator;
import es.eucm.eadventure.common.loader.Loader;
import es.eucm.eadventure.common.loader.incidences.Incidence;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier Torrente on 28/09/14.
 */
public class EAdventure1XLoader {

	public static final float STD_DURATION = 0.1F;

	public EAdventure1XLoader() {
	}

	public EAdventure1XGame load(String eAd1XGamePath) {
		EAdventure1XGame game = new EAdventure1XGame();
		File mainFolder = new File(eAd1XGamePath);
		FileInputStreamCreator fis = new FileInputStreamCreator(eAd1XGamePath);
		AdventureData adventureData = Loader.loadAdventureData(fis,
				new ArrayList<Incidence>());
		game.setAdventureData(adventureData);
		List<String> animationPaths = new ArrayList<String>();
		// Chapters
		for (Chapter chapter : adventureData.getChapters()) {
			// Player
			for (Resources resources : chapter.getPlayer().getResources()) {
				for (String assetPath : resources.getAssetValues()) {
					animationPaths.add(assetPath);
				}
			}

			// Characters
			for (NPC npc : chapter.getCharacters()) {
				// Resources
				for (Resources resources : npc.getResources()) {
					for (String assetPath : resources.getAssetValues()) {
						if (assetPath.toLowerCase().endsWith(".eaa")) {
							animationPaths.add(assetPath);
						}
					}
				}
			}

			// Cutscenes
			for (Cutscene cutscene : chapter.getCutscenes()) {
				if (cutscene instanceof Slidescene) {
					Slidescene slidescene = (Slidescene) cutscene;
					// Resources
					for (Resources resources : slidescene.getResources()) {
						for (String assetPath : resources.getAssetValues()) {
							if (assetPath.toLowerCase().endsWith(".eaa")) {
								animationPaths.add(assetPath);
							}
						}
					}
				}
			}
		}

		for (String animationPath : animationPaths) {
			if (animationPath.toLowerCase().endsWith(".eaa")) {
				game.getAnimations().put(animationPath,
						Loader.loadAnimation(fis, animationPath, fis));
			} else if (animationPath.toLowerCase().contains("_01")) {
				String firstFrame = animationPath.toLowerCase();
				String framesPrefix = firstFrame.substring(0,
						firstFrame.lastIndexOf("_"));
				String framesSuffix = firstFrame.contains(".") ? firstFrame
						.substring(firstFrame.lastIndexOf("."),
								firstFrame.length()) : "";
				if (framesSuffix.length() == 0) {
					if (new File(mainFolder, framesPrefix + "_01.png").exists()) {
						framesSuffix = ".png";
					} else if (new File(mainFolder, framesPrefix + "_01.jpg")
							.exists()) {
						framesSuffix = ".jpg";
					} else if (new File(mainFolder, framesPrefix + "_01.jpeg")
							.exists()) {
						framesSuffix = ".jpeg";
					}
				}

				int i = 1;
				Animation animation = new Animation(animationPath, fis);
				while (new File(mainFolder, framesPrefix + "_"
						+ (i < 10 ? "0" : "") + i + framesSuffix).exists()) {
					es.eucm.eadventure.common.data.animation.Frame frame = new Frame(
							fis, framesPrefix + "_" + (i < 10 ? "0" : "") + i
									+ framesSuffix,
							(long) (STD_DURATION * 1000), false);
					animation.getFrames().add(frame);
				}
				game.getAnimations().put(animationPath, animation);
			}

		}
		return game;
	}

	public static void main(String[] args) {
		EAdventure1XLoader loader = new EAdventure1XLoader();
		EAdventure1XGame game = loader
				.load("C:\\Program Files\\eAdventure1.5rc4\\Projects\\EatingOut");
		System.out.println();
	}

	private class FileInputStreamCreator implements InputStreamCreator,
			ImageLoaderFactory {

		private String eAd1XGamePath;

		public FileInputStreamCreator(String eAd1XGamePath) {
			if (!eAd1XGamePath.endsWith("/") && !eAd1XGamePath.endsWith("\\")) {
				this.eAd1XGamePath = eAd1XGamePath + "/";
			} else {
				this.eAd1XGamePath = eAd1XGamePath;
			}

		}

		@Override
		public InputStream buildInputStream(String filePath) {
			if (filePath.startsWith("/")) {
				filePath = filePath.substring(1, filePath.length());
			}

			File file = new File(eAd1XGamePath + filePath);
			if (file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public String[] listNames(String filePath) {
			if (filePath.startsWith("/")) {
				filePath = filePath.substring(1, filePath.length());
			}

			File file = new File(eAd1XGamePath + filePath);

			if (file != null && file.exists()) {
				File[] files = file.listFiles();
				String[] names = new String[files.length];
				for (int i = 0; i < files.length; i++) {
					names[i] = files[i].getName();
				}
				return names;
			}
			return new String[] {};
		}

		@Override
		public URL buildURL(String path) {
			if (path.startsWith("/")) {
				path = path.substring(1, path.length());
			}

			File file = new File(eAd1XGamePath + path);
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;

			}
		}

		@Override
		public Image getImageFromPath(String uri) {
			try {
				return ImageIO.read(buildInputStream(uri));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void showErrorDialog(String title, String message) {

		}
	}
}
