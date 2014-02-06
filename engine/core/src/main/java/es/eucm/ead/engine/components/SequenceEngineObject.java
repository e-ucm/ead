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
package es.eucm.ead.engine.components;

import es.eucm.ead.engine.AbstractEngineObject;
import es.eucm.ead.schema.components.Sequence;

/**
 * This class is used as an API for defining a function that determines the
 * sequence of numbers. These numbers may represent indexes of other data
 * structures.
 * 
 * This abstract class was implemented to be used by
 * {@link es.eucm.ead.engine.renderers.frameanimation.FrameAnimationEngineObject}
 * to determine the next frame to render. However, there's no reason to avoid
 * using this class elsewhere if needed, since it is self-contained and has no
 * reference to Frame or FrameAnimation.
 * 
 * Subclasses have to implement two methods:
 * 
 * {@link #getNextIndex(int, int)}, which returns the next index given the
 * current one and the total number of elements available (the new index
 * returned will e aways smaller than total)
 * 
 * And
 * 
 * {@link #getFirst(int)}, which returns the first index of the data structure,
 * given the total number of elements
 * 
 * Created by Javier Torrente on 2/02/14.
 */
public abstract class SequenceEngineObject<T extends Sequence> extends
		AbstractEngineObject<T> {

	/**
	 * Returns the next sequence index, given the current index and the total
	 * number of available elements
	 * 
	 * @param currentIndex
	 *            The index of the last element returned by this sequence. It
	 *            must be >=0 and <total, otherwise this method just returns
	 *            currentIndex
	 * @param total
	 *            The total number of available elements in the data structure
	 * @return The number of the next element to be used
	 */
	public abstract int getNextIndex(int currentIndex, int total);

	/**
	 * Returns the initial element index of the sequence.
	 * 
	 * @param total
	 *            The total number of elements available
	 * @return The first element of the sequence, a value between 0 (inclusive)
	 *         and total (exclusive)
	 */
	public abstract int getFirst(int total);

	@Override
	public void initialize(T schemaObject) {
		// Do nothing
	}
}
