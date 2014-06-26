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
package es.eucm.ead.engine.components.behaviors.events;

import es.eucm.ead.schema.components.behaviors.events.Key;

public class RuntimeKey extends RuntimeBehavior {

	/**
	 * code associated with each key
	 * 
	 */
	private int keycode;
	/**
	 * When ctrl is pressed along with a key
	 * 
	 */
	private boolean ctrl;
	/**
	 * When alt is pressed along with a key
	 * 
	 */
	private boolean alt;
	/**
	 * When shift is pressed along with a key
	 * 
	 */
	private boolean shift;

	/**
	 * code associated with each key
	 * 
	 */
	public int getKeycode() {
		return keycode;
	}

	/**
	 * code associated with each key
	 * 
	 */
	public void setKeycode(int keycode) {
		this.keycode = keycode;
	}

	/**
	 * When ctrl is pressed along with a key
	 * 
	 */
	public boolean isCtrl() {
		return ctrl;
	}

	/**
	 * When ctrl is pressed along with a key
	 * 
	 */
	public void setCtrl(boolean ctrl) {
		this.ctrl = ctrl;
	}

	/**
	 * When alt is pressed along with a key
	 * 
	 */
	public boolean isAlt() {
		return alt;
	}

	/**
	 * When alt is pressed along with a key
	 * 
	 */
	public void setAlt(boolean alt) {
		this.alt = alt;
	}

	/**
	 * When shift is pressed along with a key
	 * 
	 */
	public boolean isShift() {
		return shift;
	}

	/**
	 * When shift is pressed along with a key
	 * 
	 */
	public void setShift(boolean shift) {
		this.shift = shift;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (o instanceof RuntimeKey) {
			RuntimeKey key2 = (RuntimeKey) o;
			return this.getKeycode() == key2.getKeycode()
					&& this.isAlt() == key2.isAlt()
					&& this.isCtrl() == key2.isCtrl()
					&& this.isShift() == key2.isShift();
		}

		return false;
	}

}
