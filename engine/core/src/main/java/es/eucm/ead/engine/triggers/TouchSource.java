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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.behaviors.Touch;
import es.eucm.ead.schema.behaviors.Touch.Type;
import es.eucm.ead.schema.behaviors.Trigger;

/**
 * Source for touch triggers
 */
public class TouchSource implements EventListener, TriggerSource {

	@Override
	public boolean handle(Event e) {
		boolean result = false;
		if (!(e instanceof InputEvent))
			return false;
		InputEvent event = (InputEvent) e;

		Actor act = event.getListenerActor();
		if (act instanceof SceneElementActor) {
			SceneElementActor actor = (SceneElementActor) act;
			Touch.Type type = null;

			switch (event.getType()) {
			case touchDown:
				type = Type.PRESS;
				break;
			case touchUp:
				type = Type.RELEASE;
				result = true;
				break;
			case enter:
				type = Type.ENTER;
				break;
			case exit:
				type = Type.EXIT;
				break;
			}

			if (type != null) {
				for (Trigger trigger : actor.getBehaviors().keySet()) {
					if (trigger instanceof Touch
							&& ((Touch) trigger).getType() == type) {
						result |= actor.process(trigger);
					}
				}
			}
		}
		return result;
	}

	@Override
	public void act(float delta) {
		// Do nothing, stage automatically emits touch events
	}

	@Override
	public void registerForTrigger(SceneElementActor actor, Trigger event) {
		if (!actor.getListeners().contains(this, true)) {
			actor.addListener(this);
		}
	}

	@Override
	public void unregisterForAllTriggers(SceneElementActor actor) {
		if (actor.getListeners().contains(this, true)) {
			actor.removeListener(this);
		}
	}
}
