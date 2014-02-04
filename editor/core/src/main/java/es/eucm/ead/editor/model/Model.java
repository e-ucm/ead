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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import es.eucm.ead.editor.model.events.GameEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.ProjectEvent;
import es.eucm.ead.editor.model.events.SceneEvent;
import es.eucm.ead.editor.model.events.SceneEvent.Type;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

import java.util.HashMap;
import java.util.Map;

public class Model {

	private Project project;

	private Game game;

	private OrderedMap<String, Scene> scenes;

	private Map<Class<?>, Array<ModelListener>> modelListeners;

	public Model() {
		scenes = new OrderedMap<String, Scene>();
		modelListeners = new HashMap<Class<?>, Array<ModelListener>>();
	}

	public <T extends ModelEvent> void addListener(Class<T> clazz,
			ModelListener<T> modelListener) {
		Array<ModelListener> listeners = modelListeners.get(clazz);
		if (listeners == null) {
			listeners = new Array<ModelListener>();
			modelListeners.put(clazz, listeners);
		}
		listeners.add(modelListener);
	}

	public void notify(ModelEvent event) {
		Gdx.app.debug("Model", "Notifying event " + event.toString());
		Array<ModelListener> listeners = modelListeners.get(event.getClass());
		if (listeners != null) {
			for (ModelListener listener : listeners) {
				listener.modelChanged(event);
			}
		}
	}

	public Project getProject() {
		return project;
	}

	public Game getGame() {
		return game;
	}

	public void setProject(Project project) {
		this.project = project;
		notify(new ProjectEvent(project, ProjectEvent.Type.LOADED));
	}

	public void setGame(Game game) {
		this.game = game;
		notify(new GameEvent(game));
	}

	public OrderedMap<String, Scene> getScenes() {
		return scenes;
	}

	public void editScene(String sceneName) {
		if (project != null) {
			project.setEditScene(sceneName);
		}
		Scene scene = scenes.get(sceneName);
		notify(new SceneEvent(sceneName, scene, Type.EDIT));
	}

	public String addScene(Scene scene) {
		int counter = scenes.orderedKeys().size;
		String name = "scene" + counter;
		while (scenes.orderedKeys().contains(name, false)) {
			counter++;
			name = "scene" + counter;
		}
		addScene(name, scene);
		return name;
	}

	public void addScene(String name, Scene scene) {
		scenes.put(name, scene);
		notify(new SceneEvent(name, scene, Type.ADDED));
		editScene(name);
	}

	public Scene removeScene(String name) {
		int index = scenes.orderedKeys().indexOf(name, false);
		Scene scene = scenes.remove(name);
		notify(new SceneEvent(name, scene, Type.REMOVED));
		if (scenes.size > 0) {
			if (index == scenes.size) {
				index--;
			}
			editScene(scenes.orderedKeys().get(index));
		}
		return scene;
	}

	public Scene getCurrentScene() {
		return scenes.get(project.getEditScene());
	}

	public void clear() {
		scenes.clear();
		project = null;
		game = null;
		notify(new ProjectEvent(null, ProjectEvent.Type.UNLOADED));
	}

	public interface ModelListener<T extends ModelEvent> {

		public void modelChanged(T event);
	}

}
