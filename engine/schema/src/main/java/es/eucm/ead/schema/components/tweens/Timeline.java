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

package es.eucm.ead.schema.components.tweens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Base class for timelines. A timeline can be used to create complex
 * interpolations made of sequences and parallel sets of tweens.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Timeline extends BaseTween {

	/**
	 * If the timeline is a sequence or it shall begin in parallel mode.
	 * 
	 */
	private Timeline.Mode mode = Timeline.Mode.fromValue("sequence");
	/**
	 * The tweens or timelines that will be executed in sequence or in parallel
	 * mode.
	 * 
	 */
	private List<BaseTween> children = new ArrayList<BaseTween>();

	/**
	 * If the timeline is a sequence or it shall begin in parallel mode.
	 * 
	 */
	public Timeline.Mode getMode() {
		return mode;
	}

	/**
	 * If the timeline is a sequence or it shall begin in parallel mode.
	 * 
	 */
	public void setMode(Timeline.Mode mode) {
		this.mode = mode;
	}

	/**
	 * The tweens or timelines that will be executed in sequence or in parallel
	 * mode.
	 * 
	 */
	public List<BaseTween> getChildren() {
		return children;
	}

	/**
	 * The tweens or timelines that will be executed in sequence or in parallel
	 * mode.
	 * 
	 */
	public void setChildren(List<BaseTween> children) {
		this.children = children;
	}

	@Generated("org.jsonschema2pojo")
	public static enum Mode {

		SEQUENCE("sequence"), PARALLEL("parallel");
		private final String value;
		private static Map<String, Timeline.Mode> constants = new HashMap<String, Timeline.Mode>();

		static {
			for (Timeline.Mode c : Timeline.Mode.values()) {
				constants.put(c.value, c);
			}
		}

		private Mode(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Timeline.Mode fromValue(String value) {
			Timeline.Mode constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
