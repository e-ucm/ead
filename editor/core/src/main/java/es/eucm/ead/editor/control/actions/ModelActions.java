/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
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
