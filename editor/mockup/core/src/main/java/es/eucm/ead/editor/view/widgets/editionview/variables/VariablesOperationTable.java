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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public abstract class VariablesOperationTable extends LinearLayout {

	private static final float PAD = 20;

	protected Skin skin;

	protected I18N i18n;

	protected Controller controller;

	private TextButton addButton;

	protected VariablesTable variablesToSelect;

	protected ChangeListener variableChanged;

	private ClickListener addClicked;

	public VariablesOperationTable(Controller controller,
			VariablesTable variablesToSelect) {
		this(controller, variablesToSelect, null);
	}

	public VariablesOperationTable(Controller controller,
			VariablesTable variablesToSelect, ChangeListener variableChanged) {
		super(false);
		initialize(controller, variablesToSelect, variableChanged);
	}

	public VariablesOperationTable(Controller controller,
			VariablesTable variablesToSelect, ChangeListener variableChanged,
			String style) {
		super(false, style == null ? null : controller.getApplicationAssets()
				.getSkin().getDrawable(style));
		initialize(controller, variablesToSelect, variableChanged);
	}

	private void initialize(Controller controller,
			VariablesTable variablesToSelect, ChangeListener variableChanged) {

		this.controller = controller;
		this.skin = controller.getApplicationAssets().getSkin();
		this.i18n = controller.getApplicationAssets().getI18N();

		this.variablesToSelect = variablesToSelect;
		this.variableChanged = variableChanged;

		addClicked = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Actor actor = event.getListenerActor();
				VariablesOperationTable table = (VariablesOperationTable) actor
						.getParent();
				VariablesTable varTable = (VariablesTable) actor
						.getUserObject();
				varTable.hide();

				Actor newActor = table.addVariableWidget();
				Actor parent = table.getParent();
				while (parent != null) {
					if (parent instanceof PositionedHiddenPanel) {
						((PositionedHiddenPanel) parent).updatePositionPanel();
						break;
					}
					parent = parent.getParent();
				}
				addClicked(newActor);
			}
		};

		addButton = buttonThatAdd();
		addButton.setUserObject(variablesToSelect);
		addButton.addListener(addClicked);

		add(addButton).margin(PAD, PAD, PAD, PAD);
		addVariableWidget();
	}

	protected abstract void addClicked(Actor newActor);

	protected abstract TextButton buttonThatAdd();

	public abstract Actor variableWidget();

	public Actor addVariableWidget() {
		Actor actor = variableWidget();
		addVariableWidget(actor);
		return actor;
	}

	public void addVariableWidget(Actor actor) {
		add(getChildren().size - 1, actor).margin(PAD, PAD, PAD, 0).expandX();
	}

	public void addFirstVariableWidget(Actor actor) {
		add(0, actor).margin(PAD, PAD, PAD, 0).expandX();
	}

	public abstract String getExpression();

	public void reset() {
		clear();
		addButton = buttonThatAdd();
		addButton.setUserObject(variablesToSelect);
		addButton.addListener(addClicked);
		add(addButton).margin(PAD, PAD, PAD, PAD);
	}

	public void emptyWidget() {
		reset();
		addVariableWidget();
	}

	public abstract boolean isEmpty();
}