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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.control.transitions.Region;
import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;

/**
 * Holds a {@link Transition} and a {@link Region} where the transition is
 * applied. If no region is specified the whole screen will be affected.
 */
public class TransitionInfo implements Poolable {

	private static final Vector2 TEMP = new Vector2();

	Transition transition;
	Region currentScreenRegion, nextScreenRegion;

	private TransitionInfo() {
	}

	public static TransitionInfo init(Transition transition, Actor actor) {
		TransitionInfo actorInfo = Pools.obtain(TransitionInfo.class);

		actorInfo.transition = transition;

		actor.localToStageCoordinates(TEMP.set(0f, 0f));
		actorInfo.currentScreenRegion = new Region(TEMP.x, TEMP.y,
				actor.getWidth(), actor.getHeight());
		actorInfo.nextScreenRegion = new Region(TEMP.x, TEMP.y,
				actor.getWidth(), actor.getHeight());

		return actorInfo;
	}

	@Override
	public void reset() {
		transition = null;
		currentScreenRegion = nextScreenRegion = null;
	}
}
