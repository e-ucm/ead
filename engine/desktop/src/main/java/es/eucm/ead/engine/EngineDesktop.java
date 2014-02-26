/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
import es.eucm.ead.engine.utils.SwingEDTUtils;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	 * Run an eAdventure game.
	 * 
	 * @param gameUri
	 * @param internal
	 */
	public void run(String gameUri, boolean internal) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		config.width = width;
		config.height = height;
		config.forceExit = true;
		Engine engine = new Engine();
		frame = new LwjglFrame(engine, config);
		engine.loadGame(gameUri, internal);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		frame.setLocationRelativeTo(null);
        SwingEDTUtils.invokeLater(new Runnable(){

            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
    }

	private void dispose() {
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
