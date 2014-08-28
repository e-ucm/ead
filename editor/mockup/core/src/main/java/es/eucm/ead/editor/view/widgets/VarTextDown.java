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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.AddNewVariableDef;
import es.eucm.ead.editor.control.actions.AddVariables;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.VariableDef.Type;
import es.eucm.ead.schema.editor.components.Variables;

public class VarTextDown extends SelectBox<Label> {

	private Controller controller;

	private Variables variables;

	private Array items;

	private I18N i18n;

	public VarTextDown(Skin skin, final Controller controller) {
		super(skin);

		this.controller = controller;
		this.i18n = controller.getApplicationAssets().getI18N();

		items = new Array();
		setItems(items);

		reloadPanel();

		addListener(new ChangeListener() {

			private int lastSelectedIndex = 0;

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (getSelectedIndex() == 0) {
					Gdx.input.getTextInput(new TextInputListener() {
						@Override
						public void input(String text) {
							if (variables == null) {
								variables = new Variables();
								controller
										.action(AddVariables.class, variables);
							}

							VariableDef newVariable = newVariableDef(text);
							controller.action(AddNewVariableDef.class,
									newVariable, variables);

							items.add(text);
							setItems(items);

							setSelectedIndex(items.size - 1);
							lastSelectedIndex = items.size - 1;
						}

						@Override
						public void canceled() {
							setSelectedIndex(lastSelectedIndex);
						}
					}, i18n.m("edition.insertVariable "), i18n
							.m("general.variable"));
				} else {
					lastSelectedIndex = getSelectedIndex();
					doAction();
				}

			}
		});

	}

	protected void doAction() {
	}

	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
		doAction();
	}

	private void reloadPanel() {
		this.reloadPanel(null);
	}

	public void reloadPanel(String selectedVariable) {
		items.clear();
		int count = 1;
		int index = -1;

		variables = null;
		items.add(i18n.m("general.newVariable"));
		for (ModelComponent component : controller.getModel().getGame()
				.getComponents()) {
			if (component instanceof Variables) {
				variables = (Variables) component;

				for (VariableDef var : variables.getVariablesDefinitions()) {
					items.add(var.getName());
					if (selectedVariable != null
							&& selectedVariable.equals(var.getName())) {
						index = count;
					}
					count++;
				}
			}
		}

		if (index == -1) {
			index = 0;
		}

		setItems(items);
		setSelectedIndex(index);
	}

	private VariableDef newVariableDef(String text) {
		VariableDef variable = new VariableDef();
		variable.setName(text);
		variable.setInitialValue("bfalse");
		variable.setType(Type.BOOLEAN);

		return variable;
	}

	public VariableDef getSelectedVariableDef() {
		if (getSelectedIndex() > 0 && variables != null) {
			return variables.getVariablesDefinitions().get(
					getSelectedIndex() - 1);
		} else {
			return null;
		}
	}
}
