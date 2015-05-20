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
package es.eucm.ead.editor.view.builders.scene.components.variablesWidgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.Operation;

public abstract class LogicExpressionWithWidget<T extends ExpressionWithVariablesWidget>
		extends ExpressionWithVariablesWidget {

	protected Skin skin;

	protected I18N i18n;

	protected Controller controller;

	private TextButton addButton;

	protected ChangeListener variableChanged;

	protected OpValue op;

	public enum OpValue {
		AND("and"), OR("or");

		private String value;

		OpValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public OpValue getOppositte() {
			if (this == AND)
				return OR;

			return AND;
		}

		public String getNeutralValue() {
			if (this == AND)
				return "true";

			return "false";
		}
	}

	private static final ClickListener addClicked = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			Actor actor = event.getListenerActor();
			LogicExpressionWithWidget table = (LogicExpressionWithWidget) actor
					.getParent();

			table.addVariableWidget();
		}
	};

	public LogicExpressionWithWidget(Controller controller, OpValue defaultValue) {
		this(controller, defaultValue, null);
	}

	public LogicExpressionWithWidget(Controller controller,
			OpValue defaultValue, Drawable background) {
		super(false);
		initialize(controller, defaultValue, variableChanged);
		this.background(background);
	}

	private void initialize(Controller controller, OpValue defaultValue,
			ChangeListener variableChanged) {

		this.controller = controller;
		this.skin = controller.getApplicationAssets().getSkin();
		this.i18n = controller.getApplicationAssets().getI18N();
		this.op = defaultValue;

		this.variableChanged = variableChanged;

		addButton = buttonThatAdd();
		addButton.addListener(addClicked);

		add(addButton);
		addVariableWidget();
	}

	protected TextButton buttonThatAdd() {
		return new TextButton(i18n.m("add_condition", i18n.m(op.getValue())),
				skin);
	}

	public abstract T variableWidget();

	public void addVariableWidget() {
		addVariableWidget(variableWidget());
	}

	public void addVariableWidget(Actor actor) {
		add(getChildren().size - 1, actor).expandX();
		add(getChildren().size - 1, getOperationLabel()).expandX();
	}

	public void addFirstVariableWidget(Actor actor) {
		add(0, getOperationLabel()).expandX();
		add(0, actor).expandX();
	}

	private Label getOperationLabel() {
		Label label = new Label(i18n.m(op.getValue()), skin,
				SkinConstants.STYLE_TOAST_ACTION);
		label.setAlignment(Align.center);

		return label;
	}

	public String getExpression() {
		String exp = "b" + op.getNeutralValue();
		for (Actor act : getChildren()) {
			if (act instanceof ExpressionWithVariablesWidget) {
				if (((T) act).getExpression() != "") {
					exp = "( " + op.getValue() + " " + exp + " "
							+ ((T) act).getExpression() + " )";
				}
			}
		}
		if (exp.equals("b" + op.getNeutralValue())) {
			return "";
		}
		return exp;
	}

	public void loadExpression(Expression expression) {
		Operation exp;
		try {
			exp = (Operation) expression;
		} catch (Exception e) {
			return;
		}

		if (exp != null) {
			if (!exp.getName().toString().equals(op.getValue())) {
				T widget = variableWidget();
				addFirstVariableWidget(widget);
				widget.loadExpression(exp);
			} else {
				loadExpression(exp.first());
				loadExpression(exp.second());
			}
		}
	}

	public abstract void invertOperation();

	public void clearWidget() {
		clearChildren();
		addButton = buttonThatAdd();
		addButton.addListener(addClicked);
		add(addButton);
	}

	public void resetWidget() {
		clearWidget();
		addVariableWidget();
	}

	public boolean isEmpty() {
		return getChildren().size < 2;
	}

	public OpValue getOp() {
		return op;
	}
}
