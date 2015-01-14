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
package es.eucm.ead.engine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * Launcher for a game that has been exported as a standalone JAR application
 * (everything embedded into a single jar file).
 * 
 * It initializes the engine desktop full screen and launches the game which is
 * expected to live under the assets/ folder.
 * 
 * For more details on the structure a game in JAR format has, see the wiki
 * page: <a target="_blank"
 * href="https://github.com/e-ucm/ead/wiki/Game-JAR-format"
 * >https://github.com/e-ucm/ead/wiki/Game-JAR-format</a>
 * 
 * Created by Javier Torrente on 20/03/14.
 */
public class EngineJarGame {

	/**
	 * Relative path of the folder that contains the game assets (game.json,
	 * scenes/sceneX.json...)
	 */
	public static final String GAME_PATH = "assets/";

	/**
	 * Relative path to the folder that contains the images to be used as icons
	 * for the application. Any image found under this folder will be treated as
	 * an icon.
	 */
	public static final String APP_ICONS_PATH = "appicons/";

	/**
	 * Internal Java properties file with settings (e.g. window size). May not
	 * be present. (Note: update Exporter if this value changes)
	 */
	public static final String APP_ARGUMENTS = "app_arguments.txt";

	/**
	 * Property key for setting a fixed window width. If provided,
	 * {@link #WINDOW_HEIGHT} must be present too. (Note: update Exporter if
	 * this value changes)
	 */
	public static final String WINDOW_WIDTH = "WindowWidth";

	/**
	 * Property key for setting a fixed window height. If provided,
	 * {@link #WINDOW_WIDTH} must be present too. (Note: update Exporter if this
	 * value changes)
	 */
	public static final String WINDOW_HEIGHT = "WindowHeight";

	/**
	 * App icon filenames supported
	 */
	private static final String[] ICON_FILENAMES = { "16.png", "32.png",
			"64.png", "128.png", "256.png", "512.png", "1024.png" };

	public static void main(String args[]) {
		// Create the engine
		Dimension windowSize = getWindowSize();
		EngineDesktop engine = new EngineDesktop(windowSize.width,
				windowSize.height);
		// Load and set app icons
		List<? extends Image> icons = loadApplicationIcons();
		if (icons.size() > 0) {
			engine.setApplicationIcons(icons);
		}
		// Run the game
		engine.run(GAME_PATH, true);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	/**
	 * Loads a set of application icons, if any available. On desktop Java uses
	 * these icons instead of the Java logo on the OS task bar and also on the
	 * upper left little icon used in the Window.
	 * 
	 * These icons are expected to live under the {@link #APP_ICONS_PATH} folder
	 * inside the jar. They also must be provided in PNG format with the names
	 * specified in {@link #ICON_FILENAMES}. There's no obligation of providing
	 * a specific number of these icons - it could be any number between 0 and
	 * ICON_FILENAMES.length
	 * 
	 * @return A list with the images loaded, ready to be passed to the
	 *         {@link javax.swing.JFrame#setIconImages(java.util.List)} method
	 */
	private static java.util.List<? extends Image> loadApplicationIcons() {
		List<BufferedImage> list = new ArrayList<BufferedImage>();
		for (String iconSubpath : ICON_FILENAMES) {
			String iconPath = APP_ICONS_PATH + iconSubpath;
			InputStream inputStream = EngineJarGame.class
					.getResourceAsStream(iconPath);
			if (inputStream != null) {
				try {
					BufferedImage bufferedImage = ImageIO.read(inputStream);
					list.add(bufferedImage);
				} catch (IOException e) {
					Gdx.app.debug(EngineJarGame.class.getCanonicalName(),
							"Exception reading icon: " + iconPath, e);
				}
			}
		}
		return list;
	}

	/*
	 * Calculates window size. It tries to determine window's width and height
	 * from an internal properties file but, if unreadable or not provided, just
	 * uses the full size of the screen
	 */
	private static Dimension getWindowSize() {
		InputStream inputStream = EngineJarGame.class
				.getResourceAsStream(APP_ARGUMENTS);
		if (inputStream != null) {
			Properties properties = new Properties();
			try {
				properties.load(inputStream);
				Object screenWidthStr = properties.get(WINDOW_WIDTH);
				Object screenHeightStr = properties.get(WINDOW_HEIGHT);
				if (screenWidthStr != null && screenHeightStr != null) {
					try {
						Integer screenWidth = Integer.parseInt(""
								+ screenWidthStr);
						Integer screenHeight = Integer.parseInt(""
								+ screenHeightStr);
						Dimension dim = new Dimension();
						dim.setSize(screenWidth, screenHeight);
						return dim;
					} catch (NumberFormatException e) {
						Gdx.app.debug(
								EngineJarGame.class.getCanonicalName(),
								"Bad screen width (W) or height (H)".replace(
										"W", "" + screenWidthStr).replace("H",
										"" + screenHeightStr), e);
					}

				}
			} catch (IOException e) {
				Gdx.app.debug(EngineJarGame.class.getCanonicalName(),
						"Exception reading " + APP_ARGUMENTS, e);
			}
		}

		// By default, return screen size
		// Determine the size of the window for the game to be full screen
		Gdx.app.debug(EngineJarGame.class.getCanonicalName(),
				"Using default window size (full screen)");
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		Dimension dim = new Dimension();
		dim.setSize(screenWidth, screenHeight);
		return dim;
	}
}
