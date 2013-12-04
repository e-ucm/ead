package es.eucm.ead.editor.control.commands;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.core.EAdEngine;
import es.eucm.ead.editor.control.Command;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.model.ModelEvent;
import es.eucm.ead.schema.game.Game;

public class NewProjectCommand extends Command {

	private Game game;

	private FileHandle currentPath;

	public NewProjectCommand(Game game, FileHandle currentPath) {
		this.game = game;
		this.currentPath = currentPath;
	}

	@Override
	public ModelEvent performCommand(EditorModel em) {
		EAdEngine.jsonIO.toJson(game, currentPath.child("game.json"));
		currentPath.child("scenes").mkdirs();
		EAdEngine.engine.setLoadingPath(currentPath.file().getAbsolutePath());
		return null;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public ModelEvent undoCommand(EditorModel em) {
		return null;
	}

	@Override
	public boolean canRedo() {
		return false;
	}

	@Override
	public ModelEvent redoCommand(EditorModel em) {
		return null;
	}

	@Override
	public boolean combine(Command other) {
		return false; //To change body of implemented methods use File | Settings | File Templates.
	}
}
