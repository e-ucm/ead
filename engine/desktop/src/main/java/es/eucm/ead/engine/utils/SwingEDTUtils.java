/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;

/**
 * Swing Event Dispatcher Thread (EDT) utility methods to interact with the EDT.
 * 
 */
final public class SwingEDTUtils {
	private SwingEDTUtils() {
		// No default constructor to avoid instantiation
	}

	/**
	 * Execute {@code doRun} in the EDT now. The calling thread waits for the
	 * completion of {@code doRun}.
	 * 
	 * 
	 * This method use internally
	 * {@link javax.swing.SwingUtilities#invokeAndWait(Runnable)}, any exception
	 * thrown during the execution of {@code doRun} is caught in the calling
	 * thread and wrapped as a {@code RuntimeException}.
	 * 
	 * @param doRun
	 *            {@code Runnable} to be run.
	 * 
	 * @throws RuntimeException
	 *             if {@code doRun} throws an exception during its execution.
	 */
	public static void invokeNow(final Runnable doRun) {
		if (SwingUtilities.isEventDispatchThread()) {
			doRun.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(doRun);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(
						"Unexpected error during Runnable execution",
						e.getCause());
			} catch (InterruptedException e) {
				Gdx.app.debug(SwingEDTUtils.class.getName(),
						"Swing thread interrupted", e);
			}
		}
	}

	/**
	 * Execute {@code doRun} in the EDT. The calling thread does NOT wait for
	 * the completion of {@code doRun}.
	 * 
	 * This method use internally
	 * {@link javax.swing.SwingUtilities#invokeLater(Runnable)}, so any
	 * exception thrown during the execution of {@code doRun} is caught in the
	 * EDT.
	 * 
	 * @param doRun
	 *            {@code Runnable} to be run.
	 * 
	 */
	public static void invokeLater(Runnable doRun) {
		if (SwingUtilities.isEventDispatchThread()) {
			doRun.run();
		} else {
			SwingUtilities.invokeLater(doRun);
		}
	}

	/**
	 * Execute {@code callable} in the EDT now. The calling thread waits for the
	 * completion of {@code callable}.
	 * 
	 * 
	 * @param callable
	 *            {@code Callable} to be executed.
	 * 
	 * @param <V>
	 *            Type of the computed result.
	 * 
	 * @return The computed result.
	 * 
	 * 
	 * @throws RuntimeException
	 *             if {@code doRun} throws an exception during its execution.
	 * 
	 * @see #invokeNow(Runnable)
	 */
	public static <V> V callNow(final Callable<V> callable) {
		RunnableCallable<V> runnable = new RunnableCallable<V>(callable);
		invokeNow(runnable);
		return runnable.get();
	}

	/**
	 * Schedules {@code callable} for execution in the EDT. The calling thread does NOT wait
	 * for the completion of {@code callable}.
	 * 
	 * 
	 * @param callable
	 *            {@code Callable} to be executed.
	 * 
	 * @param <V>
	 *            Type of the computed result.
	 * 
	 * @return The computed result.
	 * 
	 * 
	 * @throws RuntimeException
	 *             if {@code doRun} throws an exception during its execution.
	 * 
	 * @see #invokeLater(Runnable)
	 */
	public static <V> Future<V> callLater(final Callable<V> callable) {
		SwingFuture<V> result = new SwingFuture<V>(callable);
		invokeLater(result);
		return result;
	}

	/**
	 * {@code Runnable} that executes the
	 * {@link java.util.concurrent.Callable#call()} method inside the
	 * {@code run()} method.
	 * 
	 * @see java.util.concurrent.Callable
	 * 
	 * @see Runnable
	 * 
	 * @param <V>
	 *            Type of the result.
	 */
	private static class RunnableCallable<V> implements Runnable {

		private Callable<V> callable;

		private V result;

		private boolean done;

		public RunnableCallable(Callable<V> callable) {
			this.callable = callable;
		}

		/**
		 * Returns the computed value.
		 * 
		 * @return the computed value.
		 * 
		 * @throws IllegalStateException
		 *             if this method has been called before the {@code run()}
		 *             method has been executed.
		 */
		public V get() {
			if (!this.done) {
				throw new IllegalStateException(
						"run method must be executed before getting the result");
			}
			return this.result;
		}

		/**
		 * Calls the {@link java.util.concurrent.Callable#call()} method. if the
		 * called method throws any exception, it is wrapped in a
		 * {@link RuntimeException} and rethrown.
		 * 
		 * @throws RuntimeException
		 *             if the called {@code Callable} throws an exception.
		 */
		@Override
		public void run() {
			try {
				this.result = callable.call();
				this.done = true;
			} catch (Exception e) {
				throw new RuntimeException(
						"Unexpected exception calling the Callable.", e);
			}
		}
	}

	private static class SwingFuture<V> implements RunnableFuture<V> {

		private boolean cancelled;

		private boolean done;

		private Callable<V> callable;

		private V result;

		private Exception error;

		private ReentrantLock lock;

		private Condition notDone;

		public SwingFuture(Callable<V> callable) {
			this.callable = callable;
			this.lock = new ReentrantLock();
			this.notDone = lock.newCondition();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			lock.lock();
			try {
				this.cancelled = true;
				this.done = true;
				return cancelled && !done;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean isCancelled() {
			lock.lock();
			try {
				return cancelled;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean isDone() {
			lock.lock();
			try {
				return done;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public V get() throws InterruptedException, ExecutionException {
			lock.lock();
			try {
				while (!done && !cancelled) {
					notDone.await();
				}
				if (this.done && this.error != null) {
					throw new ExecutionException(this.error);
				}
				return result;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {

			boolean timedOut = false;
			lock.lock();
			try {
				while (!done && !cancelled) {
					timedOut = !notDone.await(timeout, unit);
				}
				if (timedOut) {
					throw new TimeoutException();
				}
				if (this.done && this.error != null) {
					throw new ExecutionException(this.error);
				}
				return result;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void run() {
			lock.lock();
			try {
				if (!cancelled) {
					try {
						this.result = callable.call();
					} catch (Exception e) {
						this.error = e;
					}
					this.done = true;
					notDone.signal();
				}
			} finally {
				lock.unlock();
			}
		}

	}
}
