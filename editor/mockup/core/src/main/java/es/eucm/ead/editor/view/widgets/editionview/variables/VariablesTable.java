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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Toasts;
import es.eucm.ead.editor.control.actions.irreversibles.game.AddNewVariableDef;
import es.eucm.ead.editor.control.actions.irreversibles.game.AddVariables;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.PrefabComponentPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.VariableDef.Type;
import es.eucm.ead.schema.editor.components.Variables;

public class VariablesTable extends PositionedHiddenPanel implements
		TextInputListener, ModelListener<LoadEvent> {

	protected static final float IN_DURATION = .3F, OUT_DURATION = .2F;;

	private static final int MAX_CHAR = 15;

	private static final float LATERAL_PAD = 20, PAD = 10;

	private static ClickListener varPressed = new ClickListener() {
		public void clicked(InputEvent event, float x, float y) {
			TextButton variable = (TextButton) event.getListenerActor();
			VariablesTable table = (VariablesTable) variable.getUserObject();
			table.variablePressedAndSelected(variable.getText().toString());
		}
	};

	private I18N i18n;

	private Table varTable;

	private Variables variables;

	private Controller controller;

	private Skin skin;

	private VariableSelectorWidget objetive;

	private Toasts toasts;

	public VariablesTable(Skin skin, Position position,
			final PrefabComponentPanel reference, Controller controller) {
		super(skin, position, reference.getPanel());

		this.controller = controller;
		this.i18n = controller.getApplicationAssets().getI18N();
		this.skin = controller.getApplicationAssets().getSkin();

		setBackground(skin.getDrawable("panel"));

		Label title = new Label(i18n.m("general.variablesList"), skin);
		add(title).center().pad(LATERAL_PAD);
		row();

		controller.getModel().addLoadListener(this);

		varTable = new Table(skin);
		initialize();
		ScrollPane sp = new ScrollPane(varTable);

		add(sp);
	}

	private void initialize() {
		TextButton newVar = new TextButton(i18n.m("general.newVariable"), skin,
				"white");
		newVar.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showVarDialog();
			}
		});

		varTable.add(newVar).pad(LATERAL_PAD).expandX().fill().left();
		varTable.row();
	}

	private void showVarDialog() {
		Gdx.input.getPlaceholderTextInput(this,
				i18n.m("edition.insertVariable"), i18n.m("general.variable"));
	}

	@Override
	public void input(String text) {
		if (text != null && !text.isEmpty()
				&& text.equals(text.replace(" ", ""))) {
			if (variables == null) {
				variables = new Variables();
				controller.action(AddVariables.class, variables);
			}

			if (text.length() > MAX_CHAR) {
				text = text.substring(0, MAX_CHAR - 1);
			}

			if (!existVariableWithName(text)) {
				VariableDef newVariable = newVariableDef(text);
				controller.action(AddNewVariableDef.class, newVariable,
						variables);

				addVariableButton(text);
			}

			variablePressedAndSelected(text);
		} else {
			toasts = ((MockupViews) controller.getViews()).getToasts();
			toasts.showNotification(controller.getApplicationAssets().getI18N()
					.m("error.variables.invalidText"), 3f);
		}

	}

	private void variablePressedAndSelected(String text) {
		if (objetive != null) {
			objetive.changeVarName(text);
			objetive.selectedVariable(true);
			objetive.getVarNameButton().setChecked(false);
			hide();
			objetive = null;
		}
	}

	@Override
	public void canceled() {
	}

	private void addVariableButton(String name) {
		TextButton variable = new TextButton(name, skin, "white");
		variable.addListener(varPressed);
		variable.setUserObject(this);
		varTable.add(variable).pad(PAD, LATERAL_PAD, PAD, LATERAL_PAD)
				.expandX().fill().left();
		varTable.row();
		updatePositionPanel();
	}

	private VariableDef newVariableDef(String text) {
		VariableDef variable = new VariableDef();
		variable.setName(text);
		variable.setInitialValue("false");
		variable.setType(Type.BOOLEAN);

		return variable;
	}

	public void updatePanel() {
		if (variables == null) {
			updateVariables();
		}
		varTable.clearChildren();
		initialize();
		if (variables != null) {
			for (VariableDef var : variables.getVariablesDefinitions()) {
				addVariableButton(var.getName());
			}
		}
	}

	private void updateVariables() {
		variables = null;
		for (ModelComponent component : controller.getModel().getGame()
				.getComponents()) {
			if (component instanceof Variables) {
				variables = (Variables) component;
			}
		}
	}

	public void show() {
		super.show(fadeIn(IN_DURATION, Interpolation.fade));
	}

	@Override
	public void hide() {
		if (objetive != null) {
			objetive.getVarNameButton().setChecked(false);
		}
		objetive = null;
		super.hide(fadeOut(OUT_DURATION, Interpolation.fade));
	}

	public void setObjetive(VariableSelectorWidget button) {
		if (this.objetive != null) {
			this.objetive.getVarNameButton().setChecked(false);
		}
		this.objetive = button;
	}

	@Override
	public void modelChanged(LoadEvent event) {
		if (event.getType() == LoadEvent.Type.LOADED) {
			updateVariables();
		}
	}

	public boolean existVariableWithName(String name) {
		for (VariableDef var : variables.getVariablesDefinitions()) {
			if (var.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
