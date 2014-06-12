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

package es.eucm.ead.schema.components.behaviors;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.effects.Effect;

/**
 * Associates an event that can occur to an entity to a list of effects that are
 * launched when the event occurs
 * 
 */
@Generated("org.jsonschema2pojo")
public class Behavior {

	/**
	 * Represents something that can happen to an entity
	 * 
	 */
	private Event event;
	/**
	 * List of effects to be launched
	 * 
	 */
	private List<Effect> effects = new ArrayList<Effect>();

	/**
	 * Represents something that can happen to an entity
	 * 
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Represents something that can happen to an entity
	 * 
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * List of effects to be launched
	 * 
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * List of effects to be launched
	 * 
	 */
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}

}
