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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

public class StatesComponent extends RendererComponent {

	/**
	 * If any state has this tag, it is set for preview. If several states have
	 * this tag, the last one added will be picked.
	 */
	public static final String DEFAULT_STATE_TAG = "default";

	private Array<State> states;

	protected State currentState;

	public StatesComponent() {
		states = new Array<State>();
	}

	public void addRenderer(Array<String> stateTags, RendererComponent renderer) {
		State newState = new State(stateTags, renderer);
		states.add(newState);
		if (currentState == null
				|| stateTags.contains(DEFAULT_STATE_TAG, false)) {
			currentState = newState;
		}
	}

	@Override
	public void draw(Batch batch) {
		if (currentState != null && currentState.rendererComponent != null) {
			currentState.rendererComponent.draw(batch);
		}
	}

	@Override
	public Array<Polygon> getCollider() {
		return currentState == null || currentState.rendererComponent == null ? null
				: currentState.rendererComponent.getCollider();
	}

	@Override
	public void act(float delta) {
		if (currentState != null && currentState.rendererComponent != null) {
			currentState.rendererComponent.act(delta);
		}
	}

	@Override
	public float getWidth() {
		return currentState == null || currentState.rendererComponent == null ? 0
				: currentState.rendererComponent.getWidth();
	}

	@Override
	public float getHeight() {
		return currentState == null || currentState.rendererComponent == null ? 0
				: currentState.rendererComponent.getHeight();
	}

	@Override
	public boolean hit(float x, float y) {
		return currentState != null && currentState.rendererComponent != null
				&& currentState.rendererComponent.hit(x, y);
	}

	private static final class State {

		private Array<String> states;

		private RendererComponent rendererComponent;

		private State(Array<String> states, RendererComponent rendererComponent) {
			this.states = states;
			this.rendererComponent = rendererComponent;
		}

		private RendererComponent getRendererComponent() {
			return rendererComponent;
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
