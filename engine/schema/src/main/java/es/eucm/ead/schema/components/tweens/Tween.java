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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Base class for tweens
 * 
 */
@Generated("org.jsonschema2pojo")
public class Tween extends BaseTween {

	/**
	 * Time for the tween. Total time from begining to end equals to delay +
	 * duration
	 * 
	 */
	private float duration;
	/**
	 * If the tween is relative to the actual value of the tween target. Default
	 * is false
	 * 
	 */
	private boolean relative;
	/**
	 * Ease equation. Defines how the value for the tween is interpolated. More
	 * info about easing functions: <a
	 * href="http://easings.net/">http://easings.net/</a>. Default is linear
	 * 
	 */
	private Tween.EaseEquation easeEquation = Tween.EaseEquation
			.fromValue("linear");
	/**
	 * Ease type. Default is inout
	 * 
	 */
	private Tween.EaseType easeType = Tween.EaseType.fromValue("inout");

	/**
	 * Time for the tween. Total time from begining to end equals to delay +
	 * duration
	 * 
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * Time for the tween. Total time from begining to end equals to delay +
	 * duration
	 * 
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}

	/**
	 * If the tween is relative to the actual value of the tween target. Default
	 * is false
	 * 
	 */
	public boolean isRelative() {
		return relative;
	}

	/**
	 * If the tween is relative to the actual value of the tween target. Default
	 * is false
	 * 
	 */
	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	/**
	 * Ease equation. Defines how the value for the tween is interpolated. More
	 * info about easing functions: <a
	 * href="http://easings.net/">http://easings.net/</a>. Default is linear
	 * 
	 */
	public Tween.EaseEquation getEaseEquation() {
		return easeEquation;
	}

	/**
	 * Ease equation. Defines how the value for the tween is interpolated. More
	 * info about easing functions: <a
	 * href="http://easings.net/">http://easings.net/</a>. Default is linear
	 * 
	 */
	public void setEaseEquation(Tween.EaseEquation easeEquation) {
		this.easeEquation = easeEquation;
	}

	/**
	 * Ease type. Default is inout
	 * 
	 */
	public Tween.EaseType getEaseType() {
		return easeType;
	}

	/**
	 * Ease type. Default is inout
	 * 
	 */
	public void setEaseType(Tween.EaseType easeType) {
		this.easeType = easeType;
	}

	@Generated("org.jsonschema2pojo")
	public static enum EaseEquation {

		LINEAR("linear"), QUAD("quad"), CUBIC("cubic"), QUART("quart"), QUINT(
				"quint"), CIRC("circ"), SINE("sine"), EXPO("expo"), BACK("back"), BOUNCE(
				"bounce"), ELASTIC("elastic");
		private final String value;
		private static Map<String, Tween.EaseEquation> constants = new HashMap<String, Tween.EaseEquation>();

		static {
			for (Tween.EaseEquation c : Tween.EaseEquation.values()) {
				constants.put(c.value, c);
			}
		}

		private EaseEquation(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Tween.EaseEquation fromValue(String value) {
			Tween.EaseEquation constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

	@Generated("org.jsonschema2pojo")
	public static enum EaseType {

		IN("in"), OUT("out"), INOUT("inout");
		private final String value;
		private static Map<String, Tween.EaseType> constants = new HashMap<String, Tween.EaseType>();

		static {
			for (Tween.EaseType c : Tween.EaseType.values()) {
				constants.put(c.value, c);
			}
		}

		private EaseType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Tween.EaseType fromValue(String value) {
			Tween.EaseType constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
