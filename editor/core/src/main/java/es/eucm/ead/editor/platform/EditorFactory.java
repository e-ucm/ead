package es.eucm.ead.editor.platform;

import com.badlogic.gdx.Files;

import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.platform.loaders.ProjectLoader;
import es.eucm.ead.editor.view.widgets.engine.wrappers.EditorSceneElement;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.schema.actors.SceneElement;

public class EditorFactory extends Assets {

	public EditorFactory(Files files) {
		super(files);
		setExtraBindings();
	}

	private void setExtraBindings() {
		bind("sceneelement", SceneElement.class, EditorSceneElement.class);
	}

	@Override
	protected void setLoaders() {
		super.setLoaders();
		setLoader(Project.class, new ProjectLoader(this));
	}
}
