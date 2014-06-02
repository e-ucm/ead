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

package es.eucm.ead.schema.effects;

import javax.annotation.Generated;
import es.eucm.ead.schema.components.tweens.BaseTween;

/**
 * An effect that adds a new entity. The entity to be added can be provided
 * either inside the effect, using the {@code entity} property, or by referring
 * to another file, using the {@code entityRef} property. If both are specified,
 * {@code entity} has priority. The effect also supports in-out animation. The
 * total time the entity is shown is determined as: {@code durationIn}+
 * {@code duration}+{@code durationOut}.
 * 
 */
@Generated("org.jsonschema2pojo")
public class AddEntity extends Effect {

	/**
	 * The entity to be added to the game, read from a separate file.
	 * 
	 */
	private String entityUri;
	/**
	 * Base class for tweens and timelines
	 * 
	 */
	private BaseTween animationIn;
	/**
	 * The time this entity will be present in the game, in seconds, after the
	 * animationIn has completed. After that, it is removed. -1 (default value)
	 * means the entity is never removed.
	 * 
	 */
	private float duration = -1.0F;
	/**
	 * Base class for tweens and timelines
	 * 
	 */
	private BaseTween animationOut;

	/**
	 * The entity to be added to the game, read from a separate file.
	 * 
	 */
	public String getEntityUri() {
		return entityUri;
	}

	/**
	 * The entity to be added to the game, read from a separate file.
	 * 
	 */
	public void setEntityUri(String entityUri) {
		this.entityUri = entityUri;
	}

	/**
	 * Base class for tweens and timelines
	 * 
	 */
	public BaseTween getAnimationIn() {
		return animationIn;
	}

	/**
	 * Base class for tweens and timelines
	 * 
	 */
	public void setAnimationIn(BaseTween animationIn) {
		this.animationIn = animationIn;
	}

	/**
	 * The time this entity will be present in the game, in seconds, after the
	 * animationIn has completed. After that, it is removed. -1 (default value)
	 * means the entity is never removed.
	 * 
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * The time this entity will be present in the game, in seconds, after the
	 * animationIn has completed. After that, it is removed. -1 (default value)
	 * means the entity is never removed.
	 * 
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}

	/**
	 * Base class for tweens and timelines
	 * 
	 */
	public BaseTween getAnimationOut() {
		return animationOut;
	}

	/**
	 * Base class for tweens and timelines
	 * 
	 */
	public void setAnimationOut(BaseTween animationOut) {
		this.animationOut = animationOut;
	}

}
