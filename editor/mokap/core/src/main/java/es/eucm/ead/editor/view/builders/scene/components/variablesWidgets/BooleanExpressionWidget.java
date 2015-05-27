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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.CreateVariable;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.view.widgets.MultiStateButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.i18n.I18N;

public class BooleanExpressionWidget extends ExpressionWithVariablesWidget
		implements Input.TextInputListener, Model.ModelListener<ListEvent> {

	private static final float PAD = WidgetBuilder.dpToPixels(5);

	private static final String OP = "eq";

	private static final String TRUE = "true", FALSE = "false";

	private Controller controller;

	private Skin skin;

	private Variables variables;

	private SelectBox<String> variablesBox;

	private Array<VariableDef> variablesList;

	private MultiStateButton value;

	private I18N i18N;

	public BooleanExpressionWidget(Controller cont) {
		super(true);
		this.controller = cont;
		this.i18N = controller.getApplicationAssets().getI18N();
		this.skin = controller.getApplicationAssets().getSkin();

		ModelEntity game = controller.getModel().getGame();
		variables = Q.getComponent(game, Variables.class);
		variablesList = variables.getVariablesDefinitions();

		variablesBox = new SelectBox<String>(skin);
		variablesBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (variablesBox.getSelected().equals(
						i18N.m("new.variable") + "...")) {
					controller.action(CreateVariable.class,
							BooleanExpressionWidget.this);
				}
			}
		});
		loadItems();

		Array states = new Array();
		states.addAll(i18N.m(TRUE), i18N.m(FALSE));
		Array colors = new Array();
		colors.addAll(Color.GREEN, Color.RED);
		value = new MultiStateButton(skin, states, colors, PAD * 2);

		add(variablesBox).expandX().marginRight(PAD);
		add(value).marginRight(PAD);
		value.setVisible(false);

		controller.getModel().addListListener(variablesList, this);

		setComputeInvisibles(true);
		variablesBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (variablesBox.getSelected().toString().equals(" ")) {
					value.setVisible(false);
				} else {
					value.setVisible(true);
				}
			}
		});
	}

	@Override
	public String getExpression() {
		if (variablesBox.getSelected().equals(" ")) {
			return "";
		}

		return "( " + OP + " $" + variablesBox.getSelected() + " b"
				+ getBooleanValue() + " )";
	}

	private String getBooleanValue() {
		if (value.getText().toString().equals(i18N.m(TRUE))) {
			return TRUE;
		}

		return FALSE;
	}

	@Override
	public void loadExpression(Expression expression) {
		Operation operation;
		try {
			operation = (Operation) expression;
		} catch (Exception e) {
			return;
		}

		if (operation.getName().equals(OP)
				&& operation.first().toString() != "") {
			variablesBox.setSelected(operation.first().toString()
					.replace("$", ""));
			value.selectText(i18N.m(operation.second().toString()
					.replace("b", "")));

			return;
		}

		variablesBox.setSelected(" ");
		value.selectText(i18N.m("true"));
	}

	public void loadItems() {
		Array<String> items = new Array<String>();
		items.add(" ");
		for (VariableDef v : variablesList) {
			items.add(v.getName());
		}
		items.add(i18N.m("new.variable") + "...");

		variablesBox.setItems(items);
	}

	@Override
	public void input(String text) {
		variablesBox.setSelected(text);
	}

	@Override
	public void canceled() {
		variablesBox.setSelected(" ");
	}

	@Override
	public void modelChanged(ListEvent event) {
		loadItems();
	}
}
