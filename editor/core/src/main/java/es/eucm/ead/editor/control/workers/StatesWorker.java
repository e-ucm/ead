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
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;

/**
 * A worker that is executed through various states.
 */
public abstract class StatesWorker extends Worker {

	private static final String STATES_TAG = "StatesWorker";

	private ObjectMap<Class<? extends WorkerState>, WorkerState> states;
	private WorkerState currentState;
	private boolean done;

	private float progress;

	public StatesWorker() {
		this.states = new ObjectMap<Class<? extends WorkerState>, WorkerState>(
				8);
	}

	@Override
	protected void prepare() {
		progress = 0;
		setNextState(getInitialState());
	}

	public abstract Class<? extends WorkerState> getInitialState();

	@Override
	protected boolean step() {
		if (done) {
			return true;
		}
		currentState.step();
		return done;
	}

	private void setNextState(Class<? extends WorkerState> nextState,
			Object... args) {
		WorkerState workerState = states.get(nextState);
		if (workerState == null) {
			try {
				workerState = ClassReflection.newInstance(nextState);
			} catch (ReflectionException e) {
				Gdx.app.error(STATES_TAG, "Error creating new instance", e);
			}
		}
		if (currentState != null) {
			progress += currentState.getWeight();
		}
		currentState = workerState;
		if (currentState != null) {
			currentState.setController(controller);
			currentState.setWorker(this);
			currentState.init(args);
		} else {
			done = true;
		}
	}

	private void setCompletion(float completion) {
		float completed = progress + completion * currentState.getWeight();
		result(completed);
	}

	@Override
	protected void cancelled() {
		super.cancelled();
		if (currentState != null) {
			currentState.cancelled();
		}
	}

	public abstract static class WorkerState {

		private StatesWorker worker;
		protected Controller controller;

		public WorkerState() {

		}

		private void setWorker(StatesWorker worker) {
			this.worker = worker;
		}

		private void setController(Controller controller) {
			this.controller = controller;
		}

		public abstract void init(Object... args);

		public abstract void step();

		public abstract void cancelled();

		/**
		 * 
		 * @return a value from 0 to 1. The weight of this state in comparison
		 *         to the others. E.g. 0.2, which means that from the total
		 *         progress (100%) 20% will go for this state. Used to calculate
		 *         the completion. Note that the sum of all the weights must
		 *         equal to 1.
		 */
		public abstract float getWeight();

		public void result(Object... args) {
			worker.result(args);
		}

		/**
		 * Finish the working process
		 */
		public void end() {
			worker.done = true;
		}

		/**
		 * 
		 * @param completion
		 *            a value between 0 and 1 indicating the completion of the
		 *            current state.
		 */
		public void setCompletion(float completion) {
			worker.setCompletion(completion);
		}

		public void setNextState(Class<? extends WorkerState> nextState,
				Object... args) {
			worker.setNextState(nextState, args);
		}
	}

}
