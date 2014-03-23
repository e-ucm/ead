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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.InvalidArgumentsException;
import es.eucm.ead.editor.control.actions.editor.AddRecentGame;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.pastelisteners.SceneElementPasteListener;
import es.eucm.ead.editor.control.pastelisteners.ScenePasteListener;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.editor.actors.EditorScene;
import es.eucm.network.requests.RequestHelper;

/**
 * Mediator and main controller of the editor's functionality
 * 
 */
public class Controller {

	/**
	 * Game model managed by the editor.
	 */
	private Model model;

	/**
	 * Platform dependent functionality
	 */
	private Platform platform;

	/**
	 * Asset manager used for internal's editor assets.
	 */
	private ApplicationAssets applicationAssets;

	/**
	 * Asset manager for the current openend game's project.
	 */
	private EditorGameAssets editorGameAssets;

	protected Views views;

	private Actions actions;

	/**
	 * Manages editor preferences
	 */
	private Preferences preferences;

	/**
	 * Manage editor's command history.
	 */
	private Commands commands;

	private EditorIO editorIO;

	/**
	 * Object for dealing with http connections
	 */
	private RequestHelper requestHelper;

	/**
	 * Manage keyboard mappings to editor's functionality
	 */
	private KeyMap keyMap;

	private Clipboard clipboard;

	/**
	 * Info about the version and release type of the application. Used for
	 * checking updates
	 */
	private ReleaseInfo releaseInfo;

	private Tracker tracker;

	private Templates templates;

	private BackgroundExecutor backgroundExecutor;

	public Controller(Platform platform, Files files, Group rootComponent) {
		this.platform = platform;
		this.requestHelper = platform.getRequestHelper();
		this.applicationAssets = new ApplicationAssets(files);
		this.templates = new Templates(this);
		applicationAssets.finishLoading();
		this.editorGameAssets = new EditorGameAssets(files, applicationAssets);
		this.model = new Model();
		this.commands = new Commands(model);
		this.views = createViews(rootComponent);
		this.editorIO = new EditorIO(this);
		this.clipboard = new Clipboard(Gdx.app.getClipboard(), views,
				editorGameAssets);
		this.actions = new Actions(this);
		this.backgroundExecutor = new BackgroundExecutor();
		this.preferences = applicationAssets.loadPreferences();
		// Get the release info from editor assets
		this.releaseInfo = applicationAssets.loadReleaseInfo();
		this.keyMap = new KeyMap(this);
		setTracker();
		setClipboard();
		// Shortcuts listener
		rootComponent.addListener(new InputListener() {
			private boolean ctrl = false;
			private boolean alt = false;
			private boolean shift = false;

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Keys.CONTROL_LEFT:
				case Keys.CONTROL_RIGHT:
					ctrl = true;
					return true;
				case Keys.ALT_LEFT:
				case Keys.ALT_RIGHT:
					alt = true;
					return true;
				case Keys.SHIFT_LEFT:
				case Keys.SHIFT_RIGHT:
					shift = true;
					return true;
				default:
					String shortcut = "";
					if (ctrl) {
						shortcut += "ctrl+";
					}
					if (alt) {
						shortcut += "alt+";
					}
					if (shift) {
						shortcut += "shift+";
					}

					shortcut += Keys.toString(event.getKeyCode()).toLowerCase();
					return keyMap.shortcut(shortcut);
				}
			}

			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				switch (keycode) {
				case Keys.CONTROL_LEFT:
				case Keys.CONTROL_RIGHT:
					ctrl = false;
					return true;
				case Keys.ALT_LEFT:
				case Keys.ALT_RIGHT:
					alt = false;
					return true;
				case Keys.SHIFT_LEFT:
				case Keys.SHIFT_RIGHT:
					shift = false;
					return true;
				default:
					return false;
				}
			}
		});
		loadPreferences();
	}

	protected Views createViews(Group rootView) {
		return new Views(this, rootView);
	}

	private void setClipboard() {

		clipboard.registerPasteListener(EditorScene.class,
				new ScenePasteListener(this));
		clipboard.registerPasteListener(SceneElement.class,
				new SceneElementPasteListener(this));
	}

	private void setTracker() {
		// FIXME obtain from platform the actual tracker implementation
		this.tracker = new Tracker(releaseInfo.getBugReportURL(), this);
		tracker.setEnabled(preferences.getBoolean(Preferences.TRACKING_ENABLED,
				false));
		tracker.startSession();
	}

	/**
	 * Process preferences concerning the controller
	 */
	private void loadPreferences() {
		getApplicationAssets().getI18N().setLang(
				preferences.getString(Preferences.EDITOR_LANGUAGE));
	}

	public Model getModel() {
		return model;
	}

	public EditorGameAssets getEditorGameAssets() {
		return editorGameAssets;
	}

	public ApplicationAssets getApplicationAssets() {
		return applicationAssets;
	}

	public Platform getPlatform() {
		return platform;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public Commands getCommands() {
		return commands;
	}

	public Actions getActions() {
		return actions;
	}

	public Views getViews() {
		return views;
	}

	public void view(String viewName) {
		views.setView(viewName);
	}

	public KeyMap getKeyMap() {
		return keyMap;
	}

	public Clipboard getClipboard() {
		return clipboard;
	}

	public RequestHelper getRequestHelper() {
		return requestHelper;
	}

	public Tracker getTracker() {
		return tracker;
	}

	public Templates getTemplates() {
		return templates;
	}

	public BackgroundExecutor getBackgroundExecutor() {
		return backgroundExecutor;
	}

	/**
	 * Executes an editor action with the given name and arguments
	 * 
	 * @param actionClass
	 *            the action class
	 * @param args
	 *            the arguments for the action
	 */
	public void action(Class actionClass, Object... args) {
		try {
			Gdx.app.debug("Controller", "Executing action " + actionClass
					+ " with args" + prettyPrintArgs(args));
			actions.perform(actionClass, args);
			// FIXME correct this when actions serialization is ready
			tracker.actionPerformed(actionClass.toString());
		} catch (ClassCastException e) {
			throw new EditorActionException(
					"Something went wrong when executing action "
							+ actionClass
							+ " with arguments "
							+ prettyPrintArgs(args)
							+ ". Perhaps the number of arguments is not correct or these are not valid",
					e);
		} catch (NullPointerException e) {
			throw new EditorActionException(
					"Something went wrong when executing action "
							+ actionClass
							+ " with arguments "
							+ prettyPrintArgs(args)
							+ ". Perhaps the number of arguments is not correct or these are not valid",
					e);
		} catch (InvalidArgumentsException e) {
			// FIXME treat exception
		}
	}

	/**
	 * Just formats an array of objects for console printing. For debugging only
	 */
	private String prettyPrintArgs(Object... args) {
		String str = "[";
		for (Object arg : args) {
			str += (arg instanceof String ? "\"" : "")
					+ (arg == null ? "null" : arg.toString())
					+ (arg instanceof String ? "\"" : "") + " , ";
		}
		if (args.length > 0) {
			str = str.substring(0, str.length() - 3);
		}
		str += "]";
		return str;
	}

	/**
	 * Executes a command, an takes care of notifying to all model listeners all
	 * the changes performed by it
	 * 
	 * @param command
	 *            the command
	 */
	public void command(Command command) {
		commands.command(command);
	}

	public String getLoadingPath() {
		return editorGameAssets.getLoadingPath();
	}

	public void loadGame(String gamePath, boolean internal) {
		editorIO.load(gamePath, internal);
		action(AddRecentGame.class, getLoadingPath());
	}

	public void saveAll() {
		editorIO.saveAll(model);
	}

	public EditorIO getEditorIO() {
		return editorIO;
	}

	public void setLanguage(String language) {
		getApplicationAssets().getI18N().setLang(language);
		views.clearCache();
		views.reloadCurrentView();
		preferences.putString(Preferences.EDITOR_LANGUAGE, language);
	}

	/**
	 * Returns the version of the application (e.g. 2.0.0). Needed for setting
	 * {@link es.eucm.ead.schema.editor.game.EditorGame#appVersion} when the
	 * game is created and saved.
	 * 
	 * See {@link es.eucm.ead.editor.assets.ApplicationAssets#loadReleaseInfo()}
	 * and {@link ReleaseInfoTest} for more details
	 * 
	 * @return The version number of the application (e.g. "2.0.0").
	 */
	public String getAppVersion() {
		return releaseInfo.getAppVersion();
	}

	/**
	 * Returns the version of the model API this application saves to. Needed
	 * for setting
	 * {@link es.eucm.ead.schema.editor.game.EditorGame#modelVersion} when the
	 * game is created and saved.
	 * 
	 * See <a
	 * href="https://github.com/e-ucm/ead/wiki/Model-API-versions">https:/
	 * /github.com/e-ucm/ead/wiki/Model-API-versions</a> and
	 * {@link ReleaseInfoTest} for more details
	 * 
	 * @return The model version (e.g. 1).
	 */
	public String getModelVersion() {
		return releaseInfo.getModelVersion();
	}

	/**
	 * The editor is exiting. Perform all operations before finalizing the
	 * editor completely
	 */
	public void exit() {
		tracker.endSession();
	}

	/**
	 * The controller checks and updates pending tasks (e.g., state of
	 * background tasks)
	 */
	public void act() {
		editorGameAssets.update();
		applicationAssets.update();
		backgroundExecutor.act();
	}

	public static interface BackListener {
		/**
		 * Called when the Back key was pressed in Android.
		 */
		void onBackPressed();
	}
}
