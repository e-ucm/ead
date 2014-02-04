package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.model.events.SceneElementEvent;
import es.eucm.ead.editor.model.events.SceneElementEvent.Type;
import es.eucm.ead.schema.actors.SceneElement;

public class AddSceneElementCommand extends Command {

	private SceneElement sceneElement;

	public AddSceneElementCommand(SceneElement sceneElement) {
		this.sceneElement = sceneElement;
	}

	@Override
	public ModelEvent doCommand(Model model) {
		model.getCurrentScene().getChildren().add(sceneElement);
		return new SceneElementEvent(Type.ADDED, sceneElement);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand(Model model) {
		model.getCurrentScene().getChildren().remove(sceneElement);
		return new SceneElementEvent(Type.REMOVED, sceneElement);
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}
}
