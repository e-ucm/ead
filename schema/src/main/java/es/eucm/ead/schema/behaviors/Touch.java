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

package es.eucm.ead.schema.behaviors;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Touch extends Input {

	private Touch.Event event;

	public Touch.Event getEvent() {
		return event;
	}

	public void setEvent(Touch.Event event) {
		this.event = event;
	}

	@Generated("org.jsonschema2pojo")
	public static enum Event {

		TOUCH_DOWN("touchDown"), TOUCH_UP("touchUp"), TOUCH_DRAGGED(
				"touchDragged"), MOUSE_MOVED("mouseMoved"), SCROLLED("scrolled"), ENTER(
				"enter"), EXIT("exit");
		private final String value;
		private static Map<String, Touch.Event> constants = new HashMap<String, Touch.Event>();

		static {
			for (Touch.Event c : Touch.Event.values()) {
				constants.put(c.value, c);
			}
		}

		private Event(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Touch.Event fromValue(String value) {
			Touch.Event constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
