package es.eucm.ead.editor.platform.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.platform.loaders.ProjectLoader.ProjectParameter;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.schema.game.Game;

public class ProjectLoader extends
		AsynchronousAssetLoader<Project, ProjectParameter> {

	private Factory factory;

	private Project project;

	public ProjectLoader(FileHandleResolver resolver, Factory factory) {
		super(resolver);
		this.factory = factory;
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, ProjectParameter parameter) {
		project = factory.fromJson(Project.class, file);
		Array<AssetDescriptor> dependencies = factory.popDependencies();
		dependencies.add(new AssetDescriptor<Game>("game.json", Game.class));
		return dependencies;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, ProjectParameter parameter) {
	}

	@Override
	public Project loadSync(AssetManager manager, String fileName,
			FileHandle file, ProjectParameter parameter) {
		return project;
	}

	public static class ProjectParameter extends AssetLoaderParameters<Project> {

		public ProjectParameter(LoadedCallback loadedCallback) {
			this.loadedCallback = loadedCallback;
		}

	}
}
