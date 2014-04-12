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
import ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.MultiComponent;
import es.eucm.ead.engine.components.TouchedComponent;
import es.eucm.ead.engine.components.behaviors.TouchesComponent;
import es.eucm.ead.engine.components.controls.ControlComponent;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.entities.ActorEntity.EntityGroup;
import es.eucm.ead.engine.entities.actors.RendererActor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts {@link ModelEntity} into ashley {@link Component}s. Processors
 * transform model entities into engine entities can be registered through
 * {@link EntitiesLoader#registerComponentProcessor(Class, ComponentProcessor)}
 */
public class EntitiesLoader implements AssetLoadedCallback<ModelEntity> {

	private GameAssets gameAssets;

	private GameLoop gameLoop;

	private Map<Class, ComponentProcessor> componentProcessorMap;

	private final RenderActorListener renderActorListener = new RenderActorListener();

	private GameLayers gameLayers;

	private ObjectMap<String, String> loading;

	public EntitiesLoader(GameAssets gameAssets, GameLoop gameLoop,
			GameLayers gameLayers) {
		this.gameAssets = gameAssets;
		this.gameLoop = gameLoop;
		this.gameLayers = gameLayers;
		componentProcessorMap = new HashMap<Class, ComponentProcessor>();
		loading = new ObjectMap<String, String>();
	}

	/**
	 * Registers a processor to convert model components of the given clazz into
	 * engine components
	 */
	public <T extends ModelComponent> void registerComponentProcessor(
			Class<T> clazz, ComponentProcessor componentProcessor) {
		componentProcessorMap.put(clazz, componentProcessor);
	}

	/**
	 * Loads the entity stored in path into the given layer, completely removing
	 * whatever it was in the layer before.
	 * 
	 * @param layer
	 *            a layer inside {@link GameLayers}
	 * @param path
	 *            a path inside the game resources containing an entity
	 */
	public void addEntityToLayer(String layer, String path) {
		loading.put(path, layer);
		gameAssets.get(path, ModelEntity.class, this);
	}

	/**
	 * Converts a model component into a engine component
	 */
	public Component getComponent(ModelComponent component) {
		ComponentProcessor componentProcessor = componentProcessorMap
				.get(component.getClass());
		if (componentProcessor != null) {
			return componentProcessor.getComponent(component);
		}
		return null;
	}

	public ActorEntity addEntity(ModelEntity child) {
		ActorEntity entity = gameLoop.createEntity();
		entity.setModelEntity(child);

		for (ModelComponent component : child.getComponents()) {
			addComponent(entity, getComponent(component));

		}
		gameLoop.addEntity(entity);
		for (ModelEntity c : child.getChildren()) {
			entity.getGroup().addActor(addEntity(c).getGroup());
		}
		return entity;
	}

	private void addComponent(ActorEntity entity, Component c) {
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
					renderer.addListener(renderActorListener);
					entity.getGroup().addActor(renderer);
					entity.add(rendererComponent);
				}

				if (c instanceof ControlComponent) {
					Actor control = ((ControlComponent) c).getControl();
					control.addListener(renderActorListener);
					entity.getGroup().addActor(control);
				}

				if (c instanceof TouchesComponent) {
					entity.getGroup().addListener(renderActorListener);
				}
			}
		}
	}

	@Override
	public void loaded(String fileName, ModelEntity asset) {
		String layer = loading.remove(fileName);
		gameLayers.setLayer(layer, addEntity(asset).getGroup());
	}

	private class RenderActorListener extends ClickListener {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			Actor listenerActor = event.getListenerActor();
			while (listenerActor != null) {
				if (listenerActor instanceof EntityGroup) {
					Entity entity = ((EntityGroup) listenerActor).getEntiy();
					if (entity.hasComponent(TouchesComponent.class)) {
						TouchedComponent component = gameLoop
								.createComponent(TouchedComponent.class);
						component.touch();
						entity.add(component);
						return;
					}
				}
				listenerActor = listenerActor.getParent();
			}
		}
	}
}
