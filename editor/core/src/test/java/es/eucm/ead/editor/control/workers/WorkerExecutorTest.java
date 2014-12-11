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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import es.eucm.ead.editor.control.workers.Worker.WorkerListener;

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

		assertTrue(t1.cancelled);
		assertEquals(t2.counter, 10);
	}

	@Test
	public void testCancelled() throws Throwable {

		TestWorkerListener t1 = new TestWorkerListener();
		TestWorkerListener t2 = new TestWorkerListener();
		workerExecutor.execute(InfiniteWorker.class, t1, true);
		workerExecutor.execute(InfiniteWorker.class, t2, true);

		Thread.sleep(500);

		assertTrue(t1.cancelled && !t1.done);
		assertTrue(t2.done && !t2.cancelled);
		assertFalse(workerExecutor.isWorking());

		workerExecutor.cancelAll();
		t1 = new TestWorkerListener();
		t2 = new TestWorkerListener();

		workerExecutor.execute(InfiniteWorker.class, t1, false);
		workerExecutor.execute(InfiniteWorker.class, t2, false);

		Thread.sleep(500);

		assertTrue(!t1.cancelled && !t1.done);
		assertTrue(t2.done && !t2.cancelled);

		workerExecutor.cancel(InfiniteWorker.class, t1);

		Thread.sleep(500);

		assertTrue(t1.cancelled && !t1.done);
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

		boolean cancelled;

		int counter;

		boolean done;

		@Override
		public void start() {

		}

		@Override
		public void result(Object... args) {
			counter++;
		}

		@Override
		public void done() {
			done = true;
		}

		@Override
		public void error(Throwable ex) {

		}

		@Override
		public void cancelled() {
			cancelled = true;
		}
	}
}
