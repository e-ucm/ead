package es.eucm.ead.engine.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.assets.GameLoader.GameParameter;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

public class GameLoader extends AsynchronousAssetLoader<Game, GameParameter> {

	private Factory factory;

	private Game game;

	public GameLoader(FileHandleResolver resolver, Factory factory) {
		super(resolver);
		this.factory = factory;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, GameParameter parameter) {
		game = factory.fromJson(Game.class, file);
		Array<AssetDescriptor> dependencies = factory.popDependencies();
		dependencies.add(new AssetDescriptor<Scene>(factory
				.convertSceneNameToPath(game.getInitialScene()), Scene.class));
		return dependencies;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, GameParameter parameter) {
	}

	@Override
	public Game loadSync(AssetManager manager, String fileName,
			FileHandle file, GameParameter parameter) {
		return game;
	}

	public static class GameParameter extends AssetLoaderParameters<Game> {

		public GameParameter(LoadedCallback loadedCallback) {
			this.loadedCallback = loadedCallback;
		}

	}
}
