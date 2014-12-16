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
package es.eucm.ead.editor.control.workers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Manages workers in independent threads
 */
public class WorkerExecutor {

	private Controller controller;

	private ExecutorService executorService;

	private ObjectMap<Class, DelayedRemovalArray<Worker>> workersMap;

	private Array<WorkerExecutorListener> listeners;

	public WorkerExecutor(Controller controller) {
		this.controller = controller;
		listeners = new Array<WorkerExecutorListener>();
		workersMap = new ObjectMap<Class, DelayedRemovalArray<Worker>>();
		executorService = Executors.newFixedThreadPool(
				Math.max(1, Runtime.getRuntime().availableProcessors() - 1),
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "WorkerExecutor-Thread");
					}
				});
	}

	/**
	 * Runs in UI thread
	 */
	public void act() {
		for (DelayedRemovalArray<Worker> workers : workersMap.values()) {
			workers.begin();
			for (int i = 0, n = workers.size; i < n; ++i) {
				Worker worker = workers.get(i);
				if (!worker.isCancelled() && worker.isResultsInUIThread()) {
					worker.act();
				}
				if (worker.isDone()) {
					workers.removeIndex(i);
				}
			}
			workers.end();
		}
	}

	public void addListener(WorkerExecutorListener listener) {
		listeners.add(listener);
	}

	public void removeListener(WorkerExecutorListener listener) {
		listeners.removeValue(listener, true);
	}

	/**
	 * Cancels all running workers
	 */
	public void cancelAll() {
		for (Array<Worker> workers : workersMap.values()) {
			for (Worker worker : workers) {
				if (!worker.isDone()) {
					worker.cancel();
				}
			}
		}
	}

	/**
	 * Cancels a running worker
	 */
	public void cancel(Class clazz, WorkerListener listener) {
		Array<Worker> workers = workersMap.get(clazz);
		if (workers != null) {
			for (Worker worker : workers) {
				if (worker.getListener() == listener) {
					worker.cancel();
				}
			}
		}
	}

	/**
	 * Starts a worker and cancels any other worker of the same class
	 */
	public <T extends Worker> void execute(Class<T> workerClass,
			WorkerListener workerListener, boolean cancelOthers, Object... args) {
		try {
			DelayedRemovalArray<Worker> workers = workersMap.get(workerClass);
			if (workers == null) {
				workers = new DelayedRemovalArray<Worker>(1);
				workersMap.put(workerClass, workers);
			}
			if (cancelOthers) {
				for (Worker worker : workers) {
					if (!worker.isDone()) {
						worker.cancel();
					}
				}
			}
			Worker worker = workerClass.newInstance();
			worker.setController(controller);
			worker.setListener(workerListener);
			worker.setArguments(args);

			workers.add(worker);

			executorService.submit(worker);
			for (WorkerExecutorListener listener : listeners) {
				listener.executed(workerClass, worker.getListener());
			}
		} catch (Exception e) {
			Gdx.app.error("WorkerExecutor", "Error submitting worker", e);
		}
	}

	/**
	 * @return true if there is some worker pending
	 */
	public boolean isWorking() {
		for (Array<Worker> workers : workersMap.values()) {
			for (Worker worker : workers) {
				if (!worker.isDone()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Disposes the worker executor
	 */
	public void dispose() {
		executorService.shutdown();
	}

	/**
	 * @return the number of workers running and pending to run
	 */
	public int countWorkers() {
		int i = 0;
		for (DelayedRemovalArray<Worker> workers : workersMap.values()) {
			i += workers.size;
		}
		return i;
	}

	/**
	 * Listens to events inside worker executor
	 */
	public interface WorkerExecutorListener {

		/**
		 * Work is executed
		 */
		void executed(Class worker, WorkerListener listener);

		/**
		 * Work is cancelled
		 */
		void cancelled(Class worker, WorkerListener listener);
	}
}
