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
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.MultipleEvent;
import es.eucm.ead.editor.model.events.ResourceEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.search.Index;
import es.eucm.ead.editor.search.Index.Match;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Editor model. Contains all the resources of the current game project.
 */
public class Model {

	private Index index;

	private Map<ResourceCategory, Map<String, Object>> resourcesMap;

	private IdentityHashMap<Object, Array<ModelListener>> listeners;

	private Array<ModelListener<LoadEvent>> loadListeners;
	private Array<ModelListener<ResourceEvent>> resourcesListeners;
	private Array<SelectionListener> selectionListeners;

	private Selection selection;

	public Model() {
		resourcesMap = new HashMap<ResourceCategory, Map<String, Object>>();
		for (ResourceCategory resourceCategory : ResourceCategory.values()) {
			resourcesMap.put(resourceCategory, new HashMap<String, Object>());
		}

		listeners = new IdentityHashMap<Object, Array<ModelListener>>();
		loadListeners = new Array<ModelListener<LoadEvent>>();
		resourcesListeners = new Array<ModelListener<ResourceEvent>>();
		selectionListeners = new Array<SelectionListener>();

		index = new Index();
		index.ignoreClass(Parent.class);

		selection = new Selection();
	}

	/**
	 * @return editor selection object
	 */
	public Selection getSelection() {
		return selection;
	}

	/**
	 * @return a valid resource id in the given category
	 */
	public String createId(ResourceCategory category) {
		String prefix = category.getCategoryPrefix();
		Map<String, Object> resourcesMap = getResources(category);
		int count = 0;
		String id;
		do {
			id = prefix + category.getNamePrefix() + count++ + ".json";
		} while (resourcesMap.containsKey(id));
		return id;
	}

	/**
	 * Returns the resources of a given {@link ResourceCategory} type. This
	 * method should be used whenever write access to the map that holds the
	 * objects is needed. Also for registering {@link ModelListener}s
	 * <p/>
	 * To iterate through all resources without distinguishing among categories,
	 * use {@link #listNamedResources()} (read-only).
	 * 
	 * @param category
	 *            The type of resource
	 * @return A map with pairs <id, entity> where each entity is of the given
	 *         type
	 */
	public Map<String, Object> getResources(ResourceCategory category) {
		return resourcesMap.get(category);
	}

	/**
	 * Returns the resources with the given id and in the given category
	 * {@code null}.
	 */
	public Object getResource(String id, ResourceCategory category) {
		Map<String, Object> resources = getResources(category);
		if (resources != null) {
			return resources.get(id);
		}
		Gdx.app.error("Model", "No resource with id " + id + " in category "
				+ category);
		return null;
	}

	/**
	 * Clears all resources stored and also all the listeners, but those that
	 * listen to {@link LoadEvent}s. This method will typically be invoked when
	 * a game is loaded.
	 */
	public void reset() {
		for (Map<String, Object> resources : resourcesMap.values()) {
			resources.clear();
		}
		clearListeners();
		index.clear();
	}

	/**
	 * Adds a resource to the model. The entity is placed into the category it
	 * belongs to.
	 * 
	 * @param id
	 *            The id of the entity (e.g. "scenes/scene0.json").
	 * @param resource
	 *            The resource to be placed.
	 */
	public void putResource(String id, Object resource) {
		ResourceCategory category;
		if ((category = ResourceCategory.getCategoryOf(id)) != null) {
			resourcesMap.get(category).put(id, resource);
		}
	}

	/**
	 * Puts the given resource in the given category with the given id
	 */
	public void putResource(String id, ResourceCategory category, Object entity) {
		resourcesMap.get(category).put(id, entity);
	}

	/**
	 * Removes the resource with the given id in the given category
	 * 
	 * @return the resource removed. {@code null} if no entity was found
	 */
	public Object removeResource(String id, ResourceCategory category) {
		return resourcesMap.get(category).remove(id);
	}

	public ModelEntity getGame() {
		return (ModelEntity) getResource(GameStructure.GAME_FILE,
				ResourceCategory.GAME);
	}

	/**
	 * Finds an ID for a given resource, regardless of category.
	 * 
	 * @return an ID for this entity, if any; or null if not found.
	 */
	public String getIdFor(Object resource) {
		for (Map<String, Object> category : resourcesMap.values()) {
			for (Entry<String, Object> resourceEntry : category.entrySet()) {
				if (resourceEntry.getValue() == resource) {
					return resourceEntry.getKey();
				}
			}
		}
		return null;
	}

	/**
	 * Builds a read-only structure that allows iterating through all resources
	 * entities, regardless of category. Useful for full traversals: save,
	 * export, reindex...
	 */
	public Iterable<Entry<String, Object>> listNamedResources() {
		return new NamedResourcesIterable();
	}

	private class NamedResourcesIterable implements
			Iterable<Entry<String, Object>> {
		@Override
		public Iterator<Entry<String, Object>> iterator() {
			ArrayList<Entry<String, Object>> list = new ArrayList<Entry<String, Object>>();
			for (Map<String, Object> category : resourcesMap.values()) {
				list.addAll(category.entrySet());
			}
			return list.iterator();
		}
	}

	/**
	 * @return the index search
	 */
	public Index getIndex() {
		return index;
	}

	/**
	 * Search the index for a particular query text
	 * 
	 * @param queryText
	 *            to search (in all fields of all indexed objects)
	 * @return ranked results
	 */
	public Array<Match> search(String queryText) {
		return index.search(queryText);
	}

	/**
	 * Adds a listener to listen to resource events. Listeners are notified when
	 * a resource is added/removed from the model
	 */
	public void addResourceListener(ModelListener<ResourceEvent> listener) {
		resourcesListeners.add(listener);
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
		loadListeners.add(listener);
	}

	/**
	 * Adds a listener to listen to selection changes.
	 */
	public void addSelectionListener(SelectionListener listener) {
		selectionListeners.add(listener);
	}

	/**
	 * Adds a field listener. Whenever the indicated fields (indicated by
	 * {@link FieldListener#listenToField(String)})
	 * change in target, the listener is notified.
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
	public void addListListener(Array list, ModelListener<ListEvent> listener) {
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
	@SuppressWarnings("unchecked")
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
				index.updateIndex(event);
				if (event instanceof LoadEvent) {
					notify((LoadEvent) event, loadListeners);
				} else if (event instanceof ResourceEvent) {
					notify((ResourceEvent) event, resourcesListeners);
				} else if (event instanceof SelectionEvent) {
					SelectionEvent selectionEvent = (SelectionEvent) event;
					for (SelectionListener selectionListener : selectionListeners) {
						if (selectionListener.listenToContext(selectionEvent
								.getContextId())) {
							selectionListener.modelChanged(selectionEvent);
						}
					}
				} else {
					Array<ModelListener> listeners = this.listeners.get(event
							.getTarget());
					if (listeners != null) {
						String fieldName = event instanceof FieldEvent ? ((FieldEvent) event)
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
	}

	private <T extends ModelEvent> void notify(T event,
			Array<ModelListener<T>> listeners) {
		for (ModelListener<T> listener : listeners) {
			listener.modelChanged(event);
		}
	}

	/**
	 * Clears all model listeners, except those listening directly to the Model
	 * object
	 */
	private void clearListeners() {
		this.listeners.clear();
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
		 *            the field name
		 * @return true if this listener is interested in the fieldName
		 */
		boolean listenToField(String fieldName);

	}

	/**
	 * General interface to listen to fields
	 */
	public interface SelectionListener extends ModelListener<SelectionEvent> {

		/**
		 * @return true if this listener is interested in the context
		 */
		boolean listenToContext(String contextId);

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
	@SuppressWarnings("unchecked")
	public static <T extends ModelComponent> T getComponent(
			ModelEntity element, Class<T> componentClass) {
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

	/**
	 * @return returns the first object of given class in the iterable
	 */
	public static <T> T getObjectOfClass(Iterable iterable, Class<T> clazz) {
		for (Object o : iterable) {
			if (o.getClass() == clazz) {
				return (T) o;
			}
		}
		return null;
	}

	/**
	 * Iterates through the model entity hierarchy to recover its root ancestor
	 */
	public static ModelEntity getRootAncestor(ModelEntity modelEntity) {
		if (modelEntity == null) {
			return null;
		}

		ModelEntity rootEntity = modelEntity;
		while (true) {
			Parent parent = Model.getComponent(rootEntity, Parent.class);
			if (parent != null && parent.getParent() != null) {
				rootEntity = parent.getParent();
			} else {
				break;
			}
		}
		return rootEntity;
	}

}
