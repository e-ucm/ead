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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EngineDesktop {
	public static LwjglFrame frame;

	private int width;

	private int height;

	public EngineDesktop() {
		this(800, 600);
	}

	public EngineDesktop(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void run(String gameUri, boolean internal) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		config.width = width;
		config.height = height;
		config.forceExit = true;
		Engine engine = new Engine();
		frame = new LwjglFrame(engine, config);
		engine.setLoadingPath(gameUri, internal);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				doDispose();
			}

		});
		frame.setLocationRelativeTo(null);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	private void doDispose() {
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
