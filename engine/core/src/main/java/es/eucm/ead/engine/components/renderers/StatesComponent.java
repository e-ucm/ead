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
	public static final String DEFAULT_STATE = "default";

	private Array<State> states;

	protected RendererComponent currentRenderer;

	private Array<String> currentState;

	public StatesComponent() {
		states = new Array<State>();
	}

	public void addRenderer(Array<String> state, RendererComponent renderer) {
		states.add(new State(state, renderer));
		currentState = new Array<String>();
		if (currentRenderer == null) {
			currentRenderer = renderer;
		}
		if (state.contains(DEFAULT_STATE, false)) {
			currentRenderer = renderer;
		}
	}

	public void addState(String state) {
		if (!currentState.contains(state, false)) {
			currentState.add(state);
			int maxCount = 0;
			for (State s : states) {
				int count = s.count(currentState);
				if (count > maxCount) {
					maxCount = count;
					currentRenderer = s.getRendererComponent();
				}
			}
		}
	}

	@Override
	public void draw(Batch batch) {
		if (currentRenderer != null) {
			currentRenderer.draw(batch);
		}
	}

	@Override
	public Array<Polygon> getCollider() {
		return currentRenderer.getCollider();
	}

	@Override
	public void act(float delta) {
		if (currentRenderer != null) {
			currentRenderer.act(delta);
		}
	}

	@Override
	public float getWidth() {
		return currentRenderer == null ? 0 : currentRenderer.getWidth();
	}

	@Override
	public float getHeight() {
		return currentRenderer == null ? 0 : currentRenderer.getHeight();
	}

	@Override
	public boolean hit(float x, float y) {
		return currentRenderer != null && currentRenderer.hit(x, y);
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
