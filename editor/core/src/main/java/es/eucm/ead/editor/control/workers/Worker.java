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
import es.eucm.ead.editor.control.Controller;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A task that incrementally receives results. It will run in its own thread
 */
public abstract class Worker implements Runnable {

	protected Controller controller;

	protected Object[] args;

	private final boolean resultsInUIThread;

	private WorkerListener listener;

	private AtomicBoolean cancelled = new AtomicBoolean(false);

	private AtomicBoolean done = new AtomicBoolean(false);

	private final boolean cancellable;

	private final Array<Object> results = new Array<Object>();

	public enum Event {
		START, DONE, CANCEL
	}

	protected Worker() {
		this(false, true);
	}

	protected Worker(boolean resultsInUIThread) {
		this(resultsInUIThread, true);
	}

	/**
	 * @param resultsInUIThread
	 *            if results must be notified in the UI Thread
	 */
	protected Worker(boolean resultsInUIThread, boolean cancellable) {
		this.resultsInUIThread = resultsInUIThread;
		this.cancellable = cancellable;
	}

	public boolean isResultsInUIThread() {
		return resultsInUIThread;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	void setListener(WorkerListener listener) {
		this.listener = listener;
	}

	public void setArguments(Object[] args) {
		this.args = args;
	}

	public <T> T getArg(int index) {
		return (T) args[index];
	}

	public WorkerListener getListener() {
		return listener;
	}

	/**
	 * Method that runs in the UI Thread
	 */
	public void act() {
		synchronized (results) {
			for (Object o : results) {
				if (o instanceof Event) {
					switch ((Event) o) {
					case START:
						listener.start();
						break;
					case DONE:
						listener.done();
						break;
					case CANCEL:
						listener.cancelled();
						break;
					}
				} else if (o instanceof Throwable) {
					listener.error((Throwable) o);
				} else if (o != null && o.getClass().isArray()) {
					listener.result((Object[]) o);
				}
			}
			results.clear();
		}
	}

	private void addResult(Object result) {
		synchronized (results) {
			results.add(result);
		}
		Gdx.graphics.requestRendering();
	}

	protected void result(Object... args) {
		if (!cancelled.get()) {
			if (resultsInUIThread) {
				addResult(args);
			} else {
				listener.result(args);
			}
		}
	}

	protected void error(Throwable t) {
		if (!cancelled.get()) {
			if (resultsInUIThread) {
				addResult(t);
			} else {
				listener.error(t);
			}
		}
	}

	protected void start() {
		if (resultsInUIThread) {
			addResult(Event.START);
		} else {
			listener.start();
		}
	}

	protected void done() {
		if (resultsInUIThread) {
			addResult(Event.DONE);
		} else {
			listener.done();
		}
	}

	protected void cancelled() {
		if (resultsInUIThread) {
			addResult(Event.CANCEL);
		} else {
			listener.cancelled();
		}
	}

	@Override
	public void run() {
		if (!cancelled.get()) {
			start();
			try {
				prepare();
				while (!cancelled.get() && !step())
					;
			} catch (Exception e) {
				error(e);
				Gdx.app.error("Worker", "Error", e);
			}
		}

		if (!cancelled.get()) {
			done();
		} else {
			cancelled();
		}

		if (resultsInUIThread) {
			Gdx.graphics.requestRendering();
		}
		done.set(true);
	}

	/**
	 * Cancels the worker
	 */
	public void cancel() {
		if (cancellable) {
			cancelled.set(true);
		}
	}

	/**
	 * @return if the worked is finished or was cancelled, and its correspondent
	 *         thread stopped or is about to
	 */
	public boolean isDone() {
		return done.get();
	}

	/**
	 * Prepares the necessary data before starting
	 */
	protected abstract void prepare();

	/**
	 * Runs one step in the work. This method needs to call
	 * {@link #result(Object...)} every time a new result is found
	 * 
	 * @return {@code true} if the work is done
	 */
	protected abstract boolean step();

	public interface WorkerListener {

		/**
		 * The work was started
		 */
		void start();

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

		/**
		 * The work was cancelled
		 */
		void cancelled();

	}

}
