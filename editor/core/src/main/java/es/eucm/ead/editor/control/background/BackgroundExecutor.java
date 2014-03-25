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
package es.eucm.ead.editor.control.background;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;

/**
 * 
 * Controls the execution of {@link BackgroundTask}. Use
 * {@link BackgroundExecutor#submit(BackgroundTask, BackgroundTaskListener)} to
 * run a background task. {@link BackgroundExecutor#act()} checks the state of
 * the background tasks, and notifies changes to {@link BackgroundTaskListener}
 * s.
 * 
 * Created by angel on 25/03/14.
 */
public class BackgroundExecutor {

	private AsyncExecutor asyncExecutor;

	private Array<Execution> tasks;

	public BackgroundExecutor() {
		tasks = new Array<Execution>();
		this.asyncExecutor = new AsyncExecutor(Runtime.getRuntime()
				.availableProcessors());
	}

	/**
	 * Submits a background task to be executed
	 * 
	 * @param task
	 *            the task to be executed
	 * @param listener
	 *            the listener that will be notified of any update in the task
	 * @param <T>
	 *            the task result type
	 */
	public <T> void submit(BackgroundTask<T> task,
			BackgroundTaskListener<T> listener) {
		tasks.add(new Execution<T>(task, listener, asyncExecutor.submit(task)));
	}

	/**
	 * Checks the state of the current background tasks. If a task is done,
	 * notifies results/errors to listeners. If it is not, notifies completion
	 * percentage.
	 */
	@SuppressWarnings("unchecked")
	public void act() {
		for (Execution e : tasks) {
			if (e.result.isDone()) {
				try {
					Object result = e.result.get();
					e.listener.done(result);
				} catch (GdxRuntimeException ex) {
					e.listener.error(ex.getCause());
				}
				tasks.removeValue(e, true);
			} else {
				e.listener.completionPercentage(e.task
						.getCompletionPercentage());
			}
		}
	}

	/**
	 * Groups objects conforming a background task
	 * 
	 * @param <T>
	 *            the task result type
	 */
	private static class Execution<T> {
		BackgroundTask<T> task;
		BackgroundTaskListener<T> listener;
		AsyncResult<T> result;

		private Execution(BackgroundTask<T> task,
				BackgroundTaskListener<T> listener, AsyncResult<T> result) {
			this.task = task;
			this.listener = listener;
			this.result = result;
		}
	}

	/**
	 * Listens to updates in a background task
	 * 
	 * @param <T>
	 *            the task result type
	 */
	public interface BackgroundTaskListener<T> {

		/**
		 * Notifies the completion percentage of the task
		 */
		void completionPercentage(float percentage);

		/**
		 * Notifies the execution of the task has successfully finished
		 * 
		 * @param result
		 *            the task result
		 */
		void done(T result);

		/**
		 * Notifies the execution of the task failed
		 * 
		 * @param e
		 *            the exception thrown by the task when it failed
		 */
		void error(Throwable e);
	}

}
