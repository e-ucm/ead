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
package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditorDesktop {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.useGL20 = true;
		config.width = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth();
		config.height = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight();
		Editor.debug = false;
		for (String arg : args) {
			if ("debug".equals(arg)) {
				Editor.debug = true;
			}
		}
		final DesktopPlatform platform = new DesktopPlatform();
		Canvas canvas = new Canvas();
		final JFrame frame = new JFrame();
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		platform.setFrame(frame);

		Editor ee = new Editor(null, platform);
		final LwjglApplication app = new LwjglApplication(ee, config, canvas);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Gdx.app.log("EditorDesktop", "Window closing");
				Preferences p = Editor.controller.getPrefs();
				Vector2 size = platform.getSize();
				p.putInteger(Prefs.editorWidth, (int) size.x);
				p.putInteger(Prefs.editorHeight, (int) size.y);
				p.flush();
				app.exit();
			}
		});
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setSize(400, 300);
				frame.setLocation(100, 100);
				frame.setVisible(true);
			}
		});
	}
}
