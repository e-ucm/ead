/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.renderers.frameanimation;

import es.eucm.ead.engine.renderers.RendererEngineObject;
import es.eucm.ead.schema.renderers.frameanimation.Timed;

/**
 * Created by Javier Torrente on 2/02/14.
 */
public abstract class TimedEngineObject<T extends Timed> extends
		RendererEngineObject<T> {

	protected float elapsedTime;

	@Override
	public void initialize(T schemaObject) {
		reset();
	}

	@Override
	public void act(float delta) {
		elapsedTime += delta;
	}

	/**
	 * Returns the difference between the elapsedTime and the duration of the
	 * frame. If this difference is greater or equals zero, this means the frame
	 * should not be rendererd by the frameRendererd animation, which should
	 * change to the next frame as defined by the Sequence function.
	 * 
	 * {@link #surplusTime()} should be invoked right after {@link #act(float)},
	 * which is the method that updates elapsedTime.
	 * 
	 * @return A float value that represents the difference between the
	 *         elapsedTime of this frame (time the frame has been visible) and
	 *         the total duration of the frame, as defined in the schemaObject.
	 */
	public float surplusTime() {
		float definedDuration = getSchema().getDuration();
		return elapsedTime - definedDuration;
	}

	/**
	 * Just sets elapsedTime to zero again. THis is to be invoked after isDone()
	 * returns true, so the next time the frame is to be rendered it is still
	 * alive
	 */
	public void reset() {
		elapsedTime = 0;
	}
}
