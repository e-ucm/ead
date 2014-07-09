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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Gdx;
import es.eucm.ead.editor.control.actions.EnabledOnloadAction;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.exporter.ExportCallback;
import es.eucm.ead.editor.exporter.Exporter;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.classic.dialogs.InfoDialogBuilder;
import es.eucm.ead.engine.I18N;

/**
 * Simple action that exports the current model to the given destiny file
 * (args[0]), which is expected to be a string.If args[0] is not present, this
 * action asks for the destiny of the jar file.
 * 
 * This action also supports these two arguments:
 * 
 * args[1]: A String path pointing to the location of the engine library path
 * used for exportation. This is typically a JAR file with dependencies produced
 * by Maven. If this argument is null, the action tries to get it from the
 * controller, since this path should be specified in the release.json file.
 * 
 * args[2]: A callback, defined as an
 * {@link es.eucm.ead.editor.exporter.ExportCallback} object. This callback gets
 * updates on the exportation process.
 * 
 * Created by Javier Torrente on 20/03/14.
 */
public class ExportGame extends EnabledOnloadAction {

	private String jarPath;
	private String engineLibraryPath;
	private ExportCallback callback;

	private Exporter exporter;

	public ExportGame() {
		super(false, true, String.class, String.class, ExportCallback.class);
		resetFields();
		setEnabled(false);
	}

	private void resetFields() {
		jarPath = null;
		engineLibraryPath = null;
		callback = null;
	}

	@Override
	/**
	 * When the action is initialized, it registers itself to listen to load events
	 * (at {@link EnabledOnloadAction#initialize} )
	 * so it can set itself enabled when a game is open
	 */
	public void initialize(Controller controller) {
		super.initialize(controller);
		this.exporter = new Exporter(controller.getEditorGameAssets());
	}

	@Override
	// Destiny, engine jar file, callback
	public void perform(Object... args) {
		resetFields();

		// First argument, if present, should be the destiny path
		if (args[0] != null) {
			jarPath = (String) args[0];
		}

		// Second argument, if present, is the path to the engine library that
		// must be used for exporting the game
		if (args[1] != null) {
			engineLibraryPath = (String) args[1];
		}

		// Third argument, if present, should be a callback that gets updates on
		// the exportation process
		if (args[2] != null) {
			callback = (ExportCallback) args[2];
		}

		// Retrieve those arguments that were not specified (because they were
		// null)

		// If the engine library path is not specified, retrieve it from the
		// controller (should be specified in the
		// release.json file)
		if (engineLibraryPath == null) {
			engineLibraryPath = controller.getEngineLibPath();
		}

		// If callback is null, create a basic one
		if (callback == null)
			callback = new ExportProgressOutput();

		// If the destiny path is not specified, ask the user.
		if (jarPath == null) {
			controller.getPlatform().askForFile(
					new Platform.FileChooserListener() {
						@Override
						public void fileChosen(String path) {
							jarPath = path;
							doPerform();
						}
					});
		}
	}

	private void doPerform() {

		controller
				.getBackgroundExecutor()
				.submit(new BackgroundTask<Object>() {
					@Override
					public Object call() throws Exception {
						exporter.exportAsJar(jarPath, controller
								.getEditorGameAssets().getLoadingPath(),
								engineLibraryPath, controller.getModel()
										.listNamedResources(), callback);
						return null;
					}
				},
						(BackgroundExecutor.BackgroundTaskListener) ((callback instanceof BackgroundExecutor.BackgroundTaskListener) ? callback
								: new ExportProgressOutput()));
	}

	private class ExportProgressOutput implements ExportCallback,
			BackgroundExecutor.BackgroundTaskListener {

		private I18N i18N = controller.getApplicationAssets().getI18N();

		private static final String LOG_TAG = "Export Progress";

		// ExportCallback methods
		@Override
		public void error(String errorMessage) {
			String parsedMessage = i18N.m(errorMessage);
			Gdx.app.error(LOG_TAG, parsedMessage);
			controller.getViews().showDialog(InfoDialogBuilder.class,
					parsedMessage);
		}

		@Override
		public void progress(int percentage, String currentTask) {
			Gdx.app.debug(LOG_TAG, i18N.m(currentTask));
		}

		@Override
		public void complete(String completionMessage) {
			String parsedMessage = i18N.m(completionMessage);
			Gdx.app.debug(LOG_TAG, parsedMessage);
			controller.getViews().showDialog(InfoDialogBuilder.class,
					parsedMessage);
		}

		// BackgroundTaskListener methods
		@Override
		public void completionPercentage(float percentage) {
			progress(Math.round(percentage * 100), "");
		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor, Object result) {
			complete("");
		}

		@Override
		public void error(Throwable e) {
			error(e.toString());
		}
	}
}
