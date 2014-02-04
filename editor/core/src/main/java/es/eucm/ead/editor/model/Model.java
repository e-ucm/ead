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
import es.eucm.ead.editor.model.events.SceneEvent;
import es.eucm.ead.editor.model.events.GameEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.ProjectEvent;
import es.eucm.ead.editor.model.events.SceneEvent.Type;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Model {

	public static final String GAME_FILE_NAME = "game.json";

	public static final String SCENES_FOLDER = "scenes/";

	private Project project;

	private Assets assets;

	private Game game;

	private Map<String, Scene> scenes;

	private Scene currentScene;

	private Map<Class<?>, Array<ModelListener>> modelListeners;

	public Model(Assets assets) {
		this.assets = assets;
		scenes = new HashMap<String, Scene>();
		modelListeners = new HashMap<Class<?>, Array<ModelListener>>();
		addListener(SceneEvent.class, new ModelListener<SceneEvent>() {
			@Override
			public void modelChanged(SceneEvent event) {
				switch (event.getType()) {
				case ADDED:
					scenes.put(event.getName(), event.getScene());
					break;
				case REMOVED:
					scenes.remove(event.getName());
					break;
				case EDIT:
					currentScene = event.getScene();
				}
			}
		});
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

	public void load(String projectPath) {
		assets.setGamePath(projectPath, false);
		assets.load("project.json", Project.class);
		assets.finishLoading();
		project = assets.get("project.json", Project.class);
		game = assets.get("game.json", Game.class);

		notify(new ProjectEvent(project));
		notify(new GameEvent(game));
		editScene(game.getInitialScene());
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

	public void save() {
		Gdx.app.debug("Model", "Saving the model...");
		assets.toJson(game, assets.resolve(GAME_FILE_NAME));
		for (Entry<String, Scene> e : scenes.entrySet()) {
			assets.toJson(e.getValue(),
					assets.resolve(assets.convertSceneNameToPath(e.getKey())));
		}
	}

	public void editScene(String sceneName) {
		String scenePath = assets.convertSceneNameToPath(sceneName);
		assets.load(scenePath, Scene.class);
		assets.finishLoading();
		Scene scene = assets.get(scenePath, Scene.class);
		notify(new SceneEvent(sceneName, scene, Type.EDIT));
	}

	public SceneEvent addScene(String name, Scene scene) {
		int counter = project.getScenes().size();
		while (project.getScenes().contains(name)) {
			counter++;
			name = "scene" + counter;
		}
		project.getScenes().add(name);
		return new SceneEvent(name, scene, Type.ADDED);
	}

	public SceneEvent removeScene(String name) {
		project.getScenes().remove(name);
		Scene scene = scenes.get(name);
		return new SceneEvent(name, scene, Type.REMOVED);
	}

	public Scene getCurrentScene() {
		return currentScene;
	}

	public interface ModelListener<T extends ModelEvent> {

		public void modelChanged(T event);
	}

}
