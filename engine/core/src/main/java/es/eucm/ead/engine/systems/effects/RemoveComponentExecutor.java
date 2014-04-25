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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.effects.RemoveComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Executes {@link RemoveComponent}.
 * 
 * Created by Javier Torrente on 24/04/14.
 */
public class RemoveComponentExecutor extends EffectExecutor<RemoveComponent> {

	// Needed to convert model components to engine components
	private Map<Class<? extends ModelComponent>, Class<? extends Component>> modelToEngineComponents;

	// Needed to convert class alias into class names (e.g. textbutton =>
	// TextButton).
	private GameAssets gameAssets;

	// Needed to convert ModelComponents into engine Components so it can be
	// determined, given a model component class, what type of engine component
	// has to be removed.
	private EntitiesLoader entitiesLoader;

	public RemoveComponentExecutor(GameAssets gameAssets,
			EntitiesLoader entitiesLoader) {
		this.gameAssets = gameAssets;
		this.entitiesLoader = entitiesLoader;
		modelToEngineComponents = new HashMap<Class<? extends ModelComponent>, Class<? extends Component>>();
	}

	@Override
	public void execute(Entity owner, RemoveComponent effect) {
		// Build component to be removed
		String classAlias = effect.getComponent();
		boolean correct = true;
		try {
			Class classParameter = gameAssets.getClass(classAlias);
			// Check class returned is not null and also that it is a model
			// component
			// subclass
			if (classParameter == null
					|| !ClassReflection.isAssignableFrom(ModelComponent.class,
							classParameter)) {
				correct = false;
			} else {
				// Make conversion to engine component
				Class<? extends ModelComponent> modelComponentClass = (Class<? extends ModelComponent>) classParameter;
				Class<? extends Component> componentClass = toEngineComponentClass(modelComponentClass);
				if (componentClass != null) {
					// Remove the component
					owner.remove(componentClass);
				} else {
					correct = false;
				}
			}

		} catch (SerializationException exception) {
			correct = false;
		}

		if (!correct) {
			Gdx.app.error(
					"RemoveComponentExecutor",
					"The effect could not be executed because the class alias provided was not found to match an existing component class.");
		}
	}

	/**
	 * Tries to find the {@link Component} class equivalent to the
	 * {@link ModelComponent} provided as argument using
	 * {@link EntitiesLoader#getComponent(ModelComponent)}. If found, the result
	 * is cached so the number of new instances of {@link ModelComponent} that
	 * are created is minimum.
	 * 
	 * @param modelComponentClass
	 *            The {@link ModelComponent} class that is to be mapped to
	 *            engine class.
	 * @return The {@link Component} engine equivalent class, if found,
	 *         {@code null} otherwise.
	 */
	private Class<? extends Component> toEngineComponentClass(
			Class<? extends ModelComponent> modelComponentClass) {
		Class<? extends Component> componentClass = null;
		if (!modelToEngineComponents.containsKey(modelComponentClass)) {
			// Create model component
			try {
				ModelComponent modelComponent = ClassReflection
						.newInstance(modelComponentClass);
				// Convert to engine component
				Component component = entitiesLoader
						.getComponent(modelComponent);
				if (component != null) {
					componentClass = component.getClass();
				}
			} catch (ReflectionException e) {
			}
			// Add the mapping even if the class conversion failed. This way it
			// won't be tried again
			modelToEngineComponents.put(modelComponentClass, componentClass);
		}
		// If mapping already exists, just retrieve it
		else {
			componentClass = modelToEngineComponents.get(modelComponentClass);
		}

		if (componentClass == null) {
			Gdx.app.error(
					"RemoveComponentExecutor",
					"It was an impossible mission to determine the engine Component class that corresponds to the model component class provided. The RemoveComponent effect was skipped.");
			return null;
		} else {
			return componentClass;
		}
	}
}
