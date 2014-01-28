package es.eucm.ead.editor.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
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

	private Array<ModelListener> modelListeners;

	public Model(Assets assets, Factory factory) {
		this.assets = assets;
		this.factory = factory;
		scenes = new HashMap<String, Scene>();
		modelListeners = new Array<ModelListener>();
	}

	public void addListener(ModelListener modelListener) {
		modelListeners.add(modelListener);
	}

	public void load(String gamePath) {
		assets.setGamePath(gamePath, false);
		game = factory.fromJson(Game.class, assets.resolve(GAME_FILE_NAME));
		FileHandle scenesFolder = assets.resolve(SCENES_FOLDER);
		for (FileHandle sceneFile : scenesFolder.list()) {
			Scene scene = factory.fromJson(Scene.class, sceneFile);
			scenes.put(sceneFile.name(), scene);
		}

		for (ModelListener listener : modelListeners) {
			listener.modelChanged(new ModelEvent());
		}
	}

	public void save() {
		factory.toJson(game, assets.resolve(GAME_FILE_NAME));
		FileHandle sceneFolder = assets.resolve(SCENES_FOLDER);
		for (Entry<String, Scene> e : scenes.entrySet()) {
			factory.toJson(e.getValue(), sceneFolder.child(e.getKey()));
		}
	}

	public interface ModelListener {

		public void modelChanged(ModelEvent event);
	}

}
