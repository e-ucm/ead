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
package es.eucm.ead.engine.listeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.actions.AbstractAction;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.schema.actions.Action;
import es.eucm.ead.schema.behaviors.Touch;

public class SceneElementInputListener implements EventListener {
	static private final Vector2 tmpCoords = new Vector2();

	@Override
	public boolean handle(Event e) {
		boolean result = false;
		if (!(e instanceof InputEvent))
			return false;
		InputEvent event = (InputEvent) e;

		Actor act = event.getListenerActor();
		if (act instanceof SceneElementActor) {
			SceneElementActor actor = (SceneElementActor) act;
			Array<Action> actions = null;

			event.toCoordinates(event.getListenerActor(), tmpCoords);

			switch (event.getType()) {
			case touchDown:
				actions = actor.getActions(Touch.Event.TOUCH_DOWN);
				break;
			case touchUp:
				actions = actor.getActions(Touch.Event.TOUCH_UP);
				result = true;
				break;
			case touchDragged:
				actions = actor.getActions(Touch.Event.TOUCH_DRAGGED);
				result = true;
				break;
			case mouseMoved:
				actions = actor.getActions(Touch.Event.MOUSE_MOVED);
				break;
			case scrolled:
				actions = actor.getActions(Touch.Event.SCROLLED);
				break;
			case enter:
				actions = actor.getActions(Touch.Event.ENTER);
				break;
			case exit:
				actions = actor.getActions(Touch.Event.EXIT);
				break;
			}
			if (actions != null) {
				for (Action a : actions) {
					AbstractAction action = Engine.factory.getElement(a);
					action.setEvent(event);
					actor.addAction(action);
				}
			}
		}
		return result;
	}
}
