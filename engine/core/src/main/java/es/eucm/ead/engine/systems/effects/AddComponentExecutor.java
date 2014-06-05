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
package es.eucm.ead.engine.systems.effects;

import ashley.core.Component;
import ashley.core.Entity;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.effects.AddComponent;

/**
 * Executes effects of type {@link AddComponent}. It just uses
 * {@link ComponentLoader} to create the engine component from the model
 * component specified in the effect and adds it to the entity.
 * 
 * Created by Javier Torrente on 18/04/14.
 */
public class AddComponentExecutor extends EffectExecutor<AddComponent> {

	// To create new engine components
	private ComponentLoader componentLoader;

	public AddComponentExecutor(ComponentLoader componentLoader) {
		this.componentLoader = componentLoader;
	}

	@Override
	public void execute(Entity target, AddComponent effect) {
		// Build component to be added
		Component component = componentLoader.toEngineComponent(effect
				.getComponent());
		if (component != null) {
			// Add to entity
			if (target instanceof EngineEntity) {
				componentLoader.addComponent((EngineEntity) target, component);
			} else {
				target.add(component);
			}
		}
	}
}
