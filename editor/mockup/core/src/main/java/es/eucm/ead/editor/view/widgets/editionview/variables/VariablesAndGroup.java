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
package es.eucm.ead.editor.view.widgets.editionview.variables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;

public class VariablesAndGroup extends VariablesOperationTable {

	private static final String OPERATION = "( and";

	private static final ClickListener variablePressed = new ClickListener() {
		public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event,
				float x, float y) {
			TextButton button = (TextButton) event.getListenerActor();
			button.setChecked(true);

			VariablesTable varTable = (VariablesTable) button.getUserObject();
			varTable.setObjetive((VariableSelectorWidget) button.getParent());
			button.setChecked(true);
			varTable.show();
		}
	};

	private boolean allowOpposite;

	public VariablesAndGroup(Controller controller, boolean allowOpposite,
			VariablesTable variablesToSelect) {
		this(controller, allowOpposite, variablesToSelect, null, null);
	}

	public VariablesAndGroup(Controller controller, boolean allowOpposite,
			VariablesTable variablesToSelect, ChangeListener variableChanged) {
		this(controller, allowOpposite, variablesToSelect, variableChanged,
				null);
	}

	public VariablesAndGroup(Controller controller, boolean allowOpposite,
			VariablesTable variablesToSelect, ChangeListener variableChanged,
			String style) {
		super(controller, variablesToSelect, variableChanged, style);
		this.allowOpposite = allowOpposite;
	}

	@Override
	protected TextButton buttonThatAdd() {
		return new TextButton(i18n.m(getI18NKeyAddButton()), skin, "white");
	}

	protected String getI18NKeyAddButton() {
		return "edition.addAndCondition";
	}

	public Actor variableWidget(String name, String state) {
		VariableSelectorWidget button = new VariableSelectorWidget(controller,
				allowOpposite, name, state);
		TextButton nameVar = button.getVarNameButton();
		nameVar.setUserObject(variablesToSelect);
		nameVar.addListener(variablePressed);
		if (variableChanged != null) {
			button.addListener(variableChanged);
		}
		return button;
	}

	@Override
	public Actor variableWidget() {
		return variableWidget("", "");
	}

	@Override
	public String getExpression() {
		String expression = "btrue";
		for (Actor actor : getChildren()) {
			if (actor instanceof VariableSelectorWidget) {
				VariableSelectorWidget var = (VariableSelectorWidget) actor;
				String variable = var.getExpression();
				if (variable != null) {
					expression = OPERATION + " " + expression + " " + variable
							+ " )";
				}
			}
		}
		return expression;
	}

	public boolean isEmpty() {
		return getExpression().equals("btrue");
	}
}
