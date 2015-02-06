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
package es.eucm.ead.engine;

import ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.MultiComponent;
import es.eucm.ead.engine.components.ShaderComponent;
import es.eucm.ead.engine.components.controls.ControlComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.entities.actors.RendererActor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.components.ModelComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Javier Torrente on 13/05/14.
 */
public class ComponentLoader {

	private static final String LOG_TAG = "ComponentLoader";

	// Needed to convert model components to engine components
	private Map<Class<? extends ModelComponent>, Class<? extends Component>> modelToEngineComponents;

	private GameAssets gameAssets;

	private VariablesManager variablesManager;

	private Map<Class, ComponentProcessor> componentProcessorMap;

	public ComponentLoader(GameAssets gameAssets,
			VariablesManager variablesManager) {
		this.gameAssets = gameAssets;
		this.variablesManager = variablesManager;
		modelToEngineComponents = new HashMap<Class<? extends ModelComponent>, Class<? extends Component>>();
		componentProcessorMap = new HashMap<Class, ComponentProcessor>();
	}

	/**
	 * Tries to find the {@link Component} class equivalent to the
	 * {@link ModelComponent} provided as argument.
	 * 
	 * ModelComponent to EngineComponent mappings are created dynamically. New
	 * mappings are cached when {@link #toEngineComponent(ModelComponent)} is
	 * invoked. If a mapping is not found, then a new {@link ModelComponent} is
	 * created and it is passed to {@link #toEngineComponent(ModelComponent)} to
	 * infer the engine's component class.
	 * 
	 * @param modelClass
	 *            The class that is to be mapped to engine class.
	 * @return The {@link Component} engine equivalent class, if found,
	 *         {@code null} otherwise.
	 */
	public Class<? extends Component> toEngineComponent(
			Class<? extends ModelComponent> modelClass) {
		Class<? extends Component> component = null;

		if (!modelToEngineComponents.containsKey(modelClass)) {
			try {
				ModelComponent modelComponent = ClassReflection
						.newInstance(modelClass);

				Component componentObject = toEngineComponent(modelComponent);
				if (componentObject != null) {
					component = componentObject.getClass();
				}
				Pools.free(component);
			} catch (ReflectionException e) {
				Gdx.app.debug(LOG_TAG, "Could not create " + modelClass
						+ " using reflection. An exception was thrown", e);
			}
			// Add the mapping even if the class conversion failed. This way it
			// won't be tried again
			modelToEngineComponents.put(modelClass, component);
		}
		// If mapping already exists, just retrieve it
		else {
			component = modelToEngineComponents.get(modelClass);
		}

		if (component == null) {
			Gdx.app.error(LOG_TAG,
					"Impossible to determine engine component class for provided alias.");
			return null;
		} else {
			return component;
		}
	}

	/**
	 * Tries to find the {@link Component} class equivalent to the
	 * {@link ModelComponent} provided as argument.
	 * 
	 * ModelComponent to EngineComponent mappings are created dynamically. New
	 * mappings are cached when {@link #toEngineComponent(ModelComponent)} is
	 * invoked. If a mapping is not found, then a new {@link ModelComponent} is
	 * created and it is passed to {@link #toEngineComponent(ModelComponent)} to
	 * infer the engine's component class.
	 * 
	 * @param alias
	 *            The alias of the class that is to be mapped to engine class.
	 * @return The {@link Component} engine equivalent class, if found,
	 *         {@code null} otherwise.
	 */
	public Class<? extends Component> toEngineComponent(String alias) {
		Class modelClass;
		try {
			modelClass = gameAssets.getClass(alias);
		} catch (SerializationException e) {
			return null;
		}

		if (modelClass == null
				|| !ClassReflection.isAssignableFrom(ModelComponent.class,
						modelClass)) {
			return null;
		} else {
			return toEngineComponent(modelClass);
		}
	}

	/**
	 * @param classAlias
	 *            Class alias (e.g. "visibility")
	 * @return The associated class (e.g.
	 *         es.eucm.ead.schema.components.Visibility)
	 */
	public Class getClass(String classAlias) {
		return gameAssets.getClass(classAlias);
	}

	/**
	 * Converts a model component into an engine component
	 */
	public <T extends Component> T toEngineComponent(ModelComponent component) {
		ComponentProcessor componentProcessor = componentProcessorMap
				.get(component.getClass());
		if (componentProcessor != null) {

			component = EngineUtils.buildWithParameters(gameAssets,
					variablesManager, component);

			Component engineComponent = componentProcessor
					.getComponent(component);

			// Update modelToEngine mapping, if needed
			if (engineComponent != null
					&& !modelToEngineComponents.containsKey(component
							.getClass())) {
				modelToEngineComponents.put(component.getClass(),
						engineComponent.getClass());
			}
			return (T) engineComponent;
		}
		return null;
	}

	/**
	 * Adds the given engine component to the entity. It does also some
	 * additional processing for special components:
	 * <ul>
	 * <li><strong>{@link RendererComponent}</strong>: Creates a special actor (
	 * {@link RendererActor}) to the entity's group which actually stores the
	 * renderer.</li>
	 * <li><strong>{@link ControlComponent} Adds the control actor to the entity
	 * group</li>
	 * </ul>
	 */
	public void addComponent(EngineEntity entity, Component c) {
		if (c != null) {
			if (c instanceof MultiComponent) {
				for (Component component : ((MultiComponent) c).getComponents()) {
					addComponent(entity, component);
				}
			} else {
				entity.add(c);
				// Special cases
				if (c instanceof RendererComponent) {
					RendererComponent rendererComponent = (RendererComponent) c;
					RendererActor renderer = Pools.obtain(RendererActor.class);
					renderer.setRenderer(rendererComponent);
					entity.setGroup(renderer);
					entity.add(rendererComponent);
				} else if (c instanceof ControlComponent) {
					Actor control = ((ControlComponent) c).getControl();
					if (control instanceof Group) {
						entity.setGroup((Group) control);
					} else {
						Container<Actor> container = new Container<Actor>(
								control).fill();
						container.setTransform(true);
						entity.setGroup(container);
					}
				} else if (c instanceof ShaderComponent) {
					entity.setShader((ShaderComponent) c);
				}
			}
		}
	}

	/**
	 * Registers a processor to convert model components of the given clazz into
	 * engine components
	 */
	public <T extends ModelComponent> void registerComponentProcessor(
			Class<T> clazz, ComponentProcessor componentProcessor) {
		componentProcessorMap.put(clazz, componentProcessor);
	}
}
