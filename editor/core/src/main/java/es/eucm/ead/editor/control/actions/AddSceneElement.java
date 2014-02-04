package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.graphics.Texture;

import es.eucm.ead.editor.control.commands.AddSceneElementCommand;
import es.eucm.ead.editor.platform.Platform.StringListener;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.Image;

public class AddSceneElement extends EditorAction implements StringListener {

	public static final String NAME = "addSceneElement";

	public AddSceneElement() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		controller.action(ChooseFile.NAME, this);
	}

	@Override
	public void string(String result) {
		SceneElement sceneElement = new SceneElement();
		Image renderer = new Image();
		renderer.setUri(result);
		controller.getGameAssets().load(result, Texture.class);
		controller.getGameAssets().finishLoading();
		sceneElement.setRenderer(renderer);
		controller.getCommands().command(
				new AddSceneElementCommand(sceneElement));

	}
}
