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

import es.eucm.ead.engine.AbstractEngineObject;
import es.eucm.ead.schema.renderers.frameanimation.NextFrame;

/**
 * This abstract class is used by
 * {@link es.eucm.ead.engine.renderers.frameanimation.FrameAnimationRenderer} to
 * determine the next frame to render. Subclasses have to implement two methods,
 * {@link #getNextFrameIndex(int, int)}, which returns the next frame index
 * given the current one and the total number of frames available
 * 
 * Created by Javier Torrente on 2/02/14.
 */
public abstract class NextFrameFunction<T extends NextFrame> extends
		AbstractEngineObject<T> {

	/**
	 * Returns the next frame index to be rendered by
	 * {@link es.eucm.ead.engine.renderers.frameanimation.FrameAnimationRenderer}
	 * , given the current frame index and the total number of available frames
	 * 
	 * @param currentFrameIndex
	 *            The index of the current frame being rendererd by the
	 *            FrameAnimationRenderer. It must be >=0 and <totalFrames,
	 *            otherwise this method just returns currentFrameIndex
	 * @param totalFrames
	 *            The total number of frames this FrameAnimationRenderer has.
	 * @return The number of the next frame to be used
	 */
	public abstract int getNextFrameIndex(int currentFrameIndex, int totalFrames);

	/**
	 * Returns the initial frame index to be rendered. This method is invoked by
	 * {@link es.eucm.ead.engine.renderers.frameanimation.FrameAnimationRenderer}
	 * upon initialization.
	 * 
	 * @param totalFrames
	 *            The total number of frames this FrameAnimationRenderer has.
	 * @return The initial frame index, a value between 0 (inclusive) and
	 *         totalFrames (exclusive)
	 */
	public abstract int getInitialFrameIndex(int totalFrames);

	@Override
	public void initialize(T schemaObject) {
		// Do nothing
	}
}
