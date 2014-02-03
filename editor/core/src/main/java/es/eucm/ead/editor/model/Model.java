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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.model.events.GameEvent;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Model {

	public static final String GAME_FILE_NAME = "game.json";

	public static final String SCENES_FOLDER = "scenes/";

	private Assets assets;

	private Factory factory;

	private Game game;

	private Map<String, Scene> scenes;

	private Map<Class<?>, Array<ModelListener>> modelListeners;

	public Model(Assets assets, Factory factory) {
		this.assets = assets;
		this.factory = factory;
		scenes = new HashMap<String, Scene>();
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

	public void load(String gamePath) {
		assets.setGamePath(gamePath, false);
		assets.load("game.json", Game.class);
		assets.finishLoading();
		game = assets.get("game.json", Game.class);
		notify(new GameEvent(game));
	}

	public void notify(ModelEvent event) {
		Array<ModelListener> listeners = modelListeners.get(event.getClass());
		if (listeners != null) {
			for (ModelListener listener : listeners) {
				listener.modelChanged(event);
			}
		}
	}

	public void save() {
		factory.toJson(game, assets.resolve(GAME_FILE_NAME));
		FileHandle sceneFolder = assets.resolve(SCENES_FOLDER);
		for (Entry<String, Scene> e : scenes.entrySet()) {
			factory.toJson(e.getValue(), sceneFolder.child(e.getKey()));
		}
	}

	public interface ModelListener<T extends ModelEvent> {

		public void modelChanged(T event);
	}

}
