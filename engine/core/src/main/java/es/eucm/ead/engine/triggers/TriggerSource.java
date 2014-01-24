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
package es.eucm.ead.engine.triggers;

import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.behaviors.Trigger;

/**
 * Interface for all those components that are triggers sources. Actors listen
 * to these triggers, and when they receive one, they launch a linked action.
 * Relations between triggers and actions are defined by actor's behaviors.
 */
public interface TriggerSource {

	/**
	 * Updates the trigger producer. Checks if there's any pending trigger
	 * 
	 * @param delta
	 *            time since last update
	 */
	void act(float delta);

	/**
	 * Registers the given actor to listen for a trigger produced by this
	 * producer
	 * 
	 * @param actor
	 *            the actor
	 * @param trigger
	 *            the trigger to listen
	 */
	void registerForTrigger(SceneElementActor actor, Trigger trigger);

	/**
	 * Unregisters the given actor from all triggers produced by this producer.
	 * This method is usually used when the actor is removed from the current
	 * scene.
	 * 
	 * @param actor
	 *            the actor
	 */
	void unregisterForAllTriggers(SceneElementActor actor);
}
