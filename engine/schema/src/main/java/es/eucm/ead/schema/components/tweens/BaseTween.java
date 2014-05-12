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

import javax.annotation.Generated;

/**
 * Base class for tweens and timelines
 * 
 */
@Generated("org.jsonschema2pojo")
public class BaseTween {

	/**
	 * Time before the tween/timelines starts.
	 * 
	 */
	private float delay = 0.0F;
	/**
	 * How many times the tween/timelines repeats. -1 for forever. Default is 0
	 * 
	 */
	private int repeat = 0;
	/**
	 * Time before the tween/timelines starts a repetition. This delay applied
	 * after duration if repeat is set to something different to 0.
	 * 
	 */
	private float repeatDelay = 0.0F;
	/**
	 * If the tween/timelines must come back to its initial state
	 * 
	 */
	private boolean yoyo = false;

	/**
	 * Time before the tween/timelines starts.
	 * 
	 */
	public float getDelay() {
		return delay;
	}

	/**
	 * Time before the tween/timelines starts.
	 * 
	 */
	public void setDelay(float delay) {
		this.delay = delay;
	}

	/**
	 * How many times the tween/timelines repeats. -1 for forever. Default is 0
	 * 
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * How many times the tween/timelines repeats. -1 for forever. Default is 0
	 * 
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	/**
	 * Time before the tween/timelines starts a repetition. This delay applied
	 * after duration if repeat is set to something different to 0.
	 * 
	 */
	public float getRepeatDelay() {
		return repeatDelay;
	}

	/**
	 * Time before the tween/timelines starts a repetition. This delay applied
	 * after duration if repeat is set to something different to 0.
	 * 
	 */
	public void setRepeatDelay(float repeatDelay) {
		this.repeatDelay = repeatDelay;
	}

	/**
	 * If the tween/timelines must come back to its initial state
	 * 
	 */
	public boolean isYoyo() {
		return yoyo;
	}

	/**
	 * If the tween/timelines must come back to its initial state
	 * 
	 */
	public void setYoyo(boolean yoyo) {
		this.yoyo = yoyo;
	}

}
