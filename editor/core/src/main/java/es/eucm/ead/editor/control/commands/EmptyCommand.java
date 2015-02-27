package es.eucm.ead.editor.control.commands;

import es.eucm.ead.editor.model.events.ModelEvent;

/**
 * Command returned for those actions that must return a command but produces no
 * action over the model.
 */
public class EmptyCommand extends Command {

	private boolean modifiesResource;

	public EmptyCommand() {
		this(false);
	}

	public EmptyCommand(boolean modifiesResource) {
		this.modifiesResource = modifiesResource;
	}

	public void setModifiesResource(boolean modifiesResource) {
		this.modifiesResource = modifiesResource;
	}

	@Override
	public ModelEvent doCommand() {
		return null;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		return null;
	}

	@Override
	public boolean modifiesResource() {
		return modifiesResource;
	}
}
