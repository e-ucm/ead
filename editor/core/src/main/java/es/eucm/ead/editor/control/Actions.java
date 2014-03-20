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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.actions.*;
import es.eucm.ead.editor.control.actions.EditorAction.EditorActionListener;
import es.eucm.ead.editor.control.appdata.TimestampedEditorAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Takes care of the editor actions execution
 */
public class Actions {

	private Map<Class, EditorAction> actionsMap;

	private Controller controller;

	/**
	 * The actions log. Used for bug reporting and analytics
	 */
	private List<TimestampedEditorAction> editorActionsLog;

	public Actions(Controller controller) {
		actionsMap = new HashMap<Class, EditorAction>();
		this.controller = controller;
		editorActionsLog = new ArrayList<TimestampedEditorAction>();
	}

	/**
	 * Adds an action listener for the action class. The listener will be
	 * notified when the state of the action changes (e.g., passes from being
	 * enabled to not being enable)
	 * 
	 * @param actionClass
	 *            the action class
	 * @param listener
	 *            the listener
	 */
	public void addActionListener(Class actionClass,
			EditorActionListener listener) {
		EditorAction action = getAction(actionClass);
		if (action != null) {
			action.addListener(listener);
		} else {
			Gdx.app.error("Actions", "Action with name does not exist.");
		}
	}

	public EditorAction getAction(Class actionClass) {
		EditorAction action = actionsMap.get(actionClass);
		if (action == null) {
			try {
				action = (EditorAction) ClassReflection
						.newInstance(actionClass);
				action.setController(controller);
				actionsMap.put(actionClass, action);
			} catch (ReflectionException e) {
				Gdx.app.error("Actions",
						"Impossible to create editor action of class "
								+ actionClass, e);
			}
		}
		return action;
	}

	/**
	 * Performs the action identified by its Class with the given arguments
	 * 
	 * @param actionClass
	 *            the action class
	 * @param args
	 *            the actions arguments
	 */
	public void perform(Class actionClass, Object... args) {
		EditorAction action = getAction(actionClass);
		if (action != null && action.isEnabled()) {
			logAction(actionClass, args);
			action.perform(args);
		} else {
			Gdx.app.error("Actions", "Action with class " + actionClass
					+ (action == null ? " does not exist." : " is disabled"));
		}
	}

	/**
	 * Saves a
	 * {@link es.eucm.ead.editor.control.appdata.TimestampedEditorAction} just
	 * before each action is performed.
	 * 
	 * @param actionClass
	 *            The class of the action (e.g.
	 *            es.eucm.ead.editor.core.contro.AddSccene)
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
	 * Returns the list of actions logged so far. It is intended for, among other potential uses,
     * bug reporting. It could also be used for saving macros, for example.
     *
     * This method admits an integer argument, {@code nActions}, which determines the number of
     * logged actions to be returned. If {@code nActions} is greater than number of actions logged
     * available, the full list is returned (entities interested in getting the full list can just
     * invoke Actions#getLoggedActions(Integer.MAX_VALUE)}. If {@code nActions} is smaller than the
     * total number of actions but greater or equals to zero, the last {@code nActions} logged are
     * returned. If {@code nActions} is less than zero, null is returned.
     *
     * NOTE: Logged actions are not removed from the internal storage structure held by
     * {@link es.eucm.ead.editor.control.Actions}, since the actual list is never directly returned.
     *
     * @param nActions  The number of logged actions to be returned
	 */
	public List<TimestampedEditorAction> getLoggedActions(int nActions) {
        if ( nActions < 0 ) return null;

        List<TimestampedEditorAction> recentActions = new ArrayList<TimestampedEditorAction>();
        int actionsToReturn =Math.min(nActions, editorActionsLog.size());
        for (int i=editorActionsLog.size()-actionsToReturn; i<editorActionsLog.size(); i++){
            recentActions.add(editorActionsLog.get(i));
        }
		return recentActions;
	}
}
