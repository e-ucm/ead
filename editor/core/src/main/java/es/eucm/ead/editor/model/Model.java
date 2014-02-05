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
package es.eucm.ead.editor.model;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Model {

	private Project project;

	private Game game;

	private Map<String, Scene> scenes;

	private IdentityHashMap<Object, Array<ModelListener>> modelListeners;

	public Model() {
		scenes = new HashMap<String, Scene>();
		modelListeners = new IdentityHashMap<Object, Array<ModelListener>>();
	}

	public Project getProject() {
		return project;
	}

	public Game getGame() {
		return game;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Map<String, Scene> getScenes() {
		return scenes;
	}

	public void addListener(Object target, ModelListener listener) {
		Array<ModelListener> listeners = modelListeners.get(target);
		if (listeners == null) {
			listeners = new Array<ModelListener>();
			modelListeners.put(target, listeners);
		}
		listeners.add(listener);
	}

	public void addFieldListener(Object target, String fieldName,
			ModelListener listener) {
		// XXX We probably should make field listener poolables
		addListener(target, new FieldListener(fieldName, listener));
	}

	public void notify(ModelEvent event) {
		Array<ModelListener> listeners = modelListeners.get(event.getTarget());
		if (listeners != null) {
			String fieldName = event instanceof FieldEvent ? ((FieldEvent) event)
					.getField() : null;
			for (ModelListener listener : listeners) {
				if (fieldName != null && listener instanceof FieldListener) {
					if (fieldName.equals(((FieldListener) listener).field)) {
						listener.modelChanged(event);
					}
				} else {
					listener.modelChanged(event);
				}
			}
		}
	}

	public void clear() {
		// Keep model listeners
		Array<ModelListener> listeners = modelListeners.get(this);
		modelListeners.clear();
		modelListeners.put(this, listeners);
		scenes.clear();
		project = null;
		game = null;
	}

	public void setScenes(Map<String, Scene> scenes) {
		this.scenes = scenes;
	}

	public interface ModelListener<T extends ModelEvent> {

		public void modelChanged(T event);
	}

	private class FieldListener implements ModelListener {

		public String field;

		private ModelListener listener;

		protected FieldListener(String field, ModelListener listener) {
			this.field = field;
			this.listener = listener;
		}

		@Override
		public void modelChanged(ModelEvent event) {
			listener.modelChanged(event);
		}
	}

}
