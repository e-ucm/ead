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
package es.eucm.ead.editor.view.builders.graph.effects;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.commander.Commander;
import es.eucm.commander.actions.SetField;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.effects.ChangeVar;

public class ChangeVarModal extends EffectModal<ChangeVar> implements
		TextInputListener {

	private HorizontalGroup statement;

	private SelectBox<String> selectVariable;

	private SelectBox<String> booleanOperations;

	private Container<Actor> editor;

	private Label noVariables;

	public ChangeVarModal(ChangeVarNodeBuilder nodeBuilder,
			Commander commander, Skin skin, I18N i18N) {
		super(nodeBuilder, commander, skin, i18N);
	}

	@Override
	protected void updateEditor(ChangeVar effect) {
		if (((ChangeVarNodeBuilder) nodeBuilder).getVariables().size == 0) {
			editor.setActor(noVariables);
		} else {
			if (effect.getVariable() == null) {
				commander.perform(SetField.class, effect, "variable",
						((ChangeVarNodeBuilder) nodeBuilder).getVariables()
								.get(0).getName());
			}

			editor.setActor(statement);
			selectVariable.setSelected(effect.getVariable());
			if ("btrue".equals(effect.getExpression())) {
				booleanOperations.setSelectedIndex(0);
			} else if ("bfalse".equals(effect.getExpression())) {
				booleanOperations.setSelectedIndex(1);
			}
		}
	}

	@Override
	protected Actor buildEditor(Skin skin, final I18N i18N) {
		editor = new Container<Actor>();

		statement = new HorizontalGroup();
		statement.space(WidgetBuilder.dpToPixels(16));
		statement.addActor(new Label(i18N.m("set"), skin));
		selectVariable = new SelectBox<String>(skin);
		selectVariable.getSelection().setProgrammaticChangeEvents(false);
		selectVariable.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				commander.perform(SetField.class, effect, "variable",
						selectVariable.getSelected());
			}
		});
		statement.addActor(selectVariable);

		booleanOperations = new SelectBox<String>(skin);
		booleanOperations.getSelection().setProgrammaticChangeEvents(false);
		booleanOperations.setItems(i18N.m("true"), i18N.m("false"));
		booleanOperations.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				commander.perform(SetField.class, effect, "expression",
						booleanOperations.getSelectedIndex() == 0 ? "btrue"
								: "bfalse");
			}
		});
		statement.addActor(new Label(i18N.m("to"), skin));
		statement.addActor(booleanOperations);

		noVariables = new Label(i18N.m("no.variables"), skin);

		TextButton newVariable = new TextButton(i18N.m("new.variable"), skin,
				SkinConstants.STYLE_DIALOG);
		newVariable.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((ChangeVarNodeBuilder) nodeBuilder)
						.newVariable(ChangeVarModal.this);
			}
		});
		buttons.add(0, newVariable);
		readVariables();

		return editor;
	}

	private void readVariables() {
		Array<String> vars = new Array<String>();
		for (VariableDef variableDef : ((ChangeVarNodeBuilder) nodeBuilder)
				.getVariables()) {
			vars.add(variableDef.getName());
		}
		selectVariable.setItems(vars);

		editor.setActor(vars.size == 0 ? noVariables : statement);
	}

	@Override
	public void input(String text) {
		readVariables();
		selectVariable.setSelected(text);
		commander.perform(SetField.class, effect, "variable", text);
	}

	@Override
	public void canceled() {

	}
}
