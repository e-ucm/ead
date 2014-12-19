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

import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkerExecutorTest {

	static WorkerExecutor workerExecutor;

	@BeforeClass
	public static void setUpClass() {
		workerExecutor = new WorkerExecutor(null);
	}

	@Test
	public void testSimpleWorker() throws Throwable {
		TestWorkerListener t1 = new TestWorkerListener();
		TestWorkerListener t2 = new TestWorkerListener();

		workerExecutor.execute(TestWorker.class, t1, true);
		workerExecutor.execute(TestWorker.class, t2, true);

		Thread.sleep(3000);

		assertTrue(t1.cancelled.get());
		assertEquals(t2.counter, 10);
	}

	@Test
	public void testCancelled() throws Throwable {

		TestWorkerListener t1 = new TestWorkerListener();
		TestWorkerListener t2 = new TestWorkerListener();
		workerExecutor.execute(InfiniteWorker.class, t1, true);
		workerExecutor.execute(InfiniteWorker.class, t2, true);

		while (workerExecutor.isWorking()) {
			Thread.sleep(500);
		}

		assertTrue(t1.cancelled.get() && !t1.done.get());
		assertTrue(t2.done.get() && !t2.cancelled.get());
		assertFalse(workerExecutor.isWorking());

		workerExecutor.cancelAll();
		t1 = new TestWorkerListener();
		t2 = new TestWorkerListener();

		workerExecutor.execute(InfiniteWorker.class, t1, false);
		workerExecutor.execute(InfiniteWorker.class, t2, false);

		while (!t2.done.get()) {
			Thread.sleep(500);
		}

		assertTrue(!t1.cancelled.get() && !t1.done.get());
		assertTrue(t2.done.get() && !t2.cancelled.get());

		workerExecutor.cancel(InfiniteWorker.class, t1);

		while (!t1.cancelled.get()) {
			Thread.sleep(500);
		}

		assertTrue(t1.cancelled.get() && !t1.done.get());
		assertFalse(workerExecutor.isWorking());
	}

	@AfterClass
	public static void tearDownClass() {
		workerExecutor.dispose();
	}

	public static class TestWorker extends Worker {

		private int counter;

		@Override
		protected void prepare() {
			counter = 0;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected boolean step() {
			counter++;
			result();
			return counter == 10;
		}
	}

	public static class InfiniteWorker extends Worker {

		public static int counter = 0;

		private boolean infinite;

		@Override
		void setListener(WorkerListener listener) {
			super.setListener(listener);
			this.infinite = counter++ % 2 == 0;
		}

		@Override
		protected void prepare() {

		}

		@Override
		protected boolean step() {
			return !infinite;
		}
	}

	public class TestWorkerListener implements WorkerListener {

		AtomicBoolean cancelled = new AtomicBoolean(false);

		int counter;

		AtomicBoolean done = new AtomicBoolean(false);

		@Override
		public void start() {

		}

		@Override
		public void result(Object... args) {
			counter++;
		}

		@Override
		public void done() {
			done.set(true);
		}

		@Override
		public void error(Throwable ex) {

		}

		@Override
		public void cancelled() {
			cancelled.set(true);
		}
	}
}
