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
package es.eucm.ead.editor.control.transitions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;
import es.eucm.ead.editor.control.transitions.parallel.ParallelTransition;
import es.eucm.ead.editor.control.transitions.parallel.TransitionInfo;

/**
 * Class with static methods that create convenience transitions.
 */
public class Transitions {

	private static final float IN_OUT = .5f;

	private Transitions() {
	}

	public static Transition getFadeSlideTransition(Actor fadeActor,
			Actor slideActor, boolean in) {
		return ParallelTransition.init(TransitionInfo.init(
				Fade.init(IN_OUT, false), fadeActor), TransitionInfo.init(Slide
				.init(IN_OUT, in ? Slide.RIGHT : Slide.LEFT, !in,
						in ? Interpolation.pow2In : Interpolation.pow2Out),
				slideActor));
	}

	public static Transition getSlideTransition(boolean in) {
		return Slide.init(IN_OUT, in ? Slide.RIGHT : Slide.LEFT, !in,
				in ? Interpolation.pow2In : Interpolation.pow2Out);
	}

	public static Transition getFadeTransition(boolean in) {
		return Fade.init(IN_OUT, false);
	}
}
