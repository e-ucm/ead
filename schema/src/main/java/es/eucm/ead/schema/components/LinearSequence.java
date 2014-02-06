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
package es.eucm.ead.schema.components;

import javax.annotation.Generated;

/**
 * Linear function implementation of
 * {@link es.eucm.ead.schema.components.Sequence}. This function just returns
 * element++ given element, producing a linear sequence. If loop is set to true,
 * when the last element is reached (current==total-1) it starts again.
 * Otherwise, the last element (total-1) is returned indefinitely.
 * 
 */
@Generated("org.jsonschema2pojo")
public class LinearSequence extends Sequence {

	/**
	 * if true, the sequence will restart after the last element (total-1) is
	 * reached. Otherwise the last element (total-1) is returned for each
	 * currentIndex>=total
	 * 
	 */
	private boolean loop;

	/**
	 * if true, the sequence will restart after the last element (total-1) is
	 * reached. Otherwise the last element (total-1) is returned for each
	 * currentIndex>=total
	 * 
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * if true, the sequence will restart after the last element (total-1) is
	 * reached. Otherwise the last element (total-1) is returned for each
	 * currentIndex>=total
	 * 
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

}
