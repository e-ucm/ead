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
	public void testSimpleWork() {
		TestWorkerListener t1 = new TestWorkerListener();
		TestWorkerListener t2 = new TestWorkerListener();
		TestWorkerListener t3 = new TestWorkerListener();

		assertTrue(workerExecutor.execute(TestWorker.class, t1));
		assertFalse(workerExecutor.execute(TestWorker.class, t2));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(workerExecutor.execute(TestWorker.class, t3));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(t1.counter, 10);
		assertEquals(t2.counter, 0);
		assertEquals(t3.counter, 10);
	}

	@AfterClass
	public static void tearDownClass() {
		workerExecutor.dispose();
	}

	public static class TestWorker extends Worker {

		@Override
		protected void runWork() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < 10; i++) {
				result();
			}
			done();
		}
	}

	public class TestWorkerListener implements WorkerListener {

		int counter;

		@Override
		public void result(Object... args) {
			counter++;
		}

		@Override
		public void done() {
			assertEquals(counter, 10);
		}

		@Override
		public void error(Throwable ex) {

		}
	}
}
