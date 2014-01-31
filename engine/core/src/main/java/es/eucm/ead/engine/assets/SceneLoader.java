package es.eucm.ead.engine.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.assets.SceneLoader.SceneParameter;
import es.eucm.ead.schema.actors.Scene;

public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneParameter> {

	private Factory factory;

	private Scene scene;

	public SceneLoader(FileHandleResolver resolver, Factory factory) {
		super(resolver);
		this.factory = factory;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, SceneParameter parameter) {
		scene = factory.fromJson(Scene.class, file);
		return factory.popDependencies();
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, SceneParameter parameter) {
	}

	@Override
	public Scene loadSync(AssetManager manager, String fileName,
			FileHandle file, SceneParameter parameter) {
		return scene;
	}

	public static class SceneParameter extends AssetLoaderParameters<Scene> {

		public SceneParameter(LoadedCallback loadedCallback) {
			this.loadedCallback = loadedCallback;
		}

	}
}