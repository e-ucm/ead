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

import es.eucm.ead.editor.control.Controller;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A task that incrementally receives results. It will run in its own thread
 */
public abstract class Worker implements Runnable {

	protected Controller controller;

	private WorkerListener listener;

	private AtomicBoolean busy = new AtomicBoolean(false);

	public void setController(Controller controller) {
		this.controller = controller;
	}

	void setListener(WorkerListener listener) {
		this.listener = listener;
	}

	protected void result(Object... args) {
		listener.result(args);
	}

	protected void done() {
		listener.done();
	}

	protected void error(Throwable t) {
		listener.error(t);
	}

	public boolean isBusy() {
		return busy.getAndSet(true);
	}

	@Override
	public void run() {
		runWork();
		busy.set(false);
	}

	/**
	 * Runs the work. This method needs to call {@link #result(Object...)} every
	 * time a new result is found, and {@link #done()} when the work is done.
	 */
	protected abstract void runWork();

	public interface WorkerListener {

		/**
		 * A result was found
		 */
		void result(Object... results);

		/**
		 * Work is done
		 */
		void done();

		/**
		 * An error ocurred
		 */
		void error(Throwable ex);

	}

}
