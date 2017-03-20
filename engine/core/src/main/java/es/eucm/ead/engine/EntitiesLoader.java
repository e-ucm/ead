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

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Converts {@link ModelEntity} into ashley {@link Entity}s. Delegates in
 * {@link ComponentLoader} to transform model components into engine components.
 */
public class EntitiesLoader implements AssetLoadedCallback<Object> {

	protected GameAssets gameAssets;

	protected ComponentLoader componentLoader;

	protected GameLoop gameLoop;

	private ObjectMap<String, EntityLoadedCallback> loading;

	public EntitiesLoader(GameLoop gameLoop, GameAssets gameAssets,
			ComponentLoader componentLoader) {
		this.gameAssets = gameAssets;
		this.gameLoop = gameLoop;
		loading = new ObjectMap<String, EntityLoadedCallback>();
		this.componentLoader = componentLoader;
	}

	/**
	 * Returns the {@code ComponentLoader} used to create engine components,
	 * resolve model component alias, etc.
	 */
	public ComponentLoader getComponentLoader() {
		return componentLoader;
	}

	/**
	 * @return True if the entity stored in the given path (e.g. scenes/s1.json)
	 *         has been loaded, or its loading has been scheduled. It returns
	 *         false otherwise.
	 */
	public boolean isEntityLoaded(String path) {
		return loading.containsKey(path)
				|| gameAssets.isLoaded(path, Object.class);
	}

	/**
	 * Starts loading the model entity stored in the given {@code path}. Load is
	 * asynchronous so a {@code callback} must be passed to receive the
	 * notification when the ModelEntity is ready.
	 * 
	 * @param path
	 *            a relative path inside the game resources containing an entity
	 *            (e.g. "scenes/scene2.json")
	 * @param callback
	 *            The callback that is notified when the process is complete
	 */
	public void loadEntity(String path, EntityLoadedCallback callback) {
		bindCallback(path, callback);
		gameAssets.get(path, Object.class, this);
	}

	@Override
	// This method gets invoked when an entity scheduled for loading is ready.
	// It just notifies the associated callback(s) and converts the model entity
	// into an engine entity if necessary.
	public void loaded(String fileName, Object modelEntity) {
		EntityLoadedCallback callback = loading.remove(fileName);
		if (callback != null) {
			EngineEntity engineEntity = callback.toEngine() ? toEngineEntity((ModelEntity) modelEntity)
					: null;
			callback.loaded(fileName, engineEntity);
		}
	}

	@Override
	public void error(String fileName, Class type, Throwable exception) {
		EntityLoadedCallback callback = loading.remove(fileName);
		callback.pathNotFound(fileName);
	}

	/**
	 * This method converts the given {@code modelEntity} {@link ModelEntity}
	 * into a fully functional runtime entity ({@link EngineEntity}). It also
	 * creates any {@link Component} needed and attaches it to the recently
	 * created entity. This method works recursively on all children.
	 * 
	 * Note: this method does not actually attach the entity to LibGdx's scene
	 * tree, so it won't be rendered on the screen unless that is done
	 * separately. This method adds the EngineEntity created to the game engine,
	 * so it will be processed by any system that needs to.
	 * 
	 * @param modelEntity
	 *            The {@link ModelEntity} to be transformed into an
	 *            {@link Entity}.
	 * @return The entity added
	 */
	public EngineEntity toEngineEntity(ModelEntity modelEntity) {
		EngineEntity entity = gameLoop.createEntity();
		entity.setModelEntity(modelEntity);

		for (ModelComponent component : modelEntity.getComponents()) {
			componentLoader.addComponent(entity,
					componentLoader.toEngineComponent(component));
		}
		gameLoop.addEntity(entity);

		for (ModelEntity child : modelEntity.getChildren()) {
			entity.getGroup().addActor(toEngineEntity(child).getGroup());
		}

		return entity;
	}

	/**
	 * Simple callback that gets notified when an entity scheduled for loading
	 * from disk is ready
	 */
	public interface EntityLoadedCallback {
		/**
		 * ModelEntity loaded successfully.
		 * 
		 * @param path
		 *            The relative path of the entity
		 * @param engineEntity
		 *            The runtime entity created.
		 */
		void loaded(String path, EngineEntity engineEntity);

		/**
		 * Called when the path for the entity is not found
		 */
		void pathNotFound(String path);

		/**
		 * @return True if the callback requires {@link #EntitiesLoader} to
		 *         convert the model entity read into an engine entity (for
		 *         example, when the entity has to be added to {@link GameView},
		 *         false otherwise (e.g., when the entity's resources are being
		 *         preloaded to speed up scene transitions).
		 */
		boolean toEngine();
	}

	/**
	 * Associates the given callback with the entity to be loaded from the given
	 * path. When the entity is loaded, the callback will be retrieved and
	 * notified of its status. More than one callback per entity is allowed,
	 * since various petitions to load the same entity could be placed at the
	 * same time.
	 * 
	 * @param path
	 *            The path of the entity that is about to be loaded (e.g.
	 *            scenes/s1.json)
	 * @param newCallback
	 *            The {@link EntityLoadedCallback} object to be associated to
	 *            the entity
	 */
	private void bindCallback(String path, EntityLoadedCallback newCallback) {
		EntityLoadedCallback mainCallback = null;
		if (!loading.containsKey(path)) {
			mainCallback = newCallback;
		} else {
			MultipleEntityLoadedCallbacks multipleEntityLoadedCallbacks = null;
			EntityLoadedCallback prevCallback = loading.get(path);
			if (prevCallback instanceof MultipleEntityLoadedCallbacks) {
				multipleEntityLoadedCallbacks = (MultipleEntityLoadedCallbacks) prevCallback;
			} else {
				multipleEntityLoadedCallbacks = new MultipleEntityLoadedCallbacks();
				multipleEntityLoadedCallbacks.addCallback(prevCallback);
			}
			multipleEntityLoadedCallbacks.addCallback(newCallback);
			mainCallback = multipleEntityLoadedCallbacks;
		}
		loading.put(path, mainCallback);
	}

	/**
	 * Just a simple implementation of {@link EntityLoadedCallback} to notify
	 * several callbacks at the same time
	 */
	private static class MultipleEntityLoadedCallbacks implements
			EntityLoadedCallback {

		private Array<EntityLoadedCallback> uniqueCallbacks = new Array<EntityLoadedCallback>();

		public boolean addCallback(EntityLoadedCallback callback) {
			if (uniqueCallbacks.contains(callback, true)) {
				return false;
			}
			uniqueCallbacks.add(callback);
			return true;
		}

		@Override
		public void loaded(String path, EngineEntity engineEntity) {
			for (EntityLoadedCallback callback : uniqueCallbacks) {
				callback.loaded(path, engineEntity);
			}
		}

		@Override
		public void pathNotFound(String path) {
			for (EntityLoadedCallback callback : uniqueCallbacks) {
				callback.pathNotFound(path);
			}
		}

		@Override
		public boolean toEngine() {
			for (EntityLoadedCallback callback : uniqueCallbacks) {
				if (callback.toEngine()) {
					return true;
				}
			}
			return false;
		}
	}
}
