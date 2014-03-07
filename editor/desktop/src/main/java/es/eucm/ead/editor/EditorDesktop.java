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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.actions.Exit;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.engine.utils.SwingEDTUtils;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class EditorDesktop extends Editor {

	public static boolean debug;

	private LwjglFrame frame;

	public EditorDesktop(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		super.create();
		// Load some desktop preferences
		final Preferences preferences = controller.getPreferences();
		// Frame size
		frame = ((DesktopPlatform) platform).getFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Preferences preferences = controller.getPreferences();
				preferences.putInteger(Preferences.WINDOW_X,
						frame.getLocation().x);
				preferences.putInteger(Preferences.WINDOW_Y,
						frame.getLocation().y);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				Preferences preferences = controller.getPreferences();
				preferences.putInteger(Preferences.WINDOW_WIDTH,
						frame.getWidth());
				preferences.putInteger(Preferences.WINDOW_HEIGHT,
						frame.getHeight());
				preferences.putBoolean(Preferences.WINDOW_MAXIMIZED,
						frame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.action(Exit.class);
			}
		});

		if (preferences.getBoolean(Preferences.WINDOW_MAXIMIZED)) {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			int x = preferences.getInteger(Preferences.WINDOW_X, 0);
			int y = preferences.getInteger(Preferences.WINDOW_Y, 0);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			int width = preferences.getInteger(Preferences.WINDOW_WIDTH,
					dimension.width);
			int height = preferences.getInteger(Preferences.WINDOW_HEIGHT,
					dimension.height);
			frame.setLocation(x, y);
			frame.setSize(width, height);
		}

		// File chooser loading path
		final JFileChooser fileChooser = ((DesktopPlatform) platform)
				.getFileChooser();
		String file = preferences
				.getString(Preferences.FILE_CHOOSER_LAST_FOLDER);
		if (file != null) {
			fileChooser.setSelectedFile(new File(file));
		}
		fileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				preferences.putString(Preferences.FILE_CHOOSER_LAST_FOLDER,
						fileChooser.getSelectedFile().getAbsolutePath());
			}
		});

		controller.getModel().addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				platform.setTitle(controller
						.getEditorAssets()
						.getI18N()
						.m("application.title",
								event.getModel().getGameMetadata().getTitle()));
			}
		});

		// Setting debug
		if (debug) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
	}

	public static void main(String[] args) {
		debug = args != null && args.length > 0 && "debug".equals(args[0]);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = true;
		DesktopPlatform platform = new DesktopPlatform();
		final LwjglFrame frame = new LwjglFrame(new EditorDesktop(platform),
				config);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		platform.setFrame(frame);
		// set visible calls create()

		SwingEDTUtils.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);
			}
		});

	}
}
