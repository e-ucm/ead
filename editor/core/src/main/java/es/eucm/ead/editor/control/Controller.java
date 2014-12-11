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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.ArgumentsValidationException;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.engine.EditorEngineInitializer;
import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.editor.control.pastelisteners.BehaviorCopyListener;
import es.eucm.ead.editor.control.pastelisteners.ModelEntityCopyListener;
import es.eucm.ead.editor.control.pastelisteners.TextCopyListener;
import es.eucm.ead.editor.control.workers.WorkerExecutor;
import es.eucm.ead.editor.indexes.ControllerIndex;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.engine.EngineInitializer;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Mediator and main controller of the editor's functionality
 * 
 */
public class Controller {

	/**
	 * Singletion shaperenderer, shared across the editor
	 */
	private ShapeRenderer shapeRenderer;

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

	/**
	 * Manage keyboard mappings to editor's functionality
	 */
	private ShortcutsMap shortcutsMap;

	private Clipboard clipboard;

	/**
	 * Info about the version and release type of the application. Used for
	 * checking updates
	 */
	private ReleaseInfo releaseInfo;

	private Tracker tracker;

	private Templates templates;

	private BackgroundExecutor backgroundExecutor;

	private WorkerExecutor workerExecutor;

	private Engine engine;

	private Map<Class, ControllerIndex> indexes;

	public Controller(Platform platform, Files files, Group viewsContainer,
			Group modalsContainer) {
		this.shapeRenderer = new ShapeRenderer();
		this.platform = platform;
		this.applicationAssets = createApplicationAssets(files);
		this.editorGameAssets = new EditorGameAssets(files,
				platform.getImageUtils());
		this.templates = new Templates(this);
		this.model = new Model(editorGameAssets);
		this.commands = new Commands(model);
		this.views = createViews(viewsContainer, modalsContainer);
		this.clipboard = new Clipboard(Gdx.app.getClipboard(), this,
				editorGameAssets);
		this.actions = new Actions(this);
		this.backgroundExecutor = new BackgroundExecutor();
		this.workerExecutor = new WorkerExecutor(this);
		this.preferences = applicationAssets.loadPreferences();
		// Get the release info from editor assets
		this.releaseInfo = applicationAssets.loadReleaseInfo();
		this.shortcutsMap = new ShortcutsMap(this);
		this.engine = new Engine(this, buildEngineInitializer());
		setTracker(viewsContainer, modalsContainer);
		setClipboard();
		loadPreferences();
		indexes = new HashMap<Class, ControllerIndex>();
	}

	protected EngineInitializer buildEngineInitializer() {
		return new EditorEngineInitializer(this);
	}

	protected ApplicationAssets createApplicationAssets(Files files) {
		return new ApplicationAssets(files);
	}

	protected Views createViews(Group viewsContainer, Group modalsContainer) {
		return new Views(this, viewsContainer, modalsContainer);
	}

	private void setClipboard() {
		clipboard.registerCopyListener(ModelEntity.class,
				new ModelEntityCopyListener(this));
		clipboard.registerCopyListener(Behavior.class,
				new BehaviorCopyListener(this));
		clipboard
				.registerCopyListener(String.class, new TextCopyListener(this));
	}

	private void setTracker(Group viewsContainer, Group modalsContainer) {
		this.tracker = platform.createTracker(this);
		tracker.setEnabled(preferences.getBoolean(Preferences.TRACKING_ENABLED,
				false));
		tracker.startSession();
		InputListener buttonsPressed = new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (event.getTarget().getName() != null) {
					tracker.buttonPressed(event.getTarget().getName());
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		};

		viewsContainer.addCaptureListener(buttonsPressed);
		modalsContainer.addCaptureListener(buttonsPressed);
	}

	/**
	 * Process preferences concerning the controller
	 */
	protected void loadPreferences() {
		String language = preferences.getString(Preferences.EDITOR_LANGUAGE);
		setLanguage("".equals(language) || language == null ? platform
				.getLocale() : language);
	}

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
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

	public <T extends ViewBuilder> void view(Class<T> view) {
		views.setView(view);
	}

	public ShortcutsMap getShortcutsMap() {
		return shortcutsMap;
	}

	public Clipboard getClipboard() {
		return clipboard;
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

	public WorkerExecutor getWorkerExecutor() {
		return workerExecutor;
	}

	public Engine getEngine() {
		return engine;
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
			actions.perform(actionClass, args);
			tracker.actionPerformed(actionClass.toString());
		} catch (ClassCastException e) {
			throw new EditorActionException(getErrorMessage(actionClass, args),
					e);
		} catch (NullPointerException e) {
			throw new EditorActionException(getErrorMessage(actionClass, args),
					e);
		} catch (ArgumentsValidationException e) {
			Gdx.app.error("Controller", "Invalid arguments exception for "
					+ actionClass, e);
		}
	}

	private String getErrorMessage(Class actionClass, Object... args) {
		String message = "Something went wrong when executing action \n"
				+ actionClass + " with arguments \n{";

		for (int i = 0; i < args.length; i++) {
			Object object = args[i];
			message += " \n\t" + (object == null ? "null" : object.toString());
			if (i < args.length - 1) {
				message += ", ";
			}
		}
		message += "\n}\nPerhaps the number of arguments is not correct or these are not valid";
		return message;
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

	public void setLanguage(String language) {
		getApplicationAssets().getI18N().setLang(language);
		getEditorGameAssets().getI18N().setLang(language);
		views.reinitializeAllViews();
		preferences.putString(Preferences.EDITOR_LANGUAGE, language);
	}

	/**
	 * @return The object with all information related to the current
	 *         installation
	 */
	public ReleaseInfo getReleaseInfo() {
		return releaseInfo;
	}

	/**
	 * Returns the version of the application (e.g. 2.0.0). Needed for setting
	 * {@link es.eucm.ead.schema.editor.components.Versions#appVersion} when the
	 * game is created and saved.
	 * 
	 * See {@link es.eucm.ead.editor.assets.ApplicationAssets#loadReleaseInfo()}
	 * and ReleaseInfoTest for more details
	 * 
	 * @return The version number of the application (e.g. "2.0.0").
	 */
	public String getAppVersion() {
		return releaseInfo.getAppVersion();
	}

	/**
	 * Returns the path that points to the engine-with-dependencies.jar file
	 * used to export games as jar files. It is read from release.json. Can be a
	 * relative or absolute path
	 * 
	 * @return The path to the engine jar (e.g. "libs/engine.jar")
	 */
	public String getEngineLibPath() {
		return releaseInfo.getEngineLibPath();
	}

	/**
	 * Returnst the index of the given class
	 */
	public <T extends ControllerIndex> T getIndex(Class<T> indexClass) {
		ControllerIndex controllerIndex = indexes.get(indexClass);
		if (controllerIndex == null) {
			try {
				controllerIndex = ClassReflection.newInstance(indexClass);
				controllerIndex.initialize(this);
				indexes.put(indexClass, controllerIndex);
			} catch (ReflectionException e) {
				Gdx.app.error("Model", "Impossible to create index "
						+ indexClass);
			}
		}
		return (T) controllerIndex;
	}

	/**
	 * The editor is exiting. Perform all operations before finalizing the
	 * editor completely
	 */
	public void exit() {
		applicationAssets.dispose();
		editorGameAssets.dispose();
		shapeRenderer.dispose();
		tracker.endSession();
		preferences.flush();
	}

	/**
	 * The controller checks and updates pending tasks (e.g., state of
	 * background tasks)
	 */
	public void act(float delta) {
		editorGameAssets.update();
		applicationAssets.update();
		backgroundExecutor.act();
		workerExecutor.act();
		engine.update(delta);
	}
}
