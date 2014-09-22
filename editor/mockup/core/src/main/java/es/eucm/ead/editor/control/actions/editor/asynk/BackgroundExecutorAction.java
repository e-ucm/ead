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
package es.eucm.ead.editor.control.actions.editor.asynk;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.control.MockupController;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;

/**
 * <p>
 * An action executed in the background executor. Displays notifications while
 * performing the task.
 * </p>
 * 
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd>None.</dd>
 * </dl>
 * 
 */
public abstract class BackgroundExecutorAction<T> extends EditorAction {

	protected static final float ERROR_TIMEOUT = 3F;

	private Toasts toasts;

	private boolean showing;

	protected Object[] args;

	public BackgroundExecutorAction() {
		super(true, false, new Class[] {});
	}

	public BackgroundExecutorAction(Class[]... args) {
		super(true, false, args);
	}

	@Override
	public void perform(Object... args) {
		if (showing) {
			return;
		}
		start();
		this.args = args;
		Gdx.app.postRunnable(doProcess);
	}

	protected abstract String getProcessingI18N();

	protected abstract String getErrorProcessingI18N();

	protected void onPreExecute() {

	}

	protected abstract T doInBackground();

	protected void onPostExecute(T result) {

	}

	private final Runnable doProcess = new Runnable() {
		@Override
		public void run() {
			onPreExecute();
			controller.getBackgroundExecutor().submit(processTask,
					processListener);
		}
	};

	private final BackgroundTaskListener<T> processListener = new BackgroundTaskListener<T>() {

		@Override
		public void completionPercentage(float percentage) {

		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor, T result) {
			toasts.hideNotification();
			onPostExecute(result);
			end(false);
		}

		@Override
		public void error(Throwable e) {
			Gdx.app.error(getClass().toString(),
					"Error in background executor action", e);
			onPostExecute(null);
			end(true);
		}
	};

	private void start() {
		showing = true;
		Gdx.input.setInputProcessor(null);

		toasts = ((MockupViews) controller.getViews()).getToasts();
		toasts.showNotification(controller.getApplicationAssets().getI18N()
				.m(getProcessingI18N()));
	}

	private void end(boolean error) {
		if (error) {
			toasts.showNotification(controller.getApplicationAssets().getI18N()
					.m(getErrorProcessingI18N()), ERROR_TIMEOUT);
		}
		Gdx.input.setInputProcessor(((MockupController) controller)
				.getRootComponent().getStage());
		showing = false;
	}

	private final BackgroundTask<T> processTask = new BackgroundTask<T>() {
		@Override
		public T call() throws Exception {
			return doInBackground();
		}
	};
}
