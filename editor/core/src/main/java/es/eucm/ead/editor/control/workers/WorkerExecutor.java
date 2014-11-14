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

	private ObjectMap<Class, Worker> workersMap;

	public WorkerExecutor(Controller controller) {
		this.controller = controller;
		workersMap = new ObjectMap<Class, Worker>();
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
	 * Starts a worker
	 * 
	 * @return {@code true} if the worker started. {@code false} if the worker
	 *         was busy and was not started
	 */
	public <T extends Worker> boolean execute(Class<T> workerClass,
			WorkerListener workerListener) {
		try {
			Worker worker = workersMap.get(workerClass);
			if (worker == null) {
				worker = workerClass.newInstance();
				worker.setController(controller);
				workersMap.put(workerClass, worker);
			}

			if (!worker.isBusy()) {
				worker.setListener(workerListener);
				executorService.submit(worker);
				return true;
			}
		} catch (Exception e) {
			Gdx.app.error("WorkerExecutor", "Error submitting worker", e);
		}
		return false;
	}

	/**
	 * Disposes the worker executor
	 */
	public void dispose() {
		executorService.shutdown();
	}
}
