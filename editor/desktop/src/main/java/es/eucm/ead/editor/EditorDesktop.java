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
package es.eucm.ead.editor;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.ShortcutsMap;
import es.eucm.ead.editor.control.actions.editor.*;
import es.eucm.ead.editor.control.views.NoProjectView;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.ui.EditorWindow;
import es.eucm.ead.editor.view.tooltips.TooltipManager;
import es.eucm.ead.engine.utils.SwingEDTUtils;
import es.eucm.ead.schema.editor.components.Note;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditorDesktop extends EditorApplicationListener {

	public boolean debug;

	private LwjglFrame frame;

	private Group viewsRoot;

	/**
	 * EditorDesktop admits as optional parameter the absolute path of a
	 * project. If that parameter is not null, it will try to open that project
	 * once the editor has been initialized. This is needed to support platform
	 * file extension bindings (to allow users double click project files to
	 * open them on the editor).
	 */
	private String projectToOpenPath;

	private TooltipManager tooltipManager;

	/**
	 * The editor desktop requires a
	 * {@link es.eucm.ead.editor.platform.Platform}, that will be typically
	 * {@link es.eucm.ead.editor.DesktopPlatform}. It also can get the absolute
	 * path of a project directory to be loaded with.
	 * 
	 * @param platform
	 *            The platform object
	 * @param projectToOpenPath
	 *            The full path of a project that has to be loaded upon startup
	 *            (e.g. C:/Users/A user/eadgames/testgame/). If null, the editor
	 *            opens the default view. (see {@link #initialize()} for more
	 *            details).
	 * @param debug
	 *            True if the editor has to be launched in debug mode, false
	 *            otherwise
	 */
	public EditorDesktop(Platform platform, String projectToOpenPath,
			boolean debug) {
		super(platform);
		this.projectToOpenPath = projectToOpenPath;
		this.debug = debug;
	}

	@Override
	public void create() {
		// Setting debug ASAP
		if (debug) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}

		super.create();
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		// Load some desktop preferences
		final Preferences preferences = controller.getPreferences();
		// Frame size
		((DesktopPlatform) platform).initFileChooser(controller, stage);
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

		controller.getModel().addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				switch (event.getType()) {
				case LOADED:
					platform.setTitle(controller
							.getApplicationAssets()
							.getI18N()
							.m("application.title",
									Model.getComponent(
											event.getModel().getGame(),
											Note.class).getTitle()));
					break;
				case UNLOADED:
					platform.setTitle(controller.getApplicationAssets()
							.getI18N().m("application.title"));
					break;
				}
			}
		});

		tooltipManager = new TooltipManager(stage.getRoot(), controller
				.getApplicationAssets().getSkin()
				.get("tooltip", LabelStyle.class));

	}

	@Override
	public void render() {
		super.render();
		tooltipManager.update(Gdx.graphics.getDeltaTime());
	}

	protected Controller createController() {
		viewsRoot = new Group();
		return new Controller(platform, Gdx.files, viewsRoot);
	}

	@Override
	protected void initialize() {
		super.initialize();
		EditorWindow editorWindow = new EditorWindow(viewsRoot, controller);
		stage.addActor(editorWindow);
		// Tries to load the project.json file given as argument (main)
		if (projectToOpenPath != null) {
			controller.action(OpenGame.class, projectToOpenPath);
		}
		registerShortcuts();
		controller.action(ChangeView.class, NoProjectView.class);
		// Check updates
		controller.action(CheckUpdates.class, controller.getReleaseInfo(),
				false);
	}

	private void registerShortcuts() {
		final ShortcutsMap shortcutsMap = controller.getShortcutsMap();
		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				return !event.isHandled() && shortcutsMap.shortcut(keycode);
			}
		});

		shortcutsMap.registerShortcutCtrl(Keys.O, OpenGame.class);

		shortcutsMap.registerShortcutCtrl(Keys.S, Save.class);
		shortcutsMap.registerShortcutCtrl(Keys.X, Cut.class);
		shortcutsMap.registerShortcutCtrl(Keys.C, Copy.class);
		shortcutsMap.registerShortcutCtrl(Keys.P, Paste.class);
		shortcutsMap.registerShortcutCtrl(Keys.Z, Undo.class);
		shortcutsMap.registerShortcutCtrl(Keys.Y, Redo.class);

		shortcutsMap.registerShortcutKey(Keys.BACKSPACE, Back.class);
		shortcutsMap.registerShortcutAlt(Keys.BACKSPACE, Next.class);

	}

	/**
	 * {@link EditorDesktop} admits two optional arguments: args[0] The full
	 * path of a project.json file to open the editor with args[1] "debug" to
	 * launch the editor in debug mode. Question: What does this actually do?
	 */
	public static void main(String[] args) {
		boolean debug = false;
		if (args != null) {
			for (String arg : args) {
				if ("debug".equals(arg.toLowerCase())) {
					debug = true;
					break;
				}
			}
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = true;
		DesktopPlatform platform = new DesktopPlatform();
		final LwjglFrame frame = new LwjglFrame(new EditorDesktop(platform,
				(args.length > 0 && !"debug".equals(args[0])) ? args[0] : null,
				debug), config);
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
