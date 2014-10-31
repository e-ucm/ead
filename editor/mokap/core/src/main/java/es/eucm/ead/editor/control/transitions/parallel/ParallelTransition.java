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
package es.eucm.ead.editor.control.transitions.parallel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.control.transitions.Region;
import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;

/**
 * Executes {@link Transition} transitions in parallel.
 */
public class ParallelTransition implements Transition {

	private static final ParallelTransition instance = new ParallelTransition();

	private Array<TransitionInfo> transitions = new Array<TransitionInfo>(4);
	private float duration;

	public static ParallelTransition init() {
		return null;
	}

	public static ParallelTransition init(TransitionInfo... transitions) {
		float duration = 0.0f;
		instance.transitions.clear();
		for (int i = 0, n = transitions.length; i < n; ++i) {
			TransitionInfo transition = transitions[i];
			duration = Math.max(duration, transition.transition.getDuration());
			instance.transitions.add(transition);
		}
		instance.duration = duration;
		return instance;
	}

	private ParallelTransition() {

	}

	@Override
	public float getDuration() {
		return duration;
	}

	public void render(Batch batch, TextureRegion currScreen,
			Region currScreenRegion, TextureRegion nextScreen,
			Region nextScreenRegion, float percentageCompletion) {
		for (TransitionInfo transition : transitions) {
			Region currRegion = transition.currentScreenRegion;
			if (currRegion != null) {
				currScreen.setRegion(currRegion.x, currRegion.y, currRegion.w,
						currRegion.h);
				currScreen.flip(false, true);
			} else {
				currRegion = currScreenRegion;
			}

			Region nextRegion = transition.nextScreenRegion;
			if (nextRegion != null) {
				nextScreen.setRegion(nextRegion.x, nextRegion.y, nextRegion.w,
						nextRegion.h);
				nextScreen.flip(false, true);
			} else {
				nextRegion = nextScreenRegion;
			}
			transition.transition.render(batch, currScreen, currRegion,
					nextScreen, nextRegion, percentageCompletion);
		}
	}

	@Override
	public void end() {
		Pools.freeAll(transitions);
	}
}
