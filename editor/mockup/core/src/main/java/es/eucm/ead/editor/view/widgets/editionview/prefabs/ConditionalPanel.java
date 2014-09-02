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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.irreversibles.scene.RemoveConditionedComponent;
import es.eucm.ead.editor.view.widgets.MultiStateButton;
import es.eucm.ead.editor.view.widgets.VarTextDown;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.ModelConditionedComponent;
import es.eucm.ead.schema.entities.ModelEntity;

public abstract class ConditionalPanel extends PrefabPanel {

	private static final float PAD = 20, BUTTON_MARGIN = 75;

	protected VarTextDown varTextDown;

	private MultiStateButton stateButton;

	protected ModelConditionedComponent conditionedComponent;

	private Class<? extends ModelConditionedComponent> myClass;

	public ConditionalPanel(String icon, String namei18n, float size,
			Controller controller, Actor touchable,
			Class<? extends ModelConditionedComponent> myClass) {
		super(icon, size, namei18n, controller, touchable);

		this.myClass = myClass;

		varTextDown = new VarTextDown(skin, controller) {
			@Override
			protected void doAction() {
				actualizeCondition();
			}
		};

		Array<String> states = new Array<String>();
		states.add(i18n.m("edition.true"));
		states.add(i18n.m("edition.false"));
		Array<Color> colors = new Array<Color>();
		colors.add(Color.GREEN);
		colors.add(Color.RED);

		stateButton = new MultiStateButton(skin, states, colors, BUTTON_MARGIN);
		stateButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				actualizeCondition();
			}
		});

		LinearLayout top = new LinearLayout(true);
		top.add(new Label(i18n.m("edition.ifVariable"), skin)).margin(0, 0,
				PAD, 0);
		top.add(varTextDown);

		LinearLayout bot = new LinearLayout(true);
		bot.add(new Label(i18n.m("edition.is"), skin)).margin(0, 0, PAD, 0);
		bot.add(stateButton);

		panel.add(top).pad(PAD).center().expand();
		panel.row();
		panel.add(bot).pad(PAD).center().expand();
	}

	protected abstract void actualizeCondition();

	@Override
	protected void showPanel() {
		conditionedComponent = null;

		ModelEntity modelEntity = (ModelEntity) selection
				.getSingle(Selection.SCENE_ELEMENT);
		for (ModelComponent component : modelEntity.getComponents()) {
			if (component.getClass().equals(myClass)) {
				conditionedComponent = (ModelConditionedComponent) component;
				break;
			}
		}

		actualizePanel();
		super.showPanel();
	}

	protected String createCondition() {
		return "(eq $" + varTextDown.getSelectedVariableDef().getName() + " "
				+ getState() + ")";
	}

	private String getState() {
		if (stateButton.getText().toString().equals(i18n.m("edition.true"))) {
			return "btrue";
		} else {
			return "bfalse";
		}
	}

	public void actualizePanel() {
		String var = null;
		String expression = null;
		if (conditionedComponent != null) {
			// expression of the "(eq $var bvalor)" form
			String aux = conditionedComponent.getCondition();
			aux = aux.replace("(", "");
			aux = aux.replace(")", "");
			aux = aux.replace("$", "");

			String[] sep = aux.split(" ");

			var = sep[1];
			expression = sep[2];
		}
		varTextDown.reloadPanel(var);
		stateButton.selectText(booleanToString(expression));

	}

	private String booleanToString(String string) {
		if (string != null && string.equals("btrue")) {
			return i18n.m("edition.true");
		} else {
			return i18n.m("edition.false");
		}
	}

	@Override
	protected InputListener trashListener() {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (conditionedComponent != null) {
					controller.action(RemoveConditionedComponent.class,
							conditionedComponent);
					conditionedComponent = null;
				}
				actualizePanel();
			}
		};
	}

}
