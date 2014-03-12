package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;

import java.util.List;

/**
 * Removes from a scene (args[0]) the given scene element (args[1))
 */
public class RemoveFromScene extends EditorAction {

	@Override
	public void perform(Object... args) {
		Scene scene = (Scene) args[0];
		SceneElement element = (SceneElement) args[1];
		List list = scene.getChildren();
		controller.command(new RemoveFromListCommand(list, element));
	}
}
