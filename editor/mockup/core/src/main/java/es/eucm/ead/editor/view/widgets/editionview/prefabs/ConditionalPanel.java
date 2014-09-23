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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.irreversibles.scene.RemoveComponents;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.editionview.variables.VariablesAndGroup;
import es.eucm.ead.editor.view.widgets.editionview.variables.VariablesOrGroup;
import es.eucm.ead.editor.view.widgets.editionview.variables.VariablesTable;
import es.eucm.ead.schema.components.ModelConditionedComponent;

public abstract class ConditionalPanel extends PrefabComponentPanel {

	private VariablesTable variablesPanel;

	private VariablesOrGroup varOp;

	public ConditionalPanel(String icon, String namei18n,
			final String componentId, final Controller controller,
			Class<? extends ModelConditionedComponent> myClass) {
		super(icon, namei18n, componentId, controller);

		variablesPanel = new VariablesTable(skin, Position.RIGHT, this,
				controller);

		Array<String> states = new Array<String>();
		states.add(i18n.m("edition.true"));
		states.add(i18n.m("edition.false"));
		Array<Color> colors = new Array<Color>();
		colors.add(Color.GREEN);
		colors.add(Color.RED);

		ChangeListener varChanged = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				updateComponent();
				panel.updatePositionPanel();

			}
		};
		varOp = new VariablesOrGroup(controller, variablesPanel, varChanged);
		ScrollPane sp = new ScrollPane(varOp);
		panel.add(sp).expand().fill();
	}

	@Override
	protected void trashClicked() {
		super.trashClicked();
		panel.updatePositionPanel();
	}

	@Override
	public void showPanel() {
		super.showPanel();
		variablesPanel.updatePanel();
	}

	@Override
	public void hidePanel() {
		super.hidePanel();
		variablesPanel.hide();
	}

	@Override
	protected void actualizePanel() {
		if (component != null) {
			varOp.reset();
			evaluateExpression(((ModelConditionedComponent) component)
					.getCondition());
		} else {
			varOp.emptyWidget();
		}
	}

	protected abstract void updateTheComponent();

	protected void updateComponent() {
		if (!varOp.isEmpty()) {
			updateTheComponent();
		} else if (component != null) {
			controller.action(RemoveComponents.class, componentId);
			setUsed(false);
		}
	}

	protected void evaluateExpression(String expression) {
		String[] fields = expression.split(" ");

		VariablesAndGroup andGroup = (VariablesAndGroup) varOp.variableWidget();

		evaluateExpression(andGroup, fields, 0);
	}

	protected int evaluateExpression(VariablesAndGroup andGroup,
			String[] fields, int init) {
		int i = init;

		while (i >= 0 && i < fields.length) {
			String aux = fields[i];
			if (aux.equals("eq")) {
				String arg2 = fields[i + 2];
				andGroup.addVariableWidget(evaluateEq(andGroup, fields[i + 1],
						arg2));
				String aux2 = fields[i];
				while (!aux2.equals(")")) {
					i++;
					aux2 = fields[i];
				}
				return i;
			} else if (aux.equals("and")) {
				i = evaluateExpression(andGroup, fields, i + 1);
				i = evaluateExpression(andGroup, fields, i + 1);
			} else if (aux.equals("or")) {
				i = evaluateExpression(andGroup, fields, i + 1);
				andGroup = (VariablesAndGroup) varOp.variableWidget();
				andGroup.reset();
				varOp.addFirstVariableWidget(andGroup);
				i = evaluateExpression(andGroup, fields, i + 1);
			} else if (aux.equals(")")) {
				return i;
			} else if (aux.equals("btrue") || aux.equals("bfalse")) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private Actor evaluateEq(VariablesAndGroup andGroup, String arg0,
			String arg1) {
		if (arg0.contains("$") && !arg0.contains(" ")) {
			return andGroup.variableWidget(arg0.replace("$", ""), arg1);
		} else {
			return andGroup.variableWidget(arg1.replace("$", ""), arg0);
		}
	}

	protected String generateCondition() {
		return varOp.getExpression();
	}

}
