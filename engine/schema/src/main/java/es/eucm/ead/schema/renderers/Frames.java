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

package es.eucm.ead.schema.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 * A renderer representing a list of frames, intended for animated actors. A
 * frame can be any type of renderer extending timed.json.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Frames extends Renderer {

	private List<Frame> frames = new ArrayList<Frame>();
	/**
	 * linear just puts frames in order from first to last, cyclically if
	 * necessary. random delivers what it promises. yoyo undoes the linear
	 * sequence when the last frame is reached. Example of sequence with 4
	 * frames: 0, 1, 2, 3, 2, 1. Then it restarts at 0 (cyclical)
	 * 
	 */
	private Frames.Sequence sequence = Frames.Sequence.fromValue("linear");

	public List<Frame> getFrames() {
		return frames;
	}

	public void setFrames(List<Frame> frames) {
		this.frames = frames;
	}

	/**
	 * linear just puts frames in order from first to last, cyclically if
	 * necessary. random delivers what it promises. yoyo undoes the linear
	 * sequence when the last frame is reached. Example of sequence with 4
	 * frames: 0, 1, 2, 3, 2, 1. Then it restarts at 0 (cyclical)
	 * 
	 */
	public Frames.Sequence getSequence() {
		return sequence;
	}

	/**
	 * linear just puts frames in order from first to last, cyclically if
	 * necessary. random delivers what it promises. yoyo undoes the linear
	 * sequence when the last frame is reached. Example of sequence with 4
	 * frames: 0, 1, 2, 3, 2, 1. Then it restarts at 0 (cyclical)
	 * 
	 */
	public void setSequence(Frames.Sequence sequence) {
		this.sequence = sequence;
	}

	@Generated("org.jsonschema2pojo")
	public static enum Sequence {

		LINEAR("linear"), RANDOM("random"), YOYO("yoyo");
		private final String value;
		private static Map<String, Frames.Sequence> constants = new HashMap<String, Frames.Sequence>();

		static {
			for (Frames.Sequence c : Frames.Sequence.values()) {
				constants.put(c.value, c);
			}
		}

		private Sequence(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Frames.Sequence fromValue(String value) {
			Frames.Sequence constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
