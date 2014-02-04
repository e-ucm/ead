package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.SceneEvent;
import es.eucm.ead.schema.actors.Scene;

public class AddSceneCommand extends Command {

	private String sceneName;

	@Override
	public ModelEvent doCommand(Model model) {
		SceneEvent sceneEvent = model.addScene("scene", new Scene());
		sceneName = sceneEvent.getName();
		return sceneEvent;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand(Model model) {
		return model.removeScene(sceneName);
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}
}
