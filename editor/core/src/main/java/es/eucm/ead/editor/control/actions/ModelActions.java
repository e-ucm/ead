package es.eucm.ead.editor.control.actions;

import es.eucm.ead.editor.control.Commands;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.appdata.TimestampedEditorAction;
import es.eucm.ead.editor.control.commands.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for model actions
 */
public class ModelActions {

	private Commands commands;

	/**
	 * The actions log. Used for bug reporting and analytics
	 */
	private List<TimestampedEditorAction> editorActionsLog;

	public ModelActions(Commands commands) {
		this.commands = commands;
		editorActionsLog = new ArrayList<TimestampedEditorAction>();
	}

	public void perform(ModelAction action, Object... args) {
		logAction(action.getClass(), args);
		Command command = action.perform(args);
		commands.command(command);
	}

	/**
	 * Saves a
	 * {@link es.eucm.ead.editor.control.appdata.TimestampedEditorAction} just
	 * before each action is performed.
	 * 
	 * @param actionClass
	 *            The class of the action
	 * @param args
	 *            The arguments the action received
	 */
	private void logAction(Class actionClass, Object... args) {
		TimestampedEditorAction serializedEditorAction = new TimestampedEditorAction();
		serializedEditorAction.setTimestamp(System.currentTimeMillis() + "");
		serializedEditorAction.setActionClass(actionClass.getCanonicalName());
		List<Object> arguments = new ArrayList<Object>();
		for (Object arg : args) {
			arguments.add(arg);
		}
		serializedEditorAction.setArguments(arguments);
		editorActionsLog.add(serializedEditorAction);
	}

	/**
	 * Returns the last {@code nActions} actions logged so far. Can be used for
	 * bug reporting, saving macros...
	 * 
	 * NOTE: Logged actions are not removed from the internal storage structure
	 * held by {@link es.eucm.ead.editor.control.Actions}, since the actual list
	 * is never directly returned.
	 * 
	 * @param nActions
	 *            The number of logged actions to be returned. If greater than
	 *            number of actions logged available, the full list is returned.
	 *            Passing {@link Integer#MAX_VALUE} also returns the full list.
	 *            If {@code nActions} is less than zero, null is returned.
	 */
	public List<TimestampedEditorAction> getLoggedActions(int nActions) {
		if (nActions < 0)
			return null;

		List<TimestampedEditorAction> recentActions = new ArrayList<TimestampedEditorAction>();
		int actionsToReturn = Math.min(nActions, editorActionsLog.size());
		for (int i = editorActionsLog.size() - actionsToReturn; i < editorActionsLog
				.size(); i++) {
			recentActions.add(editorActionsLog.get(i));
		}
		return recentActions;
	}

}
