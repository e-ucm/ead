package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.platform.Platform.StringListener;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.game.Game;

public class NewGame extends EditorAction implements StringListener {

	public static final String NAME = "newGame";

	public NewGame() {
		super(NAME);
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			controller.action(ChooseFolder.NAME, this);
		} else {
			string(args[0].toString());
		}
	}

	@Override
	public void string(String result) {
		Assets assets = controller.getEditorAssets();
		I18N i18N = assets.getI18N();
		FileHandle projectFile = assets.resolve(result).child(
				i18N.m("project.untitled"));
		projectFile.mkdirs();

		Project project = new Project();
		Game game = new Game();
		game.setTitle(i18N.m("game.untitled"));
		game.setInitialScene("initial");
		project.getScenes().add("initial");
		Scene scene = new Scene();

		Json json = new Json();
		json.toJson(project, projectFile.child("project.json"));
		json.toJson(game, projectFile.child("game.json"));
		json.toJson(scene, projectFile.child(controller.getGameAssets()
				.convertSceneNameToPath("initial")));

		controller.action(OpenGame.NAME, result);
	}
}
