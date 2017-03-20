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
package es.eucm.ead.engine.components.renderers;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.EngineStage;
import es.eucm.ead.engine.entities.actors.EntityGroup;

public class StatesActor extends EntityGroup {

	/**
	 * If any state has this tag, it is set for preview. If several states have
	 * this tag, the last one added will be picked.
	 */
	public static final String DEFAULT_STATE_TAG = "default";

	private Array<StateData> states;

	protected StateData currentState;

	public StatesActor() {
		states = new Array<StateData>();
	}

	public void addRenderer(Array<String> stateTags, RendererComponent renderer) {
		StateData newState = new StateData(stateTags, renderer);
		states.add(newState);
		if (currentState == null
				|| stateTags.contains(DEFAULT_STATE_TAG, false)) {
			setState(newState);
		}
	}

	/**
	 * Updates the current state to the first one in the list that is not the
	 * current state and has the given {@code stateTag}. Nothing happens if
	 * stateTag is null, or if no state contains the tag.
	 * 
	 * @param stateTag
	 *            The tag identifying the new state
	 */
	public void changeState(String stateTag) {
		for (StateData state : states) {
			if (state.states.contains(stateTag, false) && state != currentState) {
				setState(state);
			}
		}
	}

	private void setState(StateData state) {
		if (currentState != null) {
			removeActor(currentState.renderer.getEntityGroup());
		}
		if (getStage() instanceof EngineStage) {
			EngineStage engineStage = (EngineStage) getStage();
			engineStage.updateTarget(currentState.renderer.getEntityGroup(),
					state.renderer.getEntityGroup());
		}
		currentState = state;
		currentState.renderer.restart();
		// clearChildren();
		addActor(currentState.renderer.getEntityGroup());
	}

	private static final class StateData {

		private Array<String> states;

		private RendererComponent renderer;

		private StateData(Array<String> states, RendererComponent renderer) {
			this.states = states;
			this.renderer = renderer;
		}

		public int count(Array<String> states) {
			int count = 0;
			for (String s : this.states) {
				if (states.contains(s, false)) {
					count++;
				}
			}
			return count;
		}
	}
}
