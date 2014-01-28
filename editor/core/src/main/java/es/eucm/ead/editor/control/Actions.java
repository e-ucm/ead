package es.eucm.ead.editor.control;

import es.eucm.ead.editor.control.actions.ChooseFolder;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.control.actions.ShowView;

import java.util.HashMap;
import java.util.Map;

public class Actions {

	private Map<String, EditorAction> actionsMap;

	private Controller controller;

	public Actions(Controller controller) {
		actionsMap = new HashMap<String, EditorAction>();
		this.controller = controller;
		addActions();
	}

	private void addActions() {
		addAction(new ChooseFolder());
		addAction(new OpenGame());
		addAction(new ShowView());
	}

	private void addAction(EditorAction action) {
		action.setController(controller);
		actionsMap.put(action.getName(), action);
	}

	public void perform(String actionName, Object... args) {
		EditorAction action = actionsMap.get(actionName);
		if (action != null && action.isEnabled()) {
			action.perform(args);
		}
	}
}
