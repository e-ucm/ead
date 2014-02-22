package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;

public class SceneGallery implements ViewBuilder {

	public static final String NAME = "mockup_scene";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		Skin skin = controller.getEditorAssets().getSkin();
		Table window = new Table(skin).debug();
		window.setFillParent(true);
		window.add(NAME);
		return window;
	}

	@Override
	public void initialize(Controller controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release(Controller controller) {
		// TODO Auto-generated method stub

	}

}
