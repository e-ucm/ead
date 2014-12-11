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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import es.eucm.ead.engine.effects.VideoEngineObject;
import es.eucm.ead.engine.utils.DesktopImageUtils;
import es.eucm.ead.engine.utils.SwingEDTUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Desktop Application for running eAdventure games.
 */
public class EngineDesktop {

	/**
	 * Default application width.
	 */
	public static final int DEFAULT_WIDTH = 800;

	/**
	 * Default application height.
	 */
	public static final int DEFAULT_HEIGHT = 600;

	/**
	 * Application window.
	 */
	public static LwjglFrame frame;

	/**
	 * Application width.
	 */
	private int width;

	/**
	 * Application height.
	 */
	private int height;

	/**
	 * Builds a desktop application using the default size.
	 * 
	 * @see #DEFAULT_HEIGHT
	 * @see #DEFAULT_WIDTH
	 */
	public EngineDesktop() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * Builds a desktop application.
	 * 
	 * @param width
	 *            Expected width of the application window.
	 * @param height
	 *            Expected height of the application window.
	 * 
	 * @throw IllegalArgumentException if {@code width < 0 || height < 0}
	 */
	public EngineDesktop(int width, int height) {
		if (width < 0) {
			throw new IllegalArgumentException("width must be > 0: " + width);
		}
		if (height < 0) {
			throw new IllegalArgumentException("height must be > 0: " + height);
		}
		this.width = width;
		this.height = height;
	}

	/**
	 * Sets a list of icons for this application. On desktop Java uses these
	 * icons instead of the Java logo on the OS task bar and also on the upper
	 * left little icon used in the Window
	 * 
	 * @param icons
	 *            List of Images to be used as icons
	 */
	public void setApplicationIcons(List<? extends Image> icons) {
		frame.setIconImages(icons);
	}

	public void run(final String gameUri, final boolean internal) {
		run(new EngineApplicationListener(new DesktopImageUtils()), gameUri,
				internal, true);
	}

	public void run(final String gameUri, final boolean internal,
			final boolean forceExit) {
		run(new EngineApplicationListener(new DesktopImageUtils()), gameUri,
				internal, forceExit);
	}

	/**
	 * Run an eAdventure game.
	 * 
	 * @param gameUri
	 * @param internal
	 */
	public void run(final EngineApplicationListener engineApplicationListener,
			final String gameUri, final boolean internal,
			final boolean forceExit) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = width;
		config.height = height;
		config.forceExit = forceExit;
		frame = new LwjglFrame(engineApplicationListener, config);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				engineApplicationListener.getGameLoader().loadGame(gameUri,
						internal);
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		frame.setLocationRelativeTo(null);
		SwingEDTUtils.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	protected void dispose() {
		// Just to make sure that Video Player resources are released
		VideoEngineObject.release();

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Gdx.app.exit();
			}
		});
	}
}
