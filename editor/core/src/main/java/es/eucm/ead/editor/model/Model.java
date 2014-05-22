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
package es.eucm.ead.editor.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.MultipleEvent;
import es.eucm.ead.editor.search.Index;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldNames;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.entities.ModelEntityCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Editor model. Contains all the data of the current game project.
 */
public class Model {

	private Index index;

	private Map<ModelEntityCategory, Map<String, ModelEntity>> entityMap;

	private IdentityHashMap<Object, Array<ModelListener>> listeners;

	private Array<Object> selection;

	public Model() {
		index = new Index();
		entityMap = new HashMap<ModelEntityCategory, Map<String, ModelEntity>>();
		for (ModelEntityCategory modelEntityCategory : ModelEntityCategory
				.values()) {
			entityMap.put(modelEntityCategory,
					new HashMap<String, ModelEntity>());
		}
		listeners = new IdentityHashMap<Object, Array<ModelListener>>();
		selection = new Array<Object>();
	}

	/**
	 * @return a valid entity id in the given category
	 */
	public String createId(ModelEntityCategory category) {
		String prefix = category.getCategoryPrefix();
		Map<String, ModelEntity> entitiesMap = getEntities(category);
		int count = 0;
		String id;
		do {
			id = prefix + category.getNamePrefix() + count++ + ".json";
		} while (entitiesMap.containsKey(id));
		return id;
	}

	/**
	 * Returns the entities of a given {@link ModelEntityCategory} type. This
	 * method should be used whenever write access to the map that holds the
	 * objects is needed. Also for registering {@link ModelListener}s
	 * 
	 * To iterate through all entities without distinguishing among categories,
	 * use {@link #listNamedEntities()} (read-only).
	 * 
	 * @param category
	 *            The type of model entity (e.g. scene, game).
	 * @return A map with pairs <id, entity> where each entity is of the given
	 *         type
	 */
	public Map<String, ModelEntity> getEntities(ModelEntityCategory category) {
		return entityMap.get(category);
	}

	/**
	 * Returns the entity with the given id and in the given category
	 * {@code null}.
	 */
	public ModelEntity getEntity(String id, ModelEntityCategory category) {
		Map<String, ModelEntity> entities = getEntities(category);
		if (entities != null) {
			return entities.get(id);
		}
		Gdx.app.error("Model", "No entity with id " + id + " in category "
				+ category);
		return null;
	}

	/**
	 * Clears all entities stored and also all the listeners, but those that
	 * listen to {@link LoadEvent}s. This method will typically be invoked when
	 * a game is loaded.
	 */
	public void reset() {
		for (Map<String, ModelEntity> entities : entityMap.values()) {
			entities.clear();
		}
		clearListeners();
		index.clear();
	}

	/**
	 * Adds the entity to the model. The entity is placed into the category it
	 * belongs to.
	 * 
	 * @param id
	 *            The id of the entity (e.g. "scenes/scene0.json").
	 * @param entity
	 *            The entity to be placed.
	 */
	public void putEntity(String id, ModelEntity entity) {
		ModelEntityCategory category;
		if ((category = ModelEntityCategory.getCategoryOf(id)) != null) {
			entityMap.get(category).put(id, entity);
		}
	}

	/**
	 * Puts the given entity in the given category with the given id
	 */
	public void putEntity(String id, ModelEntityCategory category,
			ModelEntity entity) {
		entityMap.get(category).put(id, entity);
	}

	/**
	 * Adds recursively all {@link ModelEntity}s using
	 * {@link #putEntity(String, ModelEntity)}. It is provided as a convenience
	 * method for setting initial values for the model when a game is loaded.
	 * 
	 * @param newEntities
	 *            The map of entities to be added to the model
	 */
	public void putEntities(Map<String, ModelEntity> newEntities) {
		for (Entry<String, ModelEntity> entry : newEntities.entrySet()) {
			putEntity(entry.getKey(), entry.getValue());
		}
	}

	public ModelEntity getGame() {
		return getEntity(GameStructure.GAME_FILE, ModelEntityCategory.GAME);
	}

	public ModelEntity getEditScene() {
		String editSceneId = Model.getComponent(getGame(), EditState.class)
				.getEditScene();
		return entityMap.get(ModelEntityCategory.SCENE).get(editSceneId);
	}

	/**
	 * Finds an ID for a given modelEntity, regardless of category.
	 * 
	 * @param modelEntity
	 * @return an ID for this entity, if any; or null if not found.
	 */
	public String getIdFor(ModelEntity modelEntity) {
		for (Map<String, ModelEntity> category : entityMap.values()) {
			for (Entry<String, ModelEntity> entity : category.entrySet()) {
				if (entity.getValue() == modelEntity) {
					return entity.getKey();
				}
			}
		}
		return null;
	}

	/**
	 * @return the current selected objects
	 */
	public Array<Object> getSelection() {
		return selection;
	}

	/**
	 * Builds a read-only structure that allows iterating through all <String,
	 * ModelEntity> entities, regardless of category. Useful for full
	 * traversals: save, export, reindex...
	 */
	public Iterable<Entry<String, ModelEntity>> listNamedEntities() {
		return new NamedEntitiesIterable();
	}

	private class NamedEntitiesIterable implements
			Iterable<Entry<String, ModelEntity>> {
		@Override
		public Iterator<Entry<String, ModelEntity>> iterator() {
			ArrayList<Entry<String, ModelEntity>> list = new ArrayList<Entry<String, ModelEntity>>();
			for (Map<String, ModelEntity> category : entityMap.values()) {
				list.addAll(category.entrySet());
			}
			return list.iterator();
		}
	}

	/**
	 * Search the index for a particular query text
	 * 
	 * @param queryText
	 *            to search (in all fields of all indexed objects)
	 * @return ranked results
	 */
	public Index.SearchResult search(String queryText) {
		return index.search(queryText);
	}

	/**
	 * Adds a listener to listen to loading events (essentially, listeners are
	 * notified when a new game project is loaded). Load listeners are
	 * perennial, i.e., they are not deleted when a new game is loaded
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addLoadListener(ModelListener<LoadEvent> listener) {
		this.addListener(this, listener);
	}

	/**
	 * Adds a field listener. Whenever the indicated fields (indicated by
	 * {@link FieldListener#listenToField(FieldNames)}) change in target, the
	 * listener is notified.
	 * 
	 * @param target
	 *            the object whose fields must be listened
	 * @param listener
	 *            the listener
	 */
	public void addFieldListener(Object target, FieldListener listener) {
		addListener(target, listener);
	}

	/**
	 * Adds a list listener. The listener will be notified whenever the given
	 * list changes
	 * 
	 * @param list
	 *            the list to listen
	 * @param listener
	 *            the listener
	 */
	public void addListListener(List list, ModelListener<ListEvent> listener) {
		addListener(list, listener);
	}

	/**
	 * Adds a map listener. The listener will be notified whenever the given map
	 * changes
	 * 
	 * @param map
	 *            the map to listen
	 * @param listener
	 *            the listener
	 */
	public void addMapListener(Map map, ModelListener<MapEvent> listener) {
		addListener(map, listener);
	}

	private void addListener(Object target, ModelListener listener) {
		Array<ModelListener> listeners = this.listeners.get(target);
		if (listeners == null) {
			listeners = new Array<ModelListener>();
			this.listeners.put(target, listeners);
		}
		if (!listeners.contains(listener, true)) {
			listeners.add(listener);
		}
	}

	/**
	 * Notifies a model event to listeners. If the event is instance of
	 * {@link MultipleEvent}, each of the events that contains is individually
	 * notified.
	 * 
	 * @param event
	 *            the event to notify. Could be {@code null}
	 */
	public void notify(ModelEvent event) {
		/*
		 * When some commands has invalid input (e.g., when
		 * RemoveFromListCommand is passed a element that is not in the list),
		 * the returned event can be null.
		 */
		if (event != null) {
			if (event instanceof MultipleEvent) {
				for (ModelEvent e : ((MultipleEvent) event).getEvents()) {
					notify(e);
				}
			} else {
				index.notify(event);
				Array<ModelListener> listeners = this.listeners.get(event
						.getTarget());
				if (listeners != null) {
					FieldNames fieldName = event instanceof FieldEvent ? ((FieldEvent) event)
							.getField() : null;
					for (ModelListener listener : listeners) {
						if (listener instanceof FieldListener) {
							if (fieldName != null
									&& ((FieldListener) listener)
											.listenToField(fieldName)) {
								listener.modelChanged(event);
							}
						} else {
							listener.modelChanged(event);
						}
					}
				}
			}
		}
	}

	/**
	 * Clears all model listeners, except those listening directly to the Model
	 * object
	 */
	private void clearListeners() {
		// Keep model listeners
		Array<ModelListener> modelListeners = this.listeners.get(this);
		this.listeners.clear();
		this.listeners.put(this, modelListeners);
	}

	/**
	 * Removes from the target listener the given listener
	 * 
	 * @param target
	 *            the target object that the given listener is listening to
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(Object target, ModelListener listener) {
		Array<ModelListener> listeners = this.listeners.get(target);
		if (listeners != null) {
			listeners.removeValue(listener, true);
		}
	}

	/**
	 * The listener stops listening to oldTarget and starts listening to
	 * newTarget
	 * 
	 * @param oldTarget
	 *            the old target object (can be null)
	 * @param newTarget
	 *            the new target object
	 * @param listener
	 *            the listener
	 */
	public void retargetListener(Object oldTarget, Object newTarget,
			ModelListener listener) {
		removeListener(oldTarget, listener);
		addListener(newTarget, listener);
	}

	public void removeListenerFromAllTargets(ModelListener listener) {
		for (Array<ModelListener> listeners : this.listeners.values()) {
			listeners.removeValue(listener, true);
		}
	}

	/**
	 * General interface to listen to the model
	 * 
	 * @param <T>
	 *            the type of the event
	 */
	public interface ModelListener<T extends ModelEvent> {

		/**
		 * Called when the model changed
		 * 
		 * @param event
		 *            the model event
		 */
		public void modelChanged(T event);
	}

	/**
	 * General interface to listen to fields
	 */
	public interface FieldListener extends ModelListener<FieldEvent> {

		/**
		 * @param fieldName
		 *            the field name (an object of enum type {@link FieldNames}
		 * @return true if this listener is interested in the fieldName
		 */
		boolean listenToField(FieldNames fieldName);

	}

	/**
	 * Returns the component for the class. If the element has no component of
	 * the given type, is automatically created and added to it.
	 * 
	 * @param element
	 *            the element with the component
	 * @param componentClass
	 *            the component class
	 * @return the component inside the element
	 */
	public static <T extends ModelComponent> T getComponent(
			es.eucm.ead.schema.entities.ModelEntity element,
			Class<T> componentClass) {
		for (ModelComponent component : element.getComponents()) {
			if (component.getClass() == componentClass) {
				return (T) component;
			}
		}
		try {
			ModelComponent component = ClassReflection
					.newInstance(componentClass);
			element.getComponents().add(component);
			return (T) component;
		} catch (ReflectionException e) {
			Gdx.app.error("Model",
					"Error creating component " + componentClass, e);
		}
		return null;
	}

	/**
	 * @return whether the given element contains a component with the given
	 *         class
	 */
	public static <T extends ModelComponent> boolean hasComponent(
			ModelEntity element, Class<T> componentClass) {
		for (ModelComponent component : element.getComponents()) {
			if (component.getClass() == componentClass) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the entity associated to the given actor. Returns {@code null} if
	 *         no entity associated is to the actor
	 */
	public static EngineEntity getActorEntity(Actor actor) {
		Object o = actor.getUserObject();
		if (o instanceof EngineEntity) {
			return ((EngineEntity) o);
		}
		return null;
	}

	/**
	 * @return the model entity associated to the given actor. Returns
	 *         {@code null} if no model entity is associated to the actor
	 */
	public static ModelEntity getModelEntity(Actor actor) {
		Object o = actor.getUserObject();
		if (o instanceof EngineEntity) {
			return ((EngineEntity) o).getModelEntity();
		}
		return null;
	}

}
