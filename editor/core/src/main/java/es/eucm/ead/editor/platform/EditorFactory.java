package es.eucm.ead.editor.platform;

import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.platform.loaders.ProjectLoader;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorSceneElement;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.schema.actors.SceneElement;

public class EditorFactory extends Factory {

	public EditorFactory(Assets assets) {
		super(assets);
		setExtraBindings();
	}

	private void setExtraBindings() {
		bind("sceneelement", SceneElement.class, EditorSceneElement.class);
	}

	@Override
	protected void setLoaders(Assets assets) {
		super.setLoaders(assets);
		assets.setLoader(Project.class, new ProjectLoader(assets, this));
	}
}
