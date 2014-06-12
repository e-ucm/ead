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
package es.eucm.ead.engine.processors.behaviors;

import ashley.core.Component;
import com.badlogic.gdx.Gdx;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.MultiComponent;
import es.eucm.ead.engine.components.behaviors.BehaviorComponent;
import es.eucm.ead.engine.components.behaviors.TimersComponent;
import es.eucm.ead.engine.components.behaviors.TouchesComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.Behaviors;
import es.eucm.ead.schema.components.behaviors.Event;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;

import java.util.HashMap;
import java.util.Map;

/**
 * Processes behaviors component
 */
public class BehaviorsProcessor extends ComponentProcessor<Behaviors> {

	private Map<Class<? extends Event>, Class<? extends BehaviorComponent>> eventsToComponents;

	public BehaviorsProcessor(GameLoop gameLoop) {
		super(gameLoop);
		eventsToComponents = new HashMap<Class<? extends Event>, Class<? extends BehaviorComponent>>();

		eventsToComponents.put(Touch.class, TouchesComponent.class);
		eventsToComponents.put(Timer.class, TimersComponent.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getComponent(Behaviors behaviors) {

		MultiComponent multiComponent = new MultiComponent();

		for (Behavior behavior : behaviors.getBehaviors()) {
			Event event = behavior.getEvent();
			Class<? extends BehaviorComponent> componentClass = eventsToComponents
					.get(event.getClass());

			if (componentClass != null) {
				BehaviorComponent component = multiComponent
						.getComponent(componentClass);

				if (component == null) {
					component = gameLoop.createComponent(componentClass);
					multiComponent.add(component);
				}

				component.addBehavior(behavior.getEvent(),
						behavior.getEffects());
			} else {
				Gdx.app.error("BehaviorsProcessor",
						"No component/setter for event " + event.getClass());
			}
		}
		return multiComponent;
	}
}
