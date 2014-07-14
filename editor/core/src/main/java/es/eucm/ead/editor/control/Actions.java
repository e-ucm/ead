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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.actions.Action;
import es.eucm.ead.editor.control.actions.ArgumentsValidationException;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.EditorActions;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.ModelActions;
import es.eucm.ead.editor.control.appdata.TimestampedEditorAction;
import es.eucm.ead.editor.view.listeners.ActionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Takes care of the actions execution
 */
public class Actions {

	private Controller controller;

	private ModelActions modelActions;

	private EditorActions editorActions;

	private Map<Class, Action> actionsMap;

	/**
	 * The actions log. Used for bug reporting and analytics
	 */
	private Array<TimestampedEditorAction> editorActionsLog;

	public Actions(Controller controller) {
		actionsMap = new HashMap<Class, Action>();
		modelActions = new ModelActions(controller.getCommands());
		editorActions = new EditorActions();
		this.controller = controller;
		editorActionsLog = new Array<TimestampedEditorAction>();
	}

	/**
	 * @return the action associated to the given class
	 */
	public <T extends Action> T getAction(Class<T> actionClass) {
		T action = (T) actionsMap.get(actionClass);
		if (action == null) {
			try {
				// create the action using reflection
				action = ClassReflection.newInstance(actionClass);
				action.initialize(controller);
				actionsMap.put(actionClass, action);
			} catch (ReflectionException e) {
				Gdx.app.error("Actions",
						"Impossible to create action of class " + actionClass,
						e);
			}
		}
		return action;
	}

	/**
	 * Performs the action, identified by its class, with the given arguments
	 */
	public void perform(Class actionClass, Object... args)
			throws ArgumentsValidationException {
		Action action = getAction(actionClass);
		if (action != null && action.isEnabled()) {
			if (action.validate(args)) {
				Gdx.app.debug("Controller",
						ClassReflection.getSimpleName(actionClass)
								+ prettyPrintArgs(args));
				logAction(action.getClass(), args);
				if (action instanceof ModelAction) {
					modelActions.perform((ModelAction) action, args);
				} else if (action instanceof EditorAction) {
					editorActions.perform((EditorAction) action, args);
				}
			} else {
				throw new ArgumentsValidationException(actionClass, args);
			}
		} else {
			Gdx.app.debug("Actions", "Action with class " + actionClass
					+ (action == null ? " does not exist." : " is disabled."));
		}
	}

	/**
	 * Just formats an array of objects for console printing. For debugging only
	 */
	private String prettyPrintArgs(Object... args) {
		if (args == null) {
			return "[]";
		}
		String str = "[";
		for (Object arg : args) {
			str += (arg instanceof String ? "\"" : "")
					+ (arg == null ? "null" : arg.toString())
					+ (arg instanceof String ? "\"" : "") + " , ";
		}
		if (args.length > 0) {
			str = str.substring(0, str.length() - 3);
		}
		str += "]";
		return str;
	}

	/**
	 * Adds a listener to an action. The listener will be notified when the
	 * state of the action changes
	 */
	public void addActionListener(Class actionClass, ActionListener listener) {
		Action action = getAction(actionClass);
		if (action != null) {
			action.addActionListener(listener);
		}
	}

	/**
	 * @return if the given action is enabled
	 */
	public boolean isEnabled(Class actionClass) {
		Action process = getAction(actionClass);
		return process != null && process.isEnabled();
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
		Array<Object> arguments = new Array<Object>();
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
	public Array<TimestampedEditorAction> getLoggedActions(int nActions) {
		if (nActions < 0)
			return null;

		Array<TimestampedEditorAction> recentActions = new Array<TimestampedEditorAction>();
		int actionsToReturn = Math.min(nActions, editorActionsLog.size);
		for (int i = editorActionsLog.size - actionsToReturn; i < editorActionsLog.size; i++) {
			recentActions.add(editorActionsLog.get(i));
		}
		return recentActions;
	}

}
