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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Controller;

public class VariablesOrGroup extends VariablesOperationTable {

	private static final String OPERATION = "( or";

	public VariablesOrGroup(Controller controller,
			VariablesTable variablesToSelect) {
		this(controller, variablesToSelect, null);
	}

	public VariablesOrGroup(Controller controller,
			VariablesTable variablesToSelect, ChangeListener variableChanged) {
		super(controller, variablesToSelect, variableChanged);
	}

	@Override
	protected TextButton buttonThatAdd() {
		TextButton add = new TextButton(i18n.m("edition.addOrCondition"), skin,
				"to_color");
		add.setColor(Color.GREEN);
		return add;
	}

	@Override
	public Actor variableWidget() {
		return new VariablesAndGroup(controller, false, variablesToSelect,
				variableChanged, "panel");
	}

	@Override
	public String getExpression() {
		String expression = "bfalse";
		for (Actor actor : getChildren()) {
			if (actor instanceof VariablesAndGroup) {
				VariablesAndGroup var = (VariablesAndGroup) actor;
				String variable = var.getExpression();
				if (variable != null) {
					expression = OPERATION + " " + expression + " " + variable
							+ " )";
				}
			}
		}
		return expression;
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public boolean isEmpty() {
		boolean empty = true;
		for (Actor actor : getChildren()) {
			if (actor instanceof VariablesAndGroup) {
				VariablesAndGroup var = (VariablesAndGroup) actor;
				if (!var.isEmpty()) {
					empty = false;
					break;
				}
			}
		}

		return empty;
	}

	@Override
	protected void addClicked(Actor newActor) {

	}
}
