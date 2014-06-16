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

package es.eucm.ead.schema.effects.controlstructures;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.effects.Effect;

/**
 * Equivalent to for (int i=0; i<n; i++){effects}
 * 
 */
@Generated("org.jsonschema2pojo")
public class Repeat extends ControlStructure {

	/**
	 * Identifies the name of the variable used for counter. Set to i by
	 * default.
	 * 
	 */
	private String counter = "i";
	/**
	 * the number of loops. The counter goes from 0 to times-1
	 * 
	 */
	private int times;
	/**
	 * A list of effects that are queued for execution if the condition is
	 * evaluated to true. If not present, nothing happens.
	 * 
	 */
	private List<Effect> effects = new ArrayList<Effect>();

	/**
	 * Identifies the name of the variable used for counter. Set to i by
	 * default.
	 * 
	 */
	public String getCounter() {
		return counter;
	}

	/**
	 * Identifies the name of the variable used for counter. Set to i by
	 * default.
	 * 
	 */
	public void setCounter(String counter) {
		this.counter = counter;
	}

	/**
	 * the number of loops. The counter goes from 0 to times-1
	 * 
	 */
	public int getTimes() {
		return times;
	}

	/**
	 * the number of loops. The counter goes from 0 to times-1
	 * 
	 */
	public void setTimes(int times) {
		this.times = times;
	}

	/**
	 * A list of effects that are queued for execution if the condition is
	 * evaluated to true. If not present, nothing happens.
	 * 
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * A list of effects that are queued for execution if the condition is
	 * evaluated to true. If not present, nothing happens.
	 * 
	 */
	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}

}
