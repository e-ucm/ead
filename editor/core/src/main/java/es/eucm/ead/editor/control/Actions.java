/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.control.actions.AddSceneElement;
import es.eucm.ead.editor.control.actions.ChangeLanguage;
import es.eucm.ead.editor.control.actions.ChangePreference;
import es.eucm.ead.editor.control.actions.ChangeSkin;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.control.actions.ChooseFile;
import es.eucm.ead.editor.control.actions.ChooseFolder;
import es.eucm.ead.editor.control.actions.CombinedAction;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.Move;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.control.actions.Redo;
import es.eucm.ead.editor.control.actions.Save;
import es.eucm.ead.editor.control.actions.ShowView;
import es.eucm.ead.editor.control.actions.Undo;

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
		addAction(new NewGame());
		addAction(new ChooseFolder());
		addAction(new OpenGame());
		addAction(new ShowView());
		addAction(new ChangeLanguage());
		addAction(new Move());
		addAction(new Undo());
		addAction(new Redo());
		addAction(new Save());
		addAction(new EditScene());
		addAction(new AddScene());
		addAction(new AddSceneElement());
		addAction(new ChooseFile());
		addAction(new ChangePreference());
		addAction(new ChangeView());
		addAction(new ChangeSkin());
		addAction(new CombinedAction());
	}

	private void addAction(EditorAction action) {
		action.setController(controller);
		actionsMap.put(action.getName(), action);
	}

	public void perform(String actionName, Object... args) {
		EditorAction action = actionsMap.get(actionName);
		if (action != null && action.isEnabled()) {
			action.perform(args);
		} else {
			Gdx.app.error("Actions", "Action with name " + actionName
					+ (action == null ? " does not exist." : "is disabled"));
		}
	}
}
