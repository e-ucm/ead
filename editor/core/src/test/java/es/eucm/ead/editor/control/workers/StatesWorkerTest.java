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

import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StatesWorkerTest extends WorkerTest implements WorkerListener {

	@Override
	public void testWorker() {
		controller.action(ExecuteWorker.class, DummyStatesWorker.class, this);
	}

	@Override
	public void asserts() {

	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		Object result = results[0];
		if (result instanceof Boolean) {
			assertTrue((Boolean) results[0]);
		} else {
			float completion = (Float) result;
			assertTrue(completion >= 0f && completion <= 1f);
		}
	}

	@Override
	public void done() {
	}

	@Override
	public void error(Throwable ex) {
		fail("Exception thrown" + ex);
	}

	@Override
	public void cancelled() {

	}

	public static class DummyStatesWorker extends StatesWorker {

		@Override
		public Class<? extends WorkerState> getInitialState() {
			return DummyInitialState.class;
		}

		public static class DummyInitialState extends WorkerState {

			private boolean mVar;

			@Override
			public void init(Object... args) {
				mVar = false;
			}

			@Override
			public void step() {
				mVar = true;
				setCompletion(1f);
				setNextState(DummyFinalState.class, mVar);
			}

			@Override
			public void cancelled() {

			}

			@Override
			public float getWeight() {
				return .5f;
			}
		}

		public static class DummyFinalState extends WorkerState {

			private boolean prevVar;

			@Override
			public void init(Object... args) {
				prevVar = (Boolean) args[0];
			}

			@Override
			public void step() {
				boolean result = prevVar;
				setCompletion(1f);
				result(result);
				end();
			}

			@Override
			public void cancelled() {

			}

			@Override
			public float getWeight() {
				return .5f;
			}
		}
	}
}
