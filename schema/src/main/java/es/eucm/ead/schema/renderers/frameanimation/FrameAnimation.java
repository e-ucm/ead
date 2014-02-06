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
package es.eucm.ead.schema.renderers.frameanimation;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.components.Sequence;
import es.eucm.ead.schema.renderers.Renderer;

/**
 * A renderer representing a list of frames, intended for animated actors. A
 * frame can be any type of renderer extending timed.json.
 * 
 */
@Generated("org.jsonschema2pojo")
public class FrameAnimation extends Renderer {

	private List<Timed> frames = new ArrayList<Timed>();
	/**
	 * Abstract function to be used as an API for any sequence of elements.
	 * First developed for FrameAnimations
	 * 
	 */
	private Sequence sequence;

	public List<Timed> getFrames() {
		return frames;
	}

	public void setFrames(List<Timed> frames) {
		this.frames = frames;
	}

	/**
	 * Abstract function to be used as an API for any sequence of elements.
	 * First developed for FrameAnimations
	 * 
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * Abstract function to be used as an API for any sequence of elements.
	 * First developed for FrameAnimations
	 * 
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

}
